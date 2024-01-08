package com.arsahub.backend.dtos.annotations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "Key is required")
@Size(min = 4, max = 200, message = "Key must be between 4 and 200 characters")
@Pattern(
    regexp = "^[a-zA-Z0-9_-]*\$",
    message = "Key must contain only alphanumeric characters, underscores, and dashes",
)
@Constraint(validatedBy = [])
annotation class ValidKey(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
