package com.arsahub.backend.dtos

/**
 * DTO for {@link com.arsahub.backend.models.IntegrationTemplate}
 */
data class IntegrationTemplateResponse(
    val name: String? = null,
    val description: String? = null,
    val triggerTemplates: MutableSet<TriggerTemplateResponse> = mutableSetOf(),
    val id: Long? = null
) {
    companion object {
        fun fromEntity(integrationTemplate: com.arsahub.backend.models.IntegrationTemplate): IntegrationTemplateResponse {
            return IntegrationTemplateResponse(
                name = integrationTemplate.name,
                description = integrationTemplate.description,
                triggerTemplates = integrationTemplate.triggerTemplates.map { TriggerTemplateResponse.fromEntity(it) }
                    .toMutableSet(),
                id = integrationTemplate.id
            )
        }
    }
}