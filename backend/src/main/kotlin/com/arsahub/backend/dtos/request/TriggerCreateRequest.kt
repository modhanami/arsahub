package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidKey
import com.arsahub.backend.dtos.annotations.ValidTitle
import com.arsahub.backend.extensions.trimmed
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class TriggerCreateRequest(
    title: String?,

    key: String?,

    description: String? = null,

    val fields: List<FieldDefinition>? = null,
) {
    @ValidTitle
    val title: String? = title.trimmed()

    @ValidKey
    val key: String? = key.trimmed()

    @ValidDescription
    val description: String? = description.trimmed()
}

data class FieldDefinition(
    @ValidKey
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