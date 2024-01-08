package com.arsahub.backend.dtos.response

data class ApiValidationError(
    val message: String,
    val errors: Map<String, String>,
) {
    constructor(errors: Map<String, String>) : this("Validation failed", errors)
}
