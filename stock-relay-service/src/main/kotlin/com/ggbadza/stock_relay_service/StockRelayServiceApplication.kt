package com.ggbadza.stock_relay_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class StockRelayServiceApplication

fun main(args: Array<String>) {
	runApplication<StockRelayServiceApplication>(*args)
}
