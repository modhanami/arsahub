package com.arsahub.backend.dtos

data class ActionDefinition(
    val key: String,
    val params: Map<String, String>
)