package com.ggbadza.stock_collection_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient

@Configuration
open class WebSocketClientConfig {
    @Bean
    open fun webSocketClient(): WebSocketClient {
        return ReactorNettyWebSocketClient()
    }
}