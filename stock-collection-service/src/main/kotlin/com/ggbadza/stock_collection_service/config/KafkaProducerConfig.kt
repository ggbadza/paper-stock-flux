package com.ggbadza.stock_collection_service.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonSerializer

import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

//@Configuration
class KafkaProducerConfig {

//    @Value("\${spring.kafka.producer.bootstrap-servers}")
//    private lateinit var bootstrapServers: String
//
//    @Bean
//    fun kafkaSender(): KafkaSender<String, String>? {
//        val props = mapOf<String, Any>(
//            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
//            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
//            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
//            ProducerConfig.ACKS_CONFIG to "0"
//        )
//
//        val senderOptions = SenderOptions.create<String, String>(props)
//
//        return KafkaSender.create(senderOptions)
//    }

}