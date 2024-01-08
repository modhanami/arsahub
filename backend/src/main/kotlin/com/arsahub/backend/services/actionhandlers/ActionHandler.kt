package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule

interface ActionHandler {
    fun handleAction(
        rule: Rule,
        appUser: AppUser,
    ): ActionResult
}
