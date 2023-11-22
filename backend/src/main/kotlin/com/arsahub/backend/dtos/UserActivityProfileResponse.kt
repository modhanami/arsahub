package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.AchievementResponse
import com.arsahub.backend.dtos.UserResponse
import com.arsahub.backend.models.UserActivity

data class UserActivityProfileResponse(
    val user: UserResponse?,
    val points: Int,
    val achievements: List<AchievementResponse>
) {
    companion object {
        fun fromEntity(userActivity: UserActivity): UserActivityProfileResponse {
            return UserActivityProfileResponse(
                user = userActivity.user?.let { UserResponse.fromEntity(it) },
                points = userActivity.points ?: 0,
                achievements = userActivity.userActivityAchievements.mapNotNull {
                    it.achievement?.let { achievement ->
                        AchievementResponse.fromEntity(achievement)
                    }
                }
            )
        }
    }
}