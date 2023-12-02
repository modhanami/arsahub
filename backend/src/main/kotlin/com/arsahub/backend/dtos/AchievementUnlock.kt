package com.arsahub.backend.dtos

data class AchievementUnlock(val userId: Long, val achievement: AchievementResponse) :
    ActivityUpdate