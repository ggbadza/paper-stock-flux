package com.ggbadza.stock_relay_service.config

import com.ggbadza.stock_relay_service.kospi.handler.KospiWebSocketHandler
import com.ggbadza.stock_relay_service.nasdaq.handler.NasdaqWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping

@Configuration
class WebSocketConfig {

    @Bean
    fun websocketKospiHandlerMapping(webSocketHandler: KospiWebSocketHandler): HandlerMapping  {
        val map = mapOf("/ws/stocks/kospi" to webSocketHandler)
        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.order = 1
        handlerMapping.urlMap = map
        return handlerMapping
    }

    @Bean
    fun websocketNasdaqHandlerMapping(webSocketHandler: NasdaqWebSocketHandler): HandlerMapping  {
        val map = mapOf("/ws/stocks/nasdaq" to webSocketHandler)
        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.order = 2
        handlerMapping.urlMap = map
        return handlerMapping
    }
}