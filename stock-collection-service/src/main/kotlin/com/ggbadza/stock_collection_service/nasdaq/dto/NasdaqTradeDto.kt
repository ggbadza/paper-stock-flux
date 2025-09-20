package com.ggbadza.stock_collection_service.nasdaq.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.ggbadza.stock_collection_service.common.StockDataMapper

/**
 * NASDAQ 실시간 체결가 정보 DTO
 */
data class NasdaqTradeDto(
    /** 실시간종목코드 */
    @get:JsonProperty("RSYM")
    val rsym: String,

    /** 종목코드 */
    @get:JsonProperty("SYMB")
    val symb: String,

    /** 수수점자리수 */
    @get:JsonProperty("ZDIV")
    val zdiv: String,

    /** 현지영업일자 */
    @get:JsonProperty("TYMD")
    val tymd: String,

    /** 현지일자 */
    @get:JsonProperty("XYMD")
    val xymd: String,

    /** 현지시간 */
    @get:JsonProperty("XHMS")
    val xhms: String,

    /** 한국일자 */
    @get:JsonProperty("KYMD")
    val kymd: String,

    /** 한국시간 */
    @get:JsonProperty("KHMS")
    val khms: String,

    /** 시가 */
    @get:JsonProperty("OPEN")
    val open: String,

    /** 고가 */
    @get:JsonProperty("HIGH")
    val high: String,

    /** 저가 */
    @get:JsonProperty("LOW")
    val low: String,

    /** 현재가 */
    @get:JsonProperty("LAST")
    val last: String,

    /** 대비구분 */
    @get:JsonProperty("SIGN")
    val sign: String,

    /** 전일대비 */
    @get:JsonProperty("DIFF")
    val diff: String,

    /** 등락율 */
    @get:JsonProperty("RATE")
    val rate: String,

    /** 매수호가 */
    @get:JsonProperty("PBID")
    val pbid: String,

    /** 매도호가 */
    @get:JsonProperty("PASK")
    val pask: String,

    /** 매수잔량 */
    @get:JsonProperty("VBID")
    val vbid: String,

    /** 매도잔량 */
    @get:JsonProperty("VASK")
    val vask: String,

    /** 체결량 */
    @get:JsonProperty("EVOL")
    val evol: String,

    /** 거래량 */
    @get:JsonProperty("TVOL")
    val tvol: String,

    /** 거래대금 */
    @get:JsonProperty("TAMT")
    val tamt: String,

    /** 매도체결량 */
    @get:JsonProperty("BIVL")
    val bivl: String,

    /** 매수체결량 */
    @get:JsonProperty("ASVL")
    val asvl: String,

    /** 체결강도 */
    @get:JsonProperty("STRN")
    val strn: String,

    /** 시장구분 (1:장중, 2:장전, 3:장후) */
    @get:JsonProperty("MTYP")
    val mtyp: String

) : StockDataMapper {

    override fun getTicker(): String {
        return symb
    }

}