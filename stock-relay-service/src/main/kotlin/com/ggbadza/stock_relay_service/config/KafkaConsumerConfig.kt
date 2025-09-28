package com.ggbadza.stock_relay_service.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.util.Collections

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
    fun kospiKafkaTradeReceiver(): KafkaReceiver<String, String> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java, // Producer의 JsonSerializer와 짝을 이룸
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest", // 처음 실행 시 가장 오래된 메시지부터 소비
            JsonDeserializer.TRUSTED_PACKAGES to "*" // 간단하게 모든 패키지를 신뢰하거나, 특정 DTO 패키지 경로를 지정
        )

        // 구독할 토픽 지정
        val receiverOptions = ReceiverOptions.create<String, String>(props)
            .subscription(Collections.singleton(kospiApiProperties.kafka.tradeTopic))

        return KafkaReceiver.create(receiverOptions)
    }

    // Kospi 호가 토픽을 위한 KafkaReceiver 빈
    @Bean
    fun kospiKafkaOrderBookReceiver(): KafkaReceiver<String, String> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java, // Producer의 JsonSerializer와 짝을 이룸
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest", // 처음 실행 시 가장 오래된 메시지부터 소비
            JsonDeserializer.TRUSTED_PACKAGES to "*" // 간단하게 모든 패키지를 신뢰하거나, 특정 DTO 패키지 경로를 지정
        )

        // 구독할 토픽 지정
        val receiverOptions = ReceiverOptions.create<String, String>(props)
            .subscription(Collections.singleton(kospiApiProperties.kafka.orderBookTopic))

        return KafkaReceiver.create(receiverOptions)
    }


    // Nasdaq 체결가 토픽을 위한 KafkaReceiver 빈
    @Bean
    fun nasdaqKafkaTradeReceiver(): KafkaReceiver<String, String> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java, // Producer의 JsonSerializer와 짝을 이룸
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest", // 처음 실행 시 가장 오래된 메시지부터 소비
            JsonDeserializer.TRUSTED_PACKAGES to "*" // 간단하게 모든 패키지를 신뢰하거나, 특정 DTO 패키지 경로를 지정
        )

        // 구독할 토픽 지정
        val receiverOptions = ReceiverOptions.create<String, String>(props)
            .subscription(Collections.singleton(nadsaqApiProperties.kafka.tradeTopic))

        return KafkaReceiver.create(receiverOptions)
    }

    // Nasdaq 호가 토픽을 위한 KafkaReceiver 빈
    @Bean
    fun nasdaqKafkaOrderBookReceiver(): KafkaReceiver<String, String> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java, // Producer의 JsonSerializer와 짝을 이룸
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest", // 처음 실행 시 가장 오래된 메시지부터 소비
            JsonDeserializer.TRUSTED_PACKAGES to "*" // 간단하게 모든 패키지를 신뢰하거나, 특정 DTO 패키지 경로를 지정
        )

        // 구독할 토픽 지정
        val receiverOptions = ReceiverOptions.create<String, String>(props)
            .subscription(Collections.singleton(nadsaqApiProperties.kafka.orderBookTopic))

        return KafkaReceiver.create(receiverOptions)
    }


}