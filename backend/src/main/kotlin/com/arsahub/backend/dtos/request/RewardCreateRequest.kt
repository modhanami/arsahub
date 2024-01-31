package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidName
import com.arsahub.backend.extensions.trimmed
import jakarta.validation.constraints.NotNull

class RewardCreateRequest(
    name: String?,
    description: String? = null,
    @NotNull
    val price: Int?,
    @NotNull
    val quantity: Int?,
) {
    @ValidName
    val name: String? = name.trimmed()

    @ValidDescription
    val description: String? = description?.trimmed()
}
