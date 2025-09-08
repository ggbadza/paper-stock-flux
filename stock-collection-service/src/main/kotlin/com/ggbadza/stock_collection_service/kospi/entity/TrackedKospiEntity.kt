package com.ggbadza.stock_collection_service.kospi.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table

@Table("tracked_stocks")
data class TrackedKospiEntity (

    @Id
    val ticker: String,

    val name: String,

    var isActive: Boolean = true,

    var modifiedDate: LastModifiedDate
)
