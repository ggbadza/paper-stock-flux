package com.ggbadza.stock_collection_service.kospi.manager

import com.ggbadza.stock_collection_service.common.StockMarketConnector
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.kospi.repository.TrackedKospiRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
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
class KospiConnectionManager(
    private val trackedKospiRepository: TrackedKospiRepository, // R2DBC 리포지토리
    private val webSocketClient: WebSocketClient, // WebSocketClientConfig에서 Bean으로 등록
    private val kafkaSender: KafkaSender<String, String>, // KafkaProducerConfig에서 Bean으로 등록
    private val apiProperties: ApiProperties // API 설정 클래스
) : StockMarketConnector {


    override fun connect(): Flux<Void> {
        return trackedKospiRepository.findAllByActiveIsTrue()
            .flatMap { stock -> // 각 주식에 대해 WebSocket 연결 실행
                val uri = URI.create(apiProperties.websocket.kospiUrl)
                logger.info { "${stock.name}(${stock.ticker})에 대한 웹소켓 연결을 시작합니다. URI: $uri" }

                val handler = createWebSocketHandler(stock.ticker) // 메시지 핸들러 생성

                webSocketClient.execute(uri, handler)
                    .doOnError { error -> logger.error(error) { "${stock.ticker} 종목의 웹소켓 연결에서 오류가 발생했습니다." } }
                    .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5))) // 재연결 시도
            }
    }

    private fun createWebSocketHandler(ticker: String): (WebSocketSession) -> Mono<Void> {
        return { session ->
            session.receive() // Flux 형태로 메세지 수신
                .map(WebSocketMessage::getPayloadAsText) // 각 Flux내 텍스트 페이로드 추출
                .flatMap { payload ->
                    // 데이터 정제 로직
                    val processedData = processStockData(payload)

                    // 정제된 데이터를 Kafka로 전송 "stock-data"
                    val record = ProducerRecord("stock-data", ticker, processedData)
                    val senderRecord = SenderRecord.create(record, null) // SenderRecord로 감싸기

                    kafkaSender.send(Mono.just(senderRecord)) // Mono/Flux 형태로 전송
                        .doOnNext { logger.info { "$ticker 종목 데이터를 카프카로 전송했습니다." } }
                        .then() // Mono<Void>를 반환하여 스트림을 계속 진행
                }
                .then() // 모든 메시지 처리가 완료될 때까지 스트림 유지
        }
    }

    private fun processStockData(payload: String): String {
        // todo JSON을 객체로 변환하는 로직
        return "PROCESSED: $payload"
    }

}