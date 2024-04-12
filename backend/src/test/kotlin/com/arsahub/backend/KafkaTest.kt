package com.arsahub.backend

import com.arsahub.backend.controllers.forceNewTransaction
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.User
import com.arsahub.backend.models.Webhook
import com.arsahub.backend.models.WebhookRepository
import com.arsahub.backend.services.WebhookDeliveryService
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.arsahub.backend.utils.SignatureUtil
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.serpro69.kfaker.faker
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.util.*

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@EmbeddedKafka(
    topics = [WebhookDeliveryService.Topics.WEBHOOK_DELIVERIES],
    partitions = 1,
)
@ActiveProfiles("dev", "test")
@AutoConfigureWireMock(port = 0)
@Transactional
class KafkaTest {
    @Autowired
    private lateinit var webhookRepository: WebhookRepository

    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @Autowired
    private lateinit var webhookDeliveryService: WebhookDeliveryService

    val faker = faker { }

    @Test
    fun `WebhookDeliveryService produces, consumes and delivers webhook events`() {
        val app =
            App(
                title = faker.name.name(),
                apiKey = faker.random.nextUUID(),
                owner =
                    User(
                        externalUserId = faker.random.nextUUID(),
                        googleUserId = faker.random.nextUUID(),
                        email = faker.internet.email(),
                        name = faker.name.name(),
                    ),
            ).apply {
                id = 1
            }
        val appUser =
            AppUser(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                displayName = "User1",
                app = app,
                points = 1000,
            )

        // Arrange webhook
        fun stubWebhook(path: String): UUID {
            val stubUUID = UUID.randomUUID()
            WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo(path)).willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Well received"),
                ).withId(stubUUID),
            )
            return stubUUID
        }

        val stubUUID1 = stubWebhook("/webhook1")
        val stubUUID2 = stubWebhook("/webhook2")
        val stubUUID3 = stubWebhook("/webhook3")

        fun createWebhook(
            id: Long,
            path: String,
            secret: String,
        ): Webhook {
            val webhook =
                Webhook(
                    app = app,
                    url = "http://localhost:${wireMockServer.port()}$path",
                    secretKey = secret,
                ).apply {
                    this.id = id
                }

            webhookRepository.save(webhook)

            return webhook
        }

        val webhook1 =
            createWebhook(1, "/webhook1", "secret1")
        val webhook2 =
            createWebhook(2, "/webhook2", "secret2")
        val webhook3 =
            createWebhook(3, "/webhook3", "secret3")

        forceNewTransaction()

        // Act
        webhookDeliveryService.publishWebhookEvents(
            app,
            listOf(
                webhook1,
                webhook2,
                webhook3,
            ),
            appUser,
            ActionResult.PointsUpdate(50, 70, 20),
        )

        // Assert webhook
        // TODO: find a way to wait for the webhook to be called from the consumer
        runBlocking { delay(3000) }

        fun verifyWebhook(
            webhook: Webhook,
            points: Int,
            pointsChange: Int,
            stubUUID: UUID,
        ) {
            wireMockServer.verify(
                WireMock.postRequestedFor(WireMock.urlEqualTo(URI(webhook.url!!).path))
                    .withRequestBody(
                        WireMock.equalToJson(
                            // id is an escaped wiremock placeholder
                            """
                            {
                                "id": "${"\${"}json-unit.any-string${"}"}",
                                "appId": ${app.id},
                                "webhookUrl": "${webhook.url}",
                                "event": "points_updated",
                                "appUserId": "${appUser.userId}",
                                "payload": {
                                    "points": $points,
                                    "pointsChange": $pointsChange
                                }
                            }
                            """.trimIndent(),
                        ),
                    ),
            )

            val allServeEvents = WireMock.getAllServeEvents(ServeEventQuery.forStubMapping(stubUUID))
            assertEquals(1, allServeEvents.size)
            val request = allServeEvents.first().request
            val signatureHeader = request.getHeader("X-Webhook-Signature")
            val actualPayload = request.bodyAsString
            val signature = SignatureUtil.createSignature(webhook.secretKey!!, actualPayload)
            assertEquals(signature, signatureHeader)
        }

        verifyWebhook(webhook1, 70, 20, stubUUID1)
        verifyWebhook(webhook2, 70, 20, stubUUID2)
        verifyWebhook(webhook3, 70, 20, stubUUID3)
    }
}
