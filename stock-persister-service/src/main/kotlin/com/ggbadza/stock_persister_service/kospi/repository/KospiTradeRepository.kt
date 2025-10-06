package com.ggbadza.stock_persister_service.kospi.repository

import com.ggbadza.stock_persister_service.kospi.entity.KospiTradeEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

/**
 * KOSPI 거래 정보 Repository
 */
@Repository
interface KospiTradeRepository : ReactiveCrudRepository<KospiTradeEntity, String>
