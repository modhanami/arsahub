package com.arsahub.backend.dtos.socketio

import com.arsahub.backend.dtos.response.AchievementResponse

data class AchievementUnlock(val userId: String, val achievement: AchievementResponse) :
    AppUpdate