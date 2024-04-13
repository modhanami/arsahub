package com.arsahub.backend.controllers

import com.arsahub.backend.controllers.utils.AuthSetup
import com.arsahub.backend.dtos.request.ActionDefinition
import com.arsahub.backend.dtos.request.AppUserCreateRequest
import com.arsahub.backend.dtos.request.FieldDefinition
import com.arsahub.backend.dtos.request.RuleCreateRequest
import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.dtos.request.TriggerDefinition
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleRepeatability
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.UnlimitedRuleRepeatability
import com.arsahub.backend.services.AppService
import com.arsahub.backend.services.RuleService
import com.arsahub.backend.services.TriggerService
import com.arsahub.backend.services.WebhookDeliveryService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.transaction.TestTransaction
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
@Transactional
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@EmbeddedKafka(
    topics = [WebhookDeliveryService.Topics.WEBHOOK_DELIVERIES],
    partitions = 1,
)
class BaseIntegrationTest {
    @Autowired
    private lateinit var appService: AppService

    @Autowired
    private lateinit var ruleService: RuleService

    @Autowired
    private lateinit var triggerService: TriggerService

    protected lateinit var authSetup: AuthSetup

    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<Nothing> =
            PostgreSQLContainer<Nothing>("postgres:16-alpine")
                .withReuse(true)
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
                description = builder.description,
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

    fun createRule(
        app: App,
        customizer: RuleBuilder.() -> Unit = {},
    ): Rule {
        val builder = RuleBuilder().apply(customizer)

        return ruleService.createRule(
            app,
            RuleCreateRequest(
                title = builder.title,
                description = builder.description,
                trigger =
                    TriggerDefinition(
                        key = builder.trigger!!.key!!,
                    ),
                action =
                    ActionDefinition(
                        key = builder.action!!.key!!,
                        params = builder.action!!.params,
                    ),
                repeatability = builder.repeatability!!.key,
                conditionExpression = builder.conditionExpression,
                accumulatedFields = builder.accumulatedFields,
            ),
        )
    }

    fun createWorkshopCompletedTrigger(
        app: App,
        customizer: TriggerBuilder.() -> Unit = {},
    ): Trigger {
        return createTrigger(app) {
            key = "workshop_completed"
            title = "Workshop Completed"
            description = "When a workshop is completed"
            fields {
                integer("workshopId", "Workshop ID")
                text("source")
            }
            apply(customizer)
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
                conditionExpression = "workshopId == $workshopIdEq && source == '$sourceEq'"
                this.repeatability = repeatability
            }.let(::WorkshopCompletedRule)

        return workshopCompletedRule
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

    class TriggerBuilder(
        var title: String? = null,
        var description: String? = null,
        var key: String? = null,
        val fields: MutableList<Field> = mutableListOf(),
    ) {
        fun fields(customizer: FieldsDsl.() -> Unit = {}) {
            FieldsDsl().apply(customizer)
        }

        inner class Field(
            var type: String? = null,
            var key: String? = null,
            var label: String? = null,
        )

        inner class FieldsDsl {
            private fun baseField(
                type: String,
                key: String,
                label: String? = null,
            ) {
                Field(
                    type = type,
                    key = key,
                    label = label,
                ).also { fields.add(it) }
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

            fun integerSet(
                key: String,
                label: String? = null,
            ) {
                baseField("integerSet", key, label)
            }

            fun textSet(
                key: String,
                label: String? = null,
            ) {
                baseField("textSet", key, label)
            }
        }
    }

    data class RuleBuilder(
        var trigger: Trigger? = null,
        var title: String? = null,
        var description: String? = null,
        var action: ActionBuilder? = null,
        var actionPoints: Int? = null,
        var repeatability: RuleRepeatability? = null,
        var conditionExpression: String? = null,
        var accumulatedFields: List<String>? = null,
    ) {
        fun action(customizer: ActionBuilder.() -> Unit = {}) {
            action = ActionBuilder().apply(customizer)
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

    /**
     * Rule wrappers for getting matching and non-matching request body contents
     * Must not directly convert dynamic fields in the model to JSON, to ensure regression when model changes
     * Refer to https://programmerfriend.com/biggest-antipattern-webmvc-tests/
     */
    @JvmInline
    value class WorkshopCompletedRule(val rule: Rule) {
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
}

fun forceNewTransaction() {
    TestTransaction.flagForCommit()
    TestTransaction.end()
    TestTransaction.start()
}
