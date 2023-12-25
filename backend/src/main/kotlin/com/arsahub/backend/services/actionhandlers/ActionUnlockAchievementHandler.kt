package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.repositories.AchievementRepository
import com.arsahub.backend.repositories.AppUserAchievementRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ActionUnlockAchievementHandler(
    private val achievementRepository: AchievementRepository,
    private val appUserAchievementRepository: AppUserAchievementRepository
) : ActionHandler {
    override fun handleAction(rule: Rule, appUser: AppUser): ActionResult {
        val achievementId = rule.actionParams?.get("achievementId")?.toString()?.toLong()
            ?: throw Exception("Achievement ID not found")
        val achievement =
            achievementRepository.findByIdOrNull(achievementId) ?: throw Exception("Achievement not found")

        // precondition: user must not have unlocked the achievement
        if (appUser.achievements.any { it.achievement?.achievementId == achievementId }) {
            val message =
                "User ${appUser.displayName}` (${appUser.userId}) already unlocked achievement"
            println(message)
            return ActionResult.Nothing(message)
        }

        appUser.addAchievement(achievement)
        // save from the owning side
        appUserAchievementRepository.saveAll(appUser.achievements)

        println("User ${appUser.displayName}` (${appUser.userId}) unlocked achievement `${achievement.title}` (${achievement.achievementId}) from rule `${rule.title}` (${rule.id})")

        return ActionResult.AchievementUpdate(achievement)
    }
}