package com.arsahub.backend.controllers

import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithUserAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.dtos.request.AchievementCreateRequest
import com.arsahub.backend.dtos.request.Action
import com.arsahub.backend.dtos.request.FieldDefinition
import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.models.AppInvitation
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.OncePerUserRuleRepeatability
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleRepeatability
import com.arsahub.backend.models.UnlimitedRuleRepeatability
import com.arsahub.backend.models.User
import com.arsahub.backend.models.WebhookRepository
import com.arsahub.backend.repositories.AchievementRepository
import com.arsahub.backend.repositories.AppInvitationRepository
import com.arsahub.backend.repositories.AppInvitationStatusRepository
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserAchievementRepository
import com.arsahub.backend.repositories.AppUserPointsHistoryRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.RuleRepository
import com.arsahub.backend.repositories.RuleTriggerFieldStateRepository
import com.arsahub.backend.repositories.TriggerRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.AchievementService
import com.arsahub.backend.services.RuleService
import com.arsahub.backend.services.TriggerService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.*

class AppControllerTest() : BaseIntegrationTest() {
    @Autowired
    private lateinit var webhookRepository: WebhookRepository

    @Autowired
    private lateinit var ruleTriggerFieldStateRepository: RuleTriggerFieldStateRepository

    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @Autowired
    private lateinit var appUserPointsHistoryRepository: AppUserPointsHistoryRepository

    @Autowired
    private lateinit var appUserAchievementRepository: AppUserAchievementRepository

    @Autowired
    private lateinit var achievementService: AchievementService

    @Autowired
    private lateinit var triggerRepository: TriggerRepository

    @Autowired
    private lateinit var appInvitationStatusRepository: AppInvitationStatusRepository

    @Autowired
    private lateinit var appInvitationRepository: AppInvitationRepository

    @Autowired
    private lateinit var appRepository: AppRepository

    @Autowired
    private lateinit var achievementRepository: AchievementRepository

    @Autowired
    private lateinit var ruleService: RuleService

    @Autowired
    private lateinit var triggerService: TriggerService

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var ruleRepository: RuleRepository

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Suppress("unused")
    fun TrigggerTestModel.toNode(): ObjectNode {
        return mapper.valueToTree(this)
    }

    fun TrigggerTestModel.toJson(): String {
        return mapper.writeValueAsString(this)
    }

    @Test
    fun `creates a trigger with 201`() {
        // Arrange
        val jsonBody =
            TrigggerTestModel(
                title = "Workshop Completed",
                key = "workshop_completed",
                fields =
                    listOf(
                        FieldTestModel(
                            type = "integer",
                            key = "workshopId",
                            label = "Workshop ID",
                        ),
                        FieldTestModel(
                            type = "text",
                            key = "source",
                        ),
                    ),
            ).toJson()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("Workshop Completed"))
            .andExpect(jsonPath("$.key").value("workshop_completed"))
            .andExpect(jsonPath("$.fields.length()").value(2))
            .andExpect(jsonPath("$.fields[0].type").value("integer"))
            .andExpect(jsonPath("$.fields[0].key").value("workshopId"))
            .andExpect(jsonPath("$.fields[0].label").value("Workshop ID"))
            .andExpect(jsonPath("$.fields[1].type").value("text"))
            .andExpect(jsonPath("$.fields[1].key").value("source"))

        // Assert DB
        val triggers = triggerService.getTriggers(authSetup.app)
        assertEquals(1, triggers.size)
        val trigger = triggers[0]
        assertEquals("Workshop Completed", trigger.title)
        assertEquals("workshop_completed", trigger.key)
        assertEquals(2, trigger.fields.size)
        val workshopIdField = trigger.fields.find { it.key == "workshopId" }
        require(workshopIdField != null)
        assertEquals("integer", workshopIdField.type)
        assertEquals("Workshop ID", workshopIdField.label)

        val sourceField = trigger.fields.find { it.key == "source" }
        require(sourceField != null)
        assertEquals("text", sourceField.type)
        assertEquals("source", sourceField.key)
    }

    @Test
    fun `fails with 400 when creating a trigger with title less than 4 characters`() {
        // Arrange
        val jsonBody =
            TrigggerTestModel(
                title = "Wor",
                key = "workshop_completed",
                fields =
                    listOf(
                        FieldTestModel(
                            type = "integer",
                            key = "workshopId",
                            label = "Workshop ID",
                        ),
                        FieldTestModel(
                            type = "text",
                            key = "source",
                        ),
                    ),
            ).toJson()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.title").value("Title must be between 4 and 200 characters"))
    }

    @Test
    fun `fails with 400 when creating a trigger without title`() {
        // Arrange
        val jsonBody =
            TrigggerTestModel(
                // TODO: evaluate if absent fields should be interpreted the same as null (right now it is)
                title = null,
                key = "workshop_completed",
                fields =
                    listOf(
                        FieldTestModel(
                            type = "integer",
                            key = "workshopId",
                            label = "Workshop ID",
                        ),
                        FieldTestModel(
                            type = "text",
                            key = "source",
                        ),
                    ),
            ).toJson()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.title").value("Title is required"))
    }

    @Test
    fun `fails with 400 when creating a trigger with invalid field types`() {
        // Arrange
        val jsonBody =
            TrigggerTestModel(
                title = "Workshop Completed",
                key = "workshop_completed",
                fields =
                    listOf(
                        FieldTestModel(
                            type = "integer",
                            key = "workshopId",
                            label = "Workshop ID",
                        ),
                        FieldTestModel(
                            type = "not_a_valid_type",
                            key = "source",
                        ),
                    ),
            ).toJson()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Invalid field type: not_a_valid_type"))
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

    @Test
    fun `creates a rule with 201`() {
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

        val jsonBody =
            """
            {
              "title": "When workshop ID 1 completed then add 100 points - unlimited",
              "trigger": {
                "key": "${trigger.key}"
              },
              "action": {
                "key": "add_points",
                "params": {
                  "points": 100
                }
              },
              "conditionExpression": "workshopId == 1",
              "repeatability": "unlimited"
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("When workshop ID 1 completed then add 100 points - unlimited"))
            .andExpect(jsonPath("$.trigger.key").value("workshop_completed"))
            .andExpect(jsonPath("$.action").value("add_points"))
            .andExpect(jsonPath("$.actionPoints").value(100))
            .andExpect(jsonPath("$.conditionExpression").value("workshopId == 1"))
            .andExpect(jsonPath("$.repeatability").value("unlimited"))

        // Assert DB
        val rules = ruleService.listRules(authSetup.app)
        assertEquals(1, rules.size)
        val rule = rules[0]
        assertEquals("When workshop ID 1 completed then add 100 points - unlimited", rule.title)
        assertEquals("workshop_completed", rule.trigger?.key)
        assertEquals("add_points", rule.action)
        assertEquals(100, rule.actionPoints)
        assertEquals("workshopId == 1", rule.conditionExpression)
        assertEquals("unlimited", rule.repeatability)
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
    }

    @Test
    fun `fires matching rules - with integer set trigger field - non-accumulated`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val trigger =
            createTrigger(authSetup.app) {
                key = "workshop_completed"
                title = "Workshop Completed"
                description = "When a workshop is completed"
                fields {
                    integerSet("workshopId", "Workshop ID")
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
                conditionExpression = "workshopId.containsAll([1, 2, 3]) && source == 'trust me'"
                repeatability = UnlimitedRuleRepeatability
            }.let(::WorkshopCompletedRule)
        val ruleCreated = ruleRepository.findById(rule.rule.id!!).get()
        assertNull(ruleCreated.accumulatedFields)

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
                            "workshopId": [2],
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert rule trigger field state
        val ruleTriggerFieldState =
            ruleTriggerFieldStateRepository.findByAppAndAppUserAndRuleAndTriggerField(
                authSetup.app,
                user,
                rule.rule,
                trigger.fields.first { it.key == "workshopId" },
            )
        assertNull(ruleTriggerFieldState)

        // Assert points
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(0, userAfter.points)

        // second trigger with workshopId 3 and 1
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": [3, 1],
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert rule trigger field state
        val ruleTriggerFieldStateAfter =
            ruleTriggerFieldStateRepository.findByAppAndAppUserAndRuleAndTriggerField(
                authSetup.app,
                user,
                rule.rule,
                trigger.fields.first { it.key == "workshopId" },
            )
        assertNull(ruleTriggerFieldStateAfter)

        // Assert points
        val userAfterSecond = appUserRepository.findById(user.id!!).get()
        assertEquals(0, userAfterSecond.points)

        // third trigger with workshopId 1, 2, 3 should fire the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": [1, 2, 3],
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
        assertEquals(100, userAfterThird.points)

        // fourth trigger without workshopId should not fire the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert points
        val userAfterFourth = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfterFourth.points)
    }

    @Test
    fun `fires matching rules - with text set trigger field - non-accumulated`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val trigger =
            createTrigger(authSetup.app) {
                key = "workshop_completed"
                title = "Workshop Completed"
                description = "When a workshop is completed"
                fields {
                    textSet("workshopId", "Workshop ID")
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
                conditionExpression = "workshopId.containsAll(['1', '2', '3']) && source == 'trust me'"
                repeatability = UnlimitedRuleRepeatability
            }.let(::WorkshopCompletedRule)
        val ruleCreated = ruleRepository.findById(rule.rule.id!!).get()
        assertNull(ruleCreated.accumulatedFields)

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
                            "workshopId": ["2"],
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert rule trigger field state
        val ruleTriggerFieldState =
            ruleTriggerFieldStateRepository.findByAppAndAppUserAndRuleAndTriggerField(
                authSetup.app,
                user,
                rule.rule,
                trigger.fields.first { it.key == "workshopId" },
            )
        assertNull(ruleTriggerFieldState)

        // Assert points
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(0, userAfter.points)

        // second trigger with workshopId 3 and 1
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": ["3", "1"],
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert rule trigger field state
        val ruleTriggerFieldStateAfter =
            ruleTriggerFieldStateRepository.findByAppAndAppUserAndRuleAndTriggerField(
                authSetup.app,
                user,
                rule.rule,
                trigger.fields.first { it.key == "workshopId" },
            )

        assertNull(ruleTriggerFieldStateAfter)

        // Assert points
        val userAfterSecond = appUserRepository.findById(user.id!!).get()
        assertEquals(0, userAfterSecond.points)

        // third trigger with workshopId 1, 2, 3 should fire the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": ["1", "2", "3"],
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
        assertEquals(100, userAfterThird.points)

        // fourth trigger without workshopId should not fire the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "source": "trust me"
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert points
        val userAfterFourth = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfterFourth.points)
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

    // App Invitations
    // An app can invite a user to join the app. If they accept, they become an app user. If they decline, nothing happens.
    // Scenarios
    // - Invite a user successfully. Return 201 Created
    // - Invite a user that already invited. Throw 409 Conflict
    // - Invite a user that already joined. Throw 409 Conflict
    // - Invite a user, user accepts. Return 201 Created
    // - Invite a user, user declines. Return 204 No Content
    // - Uninvited user tries to accept an invitation. Throw 404 Not Found
    // - Uninvited user tries to decline an invitation. Throw 404 Not Found

    @Test
    fun `invite user - success`() {
        // Arrange
        val invitee = createInvitee()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "email": "${invitee.email}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        // Assert DB
        val appInvitations = appInvitationRepository.findAll()
        assertEquals(1, appInvitations.size)
        val pendingInvitationStatus = getPendingAppInvitationStatus()
        assertEquals(
            pendingInvitationStatus!!.status!!.lowercase(),
            appInvitations[0].invitationStatus!!.status!!.lowercase(),
        )
        val appInvitation = appInvitations[0]
        assertEquals(authSetup.app.id, appInvitation.app?.id)
        assertEquals(invitee.userId, appInvitation.user?.userId)
    }

    @Test
    fun `invite user - success - already invited`() {
        // Arrange
        val invitee = createInvitee()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "email": "${invitee.email}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        mockMvc.performWithAppAuth(
            post("/api/apps/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "email": "${invitee.email}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isConflict)

        // Assert DB
        val appInvitations = appInvitationRepository.findAll()
        assertEquals(1, appInvitations.size)
    }

    @Test
    fun `invite user - failed - user already joined`() {
        // Arrange
        val invitee = createInvitee()

        val appUser =
            appUserRepository.save(
                AppUser(
                    userId = invitee.userId.toString(),
                    displayName = invitee.name,
                    user = invitee,
                    app = authSetup.app,
                    points = 0,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "email": "${invitee.email}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isConflict)

        // Assert DB
        val appInvitations = appInvitationRepository.findAll()
        assertEquals(0, appInvitations.size)
    }

    @Test
    fun `user accepts invitation - success`() {
        // Arrange
        val invitee = createInvitee()

        val appInvitation =
            appInvitationRepository.save(
                AppInvitation(
                    app = authSetup.app,
                    user = invitee,
                    invitationStatus = getPendingAppInvitationStatus(),
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithUserAuth(
            post("/api/apps/invitations/${appInvitation.id}/accept")
                .contentType(MediaType.APPLICATION_JSON),
            user = invitee,
        )
            .andExpect(status().isCreated)

        // Assert DB
        val appInvitations = appInvitationRepository.findAll()
        assertEquals(1, appInvitations.size)
        val acceptedInvitationStatus = getAcceptedAppInvitationStatus()
        assertEquals(
            acceptedInvitationStatus!!.status!!.lowercase(),
            appInvitations[0].invitationStatus!!.status!!.lowercase(),
        )

        val appUser = appUserRepository.findByAppAndUserEmail(authSetup.app, invitee.email!!)
        assertNotNull(appUser)
    }

    @Test
    fun `user declines invitation - success`() {
        // Arrange
        val invitee = createInvitee()

        val appInvitation =
            appInvitationRepository.save(
                AppInvitation(
                    app = authSetup.app,
                    user = invitee,
                    invitationStatus = getPendingAppInvitationStatus(),
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithUserAuth(
            post("/api/apps/invitations/${appInvitation.id}/decline")
                .contentType(MediaType.APPLICATION_JSON),
            user = invitee,
        )
            .andExpect(status().isNoContent)

        // Assert DB
        val appInvitations = appInvitationRepository.findAll()
        assertEquals(1, appInvitations.size)
        val declinedInvitationStatus = getDeclinedAppInvitationStatus()
        assertEquals(
            declinedInvitationStatus!!.status!!.lowercase(),
            appInvitations[0].invitationStatus!!.status!!.lowercase(),
        )

        val appUser = appUserRepository.findByAppAndUserEmail(authSetup.app, invitee.email!!)
        assertNull(appUser)
    }

    @Test
    fun `user accepts invitation - failed - not invited`() {
        // Arrange
        val invitee = createInvitee()

        // Act & Assert HTTP
        mockMvc.performWithUserAuth(
            post("/api/apps/invitations/999999999/accept")
                .contentType(MediaType.APPLICATION_JSON),
            user = invitee,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Invitation not found"))
    }

    @Test
    fun `user declines invitation - failed - not invited`() {
        // Arrange
        val invitee = createInvitee()

        // Act & Assert HTTP
        mockMvc.performWithUserAuth(
            post("/api/apps/invitations/999999999/decline")
                .contentType(MediaType.APPLICATION_JSON),
            user = invitee,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Invitation not found"))
    }

    @Test
    fun `user accepts invitation - failed - already accepted`() {
        // Arrange
        val invitee = createInvitee()

        val appInvitation =
            appInvitationRepository.save(
                AppInvitation(
                    app = authSetup.app,
                    user = invitee,
                    invitationStatus = getAcceptedAppInvitationStatus(),
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithUserAuth(
            post("/api/apps/invitations/${appInvitation.id}/accept")
                .contentType(MediaType.APPLICATION_JSON),
            user = invitee,
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Invitation is not pending"))
    }

    @Test
    fun `user accepts invitation - failed - already declined`() {
        // Arrange
        val invitee = createInvitee()

        val appInvitation =
            appInvitationRepository.save(
                AppInvitation(
                    app = authSetup.app,
                    user = invitee,
                    invitationStatus = getDeclinedAppInvitationStatus(),
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithUserAuth(
            post("/api/apps/invitations/${appInvitation.id}/accept")
                .contentType(MediaType.APPLICATION_JSON),
            user = invitee,
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Invitation is not pending"))
    }

    @Test
    fun `user declines invitation - failed - already accepted`() {
        // Arrange
        val invitee = createInvitee()

        val appInvitation =
            appInvitationRepository.save(
                AppInvitation(
                    app = authSetup.app,
                    user = invitee,
                    invitationStatus = getAcceptedAppInvitationStatus(),
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithUserAuth(
            post("/api/apps/invitations/${appInvitation.id}/decline")
                .contentType(MediaType.APPLICATION_JSON),
            user = invitee,
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Invitation is not pending"))
    }

    @Test
    fun `user declines invitation - failed - already declined`() {
        // Arrange
        val invitee = createInvitee()

        val appInvitation =
            appInvitationRepository.save(
                AppInvitation(
                    app = authSetup.app,
                    user = invitee,
                    invitationStatus = getDeclinedAppInvitationStatus(),
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithUserAuth(
            post("/api/apps/invitations/${appInvitation.id}/decline")
                .contentType(MediaType.APPLICATION_JSON),
            user = invitee,
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Invitation is not pending"))
    }

    private fun getAcceptedAppInvitationStatus() = appInvitationStatusRepository.findByStatusIgnoreCase("accepted")

    private fun getPendingAppInvitationStatus() = appInvitationStatusRepository.findByStatusIgnoreCase("pending")

    private fun getDeclinedAppInvitationStatus() = appInvitationStatusRepository.findByStatusIgnoreCase("declined")

    private fun createInvitee(): User {
        return userRepository.save(
            User(
                name = "Invitee",
                externalUserId = UUID.randomUUID().toString(),
                googleUserId = UUID.randomUUID().toString(),
                email = "invitee@test.test",
            ),
        )
    }

    // Builtin triggers
    // - Points reached
    @Test
    fun `create rule with points_reached trigger - success`() {
        // TODO: validate condition expression uses valid trigger field keys
        val pointsReachedTriggerKey = "points_reached"

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "When user reaches 100 points then add 50 points",
                      "trigger": {
                        "key": "$pointsReachedTriggerKey"
                      },
                      "action": {
                        "key": "add_points",
                        "params": {
                          "points": 50
                        }
                      },
                      "conditionExpression": "points == 100",
                      "repeatability": "once_per_user"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        // Assert DB
        val rules = ruleRepository.findAll()
        assertEquals(1, rules.size)
        val rule = rules[0]
        assertEquals(pointsReachedTriggerKey, rule.trigger!!.key)
        assertEquals("When user reaches 100 points then add 50 points", rule.title)
        assertEquals("add_points", rule.action)
        assertEquals(50, rule.actionPoints)
        assertEquals("points == 100", rule.conditionExpression)
        assertEquals("once_per_user", rule.repeatability)
    }

    @Test
    fun `create rule with points_reached trigger - failed - repeatability not once_per_user`() {
        val pointsReachedTriggerKey = "points_reached"

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "When user reaches 100 points then add 50 points",
                      "trigger": {
                        "key": "$pointsReachedTriggerKey"
                      },
                      "action": {
                        "key": "add_points",
                        "params": {
                          "points": 50
                        }
                      },
                      "conditionExpression": "points == 100",
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Repeatability must be once_per_user for this trigger"))
    }

    @Test
    fun `create rule with condition expression - success`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert
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
                          "points": 100
                        }
                      },
                      "conditionExpression": "workshopId == 1",
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        // Assert DB
        val rules = ruleRepository.findAll()
        assertEquals(1, rules.size)
        val rule = rules[0]
        assertEquals("Rule", rule.title)
        assertEquals("workshopId == 1", rule.conditionExpression)

        // Assert condition expression is evaluated
        val user = createAppUser(authSetup.app)
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": 1
                        },
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)

        // Assert condition expression is not evaluated
        val user2 = createAppUser(authSetup.app, userId = UUID.randomUUID().toString())

        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "${trigger.key}",
                        "params": {
                            "workshopId": 2
                        },
                        "userId": "${user2.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val userAfter2 = appUserRepository.findById(user2.id!!).get()
        assertEquals(0, userAfter2.points)
    }

    @Test
    fun `create rule with condition expression - failed - invalid trigger field`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert
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
                          "points": 100
                        }
                      },
                      "conditionExpression": "invalidField == 1",
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Invalid fields in condition expression"))
    }

    @Test
    fun `create rule with condition expression - failed - invalid data type`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert
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
                          "points": 100
                        }
                      },
                      "conditionExpression": "workshopId == '1'",
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Invalid fields in condition expression"))
    }

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

    @Test
    fun `create trigger - success`() {
        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Workshop completed"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        // Assert DB
        val triggers = triggerRepository.findAll()
        val trigger = triggers.first { it.title == "Workshop completed" }
        assertEquals("Workshop completed", trigger.title)
        assertEquals("workshop_completed", trigger.key)
        assertEquals(authSetup.app.id, trigger.app?.id)
    }

    @Test
    fun `create triggers with same title in two different apps - success`() {
        // Arrange
        val otherApp = setupAuth(userRepository, appRepository).app

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Workshop completed"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Workshop completed"
                    }
                    """.trimIndent(),
                ),
            app = otherApp,
        )
            .andExpect(status().isCreated)

        // Assert DB
        val triggers = triggerRepository.findAll()
        val app1Trigger = triggers.first { it.app?.id == authSetup.app.id }
        assertNotNull(app1Trigger)
        val app2Trigger = triggers.first { it.app?.id == otherApp.id }
        assertNotNull(app2Trigger)
    }

    @Test
    fun `create triggers with same title in the same app - failed`() {
        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Workshop   completed  "
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)

        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Workshop completed"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Trigger with the same title already exists"))
    }

    // Deletion

    // Delete trigger: Only when no rules are using it
    @Test
    fun `delete trigger - success`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/triggers/${trigger.id}"),
        )
            .andExpect(status().isNoContent)

        // Assert DB
        val triggers = triggerRepository.findById(trigger.id!!)
        assertTrue(triggers.isEmpty)
    }

    @Test
    fun `delete trigger - failed - rules are using it`() {
        // Arrange
        val subjectTrigger = createWorkshopCompletedTrigger(authSetup.app)
        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = subjectTrigger
                action {
                    addPoints(100)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/triggers/${subjectTrigger.id}"),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Trigger is used by one or more rules"))

        // Assert DB
        val triggers = triggerRepository.findById(subjectTrigger.id!!)
        assertTrue(triggers.isPresent)
    }

    @Test
    fun `delete trigger - unauthorized - different app`() {
        // Arrange
        val otherApp = setupAuth(userRepository, appRepository).app
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/triggers/${trigger.id}"),
            app = otherApp,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Trigger not found"))

        // Assert DB
        val triggers = triggerRepository.findById(trigger.id!!)
        assertTrue(triggers.isPresent)
    }

    // Delete achievement: Only when no rules are using it and no users have it
    @Test
    fun `delete achievement - success`() {
        // Arrange
        val achievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))
        val achievementFromDB = achievementRepository.findById(achievement.achievementId!!)
        assertTrue(achievementFromDB.isPresent)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/achievements/${achievement.achievementId}"),
        )
            .andExpect(status().isNoContent)

        // Assert DB
        val achievements = achievementRepository.findById(achievement.achievementId!!)
        assertTrue(achievements.isEmpty)
    }

    @Test
    fun `delete achievement - failed - rules are using it`() {
        // Arrange
        val subjectAchievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))

        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    unlockAchievement(subjectAchievement.achievementId!!)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/achievements/${subjectAchievement.achievementId}"),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Achievement is used in rules or unlocked by users"))

        // Assert DB
        val achievements = achievementRepository.findById(subjectAchievement.achievementId!!)
        assertTrue(achievements.isPresent)
    }

    @Test
    fun `delete achievement - failed - users have it`() {
        // Arrange
        val subjectAchievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))

        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    unlockAchievement(subjectAchievement.achievementId!!)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        val user = createAppUser(authSetup.app)

        // trigger to activate the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val appUserAchievementAfterTrigger =
            appUserAchievementRepository.findAll()
                .first { it.achievement!!.achievementId == subjectAchievement.achievementId }
        assertNotNull(appUserAchievementAfterTrigger)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/achievements/${subjectAchievement.achievementId}"),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Achievement is used in rules or unlocked by users"))

        // Assert DB
        val achievements = achievementRepository.findById(subjectAchievement.achievementId!!)
        assertTrue(achievements.isPresent)

        val appUserAchievementAfterDelete =
            appUserAchievementRepository.findAll()
                .first { it.achievement!!.achievementId == subjectAchievement.achievementId }
        assertNotNull(appUserAchievementAfterDelete)
    }

    @Test
    fun `delete achievement - failed - different app`() {
        // Arrange
        val otherApp = setupAuth(userRepository, appRepository).app
        val achievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/achievements/${achievement.achievementId}"),
            app = otherApp,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Achievement not found"))

        // Assert DB
        val achievementAfterFailedDelete =
            achievementRepository.findById(
                achievement
                    .achievementId!!,
            )
        assertTrue(achievementAfterFailedDelete.isPresent)
    }

    // Delete rule: Only when not activated once
    @Test
    fun `delete rule - success`() {
        // Arrange
        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    addPoints(100)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        val rules1 = ruleRepository.findAll()
        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/rules/${rule.id}"),
        )
            .andExpect(status().isNoContent)

        // Assert DB
        val ruleAfter = ruleRepository.findByIdAndApp(rule.id!!, authSetup.app)
        assertNull(ruleAfter)
    }

    @Test
    fun `delete rule - success - is in use`() {
        // Arrange
        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    addPoints(100)
                }
                repeatability = OncePerUserRuleRepeatability
            }

        val user = createAppUser(authSetup.app)

        // trigger to activate the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/rules/${rule.id}"),
        )
            .andExpect(status().isNoContent)

        // Assert DB
        val ruleAfter = ruleRepository.findByIdAndApp(rule.id!!, authSetup.app)
        assertNull(ruleAfter)
    }

    @Test
    fun `delete rule - failed - different app`() {
        // Arrange
        val otherApp = setupAuth(userRepository, appRepository).app
        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    addPoints(100)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/rules/${rule.id}"),
            app = otherApp,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Rule not found"))

        // Assert DB
        val rules = ruleRepository.findById(rule.id!!)
        assertTrue(rules.isPresent)
    }

    // Editing

    // Edit trigger: Only title and description
    @Test
    fun `edit trigger - success`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            patch("/api/apps/triggers/${trigger.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated Title",
                      "description": "Updated Description"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.description").value("Updated Description"))

        // Assert DB
        val triggers = triggerRepository.findById(trigger.id!!)
        assertTrue(triggers.isPresent)
        assertEquals("Updated Title", triggers.get().title)
        assertEquals("Updated Description", triggers.get().description)
    }

    @Test
    fun `edit trigger - success - title only`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            patch("/api/apps/triggers/${trigger.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated Title"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.description").value("When a workshop is completed"))

        // Assert DB
        val triggers = triggerRepository.findById(trigger.id!!)
        assertTrue(triggers.isPresent)
        assertEquals("Updated Title", triggers.get().title)
        assertEquals("When a workshop is completed", triggers.get().description)
    }

    @Test
    fun `edit trigger - success - description only`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            patch("/api/apps/triggers/${trigger.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "description": "Updated Description"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Workshop Completed"))
            .andExpect(jsonPath("$.description").value("Updated Description"))

        // Assert DB
        val triggers = triggerRepository.findById(trigger.id!!)
        assertTrue(triggers.isPresent)
        assertEquals("Workshop Completed", triggers.get().title)
        assertEquals("Updated Description", triggers.get().description)
    }

    @Test
    fun `edit trigger - failed - different app`() {
        // Arrange
        val otherApp = setupAuth(userRepository, appRepository).app
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            patch("/api/apps/triggers/${trigger.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated Title",
                      "description": "Updated Description"
                    }
                    """.trimIndent(),
                ),
            app = otherApp,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Trigger not found"))

        // Assert DB
        val triggers = triggerRepository.findById(trigger.id!!)
        assertTrue(triggers.isPresent)
    }

    // Edit rule: Only title and description
    @Test
    fun `edit rule - success`() {
        // Arrange
        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    addPoints(100)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            patch("/api/apps/rules/${rule.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated Title",
                      "description": "Updated Description"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.description").value("Updated Description"))

        // Assert DB
        val rules = ruleRepository.findById(rule.id!!)
        assertTrue(rules.isPresent)
        assertEquals("Updated Title", rules.get().title)
        assertEquals("Updated Description", rules.get().description)
    }

    @Test
    fun `edit rule - success - title only`() {
        // Arrange
        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                description = "When a workshop is completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    addPoints(100)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            patch("/api/apps/rules/${rule.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated Title"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.description").value("When a workshop is completed then add 100 points"))

        // Assert DB
        val rules = ruleRepository.findById(rule.id!!)
        assertTrue(rules.isPresent)
        assertEquals("Updated Title", rules.get().title)
        assertEquals("When a workshop is completed then add 100 points", rules.get().description)
    }

    @Test
    fun `edit rule - success - description only`() {
        // Arrange
        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    addPoints(100)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            patch("/api/apps/rules/${rule.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "description": "Updated Description"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("When workshop completed then add 100 points"))
            .andExpect(jsonPath("$.description").value("Updated Description"))

        // Assert DB
        val rules = ruleRepository.findById(rule.id!!)
        assertTrue(rules.isPresent)
        assertEquals("When workshop completed then add 100 points", rules.get().title)
        assertEquals("Updated Description", rules.get().description)
    }

    @Test
    fun `edit rule - failed - different app`() {
        // Arrange
        val otherApp = setupAuth(userRepository, appRepository).app
        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    addPoints(100)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            patch("/api/apps/rules/${rule.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Updated Title",
                      "description": "Updated Description"
                    }
                    """.trimIndent(),
                ),
            app = otherApp,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Rule not found"))

        // Assert DB
        val rules = ruleRepository.findById(rule.id!!)
        assertTrue(rules.isPresent)
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

    private fun getPointsReachedTrigger() = triggerRepository.findByKey("points_reached")!!
}
