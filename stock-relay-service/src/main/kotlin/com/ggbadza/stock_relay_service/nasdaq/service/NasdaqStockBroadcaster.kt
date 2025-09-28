package com.ggbadza.stock_relay_service.nasdaq.service

import org.springframework.stereotype.Service
import reactor.kafka.receiver.KafkaReceiver

@Service
class NasdaqStockBroadcaster(
    private val nasdaqKafkaTradeReceiver: KafkaReceiver<String, String>,
    private val nasdaqKafkaOrderBookReceiver: KafkaReceiver<String, String>
) {
}