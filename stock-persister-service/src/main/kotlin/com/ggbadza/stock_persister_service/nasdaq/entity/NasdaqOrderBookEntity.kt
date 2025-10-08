package com.ggbadza.stock_persister_service.nasdaq.entity

import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * NASDAQ 호가 정보 DB 엔티티
 */
@Table("nasdaq_order_book")
data class NasdaqOrderBookEntity(
    val time: LocalDateTime,
    val ticker: String,
    val bidVolume1 : Long,
    val bidPrice1  : BigDecimal,
    val askVolume1 : Long,
    val askPrice1  : BigDecimal
)
