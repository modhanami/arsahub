package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.User

// TODO: evaluate duplication with UserIdentity
data class UserResponse(
    val userId: Long, // TODO: remove this after backward compatibility is no longer needed
    val internalUserId: Long,
    val externalUserId: String,
    val googleUserId: String?,
    val email: String,
    val name: String,
) {
    companion object {
        fun fromEntity(user: User): UserResponse {
            return UserResponse(
                userId = user.userId!!,
                internalUserId = user.userId!!,
                externalUserId = user.externalUserId!!,
                googleUserId = user.googleUserId,
                email = user.email!!,
                name = user.name!!,
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
