package com.ggbadza.stock_collection_service.nasdaq.manager

import com.ggbadza.stock_collection_service.config.ApiProperties
import com.ggbadza.stock_collection_service.kospi.dto.ApprovalRequest
import com.ggbadza.stock_collection_service.kospi.dto.ApprovalResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.time.Duration



private val logger = KotlinLogging.logger {}

@Service
class NasdaqApiKeyManager(
    private val apiProperties: ApiProperties,
    private val webClient: WebClient
) {

    private var cachedApiKey: Mono<String> = Mono.empty()

    fun getApprovalKey(): Mono<String> {
        return cachedApiKey
            // 캐시 미존재시에 가져옴
            .switchIfEmpty(fetchAndCacheApiKey())
    }

    private fun fetchAndCacheApiKey(): Mono<String> {
        val request = ApprovalRequest(
            appKey = apiProperties.websocket.kospi.appKey,
            secretKey = apiProperties.websocket.kospi.appSecret
        )

        cachedApiKey = webClient.post()
            .uri(apiProperties.websocket.nasdaq.keyUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ApprovalResponse::class.java)
            .map { it.approvalKey }
            .doOnError { error ->
                when (error) {
                    is WebClientResponseException -> {
                        logger.error(
                            "Nasdaq API 키 요청 실패. Status: {}, Response: {}",
                            error.statusCode,
                            error.responseBodyAsString,
                            error
                        )
                    }
                    else -> {
                        logger.error("Nasdaq API 키 요청 중 알 수 없는 오류 발생", error)
                    }
                }
            }
            .cache(Duration.ofHours(12))

        return cachedApiKey
    }
}