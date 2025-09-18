package com.ggbadza.stock_collection_service.runner

import com.ggbadza.stock_collection_service.common.StockMarketConnector
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

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

        connectors.forEach { connector ->
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                logger.error(e) { "Thread sleep interrupted" }
            }
            val connectorName = connector.javaClass.simpleName
            logger.info { "[$connectorName] 데이터 수집을 시작합니다." }

            // 각 Connector의 connect 메서드를 호출하여 데이터 수집 스트림을 활성화합니다.
            connector.connect()
                .subscribe(
                    null, // onNext: Flux<Void>이므로 별도 처리 없음
                    { error -> logger.error(error) { "[$connectorName] 데이터 수집 중 심각한 오류가 발생했습니다." } }
                )
        }
    }
}
