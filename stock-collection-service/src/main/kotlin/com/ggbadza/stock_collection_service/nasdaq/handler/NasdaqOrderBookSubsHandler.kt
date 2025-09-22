package com.ggbadza.stock_collection_service.nasdaq.handler

import com.ggbadza.stock_collection_service.common.SubscriptionHandler
import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.nasdaq.dto.NasdaqOrderBookDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class NasdaqOrderBookSubsHandler(
    private val apiProperties: ApiProperties
) : SubscriptionHandler<NasdaqOrderBookDto> {

    override fun getTrId(): String = apiProperties.websocket.nasdaq.orderBookId

    override fun createRequest(approvalKey: String, ticker: String): String {
        logger.info { "'${ticker}'에 대한 호가 API 요청 생성." }
        return """
                {
                    "header": {"approval_key":"$approvalKey","custtype":"P","tr_type":"1","content-type":"utf-8"},
                    "body": {"input":{"tr_id":"${getTrId()}","tr_key":"DNAS$ticker"}}
                }
                """.trimIndent()
    }

    override fun processData(payload: String, count: Int): List<NasdaqOrderBookDto> {
        // 호가는 1건만 들어옴
        if (count != 1) {
            logger.warn { "NASDAQ 호가 데이터의 count가 1이 아닙니다: $count. 첫 번째 데이터만 처리합니다." }
        }
        logger.debug { "NASDAQ 호가 데이터 수신: $payload" }
        try {
            val fields = payload.split('^')
            // 호가 데이터는 17개 필드를 가짐
            if (fields.size < 17) {
                throw IllegalArgumentException("수신된 NASDAQ 호가 데이터의 필드 개수가 17개 미만입니다. 수신된 데이터: $payload")
            }
            val dto = NasdaqOrderBookDto(
                rsym = fields[0],
                symb = fields[1],
                zdiv = fields[2],
                xymd = fields[3],
                xhms = fields[4],
                kymd = fields[5],
                khms = fields[6],
                bvol = fields[7],
                avol = fields[8],
                bdvl = fields[9],
                advl = fields[10],
                pbid1 = fields[11],
                pask1 = fields[12],
                vbid1 = fields[13],
                vask1 = fields[14],
                dbid1 = fields[15],
                dask1 = fields[16]
            )
            return listOf(dto)
        } catch (e: Exception) {
            logger.error(e) { "NASDAQ 호가 데이터 파싱 중 오류 발생. 데이터: $payload" }
            throw e
        }
    }
}