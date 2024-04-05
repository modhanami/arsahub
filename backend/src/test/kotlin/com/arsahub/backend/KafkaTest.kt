package com.arsahub.backend

import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.User
import com.arsahub.backend.services.WebhookDeliveryService
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.serpro69.kfaker.faker
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
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
class KafkaTest {
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
        fun stubWebhook(path: String) {
            WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo(path)).willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Well received"),
                ),
            )
        }

        stubWebhook("/webhook1")
        stubWebhook("/webhook2")
        stubWebhook("/webhook3")

        // Act
        val webhook1 = URI("http://localhost:${wireMockServer.port()}/webhook1")
        val webhook2 = URI("http://localhost:${wireMockServer.port()}/webhook2")
        val webhook3 = URI("http://localhost:${wireMockServer.port()}/webhook3")
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
        Thread.sleep(1000)

        fun verifyWebhook(
            webhook: URI,
            points: Int,
            pointsChange: Int,
        ) {
            wireMockServer.verify(
                WireMock.postRequestedFor(WireMock.urlEqualTo(webhook.path))
                    .withRequestBody(
                        WireMock.equalToJson(
                            // id is an escaped wiremock placeholder
                            """
                            {
                                "id": "${"\${"}json-unit.any-string${"}"}",
                                "appId": ${app.id},
                                "webhookUrl": "$webhook",
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
        }

        verifyWebhook(webhook1, 70, 20)
        verifyWebhook(webhook2, 70, 20)
        verifyWebhook(webhook3, 70, 20)
    }
}
