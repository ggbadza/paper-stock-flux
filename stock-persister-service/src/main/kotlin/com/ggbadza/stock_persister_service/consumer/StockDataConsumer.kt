package com.ggbadza.stock_persister_service.consumer

import com.ggbadza.stock_persister_service.common.StockDataMapper
import com.ggbadza.stock_persister_service.kospi.dto.KospiOrderBookDto
import com.ggbadza.stock_persister_service.kospi.dto.KospiTradeDto
import com.ggbadza.stock_persister_service.kospi.repository.KospiOrderBookRepository
import com.ggbadza.stock_persister_service.kospi.repository.KospiTradeRepository
import com.ggbadza.stock_persister_service.nasdaq.dto.NasdaqOrderBookDto
import com.ggbadza.stock_persister_service.nasdaq.dto.NasdaqTradeDto
import com.ggbadza.stock_persister_service.nasdaq.repository.NasdaqOrderBookRepository
import com.ggbadza.stock_persister_service.nasdaq.repository.NasdaqTradeRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver

private val logger = KotlinLogging.logger {}

@Component
class StockDataConsumer(
    // KOSPI
    private val kospiTradeReceiver: KafkaReceiver<String, KospiTradeDto>,
    private val kospiOrderBookReceiver: KafkaReceiver<String, KospiOrderBookDto>,
    private val kospiTradeRepository: KospiTradeRepository,
    private val kospiOrderBookRepository: KospiOrderBookRepository,
    // NASDAQ
    private val nasdaqTradeReceiver: KafkaReceiver<String, NasdaqTradeDto>,
    private val nasdaqOrderBookReceiver: KafkaReceiver<String, NasdaqOrderBookDto>,
    private val nasdaqTradeRepository: NasdaqTradeRepository,
    private val nasdaqOrderBookRepository: NasdaqOrderBookRepository,
    // Mapper
    private val mapper: StockDataMapper
) {

    @PostConstruct
    fun startConsumers() {
        consumeKospiTrades()
        consumeKospiOrderBooks()
        consumeNasdaqTrades()
        consumeNasdaqOrderBooks()
    }

    private fun consumeKospiTrades() {
        kospiTradeReceiver.receive()
            .flatMap { record ->
                val dto = record.value()
                val entity = mapper.toEntity(dto)
                kospiTradeRepository.save(entity)
                    .doOnSuccess { logger.info { "KOSPI 체결가 저장 성공: $entity" } }
                    .doOnError { e -> logger.error(e) { "KOSPI 체결가 저장 실패: $dto" } }
            }
            .subscribe()
    }

    private fun consumeKospiOrderBooks() {
        kospiOrderBookReceiver.receive()
            .flatMap { record ->
                val dto = record.value()
                val entity = mapper.toEntity(dto)
                kospiOrderBookRepository.save(entity)
                    .doOnSuccess { logger.info { "KOSPI 호가 저장 성공: $entity" } }
                    .doOnError { e -> logger.error(e) { "KOSPI 호가 저장 실패: $dto" } }
            }
            .subscribe()
    }

    private fun consumeNasdaqTrades() {
        nasdaqTradeReceiver.receive()
            .flatMap { record ->
                val dto = record.value()
                val entity = mapper.toEntity(dto)
                nasdaqTradeRepository.save(entity)
                    .doOnSuccess { logger.info { "NASDAQ 체결가 저장 성공: $entity" } }
                    .doOnError { e -> logger.error(e) { "NASDAQ 체결가 저장 실패: $dto" } }
            }
            .subscribe()
    }

    private fun consumeNasdaqOrderBooks() {
        nasdaqOrderBookReceiver.receive()
            .flatMap { record ->
                val dto = record.value()
                val entity = mapper.toEntity(dto)
                nasdaqOrderBookRepository.save(entity)
                    .doOnSuccess { logger.info { "NASDAQ 호가 저장 성공: $entity" } }
                    .doOnError { e -> logger.error(e) { "NASDAQ 호가 저장 실패: $dto" } }
            }
            .subscribe()
    }
}
