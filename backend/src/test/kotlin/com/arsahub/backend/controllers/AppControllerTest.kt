package com.arsahub.backend.controllers

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.utils.AuthSetup
import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.models.Action
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.repositories.ActionRepository
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.APIKeyService
import com.arsahub.backend.services.AppService
import com.corundumstudio.socketio.SocketIOServer
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
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
    fun `returns list of actions with 200`() {
        // Arrange
        val action1 = Action(
            title = "Test Action 1",
            description = "Test Action Description 1",
            jsonSchema = mutableMapOf(
                "type" to "object",
                "properties" to mutableMapOf(
                    "value" to mutableMapOf(
                        "type" to "number"
                    )
                )
            ),
            key = "test-action-1",
        )
        val action2 = Action(
            title = "Test Action 2",
            description = "Test Action Description 2",
            jsonSchema = mutableMapOf(
                "type" to "object",
                "properties" to mutableMapOf(
                    "value" to mutableMapOf(
                        "type" to "number"
                    )
                )
            ),
            key = "test-action-2",
        )
        actionRepository.saveAll(listOf(action1, action2))

        // Act & Assert
        mockMvc.performWithAppAuth(
            get("/api/apps/actions")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Test Action 1"))
            .andExpect(jsonPath("$[0].description").value("Test Action Description 1"))
            .andExpect(jsonPath("$[0].key").value("test-action-1"))
            .andExpect(jsonPath("$[1].title").value("Test Action 2"))
            .andExpect(jsonPath("$[1].description").value("Test Action Description 2"))
            .andExpect(jsonPath("$[1].key").value("test-action-2"))

            .andExpect(jsonPath("$[0].jsonSchema.type").value("object"))
            .andExpect(jsonPath("$[0].jsonSchema.properties.value.type").value("number"))
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
        assert(triggers.size == 1)
        assert(triggers[0].title == "My Title")
        assert(triggers[0].key == "my_key")
    }

}
