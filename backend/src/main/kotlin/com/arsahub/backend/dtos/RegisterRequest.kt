package com.arsahub.backend.dtos

data class RegisterRequest(
    val idToken: String,
)

data class LoginResponse(
    val userId: Long,
    val username: String,
    val name: String,
    val externalUserId: String,
) {
    companion object {
        fun fromEntity(user: com.arsahub.backend.models.User): LoginResponse {
            return LoginResponse(
                userId = user.userId,
                username = user.username,
                name = user.name,
                externalUserId = user.externalUserId,
            )
        }
    }
}