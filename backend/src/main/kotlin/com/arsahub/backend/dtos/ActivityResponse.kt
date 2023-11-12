package com.arsahub.backend.dtos

import com.arsahub.backend.models.Activity

data class ActivityResponse(
    val id: Long?,
    val title: String,
    val description: String?,
    val members: List<MemberResponse> = listOf(),
) {
    companion object {
        fun fromEntity(activity: Activity): ActivityResponse {
            return ActivityResponse(
                id = activity.activityId,
                title = activity.title,
                description = activity.description,
                members = activity.members.map { MemberResponse.fromEntity(it) }
            )
        }
    }
}

data class MemberResponse(
    val memberId: Long?,
    val name: String?,
    val points: Int?,
    val userId: String?,
) {
    companion object {
        fun fromEntity(member: com.arsahub.backend.models.UserActivity): MemberResponse {
            return MemberResponse(
                memberId = member.id,
                name = member.user?.name,
                points = member.points,
                userId = member.user?.externalUserId,
            )
        }
    }
}