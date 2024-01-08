package com.arsahub.backend.dtos.socketio

import com.arsahub.backend.dtos.response.LeaderboardResponse

data class LeaderboardUpdate(val leaderboard: LeaderboardResponse) : AppUpdate