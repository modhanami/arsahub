package com.arsahub.backend.controllers

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.dtos.*
import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.Activity
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.ActivityServiceImpl
import com.arsahub.backend.services.IntegrationService
import com.corundumstudio.socketio.SocketIOServer
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.devtools.restart.RestartScope
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ActivityControllerTest {
    lateinit var ACTIVITY: Activity
    lateinit var ACHIEVEMENT: Achievement

    @MockBean
    @Suppress("unused")
    private lateinit var socketIoServer: SocketIOServer // no-op

    @MockBean
    @Suppress("unused")
    private lateinit var socketIOService: SocketIOService // no-op

    fun seed_activityWith1User() {
        // integration 1
        val integration = integrationService.createIntegration(
            IntegrationService.IntegrationCreateRequest(
                title = "Integration 1",
                description = "Integration 1",
            )
        )
        // create user
        userRepository.save(
            User(
                username = "user1",
                name = "User 1",
                externalUserId = "user1",
                externalSystem = integration
            )
        )
        // create activity
        ACTIVITY = activityServiceImpl.createActivity(
            ActivityCreateRequest(
                title = "Activity 1",
                description = "Activity 1",
            )
        )
        // add user to activity
        activityServiceImpl.addMembers(
            ACTIVITY.activityId!!,
            ActivityController.ActivityAddMembersRequest(externalUserIds = listOf("user1"))
        )
        // create trigger for integration
        integrationService.createTrigger(
            TriggerCreateRequest(
                title = "Share activity",
                description = "Triggered when activity is shared",
                key = "activity_shared"
            )
        )
        // achievement
        ACHIEVEMENT = activityServiceImpl.createAchievement(
            ACTIVITY.activityId!!,
            AchievementCreateRequest(
                title = "Achievement 1",
                description = "Achievement 1",
            )
        )
        // default action from us
        jdbcClient.sql(
            """
            INSERT INTO action (effect_id, title, description, json_schema, created_at, updated_at, key) VALUES (1, 'Add points', null, '{"type": "object", "${'$'}schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}', '2023-10-31 13:54:49.958514 +00:00', '2023-10-31 13:54:49.958514 +00:00', 'add_points');
            INSERT INTO action (effect_id, title, description, json_schema, created_at, updated_at, key) VALUES (2, 'Unlock achievement', null, '{"type": "object", "${'$'}schema": "http://json-schema.org/draft-04/schema#", "required": ["achievementId"], "properties": {"achievementId": {"type": "number"}}}', '2023-10-31 14:08:24.064419 +00:00', '2023-10-31 14:08:24.064419 +00:00', 'unlock_achievement');
        """
        ).update()

        // default triggers from us
        jdbcClient.sql(
            """
            INSERT INTO trigger (title, description, created_at, updated_at, key, json_schema) VALUES ('Points reached', 'Points reached', '2023-11-25 09:15:26.439378 +00:00', '2023-11-25 09:15:26.439378 +00:00', 'points_reached', '{"type": "object", "${'$'}schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}');
            """
        ).update()

    }

    @Autowired
    private lateinit var integrationService: IntegrationService

    @Autowired
    private lateinit var jdbcClient: JdbcClient

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var activityServiceImpl: ActivityServiceImpl

    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        @Container
        @get:RestartScope
        @ServiceConnection
        @Suppress("unused")
        val postgres = PostgreSQLContainer<Nothing>("postgres:16-alpine")
    }

    @Test
    fun `create rule should return 200 OK with the created rule`() {
        this.seed_activityWith1User()

        mockMvc.perform(
            post("/api/activities/${ACTIVITY.activityId!!}/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "Rule 1",
                        "description": "Rule 1",
                        "trigger": {
                            "key": "activity_shared",
                            "params": {}
                        },
                        "action": {
                            "key": "add_points",
                            "params": {
                                "value": "10"
                            }
                        },
                        "condition": null
                    }
                """
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Rule 1"))
            .andExpect(jsonPath("$.description").value("Rule 1"))
            .andExpect(jsonPath("$.trigger.key").value("activity_shared"))
            .andExpect(jsonPath("$.action.key").value("add_points"))
            .andExpect(jsonPath("$.action.jsonSchema.properties.value").exists())
            .andExpect(jsonPath("$.id").isNumber)

    }

    @Test
    fun `trigger should activate all actions for the user in the activity`() {
        this.seed_activityWith1User()

        // rule 1: add 10 points when activity_shared is triggered
        this.activityServiceImpl.createRule(
            ACTIVITY.activityId!!,
            RuleCreateRequest(
                name = "Rule 1",
                description = "Rule 1",
                trigger = TriggerDefinition(
                    key = "activity_shared",
                ),
                action = ActionDefinition(
                    key = "add_points",
                    params = mapOf("value" to "10")
                ),
            )
        )

        // rule 2: add 5 points when activity_shared is triggered
        this.activityServiceImpl.createRule(
            ACTIVITY.activityId!!,
            RuleCreateRequest(
                name = "Rule 2",
                description = "Rule 2",
                trigger = TriggerDefinition(
                    key = "activity_shared",
                ),
                action = ActionDefinition(
                    key = "add_points",
                    params = mapOf("value" to "5")
                ),
            )
        )

        mockMvc.perform(
            post("/api/activities/${ACTIVITY.activityId!!}/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "userId": "user1",
                        "key": "activity_shared",
                        "params": {}
                    }
                """
                )
        )
            .andExpect(status().isOk)

        // check that user has 15 points (10 + 5)
        val user = activityServiceImpl.listMembers(ACTIVITY.activityId!!)
            .find { it.user?.externalUserId == "user1" }

        assertThat(user?.points).isEqualTo(15)
    }

    @Test
    fun `trigger should implicitly activate chained triggers for the user in the activity`() {
        this.seed_activityWith1User()

        // rule 1: add 10 points when activity_shared is triggered
        this.activityServiceImpl.createRule(
            ACTIVITY.activityId!!,
            RuleCreateRequest(
                name = "Rule 1",
                description = "Rule 1",
                trigger = TriggerDefinition(
                    key = "activity_shared",
                ),
                action = ActionDefinition(
                    key = "add_points",
                    params = mapOf("value" to "10")
                ),
            )
        )

        // rule 2: add 5 points when points_reached is triggered
        this.activityServiceImpl.createRule(
            ACTIVITY.activityId!!,
            RuleCreateRequest(
                name = "Rule 2",
                description = "Rule 2",
                trigger = TriggerDefinition(
                    key = "points_reached",
                    params = mapOf("value" to "20")
                ),
                action = ActionDefinition(
                    key = "add_points",
                    params = mapOf("value" to "5")
                ),
            )
        )

        repeat(2) {
            mockMvc.perform(
                post("/api/activities/${ACTIVITY.activityId!!}/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "userId": "user1",
                            "key": "activity_shared",
                            "params": {}
                        }
                    """
                    )
            )
                .andExpect(status().isOk)
        }

        // check that user has 25 points (10 + 10 + 5)
        val user = activityServiceImpl.listMembers(ACTIVITY.activityId!!)
            .find { it.user?.externalUserId == "user1" }

        assertThat(user?.points).isEqualTo(25)
    }

    @Test
    fun `one-time action should only be activated once for the user in the activity - unlock_achievement`() {
        this.seed_activityWith1User()

        // rule 1: unlock achievement when activity_shared is triggered
        this.activityServiceImpl.createRule(
            ACTIVITY.activityId!!,
            RuleCreateRequest(
                name = "Rule 1",
                description = "Rule 1",
                trigger = TriggerDefinition(
                    key = "activity_shared",
                ),
                action = ActionDefinition(
                    key = "unlock_achievement",
                    params = mapOf("achievementId" to ACHIEVEMENT.achievementId.toString())
                ),
            )
        )

        mockMvc.perform(
            post("/api/activities/${ACTIVITY.activityId!!}/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "userId": "user1",
                        "key": "activity_shared",
                        "params": {}
                    }
                """
                )
        )
            .andExpect(status().isOk)

        // check that user has 1 achievement
        val user = activityServiceImpl.listMembers(ACTIVITY.activityId!!)
            .find { it.user?.externalUserId == "user1" }

        assertThat(user?.userActivityAchievements?.size).isEqualTo(1)

        // trigger again
        mockMvc.perform(
            post("/api/activities/${ACTIVITY.activityId!!}/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "userId": "user1",
                        "key": "activity_shared",
                        "params": {}
                    }
                """
                )
        )
            .andExpect(status().isOk)

        // check that user still has 1 achievement
        val user2 = activityServiceImpl.listMembers(ACTIVITY.activityId!!)
            .find { it.user?.externalUserId == "user1" }

        assertThat(user2?.userActivityAchievements?.size).isEqualTo(1)
    }
}
