package com.arsahub.backend.dtos.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class TriggerCreateRequest(
    @field:Size(min = 4, max = 200, message = "Title must be between 4 and 200 characters")
    @field:NotBlank(message = "Title is required")
    val title: String?,

    @field:Size(min = 4, max = 200, message = "Key must be between 4 and 200 characters")
    @field:NotBlank(message = "Key is required")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_-]*\$",
        message = "Key must contain only alphanumeric characters, underscores and dashes"
    )
    val key: String?,

    @field:Size(max = 500, message = "Description cannot be longer than 500 characters")
    val description: String? = null,

    val fields: List<FieldDefinition>? = null,
)

data class FieldDefinition(
    @field:Size(min = 4, max = 200, message = "Key must be between 4 and 200 characters")
    @field:NotBlank(message = "Key is required")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_-]*\$",
        message = "Key must contain only alphanumeric characters, underscores and dashes"
    )
    val key: String?,

    @field:NotBlank(message = "Type is required")
    val type: String?,

    @field:Size(min = 4, max = 200, message = "Label must be between 4 and 200 characters")
    val label: String? = null,

    ) {
    companion object {
        fun fromEntity(field: com.arsahub.backend.models.TriggerField): FieldDefinition {
            return FieldDefinition(
                key = field.key,
                label = field.label,
                type = field.type,
            )
        }
    }
}