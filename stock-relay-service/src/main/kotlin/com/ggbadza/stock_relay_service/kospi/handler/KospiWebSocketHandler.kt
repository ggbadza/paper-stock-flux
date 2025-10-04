package com.ggbadza.stock_relay_service.kospi.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggbadza.stock_relay_service.common.enums.WebSocketCommand
import com.ggbadza.stock_relay_service.kospi.service.KospiOrderBookBroadcaster
import com.ggbadza.stock_relay_service.kospi.service.KospiTradeBroadcaster
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class KospiWebSocketHandler(
    private val tradeBroadcaster: KospiTradeBroadcaster,
    private val orderBookBroadcaster: KospiOrderBookBroadcaster,
    private val objectMapper: ObjectMapper
) : WebSocketHandler {

    data class WebSocketRequest(val command: WebSocketCommand, val ticker: String)

    private fun parseMessage(message: String): WebSocketRequest? {
        val parts = message.split(":")
        val commandString = parts.getOrNull(0) // 커맨드 (ex: 구독 명령 등)
        val ticker = parts.getOrNull(1) // 주식 티커

        val command = WebSocketCommand.from(commandString)

        return if (command != null && ticker != null) {
            WebSocketRequest(command, ticker)
        } else {
            null
        }
    }

    override fun handle(session: WebSocketSession): Mono<Void> {

        // 클라이언트가 보내는 구독 요청 메시지(e.g., "SUBSCRIBE:005930")를 처리하는 로직
        val input = session.receive()
            .mapNotNull { parseMessage(it.payloadAsText) }
            .flatMap { request ->
                when (request!!.command) { // mapNotNull에서 처리하므로 !! 사용
                    WebSocketCommand.SUBSCRIBE -> {

                        println("Client ${session.id} subscribed to ${request.ticker}")

                        val tradeStream = tradeBroadcaster.getStockDataStream()
                            .filter { it.getTicker() == request.ticker }
                            .map { tradeData -> objectMapper.writeValueAsString(tradeData) }

                        val orderBookStream = orderBookBroadcaster.getStockDataStream()
                            .filter { it.getTicker() == request.ticker }
                            .map { orderBookData -> objectMapper.writeValueAsString(orderBookData) }

                        val combinedStream = Flux.merge(tradeStream, orderBookStream)
                            .map { jsonString -> session.textMessage(jsonString) }

                        return@flatMap session.send(combinedStream)
                    }
                    WebSocketCommand.UNSUBSCRIBE -> {
                        // TODO: 구독 취소 로직 구현
                        println("Client ${session.id} unsubscribed from ${request.ticker}")
                        return@flatMap Mono.empty<Void>()
                    }
                }
            }.then()

        return input.doFinally {
            println("Client ${session.id} disconnected.")
        }
    }


}