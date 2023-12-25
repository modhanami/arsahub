package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.repositories.AppUserRepository
import org.springframework.stereotype.Component

@Component
class ActionAddPointsHandler(private val appUserRepository: AppUserRepository) : ActionHandler {
    override fun handleAction(rule: Rule, appUser: AppUser): ActionResult {
        val points = rule.actionParams?.get("value")?.toString()?.toInt() ?: throw Exception("Points not found")
        val previousPoints = appUser.points ?: 0
        val newPoints = previousPoints + points
        appUser.addPoints(points)
        appUserRepository.save(appUser)
        println("User ${appUser.displayName}` (${appUser.userId}) received `$points` points from rule `${rule.title}` (${rule.id}), previous points: $previousPoints, new points: $newPoints")
        return ActionResult.PointsUpdate(previousPoints, newPoints, points)
    }
}