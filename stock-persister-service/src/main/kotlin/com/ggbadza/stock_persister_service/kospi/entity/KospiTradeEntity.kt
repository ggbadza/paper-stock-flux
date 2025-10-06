package com.ggbadza.stock_persister_service.kospi.entity

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * KOSPI 거래 정보 DB 엔티티
 */
@Table("kospi_trade")
data class KospiTradeEntity(
    val time: LocalDateTime,
    val ticker: String,
    val price: Long,
    val volume: Long,
    val tradeType: String?
)
