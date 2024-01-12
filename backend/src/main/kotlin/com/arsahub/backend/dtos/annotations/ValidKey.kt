package com.arsahub.backend.dtos.annotations

import com.arsahub.backend.dtos.ValidationLengths
import com.arsahub.backend.dtos.ValidationMessages
import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = ValidationMessages.Constants.KEY_REQUIRED)
@Size(
    min = ValidationLengths.Constants.KEY_MIN,
    max = ValidationLengths.Constants.KEY_MAX,
    message = ValidationMessages.Constants.KEY_LENGTH,
)
@Pattern(
    regexp = "^[a-zA-Z0-9_-]*\$",
    message = ValidationMessages.Constants.KEY_PATTERN,
)
@Constraint(validatedBy = [])
annotation class ValidKey(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
