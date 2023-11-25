package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.UserActivity
import com.arsahub.backend.repositories.AchievementRepository
import com.arsahub.backend.repositories.UserActivityAchievementRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ActionUnlockAchievementHandler(
    private val achievementRepository: AchievementRepository,
    private val userActivityAchievementRepository: UserActivityAchievementRepository
) : ActionHandler {
    override fun handleAction(rule: Rule, member: UserActivity): ActionResult {
        val achievementId = rule.actionParams?.get("achievementId")?.toString()?.toLong()
            ?: throw Exception("Achievement ID not found")
        val achievement =
            achievementRepository.findByIdOrNull(achievementId) ?: throw Exception("Achievement not found")

        // precondition: user must not have unlocked the achievement
        if (member.userActivityAchievements.any { it.achievement?.achievementId == achievementId }) {
            val message =
                "User `${member.user?.userId}` (${member.user?.externalUserId}) already unlocked achievement"
            println(message)
            return ActionResult.Nothing(message)
        }

        member.addAchievement(achievement)
        // save from the owning side
        userActivityAchievementRepository.saveAll(member.userActivityAchievements)

        println("User `${member.user?.userId}` (${member.user?.externalUserId}) unlocked achievement `${achievement.title}` (${achievement.achievementId}) for activity `${rule.activity?.title}` (${rule.activity?.activityId}) from rule `${rule.title}` (${rule.id})")

        return ActionResult.AchievementUpdate(achievement)
    }
}