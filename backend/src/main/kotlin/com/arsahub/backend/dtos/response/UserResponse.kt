package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.User

data class UserResponse(
    val userId: Long?,
    val externalUserId: String?,
) {
    companion object {
        fun fromEntity(user: User): UserResponse {
            return UserResponse(
                userId = user.userId,
                externalUserId = user.externalUserId,
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
