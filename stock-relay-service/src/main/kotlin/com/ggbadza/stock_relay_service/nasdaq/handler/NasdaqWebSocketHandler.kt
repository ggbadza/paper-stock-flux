package com.ggbadza.stock_relay_service.nasdaq.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggbadza.stock_relay_service.nasdaq.service.NasdaqOrderBookBroadcaster
import com.ggbadza.stock_relay_service.nasdaq.service.NasdaqTradeBroadcaster
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class NasdaqWebSocketHandler(
    private val tradeBroadcaster: NasdaqTradeBroadcaster,
    private val orderBookBroadcaster: NasdaqOrderBookBroadcaster,
    private val objectMapper: ObjectMapper
) : WebSocketHandler {
    override fun handle(session: WebSocketSession): Mono<Void> {

        // 클라이언트가 보내는 구독 요청 메시지(e.g., "SUBSCRIBE:005930")를 처리하는 로직
        val input = session.receive()
            .map { it.payloadAsText }
            .flatMap { message ->
                val parts = message.split(":")
                val command = parts.getOrNull(0) // 커맨드 (ex: 구독 명령 등)
                val ticker = parts.getOrNull(1) // 주식 티커

                if (command == "SUBSCRIBE" && ticker != null) {
                    println("Client ${session.id} subscribed to $ticker")


                    // 1. 거래(Trade) 데이터 스트림 생성
                    val tradeStream = tradeBroadcaster.getStockDataStream()
                        .filter { it.getTicker() == ticker }
                        // object를 JSON 문자열로 변환
                        .map { tradeData -> objectMapper.writeValueAsString(tradeData) }

                    // 2. 호가(Order Book) 데이터 스트림 생성
                    val orderBookStream = orderBookBroadcaster.getStockDataStream()
                        .filter { it.getTicker() == ticker }
                        // object를 JSON 문자열로 변환
                        .map { orderBookData -> objectMapper.writeValueAsString(orderBookData) }

                    // 3. Flux.merge()를 사용해 두 스트림을 하나로 합치기
                    val combinedStream = Flux.merge(tradeStream, orderBookStream)
                        .map { jsonString ->
                            // 합쳐진 스트림의 각 JSON 문자열을 WebSocket 메시지로 변환
                            session.textMessage(jsonString)
                        }

                    // 필터링된 스트림을 클라이언트에게 전송
                    return@flatMap session.send(combinedStream)
                }

                Mono.empty()
            }.then()

        return input.doFinally {
            println("Client ${session.id} disconnected.")
        }
    }
}