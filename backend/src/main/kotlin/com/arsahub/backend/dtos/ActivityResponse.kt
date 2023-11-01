package com.arsahub.backend.dtos

import com.arsahub.backend.models.Activity

data class ActivityResponse(
    val activityId: Long?,
    val title: String,
    val description: String?,
    val members: List<MemberResponse> = listOf(),
) {
    companion object {
        fun fromEntity(activity: Activity): ActivityResponse {
            return ActivityResponse(
                activityId = activity.activityId,
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
) {
    companion object {
        fun fromEntity(member: com.arsahub.backend.models.UserActivity): MemberResponse {
            return MemberResponse(
                memberId = member.id,
                name = member.user?.name,
                points = member.points,
            )
        }
    }
}