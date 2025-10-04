package com.ggbadza.stock_relay_service.kospi.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * KOSPI 실시간 호가 정보 DTO
 */
data class KospiOrderBookDto(
    /** 유가증권 단축 종목코드 */
    @get:JsonProperty("MKSC_SHRN_ISCD")
    val mkscShrnIscd: String,

    /** 영업 시간 */
    @get:JsonProperty("BSOP_HOUR")
    val bsopHour: String,

    /** 시간 구분 코드 */
    @get:JsonProperty("HOUR_CLS_CODE")
    val hourClsCode: String,

    /** 매도호가1 */
    @get:JsonProperty("ASKP1")
    val askp1: String,

    /** 매도호가2 */
    @get:JsonProperty("ASKP2")
    val askp2: String,

    /** 매도호가3 */
    @get:JsonProperty("ASKP3")
    val askp3: String,

    /** 매도호가4 */
    @get:JsonProperty("ASKP4")
    val askp4: String,

    /** 매도호가5 */
    @get:JsonProperty("ASKP5")
    val askp5: String,

    /** 매도호가6 */
    @get:JsonProperty("ASKP6")
    val askp6: String,

    /** 매도호가7 */
    @get:JsonProperty("ASKP7")
    val askp7: String,

    /** 매도호가8 */
    @get:JsonProperty("ASKP8")
    val askp8: String,

    /** 매도호가9 */
    @get:JsonProperty("ASKP9")
    val askp9: String,

    /** 매도호가10 */
    @get:JsonProperty("ASKP10")
    val askp10: String,

    /** 매수호가1 */
    @get:JsonProperty("BIDP1")
    val bidp1: String,

    /** 매수호가2 */
    @get:JsonProperty("BIDP2")
    val bidp2: String,

    /** 매수호가3 */
    @get:JsonProperty("BIDP3")
    val bidp3: String,

    /** 매수호가4 */
    @get:JsonProperty("BIDP4")
    val bidp4: String,

    /** 매수호가5 */
    @get:JsonProperty("BIDP5")
    val bidp5: String,

    /** 매수호가6 */
    @get:JsonProperty("BIDP6")
    val bidp6: String,

    /** 매수호가7 */
    @get:JsonProperty("BIDP7")
    val bidp7: String,

    /** 매수호가8 */
    @get:JsonProperty("BIDP8")
    val bidp8: String,

    /** 매수호가9 */
    @get:JsonProperty("BIDP9")
    val bidp9: String,

    /** 매수호가10 */
    @get:JsonProperty("BIDP10")
    val bidp10: String,

    /** 매도호가 잔량1 */
    @get:JsonProperty("ASKP_RSQN1")
    val askpRsqn1: String,

    /** 매도호가 잔량2 */
    @get:JsonProperty("ASKP_RSQN2")
    val askpRsqn2: String,

    /** 매도호가 잔량3 */
    @get:JsonProperty("ASKP_RSQN3")
    val askpRsqn3: String,

    /** 매도호가 잔량4 */
    @get:JsonProperty("ASKP_RSQN4")
    val askpRsqn4: String,

    /** 매도호가 잔량5 */
    @get:JsonProperty("ASKP_RSQN5")
    val askpRsqn5: String,

    /** 매도호가 잔량6 */
    @get:JsonProperty("ASKP_RSQN6")
    val askpRsqn6: String,

    /** 매도호가 잔량7 */
    @get:JsonProperty("ASKP_RSQN7")
    val askpRsqn7: String,

    /** 매도호가 잔량8 */
    @get:JsonProperty("ASKP_RSQN8")
    val askpRsqn8: String,

    /** 매도호가 잔량9 */
    @get:JsonProperty("ASKP_RSQN9")
    val askpRsqn9: String,

    /** 매도호가 잔량10 */
    @get:JsonProperty("ASKP_RSQN10")
    val askpRsqn10: String,

    /** 매수호가 잔량1 */
    @get:JsonProperty("BIDP_RSQN1")
    val bidpRsqn1: String,

    /** 매수호가 잔량2 */
    @get:JsonProperty("BIDP_RSQN2")
    val bidpRsqn2: String,

    /** 매수호가 잔량3 */
    @get:JsonProperty("BIDP_RSQN3")
    val bidpRsqn3: String,

    /** 매수호가 잔량4 */
    @get:JsonProperty("BIDP_RSQN4")
    val bidpRsqn4: String,

    /** 매수호가 잔량5 */
    @get:JsonProperty("BIDP_RSQN5")
    val bidpRsqn5: String,

    /** 매수호가 잔량6 */
    @get:JsonProperty("BIDP_RSQN6")
    val bidpRsqn6: String,

    /** 매수호가 잔량7 */
    @get:JsonProperty("BIDP_RSQN7")
    val bidpRsqn7: String,

    /** 매수호가 잔량8 */
    @get:JsonProperty("BIDP_RSQN8")
    val bidpRsqn8: String,

    /** 매수호가 잔량9 */
    @get:JsonProperty("BIDP_RSQN9")
    val bidpRsqn9: String,

    /** 매수호가 잔량10 */
    @get:JsonProperty("BIDP_RSQN10")
    val bidpRsqn10: String,

    /** 총 매도호가 잔량 */
    @get:JsonProperty("TOTAL_ASKP_RSQN")
    val totalAskpRsqn: String,

    /** 총 매수호가 잔량 */
    @get:JsonProperty("TOTAL_BIDP_RSQN")
    val totalBidpRsqn: String,

    /** 시간외 총 매도호가 잔량 */
    @get:JsonProperty("OVTM_TOTAL_ASKP_RSQN")
    val ovtmTotalAskpRsqn: String,

    /** 시간외 총 매수호가 잔량 */
    @get:JsonProperty("OVTM_TOTAL_BIDP_RSQN")
    val ovtmTotalBidpRsqn: String,

    /** 예상 체결가 */
    @get:JsonProperty("ANTC_CNPR")
    val antcCnpr: String,

    /** 예상 체결량 */
    @get:JsonProperty("ANTC_CNQN")
    val antcCnqn: String,

    /** 예상 거래량 */
    @get:JsonProperty("ANTC_VOL")
    val antcVol: String,

    /** 예상 체결 대비 */
    @get:JsonProperty("ANTC_CNTG_VRSS")
    val antcCntgVrss: String,

    /** 예상 체결 대비 부호 */
    @get:JsonProperty("ANTC_CNTG_VRSS_SIGN")
    val antcCntgVrssSign: String,

    /** 예상 체결 전일 대비율 */
    @get:JsonProperty("ANTC_CNTG_PRDY_CTRT")
    val antcCntgPrdyCtrt: String,

    /** 누적 거래량 */
    @get:JsonProperty("ACML_VOL")
    val acmlVol: String,

    /** 총 매도호가 잔량 증감 */
    @get:JsonProperty("TOTAL_ASKP_RSQN_ICDC")
    val totalAskpRsqnIcdc: String,

    /** 총 매수호가 잔량 증감 */
    @get:JsonProperty("TOTAL_BIDP_RSQN_ICDC")
    val totalBidpRsqnIcdc: String,

    /** 시간외 총 매도호가 증감 */
    @get:JsonProperty("OVTM_TOTAL_ASKP_ICDC")
    val ovtmTotalAskpIcdc: String,

    /** 시간외 총 매수호가 증감 */
    @get:JsonProperty("OVTM_TOTAL_BIDP_ICDC")
    val ovtmTotalBidpIcdc: String,

    /** 주식 매매 구분 코드 */
    @get:JsonProperty("STCK_DEAL_CLS_CODE")
    val stckDealClsCode: String
) {
    fun getTicker(): String {
        return mkscShrnIscd
    }
}