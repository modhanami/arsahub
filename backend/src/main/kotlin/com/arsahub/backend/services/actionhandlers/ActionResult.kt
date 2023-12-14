package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.Achievement

sealed class ActionResult {
    data class PointsUpdate(val previousPoints: Int, val newPoints: Int, val pointsAdded: Int) : ActionResult()
    data class AchievementUpdate(val achievement: Achievement) : ActionResult()
    data class Nothing(val message: String) : ActionResult()
}