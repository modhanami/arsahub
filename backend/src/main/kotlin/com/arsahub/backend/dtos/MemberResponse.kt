package com.arsahub.backend.dtos

data class MemberResponse(
    val memberId: Long?,
    val name: String?,
    val points: Int?,
    val userId: Long?,
    val username: String?,
) {
    companion object {
        fun fromEntity(member: com.arsahub.backend.models.UserActivity): MemberResponse {
            return MemberResponse(
                memberId = member.id,
                name = member.user?.name,
                points = member.points,
                userId = member.user?.userId,
                username = member.user?.username
            )
        }
    }
}