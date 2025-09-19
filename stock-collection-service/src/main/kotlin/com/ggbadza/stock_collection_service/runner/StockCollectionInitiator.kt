package com.ggbadza.stock_collection_service.runner

import com.ggbadza.stock_collection_service.common.StockMarketConnector
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

private val logger = KotlinLogging.logger {}

/**
 * 애플리케이션 시작 시, StockMarketConnector 인터페이스를 구현하는 모든 Bean을 찾아
 * 데이터 수집 프로세스를 시작시키는 범용 Runner.
 */
@Component
class StockCollectionInitiator(
    // StockMarketConnector을 구현하는 모든 @Component Bean을 찾아 리스트로 주입
    private val connectors: List<StockMarketConnector>
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (connectors.isEmpty()) {
            logger.warn { "실행할 StockMarketConnector가 없습니다. 데이터 수집을 시작하지 않습니다." }
            return
        }

        logger.info { "총 ${connectors.size}개의 주식 시장에 대한 연결을 시작합니다." }

        Flux.fromIterable(connectors)
            // 순차적 실행
            .concatMap { connector ->
                val connectorName = connector.javaClass.simpleName
                logger.info { "[$connectorName] 데이터 수집을 시작합니다." }
                connector.connect()
                    .doOnError { error -> logger.error(error) { "[$connectorName] 데이터 수집 중 심각한 오류가 발생했습니다." } }
                    .onErrorComplete() // 오류가 발생해도 다음 커넥터 실행
                    .then(Mono.delay(Duration.ofSeconds(1))) // 작업이 끝나고 1초 대기
            }
            .doOnComplete {
                logger.info { "모든 주식 시장에 대한 연결 시도가 완료되었습니다." }
            }
            .subscribe()
    }
}
