package com.arsahub.backend.dtos

data class ApiValidationError(
    val message: String,
    val errors: Map<String, String>
) {
    constructor(errors: Map<String, String>) : this("Validation failed", errors)
}