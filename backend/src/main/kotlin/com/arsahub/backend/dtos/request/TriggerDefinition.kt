package com.arsahub.backend.dtos.request

data class TriggerDefinition(
    val key: String,
    val params: Map<String, String>? = null
)