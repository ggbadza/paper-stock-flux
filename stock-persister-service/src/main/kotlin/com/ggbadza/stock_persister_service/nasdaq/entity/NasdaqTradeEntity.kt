package com.ggbadza.stock_persister_service.nasdaq.entity

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * NASDAQ 거래 정보 DB 엔티티
 */
@Table("nasdaq_trade")
data class NasdaqTradeEntity(
    val time: LocalDateTime,
    val ticker: String,
    val price: Long,
    val volume: Long,
    val tradeType: String?
)
