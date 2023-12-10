package com.arsahub.backend.dtos

data class AchievementUnlock(val userId: String, val achievement: AchievementResponse) :
    ActivityUpdate