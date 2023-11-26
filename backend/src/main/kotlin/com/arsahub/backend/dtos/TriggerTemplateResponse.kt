package com.arsahub.backend.dtos

/**
 * DTO for {@link com.arsahub.backend.models.TriggerTemplate}
 */
data class TriggerTemplateResponse(
    val title: String? = null,
    val description: String? = null,
    val key: String? = null,
    val jsonSchema: MutableMap<String, Any>? = null,
    val id: Long? = null
) {
    companion object {
        fun fromEntity(triggerTemplate: com.arsahub.backend.models.TriggerTemplate): TriggerTemplateResponse {
            return TriggerTemplateResponse(
                title = triggerTemplate.title,
                description = triggerTemplate.description,
                key = triggerTemplate.key,
                jsonSchema = triggerTemplate.jsonSchema,
                id = triggerTemplate.id
            )
        }
    }
}