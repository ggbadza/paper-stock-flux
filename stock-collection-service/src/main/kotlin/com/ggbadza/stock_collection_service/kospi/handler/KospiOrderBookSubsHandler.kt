package com.ggbadza.stock_collection_service.kospi.handler

import com.ggbadza.stock_collection_service.common.SubscriptionHandler
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.kospi.dto.KospiOrderBookDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class KospiOrderBookSubsHandler(
    private val apiProperties: ApiProperties
) : SubscriptionHandler<KospiOrderBookDto> {

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
    override fun processData(payload: String, count: Int): List<KospiOrderBookDto> {
        // 호가는 1건만 들어옴
        if (count != 1) {
            logger.warn { "KOSPI 호가 데이터의 count가 1이 아닙니다: $count. 첫 번째 데이터만 처리합니다." }
        }
        logger.debug { "KOSPI 호가 데이터 수신: $payload" }
        try {
            val fields = payload.split('^')
            // 호가 데이터는 59개 필드를 가짐
            if (fields.size < 59) {
                throw IllegalArgumentException("수신된 KOSPI 호가 데이터의 필드 개수가 59개 미만입니다. 수신된 데이터: $payload")
            }
            val dto = KospiOrderBookDto(
                mkscShrnIscd = fields[0],
                bsopHour = fields[1],
                hourClsCode = fields[2],
                askp1 = fields[3],
                askp2 = fields[4],
                askp3 = fields[5],
                askp4 = fields[6],
                askp5 = fields[7],
                askp6 = fields[8],
                askp7 = fields[9],
                askp8 = fields[10],
                askp9 = fields[11],
                askp10 = fields[12],
                bidp1 = fields[13],
                bidp2 = fields[14],
                bidp3 = fields[15],
                bidp4 = fields[16],
                bidp5 = fields[17],
                bidp6 = fields[18],
                bidp7 = fields[19],
                bidp8 = fields[20],
                bidp9 = fields[21],
                bidp10 = fields[22],
                askpRsqn1 = fields[23],
                askpRsqn2 = fields[24],
                askpRsqn3 = fields[25],
                askpRsqn4 = fields[26],
                askpRsqn5 = fields[27],
                askpRsqn6 = fields[28],
                askpRsqn7 = fields[29],
                askpRsqn8 = fields[30],
                askpRsqn9 = fields[31],
                askpRsqn10 = fields[32],
                bidpRsqn1 = fields[33],
                bidpRsqn2 = fields[34],
                bidpRsqn3 = fields[35],
                bidpRsqn4 = fields[36],
                bidpRsqn5 = fields[37],
                bidpRsqn6 = fields[38],
                bidpRsqn7 = fields[39],
                bidpRsqn8 = fields[40],
                bidpRsqn9 = fields[41],
                bidpRsqn10 = fields[42],
                totalAskpRsqn = fields[43],
                totalBidpRsqn = fields[44],
                ovtmTotalAskpRsqn = fields[45],
                ovtmTotalBidpRsqn = fields[46],
                antcCnpr = fields[47],
                antcCnqn = fields[48],
                antcVol = fields[49],
                antcCntgVrss = fields[50],
                antcCntgVrssSign = fields[51],
                antcCntgPrdyCtrt = fields[52],
                acmlVol = fields[53],
                totalAskpRsqnIcdc = fields[54],
                totalBidpRsqnIcdc = fields[55],
                ovtmTotalAskpIcdc = fields[56],
                ovtmTotalBidpIcdc = fields[57],
                stckDealClsCode = fields[58]
            )
            return listOf(dto)
        } catch (e: Exception) {
            logger.error(e) { "NASDAQ 호가 데이터 파싱 중 오류 발생. 데이터: $payload" }
            throw e
        }
    }
}