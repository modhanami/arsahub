package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.ValidationMessages
import com.arsahub.backend.dtos.annotations.RequiredTitle
import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidTitle
import jakarta.validation.constraints.NotBlank

class RuleCreateRequest(
    // TODO: remove nullability and actually customize jackson-module-kotlin
    //  with the Jackson2ObjectMapperBuilderCustomizer
    title: String?,
    description: String? = null,
    val trigger: TriggerDefinition,
    val action: ActionDefinition,
    repeatability: String?,
    val conditionExpression: String? = null,
) {
    @ValidTitle
    @RequiredTitle
    val title = title?.trim()

    @ValidDescription
    val description = description?.trim()

    @NotBlank(message = ValidationMessages.Constants.REPEATABILITY_REQUIRED)
    val repeatability = repeatability?.trim()
}

typealias TriggerDefinition = KeyAndParams
typealias ActionDefinition = KeyAndParams
