package com.arsahub.backend.utils

import com.networknt.schema.ValidationMessage

data class JsonSchemaValidationResult(
    val isValid: Boolean,
    val errors: Set<ValidationMessage>,
) {
    companion object {
        fun valid(): JsonSchemaValidationResult {
            return JsonSchemaValidationResult(
                isValid = true,
                errors = emptySet(),
            )
        }
    }
}
