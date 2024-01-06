package com.arsahub.backend.dtos.request

data class KeyAndParams(
    val key: String,
    val params: Map<String, Any>? = null
)