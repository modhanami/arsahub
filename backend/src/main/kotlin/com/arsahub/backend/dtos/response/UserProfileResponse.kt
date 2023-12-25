package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.AppUser

data class UserProfileResponse(
    val user: AppUserResponse?,
    val points: Int,
    val achievements: List<AchievementResponse>
) {
    companion object {
        fun fromEntity(appUser: AppUser): UserProfileResponse {
            return UserProfileResponse(
                user = appUser.let { AppUserResponse.fromEntity(it) },
                points = appUser.points ?: 0,
                achievements = appUser.achievements.mapNotNull {
                    it.achievement?.let { achievement ->
                        AchievementResponse.fromEntity(achievement)
                    }
                }
            )
        }
    }
}