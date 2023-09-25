package com.arsahub.backend.services

import com.arsahub.backend.dtos.LeaderboardResponse

interface LeaderboardService {
    fun getTotalPoints(): LeaderboardResponse
}