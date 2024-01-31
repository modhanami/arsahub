package com.arsahub.backend.dtos.annotations

import com.arsahub.backend.dtos.ValidationLengths
import com.arsahub.backend.dtos.ValidationMessages
import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@NotBlank(message = ValidationMessages.Constants.NAME_REQUIRED)
@Size(
    min = ValidationLengths.Constants.NAME_MIN,
    max = ValidationLengths.Constants.NAME_MAX,
    message = ValidationMessages.Constants.NAME_LENGTH,
)
@Pattern(
    regexp = "^[ a-zA-Z0-9_-]*\$",
    message = ValidationMessages.Constants.NAME_PATTERN,
)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
annotation class ValidName(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
