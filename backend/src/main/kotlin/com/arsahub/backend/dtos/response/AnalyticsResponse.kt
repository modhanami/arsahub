package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.Trigger

class AchievementWithUnlockCount(
    val achievement: Achievement,
    val count: Long,
)

data class AchievementWithUnlockCountResponse(
    val achievement: AchievementResponse,
    val count: Long,
) {
    companion object {
        fun fromEntity(entity: AchievementWithUnlockCount): AchievementWithUnlockCountResponse {
            return AchievementWithUnlockCountResponse(
                AchievementResponse.fromEntity(entity.achievement),
                entity.count,
            )
        }
    }
}

class TriggerWithTriggerCount(
    val trigger: Trigger,
    val count: Long,
)

data class TriggerWithTriggerCountResponse(
    val trigger: TriggerResponse,
    val count: Long,
) {
    companion object {
        fun fromEntity(entity: TriggerWithTriggerCount): TriggerWithTriggerCountResponse {
            return TriggerWithTriggerCountResponse(
                TriggerResponse.fromEntity(entity.trigger),
                entity.count,
            )
        }
    }
}
