package com.ggbadza.stock_relay_service.nasdaq.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggbadza.stock_relay_service.nasdaq.service.NasdaqStockBroadcaster
import org.springframework.stereotype.Component

@Component
class NasdaqWebSocketHandler(
    private val broadcaster: NasdaqStockBroadcaster, // 중앙 허브 주입
    private val objectMapper: ObjectMapper
) {
}