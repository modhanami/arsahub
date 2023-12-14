package com.arsahub.backend.dtos

import com.arsahub.backend.models.AppUser

data class AppUserResponse(
    val userId: String,
    val displayName: String
) {
    companion object {
        fun fromEntity(entity: AppUser): AppUserResponse {
            return AppUserResponse(
                userId = entity.userId ?: "",
                displayName = entity.displayName ?: ""
            )
        }
    }
}