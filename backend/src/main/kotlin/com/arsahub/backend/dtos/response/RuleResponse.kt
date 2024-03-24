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
    val action: String? = null,
    val actionPoints: Int? = null,
    val actionAchievement: AchievementResponse? = null,
    val id: Long? = null,
    val repeatability: String? = null,
    val conditionExpression: String? = null,
    val accumulatedFields: Array<String>? = null,
) {
    companion object {
        fun fromEntity(rule: com.arsahub.backend.models.Rule): RuleResponse {
            return RuleResponse(
                createdAt = rule.createdAt,
                updatedAt = rule.updatedAt,
                title = rule.title,
                description = rule.description,
                trigger = rule.trigger?.let { TriggerResponse.fromEntity(it) },
                action = rule.action,
                actionPoints = rule.actionPoints,
                actionAchievement = rule.actionAchievement?.let { AchievementResponse.fromEntity(it) },
                id = rule.id,
                repeatability = rule.repeatability,
                conditionExpression = rule.conditionExpression,
                accumulatedFields = rule.accumulatedFields,
            )
        }
    }
}
