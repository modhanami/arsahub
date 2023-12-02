package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.UserActivity
import com.arsahub.backend.repositories.UserActivityRepository
import org.springframework.stereotype.Component

@Component
class ActionAddPointsHandler(private val userActivityRepository: UserActivityRepository) : ActionHandler {
    override fun handleAction(rule: Rule, member: UserActivity): ActionResult {
        val points = rule.actionParams?.get("value")?.toString()?.toInt() ?: throw Exception("Points not found")
        val previousPoints = member.points ?: 0
        val newPoints = previousPoints + points
        member.addPoints(points)
        userActivityRepository.save(member)
        println("User ${member.user?.username}` (${member.user?.userId}) received `$points` points for activity `${rule.activity?.title}` (${rule.activity?.activityId}) from rule `${rule.title}` (${rule.id}), previous points: $previousPoints, new points: $newPoints")
        return ActionResult.PointsUpdate(previousPoints, newPoints, points)
    }
}