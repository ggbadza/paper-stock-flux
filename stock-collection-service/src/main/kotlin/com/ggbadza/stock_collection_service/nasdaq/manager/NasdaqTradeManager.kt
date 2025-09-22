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
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
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
//    private val kafkaSender: KafkaSender<String, String>, // KafkaProducerConfig에서 Bean으로 등록
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

            trackedNasdaqRepository.findAllByIsActiveIsTrue()
                .map { it.ticker }
                .collectList()
                .flatMap { tickers ->
                    if (tickers.isEmpty() || handlers.isEmpty()) {
                        logger.warn { "추적할 활성 NASDAQ 종목이 없거나, 구독 핸들러가 없습니다." }
                        return@flatMap Mono.fromRunnable<Void> { sink.success() }
                    }

                    apiKeyManager.getApprovalKey().flatMap { approvalKey ->
                        val uri = URI.create(nasdaqProperties.websocketUrl)
                        logger.info { "NASDAQ 실시간 데이터 웹소켓 연결을 시작합니다. 구독 종목 수: ${tickers.size}, 핸들러 수: ${handlers.size}" }

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
                                        logger.info { "모든 NASDAQ 구독 요청을 전송했습니다. 셋업을 완료합니다." }
                                        sink.success()
                                    }
                                }
                                .doOnError { error ->
                                    if (alreadySignaled.compareAndSet(false, true)) { sink.error(error) }
                                }

                            val receiveMessages = session.receive()
                                .map{it.payloadAsText}
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

                                        for(processedData in processedDataList){
                                            logger.info { "수신 데이터 (${handler.javaClass.simpleName}): $processedData" }

                                            val kafkaMessage = objectMapper.writeValueAsString(processedData)
                                            // 카프카에 전송
                                            logger.info { "[${payloadList[1]}_${processedData.getTicker()}]으로 데이터 전송 완료: $kafkaMessage" }

                                        }
                                    } else {
                                        logger.warn { "처리할 핸들러가 없는 데이터를 수신했습니다: $payload" }
                                    }
                                    Mono.empty<Void>()
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