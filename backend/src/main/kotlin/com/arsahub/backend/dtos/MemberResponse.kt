package com.arsahub.backend.dtos

import com.arsahub.backend.models.AppUserActivity

data class MemberResponse(
//    val memberId: Long?,
    val userId: String?,
    val displayName: String?,
    val points: Int?,
) {
    companion object {
        fun fromEntity(member: AppUserActivity): MemberResponse {
            return MemberResponse(
//                memberId = member.id,
                userId = member.appUser?.userId,
                displayName = member.appUser?.displayName,
                points = member.points,
            )
        }
    }
}