package com.ggbadza.stock_collection_service.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api")
data class ApiProperties(
    val websocket: WebsocketProperties
) {
    data class WebsocketProperties(
        val kospi: MarketProperties,
        val nasdaq: MarketProperties
    )

    data class MarketProperties(
        val keyUrl: String,
        val websocketUrl: String,
        val tradeId: String,
        val orderBookId: String,
        val appKey: String,
        val appSecret: String
    )
}