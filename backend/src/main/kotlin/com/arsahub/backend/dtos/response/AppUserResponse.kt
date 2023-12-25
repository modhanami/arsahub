package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.AppUser

data class AppUserResponse(
    val userId: String,
    val displayName: String,
    val points: Int
) {
    companion object {
        fun fromEntity(entity: AppUser): AppUserResponse {
            return AppUserResponse(
                userId = entity.userId ?: "",
                displayName = entity.displayName ?: "",
                points = entity.points ?: 0
            )
        }
    }
}