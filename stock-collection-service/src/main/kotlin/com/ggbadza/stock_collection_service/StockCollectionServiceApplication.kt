package com.ggbadza.stock_collection_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class StockCollectionServiceApplication

fun main(args: Array<String>) {
	runApplication<StockCollectionServiceApplication>(*args)
}
