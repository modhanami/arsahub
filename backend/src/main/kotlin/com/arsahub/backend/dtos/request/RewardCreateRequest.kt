package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidName
import jakarta.validation.constraints.NotNull

class RewardCreateRequest(
    name: String?,
    description: String? = null,
    @NotNull
    val price: Int?,
    val quantity: Int?,
) {
    @ValidName
    val name = name?.trim()

    @ValidDescription
    val description = description?.trim()
}
