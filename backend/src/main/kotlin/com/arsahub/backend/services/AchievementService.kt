package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.AchievementCreateRequest
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.App
import com.arsahub.backend.repositories.AchievementRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

class AchievementNotFoundException(id: Long) : NotFoundException("Achievement with ID $id not found")

class AchievementConflictException(title: String) :
    ConflictException("Achievement with this title already exists.")

@Service
class AchievementService(private val achievementRepository: AchievementRepository) {
    private val logger = KotlinLogging.logger {}

    fun createAchievement(
        app: App,
        request: AchievementCreateRequest,
    ): Achievement {
        // validate uniqueness of title in app
        val existingTrigger = achievementRepository.findByTitleAndApp(request.title!!, app)
        if (existingTrigger != null) {
            throw AchievementConflictException(request.title)
        }

        val achievement =
            Achievement(
                title = request.title,
                description = request.description,
                app = app,
            )

        achievementRepository.save(achievement)

        return achievement
    }

    fun listAchievements(app: App): List<Achievement> {
        return achievementRepository.findAllByApp(app)
    }

    fun getAchievementOrThrow(
        id: Long,
        app: App,
    ): Achievement {
        return achievementRepository.findByAchievementIdAndApp(id, app) ?: throw AchievementNotFoundException(id)
    }
}
