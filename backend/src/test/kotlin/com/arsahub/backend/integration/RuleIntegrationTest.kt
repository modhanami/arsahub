package com.arsahub.backend.integration

import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.dtos.request.FieldDefinition
import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.models.OncePerUserRuleRepeatability
import com.arsahub.backend.models.UnlimitedRuleRepeatability
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.RuleRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.RuleService
import com.arsahub.backend.services.TriggerService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import java.util.*

class RuleIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var appRepository: AppRepository

    @Autowired
    private lateinit var ruleService: RuleService

    @Autowired
    private lateinit var triggerService: TriggerService

    @Autowired
    private lateinit var ruleRepository: RuleRepository

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var userRepository: UserRepository

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

    // Delete rule: Always allowed
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
