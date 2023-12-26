package com.arsahub.backend.controllers

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.utils.AuthSetup
import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.models.Action
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleActivationType
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.APIKeyService
import com.arsahub.backend.services.AppService
import com.arsahub.backend.utils.JsonUtils
import com.corundumstudio.socketio.SocketIOServer
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.domain.Example
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AppControllerTest {
    @Autowired
    private lateinit var ruleActivationTypeRepository: RuleActivationTypeRepository

    @Autowired
    private lateinit var ruleRepository: RuleRepository

    @Autowired
    private lateinit var jsonUtils: JsonUtils

    @Autowired
    private lateinit var triggerRepository: TriggerRepository

    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    @Autowired
    private lateinit var appService: AppService

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var actionRepository: ActionRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @SpykBean
    lateinit var appRepository: AppRepository

    @SpykBean
    lateinit var userRepository: UserRepository

    @MockkBean
    lateinit var apiKeyService: APIKeyService


    private lateinit var authSetup: AuthSetup

    @MockBean
    @Suppress("unused")
    private lateinit var socketIoServer: SocketIOServer // no-op

    @MockBean
    @Suppress("unused")
    private lateinit var socketIOService: SocketIOService // no-op


    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<Nothing> =
            PostgreSQLContainer<Nothing>("postgres:16-alpine").withInitScript("init.sql")
    }

    @BeforeEach
    fun setUp() {
        authSetup = setupAuth(userRepository, appRepository, apiKeyService)
    }

    @AfterEach
    fun tearDown() {

    }

    @Test
    fun `returns list of default actions with 200`() {
        // Arrange
        // In resources/init.sql

        // Act & Assert
        mockMvc.performWithAppAuth(
            get("/api/apps/actions")
        )
            .andExpect(status().isOk)
            //INSERT INTO action (title, description, json_schema, created_at, updated_at, key) VALUES ('Add points', null, '{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["value"], "properties": {"value": {"type": "number"}}}', '2023-10-31 13:54:49.958514 +00:00', '2023-10-31 13:54:49.958514 +00:00', 'add_points');
            //INSERT INTO action (title, description, json_schema, created_at, updated_at, key) VALUES ('Unlock achievement', null, '{"type": "object", "$schema": "http://json-schema.org/draft-04/schema#", "required": ["achievementId"], "properties": {"achievementId": {"type": "number"}}}', '2023-10-31 14:08:24.064419 +00:00', '2023-10-31 14:08:24.064419 +00:00', 'unlock_achievement');
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Add points"))
            .andExpect(jsonPath("$[0].description").value(null))
            .andExpect(jsonPath("$[0].key").value("add_points"))
            .andExpect(jsonPath("$[1].title").value("Unlock achievement"))
            .andExpect(jsonPath("$[1].description").value(null))
            .andExpect(jsonPath("$[1].key").value("unlock_achievement"))
    }

    @Test
    fun `returns list of app users with 200`() {
        // Arrange
        val appUser1 = AppUser(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
            displayName = "User1",
            app = authSetup.app,
            points = 1000,
        )
        val appUser2 = AppUser(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000002").toString(),
            displayName = "User2",
            app = authSetup.app,
            points = 2000,
        )
        appUserRepository.saveAll(listOf(appUser1, appUser2))

        // Act & Assert
        mockMvc.performWithAppAuth(
            get("/api/apps/users")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value("00000000-0000-0000-0000-000000000001"))
            .andExpect(jsonPath("$[0].displayName").value("User1"))
            .andExpect(jsonPath("$[0].points").value(1000))
            .andExpect(jsonPath("$[1].userId").value("00000000-0000-0000-0000-000000000002"))
            .andExpect(jsonPath("$[1].displayName").value("User2"))
            .andExpect(jsonPath("$[1].points").value(2000))
    }

    @Test
    fun `creates a trigger with 201`() {
        // Arrange
        val jsonBody = """
            {
              "title": "My Title",
              "key": "my_key",
              "jsonSchema": {
                "type": "object",
                "${'$'}schema": "http://json-schema.org/draft-07/schema#",
                "required": [
                  "workshopId"
                ],
                "properties": {
                  "workshopId": {
                    "type": "integer"
                  }
                }
              }
            }
        """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/triggers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        )
            .andExpect(status().isCreated)
            // print the response
            .andExpect(jsonPath("$.title").value("My Title"))
            .andExpect(jsonPath("$.key").value("my_key"))
            .andExpect(jsonPath("$.jsonSchema.type").value("object"))
            .andExpect(jsonPath("$.jsonSchema.required.length()").value(1))
            .andExpect(jsonPath("$.jsonSchema.properties.workshopId.type").value("integer"))

        // Assert DB
        val triggers = appService.getTriggers(authSetup.app)
        assertEquals(1, triggers.size)
        assertEquals("My Title", triggers[0].title)
        assertEquals("my_key", triggers[0].key)
    }

    @Test
    fun `triggers matching rules - one trigger custom field (workshopId) with unlimited repeatability`() {
        // Arrange
        val trigger = appService.createTrigger(
            authSetup.app,
            TriggerCreateRequest(
                title = "Workshop Completed",
                key = "workshop_completed",
                jsonSchema = jsonUtils.convertJsonStringToMutableMap(
                    """
                       {
                            "type": "object",
                            "${'$'}schema": "http://json-schema.org/draft-07/schema#",
                            "required": [
                                "workshopId"
                            ],
                            "properties": {
                                "workshopId": {
                                    "type": "integer"
                                }
                            }
                       }
                    """.trimIndent()
                )
            )
        )

        val actionAddPoints = actionRepository.findOne(Example.of(Action(key = "add_points"))).get()

        val ruleActivationType = ruleActivationTypeRepository.save(
            RuleActivationType(
                name = "Repeatable",
            )
        )
        val rule = ruleRepository.save(
            Rule(
                title = "When workshop completed, add 100 points",
                trigger = trigger,
                action = actionAddPoints,
                actionParams = mutableMapOf(
                    "value" to 100
                ),
                app = authSetup.app,
                conditions = mutableMapOf(
                    "workshopId" to 1
                ),
                ruleActivationType = ruleActivationType
            )
        )

        val appUser = appUserRepository.save(
            AppUser(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                displayName = "User1",
                app = authSetup.app,
                points = 1000,
            )
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
                                "workshopId": 1
                            },
                            "userId": "${appUser.userId}"
                        }
                    """.trimIndent()
                    )
            )
                .andExpect(status().isOk)
        }
        repeat(2) { // 2 times not matching trigger
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "key": "workshop_completed",
                            "params": {
                                "workshopId": 2
                            },
                            "userId": "${appUser.userId}"
                        }
                    """.trimIndent()
                    )
            )
                .andExpect(status().isOk)
        }

        // Assert DB
        val appUserAfter = appUserRepository.findById(appUser.id!!).get()
        assertEquals(1200, appUserAfter.points)
    }
}
