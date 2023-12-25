package com.arsahub.backend.dtos.request

data class ActionDefinition(
    val key: String,
    val params: Map<String, String>
)