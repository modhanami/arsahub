package com.arsahub.backend.integration

import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.dtos.request.Action
import com.arsahub.backend.dtos.request.FieldDefinition
import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.models.*
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.TriggerService
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import java.util.*

class RuleEngineIntegrationTest() : BaseIntegrationTest() {
    @Autowired
    private lateinit var webhookRepository: WebhookRepository

    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @Autowired
    private lateinit var appUserPointsHistoryRepository: AppUserPointsHistoryRepository

    @Autowired
    private lateinit var triggerRepository: TriggerRepository

    @Autowired
    private lateinit var appInvitationStatusRepository: AppInvitationStatusRepository

    @Autowired
    private lateinit var appInvitationRepository: AppInvitationRepository

    @Autowired
    private lateinit var appRepository: AppRepository

    @Autowired
    private lateinit var triggerService: TriggerService

    @Autowired
    private lateinit var ruleRepository: RuleRepository

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var webhookRequestRepository: WebhookRequestRepository

    @Suppress("unused")
    fun TrigggerTestModel.toNode(): ObjectNode {
        return mapper.valueToTree(this)
    }

    @Test
    fun `triggers matching rules - one trigger with custom field (workshopId) and unlimited repeatability`() {
        // Arrange
        val trigger =
            triggerService.createTrigger(
                authSetup.app,
                TriggerCreateRequest(
                    title = "Workshop Completed",
                    fields =
                        listOf(
                            FieldDefinition(
                                key = "workshopId",
                                type = "integer",
                                label = "Workshop ID",
                            ),
                            FieldDefinition(
                                key = "source",
                                type = "text",
                            ),
                        ),
                ),
            )

        val rule =
            ruleRepository.save(
                Rule(
                    title = "When workshop completed, add 100 points",
                    trigger = trigger,
                    action = Action.ADD_POINTS,
                    actionPoints = 100,
                    app = authSetup.app,
                    conditionExpression = "workshopId == 1 && source == 'trust me'",
                    repeatability = RuleRepeatability.UNLIMITED,
                ),
            )

        val appUser =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 1000,
                ),
            )

        // Act & Assert
        repeat(2) { // 2 times matching trigger
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "key": "workshop_completed",
                            "params": {
                                "workshopId": 1,
                                "source": "trust me"
                            },
                            "userId": "${appUser.userId}"
                        }
                        """.trimIndent(),
                    ),
            )
                .andExpect(status().isOk)
        }

        // 2 times not matching trigger
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "params": {
                            "workshopId": 2,
                            "source": "trust me"
                        },
                        "userId": "${appUser.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "params": {
                            "workshopId": 1,
                            "source": "dont trust me"
                        },
                        "userId": "${appUser.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert DB
        val appUserAfter = appUserRepository.findById(appUser.id!!).get()
        assertEquals(1200, appUserAfter.points)
    }

    @Test
    fun `triggers matching rules - one trigger with custom field (workshopId) and once per user repeatability`() {
        // Arrange
        val trigger =
            triggerService.createTrigger(
                authSetup.app,
                TriggerCreateRequest(
                    title = "Workshop Completed",
                    fields =
                        listOf(
                            FieldDefinition(
                                key = "workshopId",
                                type = "integer",
                                label = "Workshop ID",
                            ),
                            FieldDefinition(
                                key = "source",
                                type = "text",
                            ),
                        ),
                ),
            )

        val rule =
            ruleRepository.save(
                Rule(
                    title = "When workshop completed, add 100 points",
                    trigger = trigger,
                    action = Action.ADD_POINTS,
                    actionPoints = 100,
                    app = authSetup.app,
                    conditionExpression = "workshopId == 1 && source == 'trust me'",
                    repeatability = RuleRepeatability.ONCE_PER_USER,
                ),
            )

        val appUser =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 1000,
                ),
            )

        // Act & Assert
        repeat(2) { // 2 times matching trigger
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "key": "workshop_completed",
                            "params": {
                                "workshopId": 1,
                                "source": "trust me"
                            },
                            "userId": "${appUser.userId}"
                        }
                        """.trimIndent(),
                    ),
            )
                .andExpect(status().isOk)
        }
        // 2 times not matching trigger
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "params": {
                            "workshopId": 2,
                            "source": "trust me"
                        },
                        "userId": "${appUser.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "params": {
                            "workshopId": 1,
                            "source": "dont trust me"
                        },
                        "userId": "${appUser.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert DB
        val appUserAfter = appUserRepository.findById(appUser.id!!).get()
        assertEquals(1100, appUserAfter.points)
    }

    /*
     Testing Rule Engine
     - Relevant pieces: trigger, rule, action, repeatable, subject (user), conditions, app (tenant)
     - Possible actions are: add_points (specify points), unlock_achievement (specify achievement ID)
     - It must fire ALL matching rules for a trigger when that rule's trigger conditions are met,
     for subject (user) in that given trigger, for that app (tenant)
       - It must not fire matching rules for other subjects (users) or other apps (tenants)
       - It must not fire rules that have already been fired for a trigger if the rule is not repeatable
       (currently named "once per user")
       - It must fire rules that have already been fired for a trigger if the rule is repeatable
       - It must not fire rules that do not match the trigger conditions

    Rule Engine responsibilities
    -  Accepts trigger, from an app, containing trigger parameters (conditions), and subject (user)
    -  Finds all rules that match the trigger, for the subject (user), for the app (tenant)
    -  Fires all matching rules
    -  Updates rule progress for the subject (user)
    -  Updates subject (user) points, achievements, etc.
    - Handle forward chaining, meaning that if a rule fires and updates the subject (user) points,
        and that triggers another rule, then that rule should be fired as well (and so on)
    - Commits all changes to the database and marks the trigger as fired

     */

    // Matching rules
    // TODO: split into multiple tests
    @Test
    fun `fires matching rules - for the given user ID in the app`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100).rule

        // Arrange webhook
        stubFor(
            WireMock.post(urlEqualTo("/webhook")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Well received"),
            ),
        )
        val webhookPath = "/webhook"
        val webhookUrl = "http://localhost:${wireMockServer.port()}$webhookPath"
        val result =
            mockMvc.performWithAppAuth(
                post("/api/apps/webhooks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "url": "$webhookUrl"
                        }
                        """.trimIndent(),
                    ),
            )
                .andExpect(status().isCreated)
                .andReturn()

        val webhookId = JsonPath.parse(result.response.contentAsString).read("$.id", Long::class.java)

        forceNewTransaction()

        // Assert DB
        val createdWebhook = webhookRepository.findAll()
        assertEquals(1, createdWebhook.size)
        assertEquals(webhookUrl, createdWebhook[0].url)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMatchingSendTriggerPayloadForWorkshopCompletedRule(rule, user, 1, "trust me")),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)

        // Assert points history
        val pointsHistories = appUserPointsHistoryRepository.findAllByAppAndAppUser(authSetup.app, userAfter)
        assertEquals(1, pointsHistories.size)
        val pointsHistory = pointsHistories[0]
        assertEquals(100, pointsHistory.points)
        assertEquals(100, pointsHistory.pointsChange)
        assertEquals(user.userId, pointsHistory.appUser!!.userId)
        assertEquals(authSetup.app.id, pointsHistory.app!!.id)
        assertEquals(rule.id, pointsHistory.fromRule!!.id)

        // Assert webhook
        // TODO: find a way to wait for the webhook to be called
        runBlocking { delay(3000) }

        wireMockServer.verify(
            postRequestedFor(urlEqualTo(webhookPath))
                .withRequestBody(
                    equalToJson(
                        // id is an escaped wiremock placeholder
                        """
                        {
                            "id": "${"\${"}json-unit.any-string${"}"}",
                            "appId": ${authSetup.app.id},
                            "webhookUrl": "$webhookUrl",
                            "event": "points_updated",
                            "appUserId": "${userAfter.userId}",
                            "payload": {
                                "points": 100,
                                "pointsChange": 100
                            }
                        }
                        """.trimIndent(),
                    ),
                ),
        )

        // Assert webhook requests saved in DB and the endpoint for fetching returns in descending order
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMatchingSendTriggerPayloadForWorkshopCompletedRule(rule, user, 1, "trust me")),
        )
            .andExpect(status().isOk)

        forceNewTransaction()

        // Assert webhook
        // TODO: find a way to wait for the webhook to be called
        runBlocking { delay(3000) }

        val webhookRequestsDescendingByCreatedAt =
            webhookRequestRepository.findAll()
                .sortedByDescending { it.createdAt }
        assertEquals(2, webhookRequestsDescendingByCreatedAt.size)

        mockMvc.performWithAppAuth(
            get("/api/apps/webhooks/$webhookId/requests"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(webhookRequestsDescendingByCreatedAt[0].id))
            .andExpect(jsonPath("$[1].id").value(webhookRequestsDescendingByCreatedAt[1].id))
    }

    private fun getMatchingSendTriggerPayloadForWorkshopCompletedRule(
        rule: Rule,
        user: AppUser,
        workshopId: Int,
        source: String,
    ): String {
        return """
            {
                "key": "${rule.trigger!!.key}",
                "params": {
                    "workshopId": $workshopId,
                    "source": "$source"
                },
                "userId": "${user.userId}"
            }
            """.trimIndent()
    }

    @Test
    fun `fires matching rules - integer in operator`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val trigger =
            createTrigger(authSetup.app) {
                key = "workshop_completed"
                title = "Workshop Completed"
                description = "When a workshop is completed"
                fields {
                    integer("workshopId", "Workshop ID")
                    text("source")
                }
            }
        val rule =
            createRule(authSetup.app) {
                this.trigger = trigger
                title = "When workshop 1, 2, 3 completed, add 100 points"
                action {
                    addPoints(100)
                }
                conditionExpression = "workshopId in [1, 2, 3] && source == 'trust me'"
                repeatability = UnlimitedRuleRepeatability
            }.let(::WorkshopCompletedRule)

        // Act & Assert
        // first trigger with workshopId 2
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": 2,
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert points
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)

        // second trigger with workshopId 3
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": 3,
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert points
        val userAfterSecond = appUserRepository.findById(user.id!!).get()
        assertEquals(200, userAfterSecond.points)

        // third trigger with workshopId 7 should not fire the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": 7,
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert points
        val userAfterThird = appUserRepository.findById(user.id!!).get()
        assertEquals(200, userAfterThird.points)
    }

    @Test
    fun `fires matching rules - text in operator`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val trigger =
            createTrigger(authSetup.app) {
                key = "workshop_completed"
                title = "Workshop Completed"
                description = "When a workshop is completed"
                fields {
                    text("workshopId", "Workshop ID")
                    text("source")
                }
            }
        val rule =
            createRule(authSetup.app) {
                this.trigger = trigger
                title = "When workshop 1, 2, 3 completed, add 100 points"
                action {
                    addPoints(100)
                }
                conditionExpression = "workshopId in ['1', '2', '3'] && source == 'trust me'"
                repeatability = UnlimitedRuleRepeatability
            }.let(::WorkshopCompletedRule)

        // Act & Assert
        // first trigger with workshopId 2
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": "2",
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert points
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)

        // second trigger with workshopId 3
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": "3",
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert points
        val userAfterSecond = appUserRepository.findById(user.id!!).get()
        assertEquals(200, userAfterSecond.points)

        // third trigger with workshopId 7 should not fire the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": "7",
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert points
        val userAfterThird = appUserRepository.findById(user.id!!).get()
        assertEquals(200, userAfterThird.points)
    }

    @Test
    fun `does not fire matching rules when dry triggering - for the given user ID in the app`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100).rule

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger/dry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMatchingSendTriggerPayloadForWorkshopCompletedRule(rule, user, 1, "trust me")),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(0, userAfter.points)
    }

    @Test
    fun `fires matching rules - empty rule conditions and empty trigger params`() {
        // Arrange
        val workShopCompletedTrigger = createWorkshopCompletedTrigger(authSetup.app)

        val rule =
            createRule(authSetup.app) {
                trigger = workShopCompletedTrigger
                title = "When empty trigger, add 100 points"
                action {
                    addPoints(100)
                }
                this.repeatability = UnlimitedRuleRepeatability
            }

        val user = createAppUser(authSetup.app)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${workShopCompletedTrigger.key}",
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!)
        assertNotNull(userAfter)
        assertEquals(100, userAfter.get().points)
    }

    @Test
    fun `fires matching rules - empty rule conditions but non-empty trigger params`() {
        // Arrange
        val workShopCompletedTrigger = createWorkshopCompletedTrigger(authSetup.app)

        val rule =
            createRule(authSetup.app) {
                trigger = workShopCompletedTrigger
                title = "When empty trigger, add 100 points"
                action {
                    addPoints(100)
                }
                this.repeatability = UnlimitedRuleRepeatability
            }

        val user = createAppUser(authSetup.app)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${workShopCompletedTrigger.key}",
                        "params": {
                            "workshopId": 1,
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!)
        assertNotNull(userAfter)
        assertEquals(100, userAfter.get().points)
    }

    @Test
    fun `does not fire non-matching rules - non-empty rule conditions but empty trigger params`() {
        // Arrange
        val workShopCompletedTrigger = createWorkshopCompletedTrigger(authSetup.app)

        val rule =
            createRule(authSetup.app) {
                trigger = workShopCompletedTrigger
                title = "When empty trigger, add 100 points"
                action {
                    addPoints(100)
                }
                conditionExpression = "workshopId == 1 && source == 'trust me'"
                this.repeatability = UnlimitedRuleRepeatability
            }

        val user = createAppUser(authSetup.app)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${workShopCompletedTrigger.key}",
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!)
        assertNotNull(userAfter)
        assertEquals(0, userAfter.get().points)
    }

    @Test
    fun `does not fire non-matching rules - for the given user ID in the app`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rule.toNonMatchingRequestBody(user, mapper)),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(0, userAfter.points)
    }

    @Test
    fun `does not fire matching rules - for other user IDs in the app`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100).rule

        val otherUser =
            createAppUser(authSetup.app, userId = UUID.randomUUID().toString())

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMatchingSendTriggerPayloadForWorkshopCompletedRule(rule, user, 1, "trust me")),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)

        val otherUserAfter = appUserRepository.findById(otherUser.id!!).get()
        assertEquals(0, otherUserAfter.points)
    }

    @Test
    fun `does not fire matching rules - for the given user ID in other apps`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100).rule

        val otherApp = setupAuth(userRepository, appRepository).app
        val otherUser = createAppUser(otherApp)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMatchingSendTriggerPayloadForWorkshopCompletedRule(rule, user, 1, "trust me")),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)

        val otherUserAfter = appUserRepository.findById(otherUser.id!!).get()
        assertEquals(0, otherUserAfter.points)
    }

    @Test
    fun `does not fire matching rules - for other user IDs in other apps`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100).rule

        val otherApp = setupAuth(userRepository, appRepository).app
        val otherUser1 =
            createAppUser(otherApp, userId = UUID.randomUUID().toString())
        val otherUser2 = createAppUser(otherApp, userId = UUID.randomUUID().toString())

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMatchingSendTriggerPayloadForWorkshopCompletedRule(rule, user, 1, "trust me")),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)

        val otherUser1After = appUserRepository.findById(otherUser1.id!!).get()
        assertEquals(0, otherUser1After.points)

        val otherUser2After = appUserRepository.findById(otherUser2.id!!).get()
        assertEquals(0, otherUser2After.points)
    }

    // Repeatability
    @Test
    fun `unlimited - given rule is fired once - when rule is matched again - then rule is fired again`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100).rule

        // Act & Assert
        repeat(2) {
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMatchingSendTriggerPayloadForWorkshopCompletedRule(rule, user, 1, "trust me")),
            )
                .andExpect(status().isOk)
        }

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(200, userAfter.points)
    }

    @Test
    fun `once per user - given rule is fired once - when rule is matched again - then rule is not fired again`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100, OncePerUserRuleRepeatability).rule

        // Act & Assert
        repeat(2) {
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMatchingSendTriggerPayloadForWorkshopCompletedRule(rule, user, 1, "trust me")),
            )
                .andExpect(status().isOk)
        }

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)
    }

    // More rules validation

    @Test
    fun `points_reached trigger - success - user reaches points`() {
        // Arrange
        val user = createAppUser(authSetup.app)

        val allTriggers = triggerRepository.findAll()
        for (trigger in allTriggers) {
            println("Trigger: ${trigger.key}")
        }
        val pointsReachedTrigger = getPointsReachedTrigger()
        val emptyTrigger =
            createTrigger(authSetup.app) {
                title = "Empty trigger"
                key = "empty"
            }

        fun sendEmptyTrigger() =
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "key": "${emptyTrigger.key}",
                            "params": {},
                            "userId": "${user.userId}"
                            }
                        """.trimIndent(),
                    ),
            )
                .andExpect(status().isOk)

        val ruleAdd60PointsWhenEmptyTriggerFired =
            createRule(authSetup.app) {
                title = "When empty trigger fired then add 60 points"
                trigger = emptyTrigger
                action {
                    addPoints(60)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        val ruleAdd50PointsWhen100PointsReached =
            createRule(authSetup.app) {
                title = "When user reaches 100 points then add 50 points"
                trigger = pointsReachedTrigger
                action {
                    addPoints(50)
                }
                conditionExpression = "points == 100"
                repeatability = OncePerUserRuleRepeatability
            }

        // Act & Assert - 1st round of empty trigger
        sendEmptyTrigger()

        // Assert DB - user has 60 points
        val userAfterEmptyTriggerFired = appUserRepository.findById(user.id!!)
        assertEquals(60, userAfterEmptyTriggerFired.get().points)

        // Act & Assert - 2nd round of empty trigger
        sendEmptyTrigger()

        // Assert DB - user has 170 points (+ 60 + 50 from points_reached trigger)
        val userAfterEmptyTriggerFiredAgain = appUserRepository.findById(user.id!!)
        assertEquals(170, userAfterEmptyTriggerFiredAgain.get().points)

        // Act & Assert - 2nd round of empty trigger
        sendEmptyTrigger()

        // Assert DB - user has 230 (+ 60) points without points_reached trigger being fired again
        val userAfterEmptyTriggerFiredAgainAgain = appUserRepository.findById(user.id!!)
        assertEquals(230, userAfterEmptyTriggerFiredAgainAgain.get().points)

        // Assert points history
        val pointsHistories =
            appUserPointsHistoryRepository.findAllByAppAndAppUser(authSetup.app, user)
                .sortedBy { it.createdAt }
        assertEquals(4, pointsHistories.size)
        // first trigger
        assertEquals(60, pointsHistories[0].points)
        assertEquals(60, pointsHistories[0].pointsChange)

        // second trigger
        assertEquals(120, pointsHistories[1].points)
        assertEquals(60, pointsHistories[1].pointsChange)
        // points_reached trigger
        assertEquals(170, pointsHistories[2].points)
        assertEquals(50, pointsHistories[2].pointsChange)

        // third trigger
        assertEquals(230, pointsHistories[3].points)
        assertEquals(60, pointsHistories[3].pointsChange)
    }

    // Dynamic points addition based on a sent trigger field.
    // For example, field of points_earned in a trigger can be used to add points to the user
    // , using template substitution.

    @Test
    fun `dynamic points addition - success`() {
        // Arrange
        val user = createAppUser(authSetup.app)

        val trigger =
            createWorkshopCompletedTrigger(authSetup.app) {
                fields {
                    integer("points_earned")
                }
            }

        // create rule
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Rule",
                      "trigger": {
                        "key": "${trigger.key}"
                      },
                      "action": {
                        "key": "add_points",
                        "params": {
                          "pointsExpression": "points_earned"
                        }
                      },
                      "conditionExpression": "workshopId == 1",
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        // Act & Assert
        repeat(2) {
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "key": "workshop_completed",
                            "params": {
                                "workshopId": 1,
                                "source": "trust me",
                                "points_earned": 100
                            },
                            "userId": "${user.userId}"
                        }
                        """.trimIndent(),
                    ),
            )
                .andExpect(status().isOk)
        }

        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(200, userAfter.points)
    }

    @Test
    fun `dynamic points addition - success with negative points`() {
        // Arrange
        val user = createAppUser(authSetup.app)

        val trigger =
            createWorkshopCompletedTrigger(authSetup.app) {
                fields {
                    integer("points_earned")
                }
            }

        // create rule
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Rule",
                      "trigger": {
                        "key": "${trigger.key}"
                      },
                      "action": {
                        "key": "add_points",
                        "params": {
                          "pointsExpression": "points_earned"
                        }
                      },
                      "conditionExpression": "workshopId == 1",
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "params": {
                            "workshopId": 1,
                            "source": "trust me",
                            "points_earned": -20
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(-20, userAfter.points)
    }

    @Test
    fun `dynamic points addition - unsupported expression - failed`() {
        // Arrange
        val user = createAppUser(authSetup.app)

        val trigger =
            createWorkshopCompletedTrigger(authSetup.app) {
                fields {
                    integer("points_earned")
                }
            }

        // create rule
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Rule",
                      "trigger": {
                        "key": "${trigger.key}"
                      },
                      "action": {
                        "key": "add_points",
                        "params": {
                          "pointsExpression": "points_earned + 100"
                        }
                      },
                      "conditionExpression": "workshopId == 1",
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Invalid points expression"))
    }

    @Test
    fun `dynamic points addition - non-integer field - failed`() {
        // Arrange
        val user = createAppUser(authSetup.app)

        val trigger =
            createWorkshopCompletedTrigger(authSetup.app) {
                fields {
                    text("points_earned")
                }
            }

        // create rule
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Rule",
                      "trigger": {
                        "key": "${trigger.key}"
                      },
                      "action": {
                        "key": "add_points",
                        "params": {
                          "pointsExpression": "points_earned"
                        }
                      },
                      "conditionExpression": "workshopId == 1",
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Invalid points expression"))
    }

    @Test
    fun testWireMock() {
        stubFor(
            WireMock.get(urlEqualTo("/resource")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/plain").withBody("Hello World!"),
            ),
        )

        val restClient =
            RestClient
                .builder()
                .baseUrl("http://localhost:" + wireMockServer.port())
                .build()
        val response = restClient.get().uri("/resource").retrieve().body<String>()
        assertEquals("Hello World!", response)
    }

    @BeforeEach
    fun setUp() {
        initIntegrationTest(postgres)
    }

    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<Nothing> =
            setupDBContainer().apply { start() }
    }
}
