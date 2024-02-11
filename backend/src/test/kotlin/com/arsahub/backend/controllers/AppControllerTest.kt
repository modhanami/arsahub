package com.arsahub.backend.controllers

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.utils.AuthSetup
import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setGlobalAuthSetup
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.dtos.request.Action
import com.arsahub.backend.dtos.request.ActionDefinition
import com.arsahub.backend.dtos.request.AppUserCreateRequest
import com.arsahub.backend.dtos.request.FieldDefinition
import com.arsahub.backend.dtos.request.RuleCreateRequest
import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.dtos.request.TriggerDefinition
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.OncePerUserRuleRepeatability
import com.arsahub.backend.models.Reward
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleRepeatability
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.UnlimitedRuleRepeatability
import com.arsahub.backend.repositories.AchievementRepository
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.RewardRepository
import com.arsahub.backend.repositories.RuleRepository
import com.arsahub.backend.repositories.TransactionRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.AppService
import com.arsahub.backend.services.AuthService
import com.arsahub.backend.services.RuleService
import com.arsahub.backend.services.TriggerService
import com.corundumstudio.socketio.SocketIOServer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasEntry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
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
@ActiveProfiles("dev", "test")
@AutoConfigureMockMvc
@Transactional
class AppControllerTest() {
    @Autowired
    private lateinit var appRepository: AppRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var rewardRepository: RewardRepository

    @Autowired
    private lateinit var achievementRepository: AchievementRepository

    @Autowired
    private lateinit var ruleService: RuleService

    @Autowired
    private lateinit var triggerService: TriggerService

    private lateinit var authSetup: AuthSetup

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var ruleRepository: RuleRepository

    @Autowired
    private lateinit var appService: AppService

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    @Suppress("unused")
    private lateinit var socketIoServer: SocketIOServer // no-op

    @MockBean
    @Suppress("unused")
    private lateinit var socketIOService: SocketIOService // no-op

    @BeforeEach
    fun setUp() {
        authSetup =
            setupAuth(
                userRepository,
                appRepository,
            )
        setGlobalAuthSetup(authSetup)
    }

    data class TrigggerTestModel(
        val title: String?,
        val key: String?,
        val fields: List<FieldTestModel>? = null,
    )

    data class FieldTestModel(
        val type: String?,
        val key: String?,
        val label: String? = null,
    )

    @Suppress("unused")
    fun TrigggerTestModel.toNode(): ObjectNode {
        return mapper.valueToTree(this)
    }

    fun TrigggerTestModel.toJson(): String {
        return mapper.writeValueAsString(this)
    }

    @Test
    fun `returns list of app users with 200`() {
        // Arrange
        val appUser1 =
            AppUser(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                displayName = "User1",
                app = authSetup.app,
                points = 1000,
            )
        val appUser2 =
            AppUser(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000002").toString(),
                displayName = "User2",
                app = authSetup.app,
                points = 2000,
            )
        appUserRepository.saveAll(listOf(appUser1, appUser2))

        // Act & Assert
        mockMvc.performWithAppAuth(
            get("/api/apps/users"),
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
                    key = "workshop_completed",
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
                    conditions =
                        mutableMapOf(
                            "workshopId" to 1,
                            "source" to "trust me",
                        ),
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
                    key = "workshop_completed",
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
                    conditions =
                        mutableMapOf(
                            "workshopId" to 1,
                            "source" to "trust me",
                        ),
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
                    key = "workshop_completed",
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
              "conditions": {
                "workshopId": 1
              },
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
            .andExpect(jsonPath("$.conditions.workshopId").value(1))
            .andExpect(jsonPath("$.repeatability").value("unlimited"))

        // Assert DB
        val rules = ruleService.listRules(authSetup.app)
        assertEquals(1, rules.size)
        val rule = rules[0]
        assertEquals("When workshop ID 1 completed then add 100 points - unlimited", rule.title)
        assertEquals("workshop_completed", rule.trigger?.key)
        assertEquals("add_points", rule.action)
        assertEquals(100, rule.actionPoints)
        assertEquals(1, rule.conditions?.get("workshopId"))
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

    data class TriggerBuilder(
        var title: String? = null,
        var key: String? = null,
        var fields: MutableList<FieldBuilder> = mutableListOf(),
    ) {
        fun fields(customizer: FieldBuilder.() -> Unit = {}) {
            fields.add(FieldBuilder().apply(customizer))
        }
    }

    data class FieldBuilder(
        var type: String? = null,
        var key: String? = null,
        var label: String? = null,
    ) {
        private fun baseField(
            type: String,
            key: String,
            label: String? = null,
        ) {
            this.type = type
            this.key = key
            this.label = label
        }

        fun integer(
            key: String,
            label: String? = null,
        ) {
            baseField("integer", key, label)
        }

        fun text(
            key: String,
            label: String? = null,
        ) {
            baseField("text", key, label)
        }
    }

    fun createTrigger(
        app: App,
        customizer: TriggerBuilder.() -> Unit = {},
    ): Trigger {
        val builder = TriggerBuilder().apply(customizer)
        return triggerService.createTrigger(
            app,
            TriggerCreateRequest(
                title = builder.title!!,
                key = builder.key!!,
                fields =
                    builder.fields.map { field ->
                        FieldDefinition(
                            type = field.type!!,
                            key = field.key!!,
                            label = field.label,
                        )
                    },
            ),
        )
    }

    data class RuleBuilder(
        var trigger: Trigger? = null,
        var title: String? = null,
        var action: ActionBuilder? = null,
        var actionPoints: Int? = null,
        var conditions: MutableList<ConditionBuilder> = mutableListOf(),
        var repeatability: RuleRepeatability? = null,
    ) {
        fun action(customizer: ActionBuilder.() -> Unit = {}) {
            action = ActionBuilder().apply(customizer)
        }

        fun conditions(customizer: ConditionBuilder.() -> Unit = {}) {
            conditions.add(ConditionBuilder().apply(customizer))
        }
    }

    data class ActionBuilder(
        var key: String? = null,
        var params: MutableMap<String, Any>? = null,
    ) {
        fun addPoints(points: Int) {
            key = "add_points"
            params = mutableMapOf("points" to points)
        }

        fun unlockAchievement(achievementId: Long) {
            key = "unlock_achievement"
            params = mutableMapOf("achievementId" to achievementId)
        }
    }

    data class ConditionBuilder(
        var key: String? = null,
        var value: Any? = null,
    ) {
        fun eq(
            key: String,
            value: Any,
        ) {
            this.key = key
            this.value = value
        }
    }

    fun createRule(
        app: App,
        customizer: RuleBuilder.() -> Unit = {},
    ): Rule {
        val builder = RuleBuilder().apply(customizer)
        val rule =
            Rule(
                title = builder.title!!,
                trigger = builder.trigger!!,
                action = builder.action!!.key!!,
            )

        if (builder.action!!.params != null) {
            if (builder.action!!.key == "add_points") {
                rule.actionPoints = builder.action!!.params!!["points"] as Int
            }

            if (builder.action!!.key == "unlock_achievement") {
                val achievementId = builder.action!!.params!!["achievementId"] as Long
                val achievementReference = achievementRepository.getReferenceById(achievementId)
                rule.actionAchievement = achievementReference
            }
        }

        // TODO: update this when support for more operators is added
        rule.conditions = builder.conditions.associate { it.key!! to it.value!! }.toMutableMap()

        rule.repeatability = builder.repeatability!!.key

        return ruleService.createRule(
            app,
            RuleCreateRequest(
                title = builder.title!!,
                description = null,
                trigger =
                    TriggerDefinition(
                        key = builder.trigger!!.key!!,
                    ),
                action =
                    ActionDefinition(
                        key = builder.action!!.key!!,
                        params = builder.action!!.params,
                    ),
                conditions = builder.conditions.associate { it.key!! to it.value!! }.toMutableMap(),
                repeatability = builder.repeatability!!.key,
            ),
        )
    }

    fun createWorkshopCompletedTrigger(app: App): Trigger {
        return createTrigger(app) {
            key = "workshop_completed"
            title = "Workshop Completed"
            fields {
                integer("workshopId", "Workshop ID")
                text("source")
            }
        }
    }

    fun setupWorkshopCompletedRule(
        app: App,
        workshopIdEq: Int,
        sourceEq: String,
        points: Int,
        repeatability: RuleRepeatability = UnlimitedRuleRepeatability,
    ): WorkshopCompletedRule {
        val workshopCompletedTrigger = createWorkshopCompletedTrigger(authSetup.app)
        val workshopCompletedRule =
            createRule(app) {
                trigger = workshopCompletedTrigger
                title = "When workshop completed, add 100 points"
                action {
                    addPoints(points)
                }
                conditions {
                    eq("workshopId", workshopIdEq)
                    eq("source", sourceEq)
                }
                this.repeatability = repeatability
            }.let(::WorkshopCompletedRule)

        return workshopCompletedRule
    }

    /*
        Rule wrappers for getting matching and non-matching request body contents
        Must not directly convert dynamic fields in the model to JSON, to ensure regression when model changes
        Refer to https://programmerfriend.com/biggest-antipattern-webmvc-tests/
     */

    @JvmInline
    value class WorkshopCompletedRule(private val rule: Rule) {
        fun toMatchingRequestBody(
            appUser: AppUser,
            objectMapper: ObjectMapper,
        ): String {
            val matchingParams = mutableMapOf<String, Any>()

            if (rule.conditions?.containsKey("workshopId") == true) {
                matchingParams["workshopId"] = rule.conditions!!["workshopId"]!!
            }

            if (rule.conditions?.containsKey("source") == true) {
                matchingParams["source"] = rule.conditions!!["source"]!!
            }

            val paramsJson = objectMapper.writeValueAsString(matchingParams)

            return """
                {
                    "key": "${rule.trigger!!.key}",
                    "params": $paramsJson,
                    "userId": "${appUser.userId}"
                }
                """.trimIndent()
        }

        fun toNonMatchingRequestBody(
            appUser: AppUser,
            objectMapper: ObjectMapper,
        ): String {
            val nonMatchingParams = mutableMapOf<String, Any>()
            nonMatchingParams["workshopId"] = 0
            nonMatchingParams["source"] = "__non_matching__"

            val paramsJson = objectMapper.writeValueAsString(nonMatchingParams)

            return """
                {
                    "key": "${rule.trigger!!.key}",
                    "params": $paramsJson,
                    "userId": "${appUser.userId}"
                }
                """.trimIndent()
        }
    }

    fun createAppUser(
        app: App,
        userId: String = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
    ): AppUser {
        val appUser =
            appService.addUser(
                app,
                AppUserCreateRequest(
                    uniqueId = userId,
                    displayName = "User $userId",
                ),
            )

        return appUser
    }

    // Matching rules
    @Test
    fun `fires matching rules - for the given user ID in the app`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rule.toMatchingRequestBody(user, mapper)),
        )
            .andExpect(status().isOk)

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)
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
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100)

        val otherUser =
            createAppUser(authSetup.app, userId = UUID.randomUUID().toString())

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rule.toMatchingRequestBody(user, mapper)),
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
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100)

        val otherApp = setupAuth(userRepository, appRepository).app
        val otherUser = createAppUser(otherApp)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rule.toMatchingRequestBody(user, mapper)),
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
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100)

        val otherApp = setupAuth(userRepository, appRepository).app
        val otherUser1 =
            createAppUser(otherApp, userId = UUID.randomUUID().toString())
        val otherUser2 = createAppUser(otherApp, userId = UUID.randomUUID().toString())

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rule.toMatchingRequestBody(user, mapper)),
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
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100)

        // Act & Assert
        repeat(2) {
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(rule.toMatchingRequestBody(user, mapper)),
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
        val rule = setupWorkshopCompletedRule(authSetup.app, 1, "trust me", 100, OncePerUserRuleRepeatability)

        // Act & Assert
        repeat(2) {
            mockMvc.performWithAppAuth(
                post("/api/apps/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(rule.toMatchingRequestBody(user, mapper)),
            )
                .andExpect(status().isOk)
        }

        // Assert DB
        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)
    }

    // More rules validation

    // Disallow empty condition key or value
    @Test
    fun `fails with 400 when creating a rule with empty condition key`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
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
                      "conditions": {
                        "": 1
                      },
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Condition key cannot be empty"))
    }

    @Test
    fun `fails with 400 when creating a rule with empty condition value`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
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
                      "conditions": {
                        "workshopId": ""
                      },
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Condition value cannot be empty"))
    }

    @Test
    fun `fails with 400 when creating a rule with null condition value`() {
        // Arrange
        val trigger = createWorkshopCompletedTrigger(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
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
                      "conditions": {
                        "workshopId": null
                      },
                      "repeatability": "unlimited"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Condition value cannot be empty"))
    }

    // Points Shop

    // Redeem points

    @Test
    fun `redeem points - success`() {
        // Arrange
        val appUserWith100Points =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 100,
                ),
            )

        val reward10Points =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 10,
                    app = authSetup.app,
                ),
            )

        // Act & Assert HTTP
        val resultActions =
            mockMvc.performWithAppAuth(
                post("/api/apps/shop/rewards/redeem")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "rewardId": ${reward10Points.id},
                            "userId": "${appUserWith100Points.userId}"
                        }
                        """.trimIndent(),
                    ),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pointsSpent").value(10))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.referenceNumber").exists())

        // Assert DB
        // Assert reward quantity
        val reward10PointsAfter = rewardRepository.findById(reward10Points.id!!).get()
        assertEquals(9, reward10PointsAfter.quantity)

        // Assert user points
        val appUserWith100PointsAfter = appUserRepository.findById(appUserWith100Points.id!!).get()
        assertEquals(90, appUserWith100PointsAfter.points)

        // Assert transaction created
        val transaction = transactionRepository.findAll().first()
        assertEquals(10, transaction.pointsSpent)

        // Assert reward redeemed
        val rewardRedeemed = transaction.reward!!
        assertEquals("10 Points", rewardRedeemed.name)

        // Assert transaction reference number
        val transactionReferenceNumber = transaction.referenceNumber
        assertDoesNotThrow { UUID.fromString(transactionReferenceNumber) }
        resultActions.andExpect(jsonPath("$.referenceNumber").value(transactionReferenceNumber))
    }

    @Test
    fun `redeem points - not enough points`() {
        // Arrange
        val appUserWith1Points =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 1,
                ),
            )

        val reward10Points =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 10,
                    app = authSetup.app,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "rewardId": ${reward10Points.id},
                        "userId": "${appUserWith1Points.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Insufficient points"))
    }

    @Test
    fun `redeem points - out of stock`() {
        // Arrange
        val appUserWith100Points =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 100,
                ),
            )

        val reward10Points =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 0,
                    app = authSetup.app,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "rewardId": ${reward10Points.id},
                        "userId": "${appUserWith100Points.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Reward unavailable"))
    }

    @Test
    fun `redeem points - invalid reward ID`() {
        // Arrange
        val appUserWith100Points =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 100,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "rewardId": 999999999,
                        "userId": "${appUserWith100Points.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Reward not found"))
    }

    // Create reward

    @Test
    fun `create reward - success`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "price": 10,
                "quantity": 10
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("10 Points"))
            .andExpect(jsonPath("$.price").value(10))
            .andExpect(jsonPath("$.quantity").value(10))

        // Assert DB
        val rewards = rewardRepository.findAll()
        assertEquals(1, rewards.size)
        val reward = rewards[0]
        assertEquals("10 Points", reward.name)
        assertEquals(10, reward.price)
        assertEquals(10, reward.quantity)
    }

    @Test
    fun `create reward - success - name and description are trimmed`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": " 10 Points ",
                "description": " 10 Points ",
                "price": 10,
                "quantity": 10
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("10 Points"))
            .andExpect(jsonPath("$.description").value("10 Points"))
            .andExpect(jsonPath("$.price").value(10))
            .andExpect(jsonPath("$.quantity").value(10))

        // Assert DB
        val rewards = rewardRepository.findAll()
        assertEquals(1, rewards.size)
        val reward = rewards[0]
        assertEquals("10 Points", reward.name)
        assertEquals("10 Points", reward.description)
        assertEquals(10, reward.price)
        assertEquals(10, reward.quantity)
    }

    @Test
    fun `create reward - success - no quantity`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "description": "10 Points",
                "price": 10,
                "quantity": null
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("10 Points"))
            .andExpect(jsonPath("$.description").value("10 Points"))
            .andExpect(jsonPath("$.price").value(10))
            .andExpect(jsonPath("$.quantity").value(null))

        // Assert DB
        val rewards = rewardRepository.findAll()
        assertEquals(1, rewards.size)
        val reward = rewards[0]
        assertEquals("10 Points", reward.name)
        assertEquals("10 Points", reward.description)
        assertEquals(10, reward.price)
        assertEquals(null, reward.quantity)
    }

    @Test
    fun `create reward - failed - invalid price`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "description": "10 Points",
                "price": -1,
                "quantity": 10
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Price must be positive"))
    }

    @Test
    fun `create reward - failed - invalid quantity`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "description": "10 Points",
                "price": 10,
                "quantity": -1
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Quantity must be positive"))
    }

    @Test
    fun `create reward - failed - duplicate name`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "description": "10 Points",
                "price": 10,
                "quantity": 10
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("10 Points"))
            .andExpect(jsonPath("$.description").value("10 Points"))
            .andExpect(jsonPath("$.price").value(10))
            .andExpect(jsonPath("$.quantity").value(10))

        // Assert DB
        val rewards = rewardRepository.findAll()
        assertEquals(1, rewards.size)
        val reward = rewards[0]
        assertEquals("10 Points", reward.name)
        assertEquals("10 Points", reward.description)
        assertEquals(10, reward.price)
        assertEquals(10, reward.quantity)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Reward with the same name already exists"))
    }

    // Get rewards

    @Test
    fun `get rewards - success - one reward`() {
        // Arrange
        val reward =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 10,
                    app = authSetup.app,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            get("/api/apps/shop/rewards"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].name").value("10 Points"))
            .andExpect(jsonPath("$[0].description").value("10 Points"))
            .andExpect(jsonPath("$[0].price").value(10))
            .andExpect(jsonPath("$[0].quantity").value(10))
    }

    @Test
    fun `get rewards - success - only rewards for the given app`() {
        // Arrange
        val reward1 =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 10,
                    app = authSetup.app,
                ),
            )

        val reward2 =
            rewardRepository.save(
                Reward(
                    name = "20 Points",
                    description = "20 Points",
                    price = 20,
                    quantity = 20,
                    app = authSetup.app,
                ),
            )

        val otherApp = setupAuth(userRepository, appRepository).app
        val otherAppReward =
            rewardRepository.save(
                Reward(
                    name = "30 Points",
                    description = "30 Points",
                    price = 30,
                    quantity = 30,
                    app = otherApp,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            get("/api/apps/shop/rewards"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(
                jsonPath(
                    "$",
                    containsInAnyOrder(
                        hasEntry(
                            "id",
                            reward1.id!!.toInt(),
                        ),
                        hasEntry(
                            "id",
                            reward2.id!!.toInt(),
                        ),
                    ),
                ),
            )
    }

    // TODO: Implement forward-chaining and support new triggers, e.g., when a user reaches 100 points, etc.
    @Test
    @Disabled
    fun testForwardChainingRuleAtoRuleB() {
    }

    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<Nothing> =
            PostgreSQLContainer<Nothing>("postgres:16-alpine").withInitScript("schema.sql")
    }
}
