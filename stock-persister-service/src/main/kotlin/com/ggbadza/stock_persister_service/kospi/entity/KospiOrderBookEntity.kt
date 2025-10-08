package com.ggbadza.stock_persister_service.kospi.entity

import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * KOSPI 호가 정보 DB 엔티티
 */
@Table("kospi_order_book")
data class KospiOrderBookEntity(
    val time: LocalDateTime,
    val ticker: String,
    // --- 매수 호가 (Bids) 1 ~ 10 ---
    val bidPrice1: Long,
    val bidVolume1: Long,
    val bidPrice2: Long,
    val bidVolume2: Long,
    val bidPrice3: Long,
    val bidVolume3: Long,
    val bidPrice4: Long,
    val bidVolume4: Long,
    val bidPrice5: Long,
    val bidVolume5: Long,
    val bidPrice6: Long,
    val bidVolume6: Long,
    val bidPrice7: Long,
    val bidVolume7: Long,
    val bidPrice8: Long,
    val bidVolume8: Long,
    val bidPrice9: Long,
    val bidVolume9: Long,
    val bidPrice10: Long,
    val bidVolume10: Long,

    // --- 매도 호가 (Asks) 1 ~ 10 ---
    val askPrice1: Long,
    val askVolume1: Long,
    val askPrice2: Long,
    val askVolume2: Long,
    val askPrice3: Long,
    val askVolume3: Long,
    val askPrice4: Long,
    val askVolume4: Long,
    val askPrice5: Long,
    val askVolume5: Long,
    val askPrice6: Long,
    val askVolume6: Long,
    val askPrice7: Long,
    val askVolume7: Long,
    val askPrice8: Long,
    val askVolume8: Long,
    val askPrice9: Long,
    val askVolume9: Long,
    val askPrice10: Long,
    val askVolume10: Long
)
