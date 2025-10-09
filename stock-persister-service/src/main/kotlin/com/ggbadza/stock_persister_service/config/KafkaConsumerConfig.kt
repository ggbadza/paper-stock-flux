package com.ggbadza.stock_persister_service.config

import com.ggbadza.stock_persister_service.nasdaq.dto.NasdaqOrderBookDto
import com.ggbadza.stock_persister_service.nasdaq.dto.NasdaqTradeDto
import com.ggbadza.stock_persister_service.kospi.dto.KospiOrderBookDto
import com.ggbadza.stock_persister_service.kospi.dto.KospiTradeDto
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.util.Collections
import kotlin.jvm.java
import kotlin.to

@Configuration
class KafkaConsumerConfig(
    private val apiProperties: ApiProperties
) {

    private val kospiApiProperties = apiProperties.kospi
    private val nadsaqApiProperties = apiProperties.nasdaq


    @Value("\${spring.kafka.consumer.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.consumer.group-id}")
    private lateinit var groupId: String


    // Kospi 체결가 토픽을 위한 KafkaReceiver 빈
    @Bean
    fun kospiKafkaTradeReceiver(): KafkaReceiver<String, KospiTradeDto> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest", // 처음 실행 시 가장 오래된 메시지부터 소비
            // 전체 패키지를 다 받아와서 지정한 DTO로 변경 처리(실 서비스에서는 스키마 레지스트리 사용)
            JsonDeserializer.TRUSTED_PACKAGES to "*",
            JsonDeserializer.VALUE_DEFAULT_TYPE to KospiTradeDto::class.java,
            JsonDeserializer.USE_TYPE_INFO_HEADERS to false
        )

        // 구독할 토픽 지정
        val receiverOptions = ReceiverOptions.create<String, KospiTradeDto>(props)
            .subscription(Collections.singleton(kospiApiProperties.kafka.tradeTopic))

        return KafkaReceiver.create(receiverOptions)
    }

    // Kospi 호가 토픽을 위한 KafkaReceiver 빈
    @Bean
    fun kospiKafkaOrderBookReceiver(): KafkaReceiver<String, KospiOrderBookDto> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest", // 처음 실행 시 가장 오래된 메시지부터 소비
            JsonDeserializer.TRUSTED_PACKAGES to "*",
            JsonDeserializer.VALUE_DEFAULT_TYPE to KospiOrderBookDto::class.java,
            JsonDeserializer.USE_TYPE_INFO_HEADERS to false
        )

        // 구독할 토픽 지정
        val receiverOptions = ReceiverOptions.create<String, KospiOrderBookDto>(props)
            .subscription(Collections.singleton(kospiApiProperties.kafka.orderBookTopic))

        return KafkaReceiver.create(receiverOptions)
    }


    // Nasdaq 체결가 토픽을 위한 KafkaReceiver 빈
    @Bean
    fun nasdaqKafkaTradeReceiver(): KafkaReceiver<String, NasdaqTradeDto> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest", // 처음 실행 시 가장 오래된 메시지부터 소비
            JsonDeserializer.TRUSTED_PACKAGES to "*",
            JsonDeserializer.VALUE_DEFAULT_TYPE to NasdaqTradeDto::class.java,
            JsonDeserializer.USE_TYPE_INFO_HEADERS to false
        )

        // 구독할 토픽 지정
        val receiverOptions = ReceiverOptions.create<String, NasdaqTradeDto>(props)
            .subscription(Collections.singleton(nadsaqApiProperties.kafka.tradeTopic))

        return KafkaReceiver.create(receiverOptions)
    }

    // Nasdaq 호가 토픽을 위한 KafkaReceiver 빈
    @Bean
    fun nasdaqKafkaOrderBookReceiver(): KafkaReceiver<String, NasdaqOrderBookDto> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest", // 처음 실행 시 가장 오래된 메시지부터 소비
            JsonDeserializer.TRUSTED_PACKAGES to "*",
            JsonDeserializer.VALUE_DEFAULT_TYPE to NasdaqOrderBookDto::class.java,
            JsonDeserializer.USE_TYPE_INFO_HEADERS to false
        )

        // 구독할 토픽 지정
        val receiverOptions = ReceiverOptions.create<String, NasdaqOrderBookDto>(props)
            .subscription(Collections.singleton(nadsaqApiProperties.kafka.orderBookTopic))

        return KafkaReceiver.create(receiverOptions)
    }


}