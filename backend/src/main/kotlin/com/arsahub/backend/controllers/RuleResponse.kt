package com.arsahub.backend.controllers

import java.time.Instant

/**
 * DTO for {@link com.arsahub.backend.models.Rule}
 */
data class RuleResponse(
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val title: String? = null,
    val description: String? = null,
    val trigger: TriggerResponse? = null,
    val action: ActionResponse? = null,
    val triggerTypeParams: MutableMap<String, Any>? = null,
    val actionParams: MutableMap<String, Any>? = null,
    val id: Long? = null
) {


    companion object {
        fun fromEntity(rule: com.arsahub.backend.models.Rule): RuleResponse {
            return RuleResponse(
                createdAt = rule.createdAt,
                updatedAt = rule.updatedAt,
                title = rule.title,
                description = rule.description,
                trigger = rule.trigger?.let { TriggerResponse.fromEntity(it) },
                action = rule.action?.let { ActionResponse.fromEntity(it) },
                triggerTypeParams = rule.triggerTypeParams,
                actionParams = rule.actionParams,
                id = rule.id
            )
        }
    }
}

/**
 * DTO for {@link com.arsahub.backend.models.Trigger}
 */
data class TriggerResponse(
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val title: String? = null,
    val description: String? = null,
    val key: String? = null,
    val id: Long? = null
) {
    companion object {
        fun fromEntity(trigger: com.arsahub.backend.models.Trigger): TriggerResponse {
            return TriggerResponse(
                createdAt = trigger.createdAt,
                updatedAt = trigger.updatedAt,
                title = trigger.title,
                description = trigger.description,
                key = trigger.key,
                id = trigger.id
            )
        }
    }
}

/**
 * DTO for {@link com.arsahub.backend.models.Action}
 */
data class ActionResponse(
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val title: String? = null,
    val description: String? = null,
    val jsonSchema: MutableMap<String, Any>? = null,
    val key: String? = null,
    val id: Long? = null
) {
    companion object {
        fun fromEntity(action: com.arsahub.backend.models.Action): ActionResponse {
            return ActionResponse(
                createdAt = action.createdAt,
                updatedAt = action.updatedAt,
                title = action.title,
                description = action.description,
                jsonSchema = action.jsonSchema,
                key = action.key,
                id = action.id
            )
        }
    }
}