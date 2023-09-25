package com.arsahub.backend.dtos

data class LeaderboardEntryResponse(
    val rank: Int,
    val user: UserResponse,
    val score: Int
)

data class LeaderboardResponse(
    val leaderboard: String,
    val entries: List<LeaderboardEntryResponse>
)