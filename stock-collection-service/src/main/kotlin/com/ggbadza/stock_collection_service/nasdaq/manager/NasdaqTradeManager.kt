package com.ggbadza.stock_collection_service.nasdaq.manager

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggbadza.stock_collection_service.common.StockDataMapper
import com.ggbadza.stock_collection_service.common.StockMarketConnector
import com.ggbadza.stock_collection_service.common.SubscriptionHandler
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.kospi.handler.KospiOrderBookSubsHandler
import com.ggbadza.stock_collection_service.kospi.handler.KospiTradeSubsHandler
import com.ggbadza.stock_collection_service.kospi.manager.KospiApiKeyManager
import com.ggbadza.stock_collection_service.kospi.repository.TrackedKospiRepository
import com.ggbadza.stock_collection_service.nasdaq.handler.NasdaqOrderBookSubsHandler
import com.ggbadza.stock_collection_service.nasdaq.handler.NasdaqTradeSubsHandler
import com.ggbadza.stock_collection_service.nasdaq.repository.TrackedNasdaqRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.Int

private val logger = KotlinLogging.logger {}

/**
 * 실시간 체결가 데이터를 웹소켓을 이용해 받아오는 Bean
 */
@Component
class NasdaqTradeManager(
    private val trackedNasdaqRepository: TrackedNasdaqRepository, // R2DBC 리포지토리
    private val webSocketClient: WebSocketClient, // WebSocketClientConfig에서 Bean으로 등록
    private val kafkaSender: KafkaSender<String, StockDataMapper>, // KafkaProducerConfig에서 Bean으로 등록
    private val objectMapper: ObjectMapper,
    private val apiProperties: ApiProperties, // API 설정 클래스
    private val apiKeyManager: NasdaqApiKeyManager,
    private val orderBookSubsHandler: NasdaqOrderBookSubsHandler,
    private val tradeSubsHandler: NasdaqTradeSubsHandler
) : StockMarketConnector {

    private val nasdaqProperties = apiProperties.websocket.nasdaq

    private val handlers: List<SubscriptionHandler<out StockDataMapper>> = listOf(tradeSubsHandler, orderBookSubsHandler)
    private val handlerMap: Map<String, SubscriptionHandler<out StockDataMapper>> = handlers.associateBy { it.getTrId() }


    // --- 웹소켓 연결 로직 ---
    override fun connect(): Mono<Void> {
        return Mono.create { sink ->
            val alreadySignaled = AtomicBoolean(false)
            sink.onDispose { alreadySignaled.set(true) }

            // 1. 활성화 된 나스닥 종목들을 가져옮
            trackedNasdaqRepository.findAllByIsActiveIsTrue()
                .map { it.ticker }
                .collectList()
                .flatMap { tickers ->
                    val dayPrefix = "RBAQ" // 야간 거래
                    val nightPrefix = "DNAS" // 주간 거래
                    val fullTickerKeys = tickers.flatMap { ticker ->
                        listOf("$dayPrefix$ticker", "$nightPrefix$ticker")
                    }

                    if (fullTickerKeys.isEmpty() || handlers.isEmpty()) {
                        logger.warn { "추적할 활성 NASDAQ 종목이 없거나, 구독 핸들러가 없습니다." }
                        return@flatMap Mono.fromRunnable<Void> { sink.success() }
                    }

                    // 2. Approval Key를 가져옴
                    apiKeyManager.getApprovalKey().flatMap { approvalKey ->
                        val uri = URI.create(nasdaqProperties.websocketUrl)
                        logger.info { "NASDAQ 실시간 데이터 웹소켓 연결을 시작합니다. 구독 키 수: ${fullTickerKeys.size}, 핸들러 수: ${handlers.size}" }

                        val headers = HttpHeaders()
                        headers.add("approval_key", approvalKey)
                        headers.add("custtype", "P")
                        headers.add("tr_type", "1")
                        headers.add("content-type", "utf-8")

                        // 3. 웹소켓 핸들러 정의
                        val sessionHandler = { session: WebSocketSession ->
                            val subscriptionMessages = Flux.fromIterable(fullTickerKeys)
                                .flatMap { fullKey ->
                                    // 각 핸들러별 요청 생성
                                    Flux.fromIterable(handlers).map { handler ->
                                        handler.createRequest(approvalKey, fullKey)
                                    }
                                }
                                .delayElements(Duration.ofSeconds(1))
                                .map { requestBody ->
                                    session.textMessage(requestBody)
                                }

                            val sendRequests = session.send(subscriptionMessages)
                                .doOnSuccess {
                                    if (alreadySignaled.compareAndSet(false, true)) {
                                        logger.info { "모든 NASDAQ 구독 요청을 전송했습니다. 셋업을 완료합니다." }
                                        sink.success()
                                    }
                                }
                                .doOnError { error ->
                                    if (alreadySignaled.compareAndSet(false, true)) { sink.error(error) }
                                }

                            // 4. 메세지 수신 정의
                            val receiveMessages = session.receive()
                                .map { it.payloadAsText }
                                .flatMap { payload ->
                                    // -- 각 데이터는 가변전문으로 "|"으로 구분 됨 --
                                    val payloadList = payload.split("|")

                                    // 4-a. 4개 미만의 페이로드 데이터는 비정상적인 데이터로 오류 처리
                                    if (payloadList.size < 4) {
                                        if (payload.contains("PINGPONG")) {
                                            logger.debug { "PINGPONG 메시지 수신" }
                                        } else {
                                            logger.warn { "예상치 못한 형식의 데이터를 수신했습니다: $payload" }
                                        }
                                        return@flatMap Mono.empty<Void>()
                                    }

                                    // 4-b. 페이로드에 대응하는 핸들러 호출
                                    val handler = handlerMap[payloadList[1]]
                                    if (handler != null) {
                                        // 4-c. 핸들러로 데이터 처리 (체결가는 한번에 여러 묶음의 전문이 올 수 있어서 리스트로 데이터를 변경)
                                        val processedDataList = handler.processData(payloadList[3], payloadList[2].toInt())
                                        val topic = if (handler.getTrId() == nasdaqProperties.tradeId) nasdaqProperties.tradeTopic else nasdaqProperties.orderBookTopic

                                        return@flatMap Flux.fromIterable(processedDataList)
                                            .flatMap { processedData ->
                                                // 4-d. 카프카로 전송할 메세지로 변경
//                                                val kafkaMessage = objectMapper.writeValueAsString(processedData)
                                                val record = SenderRecord.create(topic,null, null, "${payloadList[1]}_${processedData.getTicker()}", processedData, null)

                                                logger.info { "수신 데이터 (${handler.javaClass.simpleName}): $processedData" }

                                                // 4-e. 카프카에 메세지 전송
                                                kafkaSender.send(Mono.just(record))
                                                    .doOnNext { result ->
                                                        val metadata = result.recordMetadata()
                                                        logger.info {
                                                            "[$topic]으로 데이터 전송 완료: ${processedData.getTicker()} " +
                                                                    "partition ${metadata.partition()} offset ${metadata.offset()}"
                                                        }
                                                    }
                                                    .doOnError { e -> logger.error(e) { "Kafka 전송 실패: $processedData" } }
                                            }
                                            .then()
                                    } else {
                                        logger.warn { "처리할 핸들러가 없는 데이터를 수신했습니다: $payload" }
                                        return@flatMap Mono.empty<Void>()
                                    }
                                }

                            sendRequests.thenMany(receiveMessages).then()
                        }

                        // 5. 정의된 핸들러를 사용하여 웹소켓 연결 수행
                        webSocketClient.execute(uri, headers, sessionHandler)
                            .doOnError { error -> logger.error(error) { "NASDAQ 웹소켓 연결에서 오류가 발생했습니다." } }
                            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5)))
                    }
                }
                .subscribe(null, { error ->
                    if (alreadySignaled.compareAndSet(false, true)) { sink.error(error) }
                })
        }
    }
}