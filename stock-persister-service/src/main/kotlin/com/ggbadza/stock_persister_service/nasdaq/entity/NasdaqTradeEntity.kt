package com.ggbadza.stock_persister_service.nasdaq.entity

import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * NASDAQ 거래 정보 DB 엔티티
 */
@Table("nasdaq_trade")
data class NasdaqTradeEntity(
    val time: LocalDateTime,
    val ticker: String,
    val price: BigDecimal,
    val volume: Long,
    val tradeType: String?
)
