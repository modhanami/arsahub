package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.AchievementCreateRequest
import com.arsahub.backend.dtos.request.AchievementUpdateRequest
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.App
import com.arsahub.backend.repositories.AchievementRepository
import com.arsahub.backend.repositories.AppUserAchievementRepository
import com.arsahub.backend.repositories.RuleRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import java.util.*

class AchievementNotFoundException : NotFoundException("Achievement not found")

class AchievementConflictException(title: String) :
    ConflictException("Achievement with this title already exists.")

@ConfigurationProperties("image-upload")
class MyServiceProperties {
    lateinit var bucket: String
    val auth = Auth()

    class Auth {
        lateinit var accessKeyId: String
        lateinit var secretAccessKey: String
    }
}

@Service
class AchievementService(
    private val achievementRepository: AchievementRepository,
    private val properties: MyServiceProperties,
    private val ruleRepository: RuleRepository,
    private val appUserAchievementRepository: AppUserAchievementRepository,
    private val s3Client: S3Client,
) {
    private val logger = KotlinLogging.logger {}

    fun createAchievement(
        app: App,
        request: AchievementCreateRequest,
    ): Achievement {
        logger.info { "Creating achievement: $request" }
        // validate uniqueness of title in app
        val existingTrigger = achievementRepository.findByTitleAndApp(request.title, app)
        if (existingTrigger != null) {
            logger.error { "Achievement with title ${request.title} already exists" }
            throw AchievementConflictException(request.title)
        }

        // check if image is in temp storage
        val tempImageId =
            request.imageId?.let { imageId ->
                try {
                    UUID.fromString(imageId)
                } catch (e: IllegalArgumentException) {
                    logger.error { "Invalid image ID: $imageId" }
                    throw IllegalArgumentException("Invalid image ID")
                }
            }

        logger.debug { "Temp image ID: $tempImageId" }
        val tempImageKey = "temp-images/$tempImageId"
        logger.debug { "Checking if image exists in temp storage: $tempImageKey" }

        val achievement =
            Achievement(
                title = request.title,
                description = request.description,
                app = app,
            )

        logger.debug { "Saving achievement: ${achievement.achievementId}" }
        achievementRepository.save(achievement)
        logger.info { "Created achievement: ${achievement.achievementId}" }

        if (tempImageId != null) {
            val moveResponse =
                kotlin.runCatching { moveTempImageToAppAchievementStorage(tempImageId, app, achievement) }
                    .onFailure { e ->
                        logger.error(e) { "Failed to move image to app's S3 storage" }
                        // remove achievement if image upload fails
                        achievementRepository.delete(achievement)
                        throw e
                    }.getOrThrow()

            achievement.imageKey = moveResponse.imageKey
            achievement.imageMetadata = moveResponse.imageMetadata.toMutableMap()
            achievementRepository.save(achievement)
        }

        return achievement
    }

    fun listAchievements(app: App): List<Achievement> {
        return achievementRepository.findAllByApp(app)
    }

    fun getAchievementOrThrow(
        id: Long,
        app: App,
    ): Achievement {
        return achievementRepository.findByAchievementIdAndApp(id, app) ?: throw AchievementNotFoundException()
    }

    class AchievementInUseException : ConflictException("Achievement is used in rules or unlocked by users")

    fun deleteAchievement(
        app: App,
        achievementId: Long,
    ) {
        val achievement = getAchievementOrThrow(achievementId, app)
        assertCanDeleteAchievement(app, achievement)

        assertAchievementNotInUse(app, achievement)

        achievementRepository.delete(achievement)
    }

    private fun assertCanDeleteAchievement(
        currentApp: App,
        achievement: Achievement,
    ) {
        if (achievement.app!!.id != currentApp.id) {
            throw AchievementNotFoundException()
        }
    }

    private fun assertAchievementNotInUse(
        currentApp: App,
        achievement: Achievement,
    ) {
        val usedRules =
            ruleRepository.findAllByActionAchievement_AchievementIdAndApp(achievement.achievementId!!, currentApp)
        if (usedRules.isNotEmpty()) {
            throw AchievementInUseException()
        }

        val unlockedAchievements = appUserAchievementRepository.findAllByAchievementAndApp(achievement, currentApp)
        if (unlockedAchievements.isNotEmpty()) {
            throw AchievementInUseException()
        }
    }

    fun updateAchievement(
        app: App,
        achievementId: Long,
        request: AchievementUpdateRequest,
    ): Achievement {
        val achievement = getAchievementOrThrow(achievementId, app)

        if (request.title != null) {
            val existingAchievement = achievementRepository.findByTitleAndApp(request.title, app)
            if (existingAchievement != null && existingAchievement.achievementId != achievement.achievementId) {
                throw AchievementConflictException(request.title)
            }
            achievement.title = request.title
        }

        if (request.description != null) {
            achievement.description = request.description
        }

        if (request.imageId != null) {
            val tempImageId = UUID.fromString(request.imageId)
            val moveResponse = moveTempImageToAppAchievementStorage(tempImageId, app, achievement)
            achievement.imageKey = moveResponse.imageKey
            achievement.imageMetadata = moveResponse.imageMetadata.toMutableMap()

            achievementRepository.save(achievement)
        }

        return achievementRepository.save(achievement)
    }

    private fun moveTempImageToAppAchievementStorage(
        tempImageId: UUID,
        app: App,
        achievement: Achievement,
    ): CopyTempImageToAppAchievementStorageResponse {
        val tempImageKey = "temp-images/$tempImageId"
        val achievementImageKey = "apps/${app.id}/achievements/${achievement.achievementId}/$tempImageId"

        try {
            s3Client.headObject {
                it.bucket(properties.bucket)
                it.key(tempImageKey)
            }
        } catch (e: NoSuchKeyException) {
            throw IllegalArgumentException("Invalid image ID")
        }

        val copyObjectRequest =
            CopyObjectRequest.builder()
                .sourceBucket(properties.bucket)
                .sourceKey(tempImageKey)
                .destinationBucket(properties.bucket)
                .destinationKey(achievementImageKey)
                .build()

        logger.debug { "Copying image from temp to app's S3 storage: $copyObjectRequest" }
        val copyObjectResponse = s3Client.copyObject(copyObjectRequest)
        logger.debug { "Copied image from temp to app's S3 storage: $copyObjectResponse" }

        logger.debug { "Deleting image from temp storage: $tempImageKey" }
        s3Client.deleteObject {
            it.bucket(properties.bucket)
            it.key(tempImageKey)
        }
        logger.debug { "Deleted image from temp storage: $tempImageKey" }

        val imageMetadata =
            s3Client.headObject {
                it.bucket(properties.bucket)
                it.key(achievementImageKey)
            }.metadata()
        logger.debug { "Image metadata: $imageMetadata" }

        return CopyTempImageToAppAchievementStorageResponse(imageKey = achievementImageKey, imageMetadata)
    }
}

class CopyTempImageToAppAchievementStorageResponse(
    val imageKey: String,
    val imageMetadata: Map<String, Any> = emptyMap(),
)
