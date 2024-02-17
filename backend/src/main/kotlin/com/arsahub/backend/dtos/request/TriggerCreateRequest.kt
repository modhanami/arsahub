package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.ValidationLengths
import com.arsahub.backend.dtos.ValidationMessages
import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidKey
import com.arsahub.backend.dtos.annotations.ValidTitle
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class TriggerCreateRequest(
    title: String?,
    description: String? = null,
    val fields: List<FieldDefinition>? = null,
) {
    @ValidTitle
    val title: String? = title?.trim()

    @ValidDescription
    val description: String? = description?.trim()
}

class FieldDefinition(
    key: String?,
    type: String?,
    label: String? = null,
) {
    @ValidKey
    val key = key?.trim()

    @NotBlank(message = ValidationMessages.Constants.TYPE_REQUIRED)
    val type = type?.trim()

    @Size(
        min = ValidationLengths.Constants.LABEL_MIN,
        max = ValidationLengths.Constants.LABEL_MAX,
        message = ValidationMessages.Constants.LABEL_LENGTH,
    )
    val label = label?.trim()

    companion object {
        fun fromEntity(field: com.arsahub.backend.models.TriggerField): FieldDefinition {
            return FieldDefinition(
                key = field.key!!,
                label = field.label,
                type = field.type!!,
            )
        }
    }
}
