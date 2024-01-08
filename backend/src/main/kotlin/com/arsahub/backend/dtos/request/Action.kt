package com.arsahub.backend.dtos.request

sealed class Action(
    val key: String,
) {
    companion object {
        const val ADD_POINTS = "add_points"
        const val UNLOCK_ACHIEVEMENT = "unlock_achievement"
    }
}

data class AddPointsAction(
    val points: Int,
) : Action(ADD_POINTS)

data class UnlockAchievementAction(
    val achievementId: Long,
) : Action(
        UNLOCK_ACHIEVEMENT,
    )
