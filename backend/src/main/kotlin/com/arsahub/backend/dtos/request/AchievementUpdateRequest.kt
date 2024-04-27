package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidDescription
import com.arsahub.backend.dtos.annotations.ValidTitle

class AchievementUpdateRequest(
    title: String? = null,
    description: String? = null,
    imageId: String? = null,
) {
    @ValidTitle
    val title = title?.trim()

    @ValidDescription
    val description = description?.trim()

    val imageId = imageId?.trim()
}
