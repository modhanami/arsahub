package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.Reward

class RewardWithCount(
    val reward: Reward,
    val count: Long,
)

class RewardResponseWithCount(
    val reward: RewardResponse,
    val count: Long,
) {
    companion object {
        fun fromEntity(rewardWithCount: RewardWithCount): RewardResponseWithCount {
            return RewardResponseWithCount(
                reward = RewardResponse.fromEntity(rewardWithCount.reward),
                count = rewardWithCount.count,
            )
        }
    }
}
