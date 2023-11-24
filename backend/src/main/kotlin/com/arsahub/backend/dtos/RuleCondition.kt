package com.arsahub.backend.dtos

data class RuleCondition(
    val type: String,
    val params: Map<String, String>,
)