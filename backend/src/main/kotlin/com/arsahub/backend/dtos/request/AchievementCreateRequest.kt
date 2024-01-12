package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidTitle

data class AchievementCreateRequest(
    @ValidTitle
    val title: String?,
    @ValidDescription
    val description: String?,
)
