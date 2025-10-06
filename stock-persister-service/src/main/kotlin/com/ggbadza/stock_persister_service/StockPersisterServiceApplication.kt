package com.ggbadza.stock_persister_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class StockPersisterServiceApplication

fun main(args: Array<String>) {
	runApplication<StockPersisterServiceApplication>(*args)
}
