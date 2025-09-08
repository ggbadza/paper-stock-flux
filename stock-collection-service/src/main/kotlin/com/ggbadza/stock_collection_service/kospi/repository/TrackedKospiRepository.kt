package com.ggbadza.stock_collection_service.kospi.repository

import com.ggbadza.stock_collection_service.kospi.entity.TrackedKospiEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TrackedKospiRepository : R2dbcRepository<TrackedKospiEntity, String>  {

    fun findTrackedKospiEntityByStockCode(stockCode: String): Mono<TrackedKospiEntity?>

    fun findAllByActiveIsTrue(): Flux<TrackedKospiEntity>
}