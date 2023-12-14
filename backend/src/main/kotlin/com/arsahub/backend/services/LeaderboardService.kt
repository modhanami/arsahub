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
        val entries = userActivityRepository.findAllByActivity_ActivityId(activityId)
            .sortedByDescending { it.points }
            .mapIndexed { index, member ->
                if (member.id != null && member.appUser != null && member.points != null) {
                    LeaderboardResponse.Entry(
                        userId = member.appUser!!.userId!!,
                        memberName = member.appUser!!.displayName!!,
                        score = member.points!!,
                        rank = index + 1
                    )
                } else {
                    null
                }
            }
            .filterNotNull()
        return LeaderboardResponse(
            leaderboard = "total-points",
            entries
        )
    }
}