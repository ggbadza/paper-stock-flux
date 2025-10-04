package com.ggbadza.stock_collection_service.kospi.manager

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggbadza.stock_collection_service.common.StockDataMapper
import com.ggbadza.stock_collection_service.common.StockMarketConnector
import com.ggbadza.stock_collection_service.common.SubscriptionHandler
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.kospi.handler.KospiOrderBookSubsHandler
import com.ggbadza.stock_collection_service.kospi.handler.KospiTradeSubsHandler
import com.ggbadza.stock_collection_service.kospi.repository.TrackedKospiRepository
import io.github.oshai.kotlinlogging.KotlinLogging
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

private val logger = KotlinLogging.logger {}

/**
 * KOSPI 실시간 데이터 수집 흐름을 총괄하는 Bean
 */
//@Component
class KospiTradeManager(
    private val trackedKospiRepository: TrackedKospiRepository,
    private val webSocketClient: WebSocketClient,
    private val kafkaSender: KafkaSender<String, StockDataMapper>, // KafkaProducerConfig에서 Bean으로 등록
    private val apiProperties: ApiProperties,
    private val apiKeyManager: KospiApiKeyManager,
    private val objectMapper: ObjectMapper,
    private val orderBookSubsHandler: KospiOrderBookSubsHandler,
    private val tradeSubsHandler: KospiTradeSubsHandler
) : StockMarketConnector {

    // --- 멤버 변수 및 초기화 ---
    private val kospiProperties = apiProperties.websocket.kospi

    private val handlers: List<SubscriptionHandler<*>> = listOf(tradeSubsHandler, orderBookSubsHandler)
    private val handlerMap: Map<String, SubscriptionHandler<*>> = handlers.associateBy { it.getTrId() }


    // --- 웹소켓 연결 로직 ---
    override fun connect(): Mono<Void> {
        return Mono.create { sink ->
            val alreadySignaled = AtomicBoolean(false)
            sink.onDispose { alreadySignaled.set(true) }

            trackedKospiRepository.findAllByIsActiveIsTrue()
                .map { it.ticker }
                .collectList()
                .flatMap { tickers ->
                    if (tickers.isEmpty() || handlers.isEmpty()) {
                        logger.warn { "추적할 활성 KOSPI 종목이 없거나, 구독 핸들러가 없습니다." }
                        return@flatMap Mono.fromRunnable<Void> { sink.success() }
                    }

                    apiKeyManager.getApprovalKey().flatMap { approvalKey ->
                        val uri = URI.create(kospiProperties.websocketUrl)
                        logger.info { "KOSPI 실시간 데이터 웹소켓 연결을 시작합니다. 구독 종목 수: ${tickers.size}, 핸들러 수: ${handlers.size}" }

                        val headers = HttpHeaders()
                        headers.add("approval_key", approvalKey)
                        headers.add("custtype", "P")
                        headers.add("tr_type", "1")
                        headers.add("content-type", "utf-8")

                        val sessionHandler = { session: WebSocketSession ->
                            val subscriptionMessages = Flux.fromIterable(tickers)
                                .flatMap { ticker ->
                                    Flux.fromIterable(handlers).map { handler ->
                                        handler.createRequest(approvalKey, ticker)
                                    }
                                }
                                .delayElements(Duration.ofSeconds(1))
                                .map { requestBody ->
                                    session.textMessage(requestBody)
                                }

                            val sendRequests = session.send(subscriptionMessages)
                                .doOnSuccess {
                                    if (alreadySignaled.compareAndSet(false, true)) {
                                        logger.info { "모든 KOSPI 구독 요청을 전송했습니다. 셋업을 완료합니다." }
                                        sink.success()
                                    }
                                }
                                .doOnError { error ->
                                    if (alreadySignaled.compareAndSet(false, true)) { sink.error(error) }
                                }

                            val receiveMessages = session.receive()
                                .map { it.payloadAsText }
                                .flatMap { payload ->
                                    val payloadList = payload.split("|")

                                    if (payloadList.size < 4) {
                                        if (payload.contains("PINGPONG")) {
                                            logger.debug { "PINGPONG 메시지 수신" }
                                        } else {
                                            logger.warn { "예상치 못한 형식의 데이터를 수신했습니다: $payload" }
                                        }
                                        return@flatMap Mono.empty<Void>()
                                    }

                                    val handler = handlerMap[payloadList[1]]
                                    if (handler != null) {
                                        val processedDataList = handler.processData(payloadList[3], payloadList[2].toInt())
                                        val topic = if (handler.getTrId() == kospiProperties.tradeId) kospiProperties.tradeTopic else kospiProperties.orderBookTopic

                                        return@flatMap Flux.fromIterable(processedDataList)
                                            .flatMap { processedData ->
//                                                val kafkaMessage = objectMapper.writeValueAsString(processedData)
                                                val record = SenderRecord.create(topic, null, null, "${payloadList[1]}_${processedData.getTicker()}", processedData, null)

                                                logger.info { "수신 데이터 (${handler.javaClass.simpleName}): $processedData" }

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