package com.ggbadza.stock_collection_service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URL

@ConfigurationProperties(prefix = "api")
data class ApiProperties(
    val websocket: WebsocketProperties
) {
    data class WebsocketProperties(
        val kospiKeyUrl: String,
        val kospiTradeUrl: String,
        val kospiOrderBookUrl: String,
        val kospiAppKey: String,
        val kospiAppSecret: String
    )
}