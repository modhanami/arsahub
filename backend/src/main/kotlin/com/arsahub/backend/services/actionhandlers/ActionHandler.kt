package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.UserActivity

interface ActionHandler {
    fun handleAction(rule: Rule, member: UserActivity): ActionResult
}