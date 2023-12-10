package com.arsahub.backend.dtos

import com.arsahub.backend.models.AppUser

data class AppUserResponse(
    val uniqueId: String,
    val displayName: String
) {
    companion object {
        fun fromEntity(entity: AppUser): AppUserResponse {
            return AppUserResponse(
                uniqueId = entity.userId ?: "",
                displayName = entity.displayName ?: ""
            )
        }
    }
}