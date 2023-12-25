package com.arsahub.backend.dtos.response

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
                actionParams = rule.actionParams,
                id = rule.id
            )
        }
    }
}

