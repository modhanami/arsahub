package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.LeaderboardResponse
import com.arsahub.backend.services.LeaderboardService
import com.arsahub.backend.services.LeaderboardServiceImpl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/leaderboard")
class LeaderboardController(private val leaderboardService: LeaderboardServiceImpl) {

    @GetMapping("/total-points")
    fun getTotalPoints(): LeaderboardResponse {
        return leaderboardService.getTotalPoints()
    }
}