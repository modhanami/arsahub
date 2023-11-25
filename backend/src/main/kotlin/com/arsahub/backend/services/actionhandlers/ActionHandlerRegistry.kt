package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.UserActivity
import org.springframework.stereotype.Component

@Component
class ActionHandlerRegistry(
    actionAddPointsHandler: ActionAddPointsHandler,
    actionUnlockAchievementHandler: ActionUnlockAchievementHandler
) : ActionHandler {
    val handlers = mapOf(
        "add_points" to actionAddPointsHandler,
        "unlock_achievement" to actionUnlockAchievementHandler
    )

    override fun handleAction(rule: Rule, member: UserActivity): ActionResult {
        val handler = handlers[rule.action?.key] ?: throw Exception("Action handler not found")
        return handler.handleAction(rule, member)
    }
}