package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidTitle

class RuleUpdateRequest(
    title: String?,
    description: String? = null,
) {
    @ValidTitle
    val title = title?.trim()

    @ValidDescription
    val description = description?.trim()
}
