package com.ggbadza.stock_persister_service.nasdaq.repository

import com.ggbadza.stock_persister_service.nasdaq.entity.NasdaqTradeEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

/**
 * NASDAQ 거래 정보 Repository
 */
@Repository
interface NasdaqTradeRepository : ReactiveCrudRepository<NasdaqTradeEntity, String>
