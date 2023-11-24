package com.arsahub.backend.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RuleCreateRequest(
    @field:Size(min = 4, max = 200, message = "Name must be between 4 and 200 characters")
    @field:NotBlank(message = "Name is required")
    val title: String?, // TODO: remove nullability and actually customize jackson-module-kotlin with the Jackson2ObjectMapperBuilderCustomizer
    @field:Size(max = 500, message = "Description cannot be longer than 500 characters")
    val description: String?,
    val trigger: TriggerDefinition,
    val action: ActionDefinition,
    val condition: RuleCondition? = null
)