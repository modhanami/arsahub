package com.arsahub.backend.services

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.AppController
import com.arsahub.backend.dtos.request.AppUserCreateRequest
import com.arsahub.backend.dtos.request.TriggerSendRequest
import com.arsahub.backend.dtos.response.AchievementResponse
import com.arsahub.backend.dtos.socketio.AchievementUnlock
import com.arsahub.backend.dtos.socketio.LeaderboardUpdate
import com.arsahub.backend.dtos.socketio.PointsUpdate
import com.arsahub.backend.dtos.supabase.UserIdentity
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppInvitation
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.AppUserPointsHistory
import com.arsahub.backend.models.Webhook
import com.arsahub.backend.models.WebhookRepository
import com.arsahub.backend.repositories.AppInvitationRepository
import com.arsahub.backend.repositories.AppInvitationStatusRepository
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserPointsHistoryRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.arsahub.backend.services.ruleengine.RuleEngine
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import java.net.URI
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
    private val appInvitationStatusRepository: AppInvitationStatusRepository,
    private val appInvitationRepository: AppInvitationRepository,
    private val appUserPointsHistoryRepository: AppUserPointsHistoryRepository,
    private val webhookRepository: WebhookRepository,
) {
    private val logger = KotlinLogging.logger {}
    private val restClient = RestClient.builder().build()

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

    fun trigger(
        app: App,
        @Valid request: TriggerSendRequest,
        rawRequestJson: Map<String, Any>,
    ) {
        val appUser = getAppUserOrThrow(app, request.userId!!)
        ruleEngine.trigger(app, appUser, request, rawRequestJson) { actionResult, rule ->
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

            broadcastActionResult(actionResult, app, request.userId)
            publishWebhookEvents(app, appUser, actionResult)
        }
    }

    data class WebhookPayload(
        val id: UUID,
        val event: String,
        val appUserId: String,
        val payload: Map<String, Any>,
    )

    private fun publishWebhookEvents(
        app: App,
        appUser: AppUser,
        actionResult: ActionResult,
    ) {
        val appWebhook = webhookRepository.findByApp(app).firstOrNull()?.url?.let { URI(it) } ?: return
        val eventKey =
            when (actionResult) {
                is ActionResult.AchievementUpdate -> "achievement_unlocked"
                is ActionResult.PointsUpdate -> "points_updated"
                else -> return
            }

        val payload =
            when (actionResult) {
                is ActionResult.AchievementUpdate -> {
                    mapOf(
                        "achievement" to AchievementResponse.fromEntity(actionResult.achievement),
                    )
                }

                is ActionResult.PointsUpdate -> {
                    mapOf(
                        "points" to actionResult.newPoints,
                        "pointsChange" to actionResult.pointsAdded,
                    )
                }

                else -> {
                    return
                }
            }

        val response =
            restClient.post()
                .uri(appWebhook)
                .body(
                    WebhookPayload(
                        id = UUID.randomUUID(),
                        event = eventKey,
                        appUserId = appUser.userId!!,
                        payload = payload,
                    ),
                )
                .retrieve()
                .toBodilessEntity()

        if (response.statusCode.isError) {
            logger.error { "Webhook failed: ${response.statusCode}" }
            // TODO: handle webhook failure
        } else {
            logger.debug { "Webhook succeeded: ${response.statusCode}" }
        }
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

    class UserAlreadyInvitedException : ConflictException("User already invited")

    fun inviteUser(
        app: App,
        request: AppController.InviteUserRequest,
    ): AppInvitation {
        val user = userRepository.findByEmail(request.email) ?: throw UserNotFoundException()

        // check if user is already a member
        val appUser = appUserRepository.findByAppAndUserEmail(app, request.email)
        if (appUser != null) {
            throw AppUserAlreadyExistsException()
        }

        // check existing invitation
        val existingInvitation = appInvitationRepository.findByAppAndUser(app, user)
        if (existingInvitation != null) {
            throw UserAlreadyInvitedException()
        }

        val invitationPendingStatus = getPendingAppInvitationStatusOrThrow()

        val invite =
            AppInvitation(
                app = app,
                user = user,
                invitationStatus = invitationPendingStatus,
            )

        return appInvitationRepository.save(invite)
    }

    class AppInvitationNotFoundException : NotFoundException("Invitation not found")

    class AppInvitationNotInPendingStateException : ConflictException("Invitation is not pending")

    @Transactional
    fun acceptInvitation(
        invitationId: Long,
        identity: UserIdentity,
    ) {
        val invitation = getInvitationOrThrow(invitationId)
        assertInvitationIsForUserOrThrow(invitation, identity)

        assertCanAcceptInvitationOrThrow(invitation)

        // accept invitation
        val acceptedStatus = getAcceptedAppInvitationStatusOrThrow()

        invitation.invitationStatus = acceptedStatus
        appInvitationRepository.save(invitation)

        // add user to app
        val user = invitation.user!!
        val app = invitation.app!!
        val appUser =
            AppUser(
                app = app,
                user = user,
                userId = user.email!!,
                displayName = user.name!!,
                points = 0,
            )

        appUserRepository.save(appUser)
    }

    private fun assertCanAcceptInvitationOrThrow(invitation: AppInvitation) {
        if (invitation.invitationStatus?.status != "pending") {
            throw AppInvitationNotInPendingStateException()
        }
    }

    @Transactional
    fun declineInvitation(
        invitationId: Long,
        identity: UserIdentity,
    ) {
        val invitation = getInvitationOrThrow(invitationId)
        assertInvitationIsForUserOrThrow(invitation, identity)

        assertCanDeclineInvitationOrThrow(invitation)

        val declinedStatus = getDeclinedAppInvitationStatusOrThrow()

        invitation.invitationStatus = declinedStatus
        appInvitationRepository.save(invitation)
    }

    private fun assertCanDeclineInvitationOrThrow(invitation: AppInvitation) {
        if (invitation.invitationStatus?.status != "pending") {
            throw AppInvitationNotInPendingStateException()
        }
    }

    private fun getInvitationOrThrow(id: Long): AppInvitation {
        return appInvitationRepository.findByIdOrNull(id)
            ?: throw AppInvitationNotFoundException()
    }

    private fun assertInvitationIsForUserOrThrow(
        invitation: AppInvitation,
        identity: UserIdentity,
    ) {
        if (invitation.user?.userId != identity.internalUserId) {
            throw AppInvitationNotFoundException()
        }
    }

    private fun getAcceptedAppInvitationStatusOrThrow() =
        checkNotNull(appInvitationStatusRepository.findByStatusIgnoreCase("accepted")) {
            "App invitation status 'accepted' not found"
        }

    private fun getPendingAppInvitationStatusOrThrow() =
        checkNotNull(appInvitationStatusRepository.findByStatusIgnoreCase("pending")) {
            "App invitation status 'pending' not found"
        }

    private fun getDeclinedAppInvitationStatusOrThrow() =
        checkNotNull(appInvitationStatusRepository.findByStatusIgnoreCase("declined")) {
            "App invitation status 'declined' not found"
        }

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

    fun createWebhook(
        app: App,
        request: AppController.WebhookCreateRequest,
    ): Webhook {
        // TODO: validate URL, and check if it's reachable?
        // TODO: multiple webhooks per app
        // TODO: specify events to listen to
        // TODO: shared secret for signing requests

        // limit to one webhook per app for now
        val existingWebhooks = webhookRepository.findByApp(app)
        if (existingWebhooks.isNotEmpty()) {
            throw ConflictException("Only one webhook per app is allowed")
        }

        val webhook = Webhook(app = app, url = request.url)
        logger.debug { "Creating webhook for app ${app.title}: ${webhook.url}" }

        return webhookRepository.save(webhook)
    }

    fun updateWebhook(
        app: App,
        webhookId: Long,
        request: AppController.WebhookCreateRequest,
    ): Webhook {
        val webhook = webhookRepository.findByAppAndId(app, webhookId) ?: throw NotFoundException("Webhook not found")
        logger.debug { "Updating webhook for app ${app.title}: ${webhook.url} -> ${request.url}" }
        webhook.url = request.url
        return webhookRepository.save(webhook)
    }
}
