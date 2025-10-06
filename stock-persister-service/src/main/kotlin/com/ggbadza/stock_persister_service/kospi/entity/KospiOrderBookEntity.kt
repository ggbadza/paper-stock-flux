package com.ggbadza.stock_persister_service.kospi.entity

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * KOSPI 호가 정보 DB 엔티티
 */
@Table("kospi_order_book")
data class KospiOrderBookEntity(
    val time: LocalDateTime,
    val ticker: String,
    val price: Long,
    val volume: Long,
    val orderType: String?
)
