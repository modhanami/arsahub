package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.AppUser
import java.time.Instant

data class AppUserResponse(
    val userId: String,
    val displayName: String,
    val points: Int,
    val achievements: List<AchievementResponse>,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
) {
    companion object {
        fun fromEntity(entity: AppUser): AppUserResponse {
            return AppUserResponse(
                userId = entity.userId ?: "",
                displayName = entity.displayName ?: "",
                points = entity.points ?: 0,
                achievements =
                    entity.achievements.mapNotNull {
                        it.achievement?.let { achievement ->
                            AchievementResponse.fromEntity(achievement)
                        }
                    },
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
        }
    }
}
