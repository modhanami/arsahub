package com.arsahub.backend.dtos.request

import org.springframework.web.multipart.MultipartFile

data class RewardSetImageRequest(
    val rewardId: Long,
    val image: MultipartFile,
)
