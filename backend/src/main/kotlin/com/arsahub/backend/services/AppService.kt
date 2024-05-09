package com.arsahub.backend.services

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.AppController
import com.arsahub.backend.dtos.AnalyticsConstants
import com.arsahub.backend.dtos.request.AppUserCreateRequest
import com.arsahub.backend.dtos.request.AppUserUpdateRequest
import com.arsahub.backend.dtos.request.TriggerSendRequest
import com.arsahub.backend.dtos.request.WebhookCreateRequest
import com.arsahub.backend.dtos.response.*
import com.arsahub.backend.dtos.socketio.AchievementUnlock
import com.arsahub.backend.dtos.socketio.LeaderboardUpdate
import com.arsahub.backend.dtos.socketio.PointsUpdate
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.*
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.arsahub.backend.services.ruleengine.RuleEngine
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.time.Instant
import java.util.*

class AppUserNotFoundException : NotFoundException("App user not found")

class AppUserAlreadyExistsException : ConflictException("App user with this UID already exists")

class SomeUsersAlreadyExistException : ConflictException("Some users already exist")

class AppNotFoundException(appId: Long) : NotFoundException("App with ID $appId not found")

class AppNotFoundForUserException : NotFoundException("App not found for this user")

class UserNotFoundException : NotFoundException("User with this ID not found")

@Service
class AppService(
    private val appRepository: AppRepository,
    private val appUserRepository: AppUserRepository,
    private val socketIOService: SocketIOService,
    private val leaderboardService: LeaderboardService,
    private val ruleEngine: RuleEngine,
    private val userRepository: UserRepository,
    private val appUserPointsHistoryRepository: AppUserPointsHistoryRepository,
    private val webhookRepository: WebhookRepository,
    private val webhookDeliveryService: WebhookDeliveryService,
    private val achievementRepository: AchievementRepository,
    private val appUserAchievementRepository: AppUserAchievementRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val webhookRequestRepository: WebhookRequestRepository,
    private val properties: MyServiceProperties,
    private val s3Client: S3Client,
) {
    private val logger = KotlinLogging.logger {}

    fun getAppOrThrow(id: Long): App {
        return appRepository.findById(id).orElseThrow { AppNotFoundException(id) }
    }

    fun getAppUserOrThrow(
        app: App,
        userId: String,
    ): AppUser {
        return appUserRepository.findByAppAndUserId(app, userId) ?: throw AppUserNotFoundException()
    }

    fun getAppUserOrThrow(
        appId: Long,
        userId: String,
    ): AppUser {
        val app = getAppOrThrow(appId)
        return getAppUserOrThrow(app, userId)
    }

    fun getAppByUserId(userId: Long): App {
        return appRepository.findFirstByOwner_UserId(userId) ?: throw AppNotFoundForUserException()
    }

    fun addUser(
        app: App,
        request: AppUserCreateRequest,
    ): AppUser {
        val appUser = appUserRepository.findByAppAndUserId(app, request.uniqueId)
        if (appUser != null) {
            throw AppUserAlreadyExistsException()
        }
        val newAppUser =
            AppUser(
                userId = request.uniqueId,
                displayName = request.displayName,
                app = app,
                points = 0,
            )
        appUserRepository.save(newAppUser)
        return newAppUser
    }

    @Transactional
    fun addUsers(
        app: App,
        requests: List<AppUserCreateRequest>,
    ): List<AppUser> {
        val userIds = requests.map { it.uniqueId }
        val existingUsers = appUserRepository.findByAppAndUserIdIn(app, userIds)
        if (existingUsers.isNotEmpty()) {
            throw SomeUsersAlreadyExistException()
        }
        val newAppUsers =
            requests.map { request ->
                AppUser(
                    userId = request.uniqueId,
                    displayName = request.displayName,
                    app = app,
                    points = 0,
                )
            }
        return appUserRepository.saveAll(newAppUsers)
    }

    fun listUsers(app: App): List<AppUser> {
        return appUserRepository.findAllByApp(app)
    }

    // TODO: use a queue for async processing
    @Transactional
    fun trigger(
        app: App,
        @Valid request: TriggerSendRequest,
        rawRequestJson: Map<String, Any>,
    ) {
        val actionResults = mutableListOf<Pair<ActionResult, Rule>>()

        val appUser = getAppUserOrThrow(app, request.userId!!)
        ruleEngine.trigger(app, appUser, request, rawRequestJson) { actionResult, rule ->
            actionResults.add(actionResult to rule)
            Unit
        }
        handleActionResults(actionResults, app, appUser)
    }

    private fun handleActionResults(
        actionResults: MutableList<Pair<ActionResult, Rule>>,
        app: App,
        appUser: AppUser,
    ) {
        runBlocking {
            actionResults.forEach { (actionResult, rule) ->
                // save points history
                if (actionResult is ActionResult.PointsUpdate) {
                    val pointsHistory =
                        AppUserPointsHistory(
                            app = app,
                            appUser = appUser,
                            points = actionResult.newPoints.toLong(), // TODO: convert the source type to long?
                            pointsChange = actionResult.pointsAdded.toLong(),
                            fromRule = rule,
                        )
                    logger.debug {
                        "Saving points history for app user ${appUser.userId} in app ${app.title}: " +
                            "pointsChange=${pointsHistory.pointsChange}, points=${pointsHistory.points}"
                    }
                    appUserPointsHistoryRepository.save(pointsHistory)
                }

                val appWebhooks = webhookRepository.findByApp(app)

                broadcastActionResult(actionResult, app, appUser.userId!!)

                withContext(Dispatchers.IO) {
                    runCatching {
                        webhookDeliveryService.publishWebhookEvents(app, appWebhooks, appUser, actionResult)
                    }.onFailure {
                        logger.error(it) { "Failed to deliver webhook events" }
                    }
                }
            }
        }
    }

    fun dryTrigger(
        app: App,
        @Valid request: TriggerSendRequest,
        rawRequestJson: Map<String, Any>,
    ): List<Rule> {
        val appUser = getAppUserOrThrow(app, request.userId!!)
        return ruleEngine.dryTrigger(app, appUser, request)
    }

    private fun broadcastActionResult(
        actionResult: ActionResult,
        app: App,
        userId: String,
    ) {
        when (actionResult) {
            is ActionResult.AchievementUpdate -> {
                val (achievement) = actionResult
                socketIOService.broadcastToAppRoom(
                    app,
                    AchievementUnlock(
                        userId = userId,
                        achievement =
                            AchievementResponse.fromEntity(
                                achievement,
                            ),
                    ),
                )
                socketIOService.broadcastToUserRoom(
                    userId,
                    AchievementUnlock(
                        userId = userId,
                        achievement =
                            AchievementResponse.fromEntity(
                                achievement,
                            ),
                    ),
                )
            }

            is ActionResult.PointsUpdate -> {
                val (_, newPoints, _) = actionResult
                socketIOService.broadcastToAppRoom(
                    app,
                    PointsUpdate(
                        userId = userId,
                        points = newPoints,
                    ),
                )
                socketIOService.broadcastToAppRoom(
                    app,
                    LeaderboardUpdate(
                        leaderboard = leaderboardService.getTotalPointsLeaderboard(app.id!!),
                    ),
                )

                socketIOService.broadcastToUserRoom(
                    userId,
                    PointsUpdate(
                        userId = userId,
                        points = newPoints,
                    ),
                )
            }

            is ActionResult.Nothing -> {}
        }
    }

    class AppInvitationNotFoundException : NotFoundException("Invitation not found")

    class AppInvitationNotInPendingStateException : ConflictException("Invitation is not pending")

    fun deleteAppUser(
        app: App,
        userId: String,
    ) {
        val appUser = getAppUserOrThrow(app, userId)
        assertCanDeleteAppUserOrThrow(app, appUser)
        appUserRepository.delete(appUser)
    }

    private fun assertCanDeleteAppUserOrThrow(
        app: App,
        appUser: AppUser,
    ) {
        if (app.owner == appUser.user) {
            throw AppUserNotFoundException()
        }
    }

    @Transactional
    fun createWebhook(
        app: App,
        request: WebhookCreateRequest,
    ): Webhook {
        // TODO: check if it's reachable?
        // TODO: specify events to listen to
        // TODO: shared secret for signing requests

        webhookRepository.findByAppAndUrl(app, request.url!!).let {
            if (it != null) {
                throw ConflictException("Webhook already exists")
            }
        }

        val secretKey = UUID.randomUUID().toString()
        val webhook = Webhook(app = app, url = request.url, secretKey = secretKey)
        logger.debug { "Creating webhook for app ${app.title}: ${webhook.url}" }

        return webhookRepository.save(webhook)
    }

    fun updateWebhook(
        app: App,
        webhookId: Long,
        request: WebhookCreateRequest,
    ): Webhook {
        val webhook = webhookRepository.findByAppAndId(app, webhookId) ?: throw NotFoundException("Webhook not found")
        logger.debug { "Updating webhook for app ${app.title}: ${webhook.url} -> ${request.url}" }
        webhook.url = request.url
        return webhookRepository.save(webhook)
    }

    fun listWebhooks(app: App): List<Webhook> {
        return webhookRepository.findByApp(app)
    }

    fun deleteWebhook(
        app: App,
        webhookId: Long,
    ) {
        val webhook = webhookRepository.findByAppAndId(app, webhookId) ?: throw NotFoundException("Webhook not found")

        webhook.markAsDeleted()

        webhookRepository.save(webhook)
    }

    fun getWebhookRequests(
        app: App,
        webhookId: Long,
    ): List<WebhookRequestResponse> {
        val webhook = webhookRepository.findByAppAndId(app, webhookId) ?: throw NotFoundException("Webhook not found")
        return webhookRequestRepository.findByAppAndWebhookAndStatusOrderByCreatedAtDesc(
            app,
            webhook,
            WebhookRequestStatusEnum.SUCCESS.entity,
        ).map { WebhookRequestResponse.fromEntity(it) }
    }

    fun updateAppUser(
        app: App,
        userId: String,
        request: AppUserUpdateRequest,
    ): AppUser {
        val appUser = getAppUserOrThrow(app, userId)
        appUser.displayName = request.displayName
        return appUserRepository.save(appUser)
    }

    @Transactional
    fun addPointsToUser(
        app: App,
        userId: String,
        request: AppController.AppUserPointsAddRequest,
    ) {
        val appUser = getAppUserOrThrow(app, userId)
        val points = request.points
        val newPoints = appUser.points!! + points
        appUser.points = newPoints
        appUserRepository.save(appUser)

        val pointsHistory =
            AppUserPointsHistory(
                app = app,
                appUser = appUser,
                points = newPoints.toLong(), // TODO: convert the source type to long?
                pointsChange = points.toLong(),
            )
        logger.debug {
            "Saving points history for app user ${appUser.userId} in app ${app.title}: " +
                "pointsChange=${pointsHistory.pointsChange}, points=${pointsHistory.points}"
        }
        appUserPointsHistoryRepository.save(pointsHistory)

        val actionResults = mutableListOf<Pair<ActionResult, Rule>>()
        ruleEngine.handleForwardChain(app, appUser) { actionResult, rule ->
            actionResults.add(actionResult to rule)
            Unit
        }
        handleActionResults(actionResults, app, appUser)
    }

    fun unlockAchievementForUser(
        app: App,
        userId: String,
        request: AppController.AppUserAchievementUnlockRequest,
    ) {
        val appUser = getAppUserOrThrow(app, userId)
        val achievement =
            achievementRepository.findByAchievementIdAndApp(request.achievementId, app)
                ?: throw NotFoundException("Achievement not found")

        /**
         * @see com.arsahub.backend.services.actionhandlers.ActionUnlockAchievementHandler
         */
        if (appUser.achievements.any { it.achievement?.achievementId == achievement.achievementId }) {
            val message =
                "User ${appUser.displayName}` (${appUser.userId}) already unlocked achievement"
            logger.info { message }
            return
        }

        appUser.addAchievement(achievement, Instant.now())
        // save from the owning side
        appUserAchievementRepository.saveAll(appUser.achievements)

        logger.info {
            "User ${appUser.displayName}` (${appUser.userId}) unlocked achievement " +
                "`${achievement.title}` (${achievement.achievementId}) from direct unlock"
        }
    }

    fun getAnalytics(
        app: App,
        type: String,
        start: Instant,
        end: Instant,
    ): Any {
        val timeRange = TimeRange(start, end)
        logger.info { "Getting analytics for app ${app.title}: type=$type, start=$start, end=$end" }

        if (start.isAfter(end)) {
            throw IllegalArgumentException("Start time must not be after end time")
        }

        val analyticsType =
            AnalyticsConstants.fromString(type) ?: throw NotFoundException("Invalid analytics type")

        return when (analyticsType) {
            AnalyticsConstants.TOTAL_UNLOCKED_ACHIEVEMENTS -> {
                analyticsRepository.getTotalUnlockedAchievements(app, timeRange)
            }

            AnalyticsConstants.TOP_10_ACHIEVEMENTS -> {
                analyticsRepository.getAchievementsWithUnlockedCount(app, timeRange).map {
                    AchievementWithUnlockCountResponse.fromEntity(it)
                }
            }

            AnalyticsConstants.TOP_10_TRIGGERS -> {
                analyticsRepository.getTriggersWithTriggerCount(app, timeRange).map {
                    TriggerWithTriggerCountResponse.fromEntity(it)
                }
            }

            AnalyticsConstants.TOTAL_APP_USERS -> {
                analyticsRepository.getTotalAppUsers(app, timeRange) ?: 0
            }

            AnalyticsConstants.TOTAL_POINTS_EARNED -> {
                analyticsRepository.getTotalPointsEarned(app, timeRange) ?: 0
            }
        }
    }

    fun getPointsHistory(
        app: App,
        userId: String,
    ): List<AppUserPointsHistory> {
        val appUser = getAppUserOrThrow(app, userId)
        return appUserPointsHistoryRepository.findAllByAppAndAppUserOrderByCreatedAtDesc(app, appUser)
    }

    fun uploadTempImage(image: MultipartFile): TempImageUploadResponse {
        validateImageOrThrow(image)

        val file = image.originalFilename?.let { File(it) }
        val uuid = UUID.randomUUID()
        val key = "temp-images/$uuid"
        val imageBytes = image.bytes
        val contentType = image.contentType

        logger.info {
            "Uploading temp image for achievement $uuid: " +
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
        logger.debug { "Uploaded temp image $uuid: response=$putObjectResponse" }

        return TempImageUploadResponse(id = uuid.toString())
    }

    private fun validateImageOrThrow(image: MultipartFile) {
        if (image.isEmpty) {
            logger.error { "Image is empty" }
            throw ConflictException("Image is empty")
        }

        if (image.contentType?.startsWith("image/") != true) {
            logger.error { "Invalid image content type: ${image.contentType}" }
            throw ConflictException("Invalid image content type")
        }
    }

    companion object {
        const val WEBHOOK_TIMEOUT_SECONDS: Long = 5
    }
}
