package com.arsahub.backend.dtos.response

import java.time.Instant

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