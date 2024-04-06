package com.arsahub.backend.services

import com.arsahub.backend.dtos.response.AchievementResponse
import com.arsahub.backend.dtos.response.WebhookPayload
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Webhook
import com.arsahub.backend.models.WebhookRepository
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.arsahub.backend.utils.SignatureUtil
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.client.RestClient
import java.net.URI
import java.net.http.HttpClient
import java.time.Duration
import java.util.*
import kotlin.time.measureTime

fun printTransactionStatus(name: String) {
    println(
        """
        Transaction status: $name
        Transaction sync (current thread): ${Thread.currentThread().name}
        Isolation: ${TransactionSynchronizationManager.getCurrentTransactionIsolationLevel()}
        Active: ${TransactionSynchronizationManager.isActualTransactionActive()}
        Transaction name: ${TransactionSynchronizationManager.getCurrentTransactionName()}
        """.trimIndent(),
    )
}

@Service
class WebhookSecretProvider(
    private val appRepository: AppRepository,
    private val webhookRepository: WebhookRepository,
) {
    fun getSecret(
        appId: Long,
        webhookUrl: String,
    ): String? {
        val app =
            appRepository.findByIdOrNull(appId)
                ?: return null
        val findByAppAndUrl = webhookRepository.findByAppAndUrl(app, webhookUrl)
        return findByAppAndUrl?.secretKey
    }
}

@Service
class WebhookDeliveryService(
    private val kafkaTemplate: KafkaTemplate<String, WebhookPayload>,
    private val webhookSecretProvider: WebhookSecretProvider,
    private val appRepository: AppRepository,
    private val webhookRepository: WebhookRepository,
) {
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

    // read uncommitted to avoid losing messages
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @KafkaListener(topics = [WEBHOOK_DELIVERIES])
    fun listenTriggerRequests(
        value: WebhookPayload,
        ack: Acknowledgment,
    ) {
        logger.info { "Received WebhookPayload: $value" }

        printTransactionStatus("listenTriggerRequests")
        println("App count: ${appRepository.count()}")

        // TODO: evaluate runBlocking
        runBlocking {
            deliverWebhookEvent(
                URI.create(value.webhookUrl),
                value.appId,
                value,
            ) {
                ack.acknowledge()
            }
        }
    }

    suspend fun deliverWebhookEvent(
        webhookUrl: URI,
        appId: Long,
        payload: WebhookPayload,
        onSuccessfulDelivery: () -> Unit,
    ) {
        logger.debug { "Delivering webhook for app ID $appId: $webhookUrl" }
        val objectMapper = ObjectMapper()
        val stringPayload = objectMapper.writeValueAsString(payload)
        logger.debug { "Payload: $stringPayload" }
        val app =
            appRepository.findByIdOrNull(appId)
//        val webhook = webhookRepository.findByAppAndUrl(app!!, webhookUrl.toString())
        val webhook =
            withContext(Dispatchers.IO) {
                webhookRepository.findByAppAndUrl(app!!, webhookUrl.toString())
            }
        requireNotNull(webhook) { "Webhook not found for app ID $appId: $webhookUrl" }
        val secretKey = webhook.secretKey
        requireNotNull(secretKey) { "Webhook secret key not found for app ID $appId: $webhookUrl" }
        val signature = SignatureUtil.createSignature(secretKey, stringPayload)
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
                                    stringPayload,
                                )
                                .header(
                                    "X-Webhook-Signature",
                                    signature,
                                )
                                .header(
                                    "Content-Type",
                                    "application/json",
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

    fun publishWebhookEvents(
        app: App,
        appWebhooks: List<Webhook>,
        appUser: AppUser,
        actionResult: ActionResult,
    ) {
        logger.debug { "Publishing webhook events for app ${app.title}" }
        if (appWebhooks.isEmpty()) {
            return
        }

        // TODO: more events. e.g. rule activated, etc.
        appWebhooks.forEach { webhook ->
            val payload =
                when (actionResult) {
                    is ActionResult.AchievementUpdate -> {
                        WebhookPayload(
                            id = UUID.randomUUID(),
                            appId = app.id!!,
                            webhookUrl = webhook.url!!,
                            event = "achievement_unlocked",
                            appUserId = appUser.userId!!,
                            payload =
                                mapOf(
                                    "achievement" to AchievementResponse.fromEntity(actionResult.achievement),
                                ),
                        )
                    }

                    is ActionResult.PointsUpdate -> {
                        WebhookPayload(
                            id = UUID.randomUUID(),
                            appId = app.id!!,
                            webhookUrl = webhook.url!!,
                            event = "points_updated",
                            appUserId = appUser.userId!!,
                            payload =
                                mapOf(
                                    "points" to actionResult.newPoints,
                                    "pointsChange" to actionResult.pointsAdded,
                                ),
                        )
                    }

                    else -> {
                        logger.warn { "Unsupported action result: $actionResult" }
                        return
                    }
                }

            kafkaTemplate.send("webhookDeliveries", payload)
            logger.debug { "Sent webhook payload to Kafka: $payload" }
        }
    }
}
