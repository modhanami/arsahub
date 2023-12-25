package com.arsahub.backend.services

import com.arsahub.backend.dtos.response.LeaderboardResponse
import com.arsahub.backend.models.App
import com.arsahub.backend.repositories.AppUserRepository
import org.springframework.stereotype.Service

@Service
class LeaderboardService(
    private val appUserRepository: AppUserRepository
) {
    fun getTotalPointsLeaderboard(app: App): LeaderboardResponse {
        val entries = appUserRepository.findAllByApp(app)
            .sortedByDescending { it.points }
            .mapIndexed { index, appUser ->
                if (appUser.id != null && appUser.points != null) {
                    LeaderboardResponse.Entry(
                        userId = appUser.userId!!,
                        memberName = appUser.displayName!!,
                        score = appUser.points!!,
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