package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidKey

class KeyAndParams(
    key: String?,
    val params: Map<String, Any>? = null,
) {
    @ValidKey
    val key = key?.trim()
}
