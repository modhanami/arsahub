package com.arsahub.backend

import com.arsahub.backend.dtos.response.WebhookPayload
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableJpaAuditing
class BackendApplication {
    private val logger = KotlinLogging.logger {}

    @Bean
    fun kafkaListenerContainerFactory() =
        ConcurrentKafkaListenerContainerFactory<String, Any>().apply {
            consumerFactory =
                DefaultKafkaConsumerFactory(
                    consumerConfigs(),
                    StringDeserializer(),
                    JsonDeserializer<Any?>().apply {
                        addTrustedPackages("*")
                    },
                )
            containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        }

    private fun consumerConfigs() =
        mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "main",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
        )

    private fun producerConfigs() =
        mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
        )

    @Bean
    fun kafkaProducerFactoryWebhookPayload() = DefaultKafkaProducerFactory<String, WebhookPayload>(producerConfigs())

    @Bean
    fun kafkaTemplateWebhookPayload(producerFactory: ProducerFactory<String, WebhookPayload>) = KafkaTemplate(producerFactory)

    @Value("\${socketio.server.host}")
    val host: String? = null

    @Value("\${socketio.server.port}")
    private var port: Int = 0

    @Bean
    fun socketIOServer(): SocketIOServer {
        val config = Configuration()
        config.hostname = host
        config.port = port
        config.socketConfig.isReuseAddress = true
        return SocketIOServer(config)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun startup() {
        val socketIOServer = socketIOServer()
        logger.info { "Starting Socket.IO server on $host:$port" }
        socketIOServer.addEventListener("connection_error", String::class.java) { client, data, ackRequest ->
            logger.info { "Connection error: $data" }
        }

        socketIOServer.start()
    }

    @EventListener(ContextClosedEvent::class)
    fun shutdown() {
        socketIOServer().stop()
    }
}

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
