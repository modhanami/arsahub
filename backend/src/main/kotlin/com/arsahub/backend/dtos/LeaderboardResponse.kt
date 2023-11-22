package com.arsahub.backend.dtos

data class LeaderboardResponse(
    val leaderboard: String,
    val entries: List<Entry>
) {
    data class Entry(
        val rank: Int,
        val memberId: Long,
        val memberName: String,
        val score: Int,
    )
}