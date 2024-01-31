package com.arsahub.backend.dtos.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class RewardRedeemRequest(
    @NotNull
    val rewardId: Long,
    @NotEmpty
    val userId: String,
)
