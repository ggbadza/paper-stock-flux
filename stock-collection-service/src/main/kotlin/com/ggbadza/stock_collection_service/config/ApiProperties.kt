package com.ggbadza.stock_collection_service.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api")
data class ApiProperties(
    val websocket: WebsocketProperties
) {
    data class WebsocketProperties(
        val kospiUrl: String
    )
}