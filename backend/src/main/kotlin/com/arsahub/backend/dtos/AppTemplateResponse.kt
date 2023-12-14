package com.arsahub.backend.dtos

/**
 * DTO for {@link com.arsahub.backend.models.AppTemplate}
 */
data class AppTemplateResponse(
    val name: String? = null,
    val description: String? = null,
    val triggerTemplates: MutableSet<TriggerTemplateResponse> = mutableSetOf(),
    val id: Long? = null
) {
    companion object {
        fun fromEntity(appTemplate: com.arsahub.backend.models.AppTemplate): AppTemplateResponse {
            return AppTemplateResponse(
                name = appTemplate.name,
                description = appTemplate.description,
                triggerTemplates = appTemplate.triggerTemplates.map { TriggerTemplateResponse.fromEntity(it) }
                    .toMutableSet(),
                id = appTemplate.id
            )
        }
    }
}