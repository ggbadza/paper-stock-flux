package com.ggbadza.stock_persister_service.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api")
data class ApiProperties(
    val nasdaq: MarketProperties,
    val kospi: MarketProperties
) {
    data class MarketProperties(
        val kafka: KafkaProperties
    )

    data class KafkaProperties(
        val tradeTopic: String,
        val orderBookTopic: String
    )
}