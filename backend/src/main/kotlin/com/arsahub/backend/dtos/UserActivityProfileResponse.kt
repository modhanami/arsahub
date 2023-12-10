package com.arsahub.backend.dtos

import com.arsahub.backend.models.AppUserActivity

data class UserActivityProfileResponse(
    val user: AppUserResponse?,
    val points: Int,
    val achievements: List<AchievementResponse>
) {
    companion object {
        fun fromEntity(appUserActivity: AppUserActivity): UserActivityProfileResponse {
            return UserActivityProfileResponse(
                user = appUserActivity.appUser?.let { AppUserResponse.fromEntity(it) },
                points = appUserActivity.points ?: 0,
                achievements = appUserActivity.userActivityAchievements.mapNotNull {
                    it.achievement?.let { achievement ->
                        AchievementResponse.fromEntity(achievement)
                    }
                }
            )
        }
    }
}