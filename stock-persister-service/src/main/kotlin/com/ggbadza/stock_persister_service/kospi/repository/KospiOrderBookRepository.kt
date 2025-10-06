package com.ggbadza.stock_persister_service.kospi.repository

import com.ggbadza.stock_persister_service.kospi.entity.KospiOrderBookEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * KOSPI 호가 정보 Repository
 */
@Repository
interface KospiOrderBookRepository : ReactiveCrudRepository<KospiOrderBookEntity, String>
