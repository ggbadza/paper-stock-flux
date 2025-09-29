package com.ggbadza.stock_relay_service.nasdaq.service

import com.ggbadza.stock_collection_service.nasdaq.dto.NasdaqOrderBookDto
import com.ggbadza.stock_collection_service.nasdaq.dto.NasdaqTradeDto
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.kafka.receiver.KafkaReceiver

@Service
class NasdaqOrderBookBroadcaster(
    private val nasdaqKafkaTradeReceiver: KafkaReceiver<String, NasdaqTradeDto>
) {
    // 멀티캐스트(multicast)가 가능한 Sink를 생성
    private val sink: Sinks.Many<NasdaqTradeDto> = Sinks.many().multicast().onBackpressureBuffer()

    // 애플리케이션 시작 시 카프카 메시지 수신을 시작
    @PostConstruct
    fun startKafkaConsumption() {
        nasdaqKafkaTradeReceiver.receive()
            .doOnNext { record ->
                // 카프카에서 메시지를 받으면 Sink로 즉시 전달
                // key는 주식 코드(ticker)
                val stockData = record.value()
                println("Received from Kafka: $stockData")
                sink.tryEmitNext(stockData) // Sink에 데이터 발행
            }
            .subscribe() // 구독을 시작
    }

    /**
     * 웹소켓 핸들러가 이 메소드를 호출하여 나스닥 체결가에 대한 Flux 객체를 획득
     */
    fun getStockDataStream(): Flux<NasdaqTradeDto> {
        return sink.asFlux()
    }
}