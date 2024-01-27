package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.User

data class UserResponse(
    val userId: Long?,
    val name: String,
    val username: String?,
) {
    companion object {
        fun fromEntity(user: User): UserResponse {
            return UserResponse(
                userId = user.userId,
                name = user.name,
                username = user.username,
            )
        }
    }
}

//
// fun User.toUserResponse(): UserResponse = UserResponse(
//    userId = userId,
//    name = name,
//    username = username,
// )
