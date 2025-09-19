package com.ggbadza.stock_collection_service.nasdaq.manager

import com.ggbadza.stock_collection_service.common.StockMarketConnector
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.kospi.manager.KospiApiKeyManager
import com.ggbadza.stock_collection_service.kospi.repository.TrackedKospiRepository
import com.ggbadza.stock_collection_service.nasdaq.repository.TrackedNasdaqRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean


private val logger = KotlinLogging.logger {}

/**
 * 실시간 호가 데이터를 웹소켓을 이용해 받아오는 Bean
 */
@Component
class NasdaqOrderBookManager(
    private val trackedNasdaqRepository: TrackedNasdaqRepository,
    private val webSocketClient: WebSocketClient,
//    private val kafkaSender: KafkaSender<String, String>,
    private val apiProperties: ApiProperties,
    private val apiKeyManager: NasdaqApiKeyManager
) : StockMarketConnector {

    val nasdaqProperties = apiProperties.websocket.nasdaq

    override fun connect(): Mono<Void> {
        return Mono.create { sink ->
            val alreadySignaled = AtomicBoolean(false)
            sink.onDispose { alreadySignaled.set(true) }

            trackedNasdaqRepository.findAllByIsActiveIsTrue()
                .map { it.ticker }
                .collectList()
                .flatMap { tickers ->
                    if (tickers.isEmpty()) {
                        logger.warn { "추적할 활성 NASDAQ 종목이 없습니다." }
                        return@flatMap Mono.fromRunnable<Void> { sink.success() }
                    }

                    apiKeyManager.getApprovalKey().flatMap { approvalKey ->
                        val uri = URI.create(nasdaqProperties.websocketUrl + nasdaqProperties.orderBookId)
                        logger.info { "NASDAQ 실시간 호가 웹소켓 연결을 시작합니다. 구독 종목 수: ${tickers.size}" }

                        val headers = HttpHeaders()
                        headers.add("approval_key", approvalKey)
                        headers.add("custtype", "P")
                        headers.add("tr_type", "1")
                        headers.add("content-type", "utf-8")

                        val handler = { session: WebSocketSession ->
                            val subscriptionMessages = Flux.fromIterable(tickers)
                                .delayElements(Duration.ofSeconds(1)) // 1초 딜레이 추가
                                .map { ticker ->
                                    val requestBody = """
                                    {
                                        "header": {
                                            "approval_key": "$approvalKey",
                                            "custtype": "P",
                                            "tr_type": "1",
                                            "content-type": "utf-8"
                                        },
                                        "body": {
                                            "input": {
                                                "tr_id": "${nasdaqProperties.orderBookId}",
                                                "tr_key": "DNAS$ticker"
                                            }
                                        }
                                    }
                                    """.trimIndent()
                                    logger.info { "'${tickers}'에 대한 호가 API 요청 시도." }
                                    session.textMessage(requestBody)
                                }

                            val sendRequests = session.send(subscriptionMessages)
                                .doOnSuccess {
                                    if (alreadySignaled.compareAndSet(false, true)) {
                                        logger.info { "모든 NASDAQ 호가 구독 요청을 전송했습니다. 셋업을 완료합니다." }
                                        sink.success()
                                    }
                                }
                                .doOnError { error ->
                                    if (alreadySignaled.compareAndSet(false, true)) {
                                        sink.error(error)
                                    }
                                }

                            val receiveMessages = session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .flatMap { payload ->
                                    if (payload.contains("PINGPONG")) {
                                        logger.debug { "PINGPONG 메시지 수신" }
                                        return@flatMap Mono.empty<Void>()
                                    }
                                    val processedData = processOrderBookData(payload)
                                    logger.info { "수신 데이터: $processedData" }
                                    Mono.empty<Void>()
                                }

                            sendRequests.thenMany(receiveMessages).then()
                        }

                        webSocketClient.execute(uri, headers, handler)
                            .doOnError { error -> logger.error(error) { "NASDAQ 웹소켓 연결에서 오류가 발생했습니다." } }
                            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5)))
                    }
                }
                .subscribe(null, { error ->
                    if (alreadySignaled.compareAndSet(false, true)) {
                        sink.error(error)
                    }
                })
        }
    }

    private fun processOrderBookData(payload: String): String {
        // todo 호가 데이터에 맞는 JSON 객체 변환 로직 구현
        return "PROCESSED_ORDER_BOOK: $payload"
    }
}