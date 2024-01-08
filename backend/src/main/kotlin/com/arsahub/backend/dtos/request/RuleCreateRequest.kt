package com.arsahub.backend.dtos.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RuleCreateRequest(
    // TODO: remove nullability and actually customize jackson-module-kotlin
    //  with the Jackson2ObjectMapperBuilderCustomizer
    @field:Size(min = 4, max = 200, message = "Name must be between 4 and 200 characters")
    @field:NotBlank(message = "Name is required")
    val title: String?,
    @field:Size(max = 500, message = "Description cannot be longer than 500 characters")
    val description: String?,
    val trigger: TriggerDefinition,
    val action: ActionDefinition,
    val conditions: Map<String, Any>?,
    @field:NotBlank(message = "Repeatability is required")
    val repeatability: String,
)

typealias TriggerDefinition = KeyAndParams
typealias ActionDefinition = KeyAndParams
