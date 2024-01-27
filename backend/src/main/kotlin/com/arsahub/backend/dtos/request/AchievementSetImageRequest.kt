package com.arsahub.backend.dtos.request

import org.springframework.web.multipart.MultipartFile

data class AchievementSetImageRequest(
    val achievementId: Long,
    val image: MultipartFile,
)
