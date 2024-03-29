package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.RequiredTitle
import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidTitle

class AchievementCreateRequest(
    title: String,
    description: String? = null,
) {
    @ValidTitle
    @RequiredTitle
    val title: String = title.trim()

    @ValidDescription
    val description: String? = description?.trim()
}
