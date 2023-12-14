package com.arsahub.backend.dtos

data class TriggerDefinition(
    val key: String,
    val params: Map<String, String>? = null
)