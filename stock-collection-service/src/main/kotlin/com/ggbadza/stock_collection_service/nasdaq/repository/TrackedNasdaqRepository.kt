package com.ggbadza.stock_collection_service.nasdaq.repository

import com.ggbadza.stock_collection_service.nasdaq.entity.TrackedNasdaqEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TrackedNasdaqRepository : R2dbcRepository<TrackedNasdaqEntity, String>  {

    fun findTrackedKospiEntityByTicker(stockCode: String): Mono<TrackedNasdaqEntity?>

    fun findAllByIsActiveIsTrue(): Flux<TrackedNasdaqEntity>
}