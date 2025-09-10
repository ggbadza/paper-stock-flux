package com.ggbadza.stock_collection_service.kospi.manager

import com.ggbadza.stock_collection_service.common.StockMarketConnector
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.kospi.repository.TrackedKospiRepository
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

private val logger = KotlinLogging.logger {}

@Component
class KospiOrderBookManager(
    private val trackedKospiRepository: TrackedKospiRepository,
    private val webSocketClient: WebSocketClient,
    private val kafkaSender: KafkaSender<String, String>,
    private val apiProperties: ApiProperties
) : StockMarketConnector {

    override fun connect(): Flux<Void> {
        return trackedKospiRepository.findAllByActiveIsTrue()
            .flatMap { stock ->
                val uri = URI.create(apiProperties.websocket.kospiOrderBookUrl)
                logger.info { "KOSPI 호가(${stock.ticker})에 대한 웹소켓 연결을 시작합니다. URI: $uri" }

                // 웹소켓 헤더 설정
                val headers = HttpHeaders()
                headers.add("approval_key", apiProperties.websocket.kospiApprovalKey)
                headers.add("custtype", "P")
                headers.add("tr_type", "2") // "1"은 실시간 체결가, "2"는 실시간 호가
                headers.add("content-type", "utf-8")

                val handler = createWebSocketHandler(stock.ticker)

                webSocketClient.execute(uri, headers, handler)
                    .doOnError { error -> logger.error(error) { "${stock.ticker} 종목의 호가 웹소켓 연결에서 오류가 발생했습니다." } }
                    .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5)))
            }
    }

    private fun createWebSocketHandler(ticker: String): (WebSocketSession) -> Mono<Void> {
        return { session ->
            session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap { payload ->
                    val processedData = processOrderBookData(payload)

                    // 정제된 데이터를 Kafka "kospi-order-book" 토픽으로 전송
                    val record = ProducerRecord("kospi-order-book", ticker, processedData)
                    val senderRecord = SenderRecord.create(record, null)

                    kafkaSender.send(Mono.just(senderRecord))
                        .doOnNext { logger.info { "$ticker 종목 호가 데이터를 카프카로 전송했습니다." } }
                        .then()
                }
                .then()
        }
    }

    private fun processOrderBookData(payload: String): String {
        // todo 호가 데이터에 맞는 JSON 객체 변환 로직 구현
        return "PROCESSED_ORDER_BOOK: $payload"
    }
}
