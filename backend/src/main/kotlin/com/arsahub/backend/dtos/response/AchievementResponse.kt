package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.Achievement

data class AchievementResponse(
    val achievementId: Long,
    val title: String,
    val description: String?,
    val imageKey: String?,
    val imageMetadata: Map<String, Any>?,
) {
    companion object {
        fun fromEntity(achievement: Achievement): AchievementResponse {
            return AchievementResponse(
                achievementId = achievement.achievementId!!,
                title = achievement.title,
                description = achievement.description,
                imageKey = achievement.imageKey,
                imageMetadata = achievement.imageMetadata,
            )
        }
    }
}
