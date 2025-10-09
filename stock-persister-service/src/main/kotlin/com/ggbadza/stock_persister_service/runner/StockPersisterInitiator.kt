package com.ggbadza.stock_persister_service.runner

import com.ggbadza.stock_persister_service.consumer.StockDataConsumer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class StockPersisterInitiator(
    private val stockDataConsumer: StockDataConsumer
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        logger.info { "주식 데이터 소비 시작" }
        stockDataConsumer.startConsumers()
    }
}
