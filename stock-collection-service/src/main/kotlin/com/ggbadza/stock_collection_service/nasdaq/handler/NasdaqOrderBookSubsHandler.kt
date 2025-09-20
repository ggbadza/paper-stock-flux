package com.ggbadza.stock_collection_service.nasdaq.handler

import com.ggbadza.stock_collection_service.common.SubscriptionHandler
import com.ggbadza.stock_collection_service.config.ApiProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class NasdaqOrderBookSubsHandler(
    private val apiProperties: ApiProperties
) : SubscriptionHandler {

    override fun getTrId(): String = apiProperties.websocket.kospi.orderBookId
    override fun createRequest(approvalKey: String, ticker: String): String {
        logger.info { "'${ticker}'에 대한 호가 API 요청 생성." }
        return """
                {
                    "header": {"approval_key":"$approvalKey","custtype":"P","tr_type":"1","content-type":"utf-8"},
                    "body": {"input":{"tr_id":"${getTrId()}","tr_key":"$ticker"}}
                }
                """.trimIndent()
    }
    override fun processData(payload: String): String {
        // todo: 호가 JSON을 객체로 변환하는 로직
        return "PROCESSED_ORDER_BOOK: $payload"
    }
}