package com.arsahub.backend.dtos

import com.arsahub.backend.models.Achievement

data class AchievementResponse(
    val achievementId: Long,
    val title: String,
    val description: String?,
    val imageUrl: String?,
) {
    companion object {
        fun fromEntity(achievement: Achievement): AchievementResponse {
            return AchievementResponse(
                achievementId = achievement.achievementId!!,
                title = achievement.title,
                description = achievement.description,
                imageUrl = achievement.imageUrl,
            )
        }
    }
}