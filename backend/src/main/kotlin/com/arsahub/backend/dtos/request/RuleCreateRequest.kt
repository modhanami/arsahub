package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.ValidationMessages
import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidTitle
import jakarta.validation.constraints.NotBlank

data class RuleCreateRequest(
    // TODO: remove nullability and actually customize jackson-module-kotlin
    //  with the Jackson2ObjectMapperBuilderCustomizer
    @ValidTitle
    val title: String?,
    @ValidDescription
    val description: String?,
    val trigger: TriggerDefinition,
    val action: ActionDefinition,
    val conditions: Map<String, Any>?,
    @field:NotBlank(message = ValidationMessages.Constants.REPEATABILITY_REQUIRED)
    val repeatability: String,
)

typealias TriggerDefinition = KeyAndParams
typealias ActionDefinition = KeyAndParams
