package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidKey
import jakarta.validation.constraints.NotNull

class TriggerSendRequest(
    key: String?,
    val params: Map<String, Any>? = null,
    userId: String?,
) {
    @ValidKey
    val key: String? = key?.trim()

    @NotNull
    val userId: String? = userId?.trim()
}
