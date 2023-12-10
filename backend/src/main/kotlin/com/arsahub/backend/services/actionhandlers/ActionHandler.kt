package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.AppUserActivity
import com.arsahub.backend.models.Rule

interface ActionHandler {
    fun handleAction(rule: Rule, member: AppUserActivity): ActionResult
}