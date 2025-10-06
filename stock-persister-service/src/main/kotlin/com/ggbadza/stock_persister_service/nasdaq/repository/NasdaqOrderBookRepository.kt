package com.ggbadza.stock_persister_service.nasdaq.repository

import com.ggbadza.stock_persister_service.nasdaq.entity.NasdaqOrderBookEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

/**
 * NASDAQ 호가 정보 Repository
 */
@Repository
interface NasdaqOrderBookRepository : ReactiveCrudRepository<NasdaqOrderBookEntity, String>
