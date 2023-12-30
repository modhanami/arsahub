package com.arsahub.backend.dtos.annotations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Size(max = 500, message = "Description cannot be longer than 500 characters")
@Constraint(validatedBy = [])
annotation class ValidDescription(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)