package com.arsahub.backend.services

import com.arsahub.backend.dtos.response.WebhookPayload
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.net.URI
import java.net.http.HttpClient
import java.time.Duration
import kotlin.time.measureTime

@Service
class WebhookDeliveryService {
    private val logger = KotlinLogging.logger {}

    private val webhookTimeout = Duration.ofSeconds(AppService.WEBHOOK_TIMEOUT_SECONDS)

    private val restClient =
        RestClient
            .builder()
            .requestFactory(
                JdkClientHttpRequestFactory(
                    HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1) // TODO: evaluate HTTP/2 (this is hotfix for EOFException)
                        .build(),
                ).apply {

                    setReadTimeout(webhookTimeout)
                },
            )
            .build()

    companion object Topics {
        const val WEBHOOK_DELIVERIES = "webhookDeliveries"
    }

    @Bean
    fun webhookDeliveriesTopic() = NewTopic(WEBHOOK_DELIVERIES, 1, 1)

    @KafkaListener(topics = [WEBHOOK_DELIVERIES])
    fun listenTriggerRequests(
        value: WebhookPayload,
        ack: Acknowledgment,
    ) {
        logger.info { "Received WebhookPayload: $value" }

        // TODO: evaluate runBlocking
        runBlocking {
            publishWebhookEvent(
                URI.create(value.webhookUrl),
                value.appId,
                value,
            ) {
                ack.acknowledge()
            }
        }
    }

    private suspend fun publishWebhookEvent(
        webhookUrl: URI,
        appId: Long,
        payload: WebhookPayload,
        onSuccessfulDelivery: () -> Unit,
    ) {
        logger.debug { "Delivering webhook for app ID $appId: $webhookUrl" }
        // TODO: retry?
        val duration =
            measureTime {
                try {
                    val response =
                        // the underlying rest client is blocking, so we need to switch to IO dispatcher
                        withContext(Dispatchers.IO) {
                            restClient.post()
                                .uri(webhookUrl)
                                .body(
                                    payload,
                                )
                                .retrieve()
                                .toBodilessEntity()
                        }

                    if (response.statusCode.isError) {
                        logger.error { "Webhook $webhookUrl failed for app ID $appId: ${response.statusCode}" }
                        // TODO: handle webhook failure
                    } else {
                        logger.debug { "Webhook $webhookUrl succeeded for app ID $appId: ${response.statusCode}" }
                        onSuccessfulDelivery()
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Webhook $webhookUrl failed for app ID $appId" }
                    // TODO: handle webhook failure
                }
            }
        logger.debug { "Webhook $webhookUrl took $duration for app ID $appId " }
    }
}
