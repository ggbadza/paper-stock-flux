package com.ggbadza.stock_collection_service.kospi.handler

import com.ggbadza.stock_collection_service.common.SubscriptionHandler
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.kospi.dto.KospiTradeDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class KospiTradeSubsHandler(
    private val apiProperties: ApiProperties
) : SubscriptionHandler<KospiTradeDto> {

    override fun getTrId(): String = apiProperties.websocket.kospi.tradeId

    override fun createRequest(approvalKey: String, ticker: String): String {
        logger.info { "'${ticker}'에 대한 체결가 API 요청 생성." }
        return """
                {
                    "header": {"approval_key":"$approvalKey","custtype":"P","tr_type":"1","content-type":"utf-8"},
                    "body": {"input":{"tr_id":"${getTrId()}","tr_key":"$ticker"}}
                }
                """.trimIndent()
    }

    override fun processData(payload: String, count: Int): List<KospiTradeDto> {
        logger.debug { "KOSPI 체결 데이터 수신 ({$count}개): {$payload}"}
        try {
            val fields = payload.split('^')
            val expectedFieldCount = 46 * count
            if (fields.size < expectedFieldCount) {
                throw IllegalArgumentException("수신된 KOSPI 체결 데이터의 필드 개수(${fields.size})가 예상(${expectedFieldCount})보다 적습니다.")
            }

            return (0 until count).map { i ->
                val offset = i * 46
                KospiTradeDto(
                    mkscShrnIscd = fields[offset + 0],
                    stckCntgHour = fields[offset + 1],
                    stckPrpr = fields[offset + 2],
                    prdyVrssSign = fields[offset + 3],
                    prdyVrss = fields[offset + 4],
                    prdyCtrt = fields[offset + 5],
                    wghnAvrgStckPrc = fields[offset + 6],
                    stckOprc = fields[offset + 7],
                    stckHgpr = fields[offset + 8],
                    stckLwpr = fields[offset + 9],
                    askp1 = fields[offset + 10],
                    bidp1 = fields[offset + 11],
                    cntgVol = fields[offset + 12],
                    acmlVol = fields[offset + 13],
                    acmlTrPbmn = fields[offset + 14],
                    selnCntgCsnu = fields[offset + 15],
                    shnuCntgCsnu = fields[offset + 16],
                    ntbyCntgCsnu = fields[offset + 17],
                    cttr = fields[offset + 18],
                    selnCntgSmtn = fields[offset + 19],
                    shnuCntgSmtn = fields[offset + 20],
                    ccldDvsn = fields[offset + 21],
                    shnuRate = fields[offset + 22],
                    prdyVolVrssAcmlVolRate = fields[offset + 23],
                    oprcHour = fields[offset + 24],
                    oprcVrssPrprSign = fields[offset + 25],
                    oprcVrssPrpr = fields[offset + 26],
                    hgprHour = fields[offset + 27],
                    hgprVrssPrprSign = fields[offset + 28],
                    hgprVrssPrpr = fields[offset + 29],
                    lwprHour = fields[offset + 30],
                    lwprVrssPrprSign = fields[offset + 31],
                    lwprVrssPrpr = fields[offset + 32],
                    bsopDate = fields[offset + 33],
                    newMkopClsCode = fields[offset + 34],
                    trhtYn = fields[offset + 35],
                    askpRsqn1 = fields[offset + 36],
                    bidpRsqn1 = fields[offset + 37],
                    totalAskpRsqn = fields[offset + 38],
                    totalBidpRsqn = fields[offset + 39],
                    volTnrt = fields[offset + 40],
                    prdySmnsHourAcmlVol = fields[offset + 41],
                    prdySmnsHourAcmlVolRate = fields[offset + 42],
                    hourClsCode = fields[offset + 43],
                    mrktTrtmClsCode = fields[offset + 44],
                    viStndPrc = fields[offset + 45]
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "KOSPI 체결 데이터 파싱 중 오류 발생. 데이터: $payload" }
            throw e
        }
    }
}