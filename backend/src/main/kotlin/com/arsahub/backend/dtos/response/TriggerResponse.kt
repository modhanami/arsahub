package com.arsahub.backend.dtos.response

import com.arsahub.backend.dtos.request.FieldDefinition
import java.time.Instant

/**
 * DTO for {@link com.arsahub.backend.models.Trigger}
 */
data class TriggerResponse(
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val title: String? = null,
    val description: String? = null,
    val key: String? = null,
    val id: Long? = null,
    val fields: List<FieldDefinition>? = null,
) {
    companion object {
        fun fromEntity(trigger: com.arsahub.backend.models.Trigger): TriggerResponse {
            return TriggerResponse(
                createdAt = trigger.createdAt,
                updatedAt = trigger.updatedAt,
                title = trigger.title,
                description = trigger.description,
                key = trigger.key,
                id = trigger.id,
                fields = trigger.fields.map { FieldDefinition.fromEntity(it) },
            )
        }
    }
}
