package com.ggbadza.stock_collection_service.common

import reactor.core.publisher.Flux

interface StockMarketConnector {

    fun connect(): Flux<Void>

}