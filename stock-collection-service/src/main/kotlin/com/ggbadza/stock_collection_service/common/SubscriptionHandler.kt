package com.ggbadza.stock_collection_service.common

/**
 * 실시간 데이터 구독 요청 및 처리에 대한 규격
 */
interface SubscriptionHandler<T : StockDataMapper> {
    fun getTrId(): String
    fun createRequest(approvalKey: String, ticker: String): String
    fun processData(payload: String, count: Int): List<T>
}