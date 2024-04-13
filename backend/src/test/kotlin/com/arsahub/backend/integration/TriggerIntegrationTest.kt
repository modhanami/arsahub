// package com.arsahub.backend.integration
//
// import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
// import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
// import com.arsahub.backend.models.UnlimitedRuleRepeatability
// import com.arsahub.backend.repositories.AppRepository
// import com.arsahub.backend.repositories.TriggerRepository
// import com.arsahub.backend.repositories.UserRepository
// import com.arsahub.backend.services.TriggerService
// import org.junit.jupiter.api.Assertions.assertEquals
// import org.junit.jupiter.api.Assertions.assertNotNull
// import org.junit.jupiter.api.Assertions.assertTrue
// import org.junit.jupiter.api.BeforeEach
// import org.junit.jupiter.api.Test
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.testcontainers.service.connection.ServiceConnection
// import org.springframework.http.MediaType
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
// import org.testcontainers.containers.PostgreSQLContainer
// import org.testcontainers.junit.jupiter.Container
//
// class TriggerIntegrationTest : BaseIntegrationTest() {
//    @Autowired
//    private lateinit var triggerRepository: TriggerRepository
//
//    @Autowired
//    private lateinit var appRepository: AppRepository
//
//    @Autowired
//    private lateinit var triggerService: TriggerService
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    fun TrigggerTestModel.toJson(): String {
//        return mapper.writeValueAsString(this)
//    }
//
//    @Test
//    fun `creates a trigger with 201`() {
//        // Arrange
//        val jsonBody =
//            TrigggerTestModel(
//                title = "Workshop Completed",
//                key = "workshop_completed",
//                fields =
//                    listOf(
//                        FieldTestModel(
//                            type = "integer",
//                            key = "workshopId",
//                            label = "Workshop ID",
//                        ),
//                        FieldTestModel(
//                            type = "text",
//                            key = "source",
//                        ),
//                    ),
//            ).toJson()
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonBody),
//        )
//            .andExpect(status().isCreated)
//            .andExpect(jsonPath("$.title").value("Workshop Completed"))
//            .andExpect(jsonPath("$.key").value("workshop_completed"))
//            .andExpect(jsonPath("$.fields.length()").value(2))
//            .andExpect(jsonPath("$.fields[0].type").value("integer"))
//            .andExpect(jsonPath("$.fields[0].key").value("workshopId"))
//            .andExpect(jsonPath("$.fields[0].label").value("Workshop ID"))
//            .andExpect(jsonPath("$.fields[1].type").value("text"))
//            .andExpect(jsonPath("$.fields[1].key").value("source"))
//
//        // Assert DB
//        val triggers = triggerService.getTriggers(authSetup.app)
//        assertEquals(1, triggers.size)
//        val trigger = triggers[0]
//        assertEquals("Workshop Completed", trigger.title)
//        assertEquals("workshop_completed", trigger.key)
//        assertEquals(2, trigger.fields.size)
//        val workshopIdField = trigger.fields.find { it.key == "workshopId" }
//        require(workshopIdField != null)
//        assertEquals("integer", workshopIdField.type)
//        assertEquals("Workshop ID", workshopIdField.label)
//
//        val sourceField = trigger.fields.find { it.key == "source" }
//        require(sourceField != null)
//        assertEquals("text", sourceField.type)
//        assertEquals("source", sourceField.key)
//    }
//
//    @Test
//    fun `fails with 400 when creating a trigger with title less than 4 characters`() {
//        // Arrange
//        val jsonBody =
//            TrigggerTestModel(
//                title = "Wor",
//                key = "workshop_completed",
//                fields =
//                    listOf(
//                        FieldTestModel(
//                            type = "integer",
//                            key = "workshopId",
//                            label = "Workshop ID",
//                        ),
//                        FieldTestModel(
//                            type = "text",
//                            key = "source",
//                        ),
//                    ),
//            ).toJson()
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonBody),
//        )
//            .andExpect(status().isBadRequest)
//            .andExpect(jsonPath("$.errors.title").value("Title must be between 4 and 200 characters"))
//    }
//
//    @Test
//    fun `fails with 400 when creating a trigger without title`() {
//        // Arrange
//        val jsonBody =
//            TrigggerTestModel(
//                // TODO: evaluate if absent fields should be interpreted the same as null (right now it is)
//                title = null,
//                key = "workshop_completed",
//                fields =
//                    listOf(
//                        FieldTestModel(
//                            type = "integer",
//                            key = "workshopId",
//                            label = "Workshop ID",
//                        ),
//                        FieldTestModel(
//                            type = "text",
//                            key = "source",
//                        ),
//                    ),
//            ).toJson()
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonBody),
//        )
//            .andExpect(status().isBadRequest)
//            .andExpect(jsonPath("$.errors.title").value("Title is required"))
//    }
//
//    @Test
//    fun `fails with 400 when creating a trigger with invalid field types`() {
//        // Arrange
//        val jsonBody =
//            TrigggerTestModel(
//                title = "Workshop Completed",
//                key = "workshop_completed",
//                fields =
//                    listOf(
//                        FieldTestModel(
//                            type = "integer",
//                            key = "workshopId",
//                            label = "Workshop ID",
//                        ),
//                        FieldTestModel(
//                            type = "not_a_valid_type",
//                            key = "source",
//                        ),
//                    ),
//            ).toJson()
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonBody),
//        )
//            .andExpect(status().isBadRequest)
//            .andExpect(jsonPath("$.message").value("Invalid field type: not_a_valid_type"))
//    }
//
//    @Test
//    fun `create trigger - success`() {
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "title": "Workshop completed"
//                    }
//                    """.trimIndent(),
//                ),
//        )
//            .andExpect(status().isCreated)
//
//        // Assert DB
//        val triggers = triggerRepository.findAll()
//        val trigger = triggers.first { it.title == "Workshop completed" }
//        assertEquals("Workshop completed", trigger.title)
//        assertEquals("workshop_completed", trigger.key)
//        assertEquals(authSetup.app.id, trigger.app?.id)
//    }
//
//    @Test
//    fun `create triggers with same title in two different apps - success`() {
//        // Arrange
//        val otherApp = setupAuth(userRepository, appRepository).app
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "title": "Workshop completed"
//                    }
//                    """.trimIndent(),
//                ),
//        )
//            .andExpect(status().isCreated)
//
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "title": "Workshop completed"
//                    }
//                    """.trimIndent(),
//                ),
//            app = otherApp,
//        )
//            .andExpect(status().isCreated)
//
//        // Assert DB
//        val triggers = triggerRepository.findAll()
//        val app1Trigger = triggers.first { it.app?.id == authSetup.app.id }
//        assertNotNull(app1Trigger)
//        val app2Trigger = triggers.first { it.app?.id == otherApp.id }
//        assertNotNull(app2Trigger)
//    }
//
//    @Test
//    fun `create triggers with same title in the same app - failed`() {
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "title": "Workshop   completed  "
//                    }
//                    """.trimIndent(),
//                ),
//        )
//            .andExpect(status().isCreated)
//
//        mockMvc.performWithAppAuth(
//            post("/api/apps/triggers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "title": "Workshop completed"
//                    }
//                    """.trimIndent(),
//                ),
//        )
//            .andExpect(status().isConflict)
//            .andExpect(jsonPath("$.message").value("Trigger with the same title already exists"))
//    }
//
//    // Deletion
//
//    // Delete trigger: Only when no rules are using it
//    @Test
//    fun `delete trigger - success`() {
//        // Arrange
//        val trigger = createWorkshopCompletedTrigger(authSetup.app)
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            delete("/api/apps/triggers/${trigger.id}"),
//        )
//            .andExpect(status().isNoContent)
//
//        // Assert DB
//        val triggers = triggerRepository.findById(trigger.id!!)
//        assertTrue(triggers.isEmpty)
//    }
//
//    @Test
//    fun `delete trigger - failed - rules are using it`() {
//        // Arrange
//        val subjectTrigger = createWorkshopCompletedTrigger(authSetup.app)
//        val rule =
//            createRule(authSetup.app) {
//                title = "When workshop completed then add 100 points"
//                trigger = subjectTrigger
//                action {
//                    addPoints(100)
//                }
//                repeatability = UnlimitedRuleRepeatability
//            }
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            delete("/api/apps/triggers/${subjectTrigger.id}"),
//        )
//            .andExpect(status().isConflict)
//            .andExpect(jsonPath("$.message").value("Trigger is used by one or more rules"))
//
//        // Assert DB
//        val triggers = triggerRepository.findById(subjectTrigger.id!!)
//        assertTrue(triggers.isPresent)
//    }
//
//    @Test
//    fun `delete trigger - unauthorized - different app`() {
//        // Arrange
//        val otherApp = setupAuth(userRepository, appRepository).app
//        val trigger = createWorkshopCompletedTrigger(authSetup.app)
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            delete("/api/apps/triggers/${trigger.id}"),
//            app = otherApp,
//        )
//            .andExpect(status().isNotFound)
//            .andExpect(jsonPath("$.message").value("Trigger not found"))
//
//        // Assert DB
//        val triggers = triggerRepository.findById(trigger.id!!)
//        assertTrue(triggers.isPresent)
//    }
//
//    // Edit trigger: Only title and description
//    @Test
//    fun `edit trigger - success`() {
//        // Arrange
//        val trigger = createWorkshopCompletedTrigger(authSetup.app)
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            patch("/api/apps/triggers/${trigger.id}")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "title": "Updated Title",
//                      "description": "Updated Description"
//                    }
//                    """.trimIndent(),
//                ),
//        )
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.title").value("Updated Title"))
//            .andExpect(jsonPath("$.description").value("Updated Description"))
//
//        // Assert DB
//        val triggers = triggerRepository.findById(trigger.id!!)
//        assertTrue(triggers.isPresent)
//        assertEquals("Updated Title", triggers.get().title)
//        assertEquals("Updated Description", triggers.get().description)
//    }
//
//    @Test
//    fun `edit trigger - success - title only`() {
//        // Arrange
//        val trigger = createWorkshopCompletedTrigger(authSetup.app)
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            patch("/api/apps/triggers/${trigger.id}")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "title": "Updated Title"
//                    }
//                    """.trimIndent(),
//                ),
//        )
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.title").value("Updated Title"))
//            .andExpect(jsonPath("$.description").value("When a workshop is completed"))
//
//        // Assert DB
//        val triggers = triggerRepository.findById(trigger.id!!)
//        assertTrue(triggers.isPresent)
//        assertEquals("Updated Title", triggers.get().title)
//        assertEquals("When a workshop is completed", triggers.get().description)
//    }
//
//    @Test
//    fun `edit trigger - success - description only`() {
//        // Arrange
//        val trigger = createWorkshopCompletedTrigger(authSetup.app)
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            patch("/api/apps/triggers/${trigger.id}")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "description": "Updated Description"
//                    }
//                    """.trimIndent(),
//                ),
//        )
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.title").value("Workshop Completed"))
//            .andExpect(jsonPath("$.description").value("Updated Description"))
//
//        // Assert DB
//        val triggers = triggerRepository.findById(trigger.id!!)
//        assertTrue(triggers.isPresent)
//        assertEquals("Workshop Completed", triggers.get().title)
//        assertEquals("Updated Description", triggers.get().description)
//    }
//
//    @Test
//    fun `edit trigger - failed - different app`() {
//        // Arrange
//        val otherApp = setupAuth(userRepository, appRepository).app
//        val trigger = createWorkshopCompletedTrigger(authSetup.app)
//
//        // Act & Assert HTTP
//        mockMvc.performWithAppAuth(
//            patch("/api/apps/triggers/${trigger.id}")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                    {
//                      "title": "Updated Title",
//                      "description": "Updated Description"
//                    }
//                    """.trimIndent(),
//                ),
//            app = otherApp,
//        )
//            .andExpect(status().isNotFound)
//            .andExpect(jsonPath("$.message").value("Trigger not found"))
//
//        // Assert DB
//        val triggers = triggerRepository.findById(trigger.id!!)
//        assertTrue(triggers.isPresent)
//    }
//
//    @BeforeEach
//    fun setUp() {
//        initIntegrationTest(postgres)
//    }
//
//    companion object {
//        @Container
//        @ServiceConnection
//        val postgres: PostgreSQLContainer<Nothing> =
//            setupDBContainer().apply { start() }
//    }
// }
