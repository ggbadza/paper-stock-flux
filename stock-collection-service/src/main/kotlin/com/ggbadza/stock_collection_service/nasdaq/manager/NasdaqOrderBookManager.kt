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
        return trackedNasdaqRepository.findAllByIsActiveIsTrue()
            .map { it.ticker }
            .collectList()
            .flatMap { tickers ->
                if (tickers.isEmpty()) {
                    logger.warn { "추적할 활성 NASDAQ 종목이 없습니다." }
                    return@flatMap Mono.empty<Void>()
                }

                apiKeyManager.getApprovalKey().flatMap { approvalKey ->
                    val uri = URI.create(nasdaqProperties.websocketUrl + nasdaqProperties.orderBookId)
                    logger.info { "NASDAQ 실시간 호가 웹소켓 연결을 시작합니다. 구독 종목 수: ${tickers.size}" }

                    val headers = HttpHeaders()
                    headers.add("approval_key", approvalKey)
                    headers.add("custtype", "P")
                    headers.add("tr_type", "1")
                    headers.add("content-type", "utf-8")

                    // ✅ 변경점 2: 핸들러에 티커 리스트 전체를 전달
                    val handler = createWebSocketHandler(approvalKey, tickers)

                    webSocketClient.execute(uri, headers, handler)
                        .doOnError { error -> logger.error(error) { "NASDAQ 웹소켓 연결에서 오류가 발생했습니다." } }
                        .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5)))
                }
            }
    }


    private fun createWebSocketHandler(approvalKey: String, tickers: List<String>): (WebSocketSession) -> Mono<Void> {
        return { session ->
            val subscriptionMessages = Flux.fromIterable(tickers)
                .map { ticker ->
                    // 헤더 및 바디 설정
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
                    session.textMessage(requestBody)
                }

            // 여러 종목에 대해 순차적 구독 요청
            val sendRequests = session.send(subscriptionMessages)
                .doOnNext { logger.info { "${tickers.size}개 종목에 대한 호가 구독 요청을 모두 전송했습니다." } }


            // 서버로부터 오는 모든 실시간 데이터를 수신
            val receiveMessages = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap { payload ->
                    // PINGPONG 데이터 처리 로직
                    if (payload.contains("PINGPONG")) {
                        logger.debug { "PINGPONG 메시지 수신" }
                        return@flatMap Mono.empty<Void>()
                    }

                    val processedData = processOrderBookData(payload)
                    logger.info { "수신 데이터: $processedData" }

                    // todo Kafka 전송 로직...
                    Mono.empty<Void>()
                }

            sendRequests.thenMany(receiveMessages).then()
        }
    }

    private fun processOrderBookData(payload: String): String {
        // todo 호가 데이터에 맞는 JSON 객체 변환 로직 구현
        return "PROCESSED_ORDER_BOOK: $payload"
    }
}