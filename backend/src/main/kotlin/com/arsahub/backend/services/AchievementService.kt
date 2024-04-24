package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.AchievementCreateRequest
import com.arsahub.backend.dtos.request.AchievementSetImageRequest
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
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
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

        if (tempImageId != null) {
            // check if image exists in temp S3 storage
            // if it doesn't, throw an error
            try {
                s3Client.headObject(
                    HeadObjectRequest.builder()
                        .bucket(properties.bucket)
                        .key(tempImageKey)
                        .build(),
                )
                logger.debug { "Image $tempImageId exists in temp storage" }
            } catch (e: NoSuchKeyException) {
                logger.error { "Image $tempImageId not found in temp storage" }
                throw IllegalArgumentException("Invalid image ID")
            }
        }

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
            val achievementImageKey = "apps/${app.id}/achievements/${achievement.achievementId}/$tempImageId"
            achievement.imageKey = achievementImageKey

            // move image from temp to app's S3 storage
            val copyObjectRequest =
                CopyObjectRequest.builder()
                    .sourceBucket(properties.bucket)
                    .sourceKey(tempImageKey)
                    .destinationBucket(properties.bucket)
                    .destinationKey(achievement.imageKey)
                    .build()

            logger.debug { "Copying image from temp to app's S3 storage: $copyObjectRequest" }
            val copyObjectResponse = s3Client.copyObject(copyObjectRequest)
            logger.debug { "Copied image from temp to app's S3 storage: $copyObjectResponse" }

            // delete image from temp storage
            logger.debug { "Deleting image from temp storage: $tempImageKey" }
            s3Client.deleteObject {
                it.bucket(properties.bucket)
                it.key(tempImageKey)
            }

            logger.debug { "Deleted image from temp storage: $tempImageKey" }

            logger.debug { "Saving achievement with image: $achievement" }
            achievementRepository.save(achievement)
            logger.info { "Created achievement with image: $achievement" }
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

    fun setImageForAchievement(
        app: App,
        request: AchievementSetImageRequest,
    ): Achievement {
        val achievementId = request.achievementId
        val image = request.image
        val achievement = getAchievementOrThrow(achievementId, app)

        // check if achievement already has an image
        if (achievement.imageKey != null) {
            logger.error { "Achievement ${achievement.achievementId} already has an image" }
            throw ConflictException("Achievement already has an image")
        }

        val file = image.originalFilename?.let { File(it) }
        val uuid = UUID.randomUUID()
        val key = "apps/${app.id}/achievements/${achievement.achievementId}/$uuid"
        val imageBytes = image.bytes
        val contentType = image.contentType

        logger.info {
            "Uploading image for achievement ${achievement.achievementId}: " +
                "size=${imageBytes.size}, contentType=$contentType, " +
                "name=${image.name}, originalFilename=${file?.name}"
        }

        val metadata = mutableMapOf<String, Any>()

        file?.name?.let { metadata["originalFilename"] = it }
        contentType?.let { metadata["contentType"] = it }
        metadata["size"] = imageBytes.size

        val putObjectRequest =
            PutObjectRequest.builder()
                .bucket(properties.bucket)
                .key(key)
                .contentType(contentType)
                .metadata(
                    metadata.mapValues { it.value.toString() },
                )
                .build()

        val putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes))
        logger.debug { "Uploaded image for achievement ${achievement.achievementId}: $putObjectResponse" }

        achievement.imageKey = key
        achievement.imageMetadata = metadata

        return achievementRepository.save(achievement)
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
}
