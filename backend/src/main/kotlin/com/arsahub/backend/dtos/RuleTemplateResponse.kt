package com.arsahub.backend.dtos

/**
 * DTO for {@link com.arsahub.backend.models.RuleTemplate}
 */
data class RuleTemplateResponse(
    val name: String? = null,
    val description: String? = null,
    val trigger: TriggerResponse? = null,
    val action: ActionResponse? = null,
    val externalSystem: IntegrationResponse? = null,
    val effectParams: MutableMap<String, Any>? = null,
    val triggerParams: MutableMap<String, Any>? = null,
    val id: Long? = null
) {
    companion object {
        fun fromEntity(ruleTemplate: com.arsahub.backend.models.RuleTemplate): RuleTemplateResponse {
            return RuleTemplateResponse(
                name = ruleTemplate.name,
                description = ruleTemplate.description,
                trigger = TriggerResponse.fromEntity(ruleTemplate.trigger!!),
                action = ActionResponse.fromEntity(ruleTemplate.action!!),
                externalSystem = IntegrationResponse.fromEntity(ruleTemplate.externalSystem!!),
                effectParams = ruleTemplate.effectParams,
                triggerParams = ruleTemplate.triggerParams,
                id = ruleTemplate.id
            )
        }
    }
}