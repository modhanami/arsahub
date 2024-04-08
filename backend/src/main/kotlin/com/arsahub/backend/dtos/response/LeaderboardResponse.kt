package com.arsahub.backend.dtos.response

data class LeaderboardResponse(
    val leaderboard: String,
    val entries: List<Entry>,
) {
    data class Entry(
        val rank: Long,
        val userId: String,
        val memberName: String,
        val score: Long,
    )
}
