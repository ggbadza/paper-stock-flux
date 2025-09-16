package com.ggbadza.stock_collection_service.kospi.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tracked_stocks")
data class TrackedKospiEntity (

    @Id
    val ticker: String,

    val stockName: String,

    var isActive: Boolean = true,

    @CreatedDate
    val createdDate: LocalDateTime? = null,

    @LastModifiedDate
    var modifiedDate: LocalDateTime? = null
)
