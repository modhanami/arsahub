package com.arsahub.backend.dtos.annotations

import com.arsahub.backend.dtos.ValidationLengths
import com.arsahub.backend.dtos.ValidationMessages
import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Size(max = ValidationLengths.Constants.DESCRIPTION_MAX, message = ValidationMessages.Constants.DESCRIPTION_LENGTH)
@Constraint(validatedBy = [])
annotation class ValidDescription(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
