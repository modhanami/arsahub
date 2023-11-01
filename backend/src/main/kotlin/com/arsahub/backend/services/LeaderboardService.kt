package com.arsahub.backend.services

import com.arsahub.backend.dtos.LeaderboardResponse
import com.arsahub.backend.repositories.UserActivityRepository
import org.springframework.stereotype.Service

interface LeaderboardService {
    fun getTotalPointsLeaderboard(activityId: Long): LeaderboardResponse
}

@Service
class LeaderboardServiceImpl(
    private val userActivityRepository: UserActivityRepository
) : LeaderboardService {
    override fun getTotalPointsLeaderboard(activityId: Long): LeaderboardResponse {
//        val entries = memberRepository.findAllByActivity_ActivityId(activityId)
//            .sortedByDescending { it.points }
//            .mapIndexed { index, member ->
//                LeaderboardResponse.Entry(
//                    memberId = member.id,
//                    memberName = member.user.name,
//                    score = member.points,
//                    rank = index + 1
//                )
//            }
//        return LeaderboardResponse(
//            leaderboard = "total-points",
//            entries
//        )
        return LeaderboardResponse(
            leaderboard = "total-points",
            entries = listOf()
        )
    }
}