package com.ggbadza.stock_collection_service.nasdaq.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * NASDAQ 실시간 호가 정보 DTO
 */
data class NasdaqOrderBookDto(
    /** 실시간종목코드 */
    @get:JsonProperty("RSYM")
    val rsym: String,

    /** 종목코드 */
    @get:JsonProperty("SYMB")
    val symb: String,

    /** 소숫점자리수 */
    @get:JsonProperty("ZDIV")
    val zdiv: String,

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

    /** 매수총잔량 */
    @get:JsonProperty("BVOL")
    val bvol: String,

    /** 매도총잔량 */
    @get:JsonProperty("AVOL")
    val avol: String,

    /** 매수총잔량대비 */
    @get:JsonProperty("BDVL")
    val bdvl: String,

    /** 매도총잔량대비 */
    @get:JsonProperty("ADVL")
    val advl: String,

    /** 매수호가1 */
    @get:JsonProperty("PBID1")
    val pbid1: String,

    /** 매도호가1 */
    @get:JsonProperty("PASK1")
    val pask1: String,

    /** 매수잔량1 */
    @get:JsonProperty("VBID1")
    val vbid1: String,

    /** 매도잔량1 */
    @get:JsonProperty("VASK1")
    val vask1: String,

    /** 매수잔량대비1 */
    @get:JsonProperty("DBID1")
    val dbid1: String,

    /** 매도잔량대비1 */
    @get:JsonProperty("DASK1")
    val dask1: String

)  {
    fun getTicker(): String {
        return symb
    }
}
