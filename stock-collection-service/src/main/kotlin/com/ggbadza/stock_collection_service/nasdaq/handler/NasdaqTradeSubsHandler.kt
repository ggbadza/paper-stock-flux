package com.ggbadza.stock_collection_service.nasdaq.handler

import com.ggbadza.stock_collection_service.common.SubscriptionHandler
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.nasdaq.dto.NasdaqTradeDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class NasdaqTradeSubsHandler(
    private val apiProperties: ApiProperties
) : SubscriptionHandler<NasdaqTradeDto> {

    override fun getTrId(): String = apiProperties.websocket.nasdaq.tradeId

    override fun createRequest(approvalKey: String, ticker: String): String {
        logger.info { "'${ticker}'에 대한 체결가 API 요청 생성." }
        return """
                {
                    "header": {"approval_key":"$approvalKey","custtype":"P","tr_type":"1","content-type":"utf-8"},
                    "body": {"input":{"tr_id":"${getTrId()}","tr_key":"$ticker"}}
                }
                """.trimIndent()
    }

    override fun processData(payload: String, count: Int): List<NasdaqTradeDto> {
        logger.debug { "NASDAQ 체결 데이터 수신 ({$count}개): {$payload}"}
        try {
            val fields = payload.split('^')
            val expectedFieldCount = 26 * count
            if (fields.size < expectedFieldCount) {
                throw IllegalArgumentException("수신된 NASDAQ 체결 데이터의 필드 개수(${fields.size})가 예상(${expectedFieldCount})보다 적습니다.")
            }

            // 0부터 count-1까지 반복하면서 List<NasdaqTradeDto>를 생성
            return (0 until count).map { i ->
                val offset = i * 26 // 각 데이터의 시작 인덱스
                NasdaqTradeDto(
                    rsym = fields[offset + 0],
                    symb = fields[offset + 1],
                    zdiv = fields[offset + 2],
                    tymd = fields[offset + 3],
                    xymd = fields[offset + 4],
                    xhms = fields[offset + 5],
                    kymd = fields[offset + 6],
                    khms = fields[offset + 7],
                    open = fields[offset + 8],
                    high = fields[offset + 9],
                    low = fields[offset + 10],
                    last = fields[offset + 11],
                    sign = fields[offset + 12],
                    diff = fields[offset + 13],
                    rate = fields[offset + 14],
                    pbid = fields[offset + 15],
                    pask = fields[offset + 16],
                    vbid = fields[offset + 17],
                    vask = fields[offset + 18],
                    evol = fields[offset + 19],
                    tvol = fields[offset + 20],
                    tamt = fields[offset + 21],
                    bivl = fields[offset + 22],
                    asvl = fields[offset + 23],
                    strn = fields[offset + 24],
                    mtyp = fields[offset + 25]
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "NASDAQ 체결 데이터 파싱 중 오류 발생. 데이터: $payload" }
            throw e
        }
    }
}