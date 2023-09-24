package com.arsahub.backend.dtos

import com.arsahub.backend.models.User

data class UserResponse(
    val userId: Long?,
    val name: String,
) {
    companion object {
        fun fromEntity(user: User): UserResponse {
            return UserResponse(
                userId = user.userId,
                name = user.username,
            )
        }
    }
}