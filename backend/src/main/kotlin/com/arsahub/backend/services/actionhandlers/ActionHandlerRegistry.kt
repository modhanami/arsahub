package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import org.springframework.stereotype.Component

@Component
class ActionHandlerRegistry(
    actionAddPointsHandler: ActionAddPointsHandler,
    actionUnlockAchievementHandler: ActionUnlockAchievementHandler,
) : ActionHandler {
    val handlers =
        mapOf(
            "add_points" to actionAddPointsHandler,
            "unlock_achievement" to actionUnlockAchievementHandler,
        )

    override fun handleAction(
        rule: Rule,
        appUser: AppUser,
        params: Map<String, Any>?,
    ): ActionResult {
        val handler = handlers[rule.action] ?: throw IllegalArgumentException("Handler not found")
        return handler.handleAction(rule, appUser, params)
    }
}
