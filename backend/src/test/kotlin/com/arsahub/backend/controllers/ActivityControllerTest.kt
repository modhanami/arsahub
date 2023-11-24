package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.ActivityCreateRequest
import com.arsahub.backend.dtos.TriggerCreateRequest
import com.arsahub.backend.models.Activity
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.UserActivityRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.ActivityServiceImpl
import com.arsahub.backend.services.IntegrationService
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
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
class ActivityControllerTest {

    lateinit var activity: Activity

    fun activityId(): Long {
        return activity.activityId!!
    }

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
        activity = activityServiceImpl.createActivity(
            ActivityCreateRequest(
                title = "Activity 1",
                description = "Activity 1",
            )
        )

        // add user to activity
        activityServiceImpl.addMembers(
            activity.activityId!!,
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

        // default action from us
        jdbcClient.sql(
            """
            INSERT INTO action (effect_id, title, description, json_schema, created_at, updated_at, key) VALUES (1, 'Add points', null, '{"type": "object", "${'$'}schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}', '2023-10-31 13:54:49.958514 +00:00', '2023-10-31 13:54:49.958514 +00:00', 'add_points');
            INSERT INTO action (effect_id, title, description, json_schema, created_at, updated_at, key) VALUES (2, 'Unlock achievement', null, '{"type": "object", "${'$'}schema": "http://json-schema.org/draft-04/schema#", "required": ["achievementId"], "properties": {"achievementId": {"type": "number"}}}', '2023-10-31 14:08:24.064419 +00:00', '2023-10-31 14:08:24.064419 +00:00', 'unlock_achievement');
        """
        ).update()

    }

    fun seed_activityWith1UserWithRule() {
        seed_activityWith1User()

        // create rule for activity
        activityServiceImpl.createRule(
            1,
            ActivityController.RuleCreateRequest(
                title = "Rule 1",
                description = "Rule 1",
                trigger = ActivityController.TriggerDefinition(
                    key = "activity_shared",
                ),

                action = ActivityController.ActionDefinition(
                    key = "add_points",
                    params = mapOf("value" to "10")
                ),
            )
        )
    }

    @Autowired
    private lateinit var userActivityRepository: UserActivityRepository

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
        val postgres = PostgreSQLContainer<Nothing>("postgres:16-alpine")
            .apply {
                withDatabaseName("arsahub")
                withUsername("user")
                withPassword("password")
                withCreateContainerCmdModifier { cmd ->
                    cmd.withHostConfig(
                        HostConfig.newHostConfig()
                            .withPortBindings(
                                PortBinding(Ports.Binding.bindPort(5437), ExposedPort(5432))
                            )
                    )
                }
                withReuse(true)
            }

        @JvmStatic
        @DynamicPropertySource

        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Test
    @Transactional
    fun `create rule should return 200 OK with the created rule`() {
        this.seed_activityWith1User()

        mockMvc.perform(
            post("/api/activities/${activityId()}/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "Rule 1",
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
    @Transactional
    fun `trigger should activate action for the user in the activity`() {
        this.seed_activityWith1UserWithRule()

        mockMvc.perform(
            post("/api/activities/${activityId()}/trigger")
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

        // check that user has 10 points
        activityServiceImpl.listMembers(1)
            .find { it.user?.externalUserId == "user1" }
            ?.let {
                assert(it.points == 10)
            }
    }


}