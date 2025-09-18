package com.ggbadza.stock_collection_service.common

import reactor.core.publisher.Mono

interface StockMarketConnector {

    fun connect(): Mono<Void>

}