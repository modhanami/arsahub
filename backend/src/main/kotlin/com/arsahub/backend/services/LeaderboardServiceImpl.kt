package com.arsahub.backend.services

import com.arsahub.backend.dtos.LeaderboardResponse
import org.springframework.stereotype.Service

@Service
class LeaderboardServiceImpl : LeaderboardService {
    override fun getTotalPoints(): LeaderboardResponse {
        return LeaderboardResponse(
            leaderboard = "total-points",
            entries = listOf()
        )
    }
}