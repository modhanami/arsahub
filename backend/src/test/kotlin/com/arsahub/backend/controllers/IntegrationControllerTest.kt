package com.arsahub.backend.controllers

import com.arsahub.backend.models.CustomUnit
import com.arsahub.backend.repositories.CustomUnitRepository
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
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
class IntegrationControllerTest {

    @Autowired
    private lateinit var customUnitRepository: CustomUnitRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withDatabaseName("arsahub")
            withUsername("user")
            withPassword("password")
            withCreateContainerCmdModifier { cmd ->
                cmd.withHostConfig(
                    HostConfig.newHostConfig().withPortBindings(
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

    @BeforeEach
    fun setUp() {
        customUnitRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `create custom unit should return 200 OK with the created custom unit`() {
        mockMvc.perform(
            post("/api/integrations/custom-units").contentType(MediaType.APPLICATION_JSON).content(
                """
                    {
                        "name": "Steps",
                        "key": "steps"
                    }
                """
            )
        ).andExpect(status().is2xxSuccessful).andExpect(jsonPath("$.name").value("Steps"))
            .andExpect(jsonPath("$.key").value("steps")).andExpect(jsonPath("$.id").isNumber)
    }

    @Test
    fun `when custom unit with the same key already exists, should return 409 Conflict`() {
        customUnitRepository.save(
            CustomUnit(
                name = "Steps", key = "steps"
            )
        )

        mockMvc.perform(
            post("/api/integrations/custom-units").contentType(MediaType.APPLICATION_JSON).content(
                """
                    {
                        "name": "Steps",
                        "key": "steps"
                    }
                """
            )
        ).andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Custom unit with key steps already exists"))
    }

}
