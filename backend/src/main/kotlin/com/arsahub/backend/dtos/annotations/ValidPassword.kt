package com.arsahub.backend.dtos.annotations

import com.arsahub.backend.dtos.ValidationLengths
import com.arsahub.backend.dtos.ValidationMessages
import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@NotBlank(message = ValidationMessages.Constants.PASSWORD_REQUIRED)
@Size(
    min = ValidationLengths.Constants.PASSWORD_MIN,
    max = ValidationLengths.Constants.PASSWORD_MAX,
    message = ValidationMessages.Constants.PASSWORD_LENGTH,
)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
annotation class ValidPassword(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
