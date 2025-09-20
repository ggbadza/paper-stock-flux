package com.ggbadza.stock_collection_service.kospi.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.ggbadza.stock_collection_service.common.StockDataMapper

/**
 * KOSPI 실시간 체결가 정보 DTO
 */
data class KospiTradeDto(
    /** 유가증권 단축 종목코드 */
    @get:JsonProperty("MKSC_SHRN_ISCD")
    val mkscShrnIscd: String,

    /** 주식 체결 시간 */
    @get:JsonProperty("STCK_CNTG_HOUR")
    val stckCntgHour: String,

    /** 주식 현재가 */
    @get:JsonProperty("STCK_PRPR")
    val stckPrpr: String,

    /** 전일 대비 부호 */
    @get:JsonProperty("PRDY_VRSS_SIGN")
    val prdyVrssSign: String,

    /** 전일 대비 */
    @get:JsonProperty("PRDY_VRSS")
    val prdyVrss: String,

    /** 전일 대비율 */
    @get:JsonProperty("PRDY_CTRT")
    val prdyCtrt: String,

    /** 가중 평균 주식 가격 */
    @get:JsonProperty("WGHN_AVRG_STCK_PRC")
    val wghnAvrgStckPrc: String,

    /** 주식 시가 */
    @get:JsonProperty("STCK_OPRC")
    val stckOprc: String,

    /** 주식 최고가 */
    @get:JsonProperty("STCK_HGPR")
    val stckHgpr: String,

    /** 주식 최저가 */
    @get:JsonProperty("STCK_LWPR")
    val stckLwpr: String,

    /** 매도호가1 */
    @get:JsonProperty("ASKP1")
    val askp1: String,

    /** 매수호가1 */
    @get:JsonProperty("BIDP1")
    val bidp1: String,

    /** 체결 거래량 */
    @get:JsonProperty("CNTG_VOL")
    val cntgVol: String,

    /** 누적 거래량 */
    @get:JsonProperty("ACML_VOL")
    val acmlVol: String,

    /** 누적 거래 대금 */
    @get:JsonProperty("ACML_TR_PBMN")
    val acmlTrPbmn: String,

    /** 매도 체결 건수 */
    @get:JsonProperty("SELN_CNTG_CSNU")
    val selnCntgCsnu: String,

    /** 매수 체결 건수 */
    @get:JsonProperty("SHNU_CNTG_CSNU")
    val shnuCntgCsnu: String,

    /** 순매수 체결 건수 */
    @get:JsonProperty("NTBY_CNTG_CSNU")
    val ntbyCntgCsnu: String,

    /** 체결강도 */
    @get:JsonProperty("CTTR")
    val cttr: String,

    /** 총 매도 수량 */
    @get:JsonProperty("SELN_CNTG_SMTN")
    val selnCntgSmtn: String,

    /** 총 매수 수량 */
    @get:JsonProperty("SHNU_CNTG_SMTN")
    val shnuCntgSmtn: String,

    /** 체결구분 */
    @get:JsonProperty("CCLD_DVSN")
    val ccldDvsn: String,

    /** 매수비율 */
    @get:JsonProperty("SHNU_RATE")
    val shnuRate: String,

    /** 전일 거래량 대비 등락율 */
    @get:JsonProperty("PRDY_VOL_VRSS_ACML_VOL_RATE")
    val prdyVolVrssAcmlVolRate: String,

    /** 시가 시간 */
    @get:JsonProperty("OPRC_HOUR")
    val oprcHour: String,

    /** 시가대비구분 */
    @get:JsonProperty("OPRC_VRSS_PRPR_SIGN")
    val oprcVrssPrprSign: String,

    /** 시가대비 */
    @get:JsonProperty("OPRC_VRSS_PRPR")
    val oprcVrssPrpr: String,

    /** 최고가 시간 */
    @get:JsonProperty("HGPR_HOUR")
    val hgprHour: String,

    /** 고가대비구분 */
    @get:JsonProperty("HGPR_VRSS_PRPR_SIGN")
    val hgprVrssPrprSign: String,

    /** 고가대비 */
    @get:JsonProperty("HGPR_VRSS_PRPR")
    val hgprVrssPrpr: String,

    /** 최저가 시간 */
    @get:JsonProperty("LWPR_HOUR")
    val lwprHour: String,

    /** 저가대비구분 */
    @get:JsonProperty("LWPR_VRSS_PRPR_SIGN")
    val lwprVrssPrprSign: String,

    /** 저가대비 */
    @get:JsonProperty("LWPR_VRSS_PRPR")
    val lwprVrssPrpr: String,

    /** 영업 일자 */
    @get:JsonProperty("BSOP_DATE")
    val bsopDate: String,

    /** 신 장운영 구분 코드 */
    @get:JsonProperty("NEW_MKOP_CLS_CODE")
    val newMkopClsCode: String,

    /** 거래정지 여부 */
    @get:JsonProperty("TRHT_YN")
    val trhtYn: String,

    /** 매도호가 잔량1 */
    @get:JsonProperty("ASKP_RSQN1")
    val askpRsqn1: String,

    /** 매수호가 잔량1 */
    @get:JsonProperty("BIDP_RSQN1")
    val bidpRsqn1: String,

    /** 총 매도호가 잔량 */
    @get:JsonProperty("TOTAL_ASKP_RSQN")
    val totalAskpRsqn: String,

    /** 총 매수호가 잔량 */
    @get:JsonProperty("TOTAL_BIDP_RSQN")
    val totalBidpRsqn: String,

    /** 거래량 회전율 */
    @get:JsonProperty("VOL_TNRT")
    val volTnrt: String,

    /** 전일 동시간 누적 거래량 */
    @get:JsonProperty("PRDY_SMNS_HOUR_ACML_VOL")
    val prdySmnsHourAcmlVol: String,

    /** 전일 동시간 누적 거래량 비율 */
    @get:JsonProperty("PRDY_SMNS_HOUR_ACML_VOL_RATE")
    val prdySmnsHourAcmlVolRate: String,

    /** 시간 구분 코드 */
    @get:JsonProperty("HOUR_CLS_CODE")
    val hourClsCode: String,

    /** 임의종료구분코드 */
    @get:JsonProperty("MRKT_TRTM_CLS_CODE")
    val mrktTrtmClsCode: String,

    /** 정적VI발동기준가 */
    @get:JsonProperty("VI_STND_PRC")
    val viStndPrc: String
) : StockDataMapper {

    override fun getTicker(): String {
        return mkscShrnIscd
    }

}