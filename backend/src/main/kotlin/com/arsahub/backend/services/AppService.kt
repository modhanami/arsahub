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
import com.arsahub.backend.repositories.AppInvitationRepository
import com.arsahub.backend.repositories.AppInvitationStatusRepository
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.arsahub.backend.services.ruleengine.RuleEngine
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

class AppUserNotFoundException(userId: String) : NotFoundException("App user with ID $userId not found")

class AppUserAlreadyExistsException : ConflictException("App user with this UID already exists")

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
) {
    private val logger = KotlinLogging.logger {}

    fun getAppOrThrow(id: Long): App {
        return appRepository.findById(id).orElseThrow { AppNotFoundException(id) }
    }

    fun getAppUserOrThrow(
        app: App,
        userId: String,
    ): AppUser {
        return appUserRepository.findByAppAndUserId(app, userId) ?: throw AppUserNotFoundException(userId)
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

    fun listUsers(app: App): List<AppUser> {
        return appUserRepository.findAllByApp(app)
    }

    fun trigger(
        app: App,
        request: TriggerSendRequest,
        rawRequestJson: Map<String, Any>,
    ) {
        val appUser = getAppUserOrThrow(app, request.userId)
        ruleEngine.trigger(app, appUser, request, rawRequestJson) { actionResult ->
            broadcastActionResult(actionResult, app, request.userId)
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

        val invitationPendingStatus =
            appInvitationStatusRepository.findByStatusIgnoreCase("pending")
                ?: throw NotFoundException("Invitation status not found")

        val invite =
            AppInvitation(
                app = app,
                user = user,
                invitationStatus = invitationPendingStatus,
            )

        return appInvitationRepository.save(invite)
    }

    class AppInvitationNotFoundException : NotFoundException("Invitation not found")

    class AppInvitationAcceptedException : ConflictException("Invitation already resolved")

    @Transactional
    fun acceptInvitation(
        identity: UserIdentity,
        id: Long,
    ) {
        // invitation exists and is for the user
        val invitation =
            appInvitationRepository.findByUserUserId(
                identity.internalUserId,
            )
                ?: throw AppInvitationNotFoundException()

        if (invitation.user?.userId != identity.internalUserId) {
            throw AppInvitationNotFoundException()
        }

        // is pending
        if (invitation.invitationStatus?.status != "pending") {
            throw AppInvitationAcceptedException()
        }

        // resolve invitation
        val acceptedStatus =
            appInvitationStatusRepository.findByStatusIgnoreCase("accepted")
                ?: throw NotFoundException("Invitation status not found")

        invitation.invitationStatus = acceptedStatus
        appInvitationRepository.save(invitation)

//        // add user to app
//        val user = invitation.user!!
//        val app = invitation.app!!
//        val appUser =
//            AppUser(
//                userId = user.userId!!,
//                displayName = user.displayName!!,
//                app = app,
//                points = 0,
//            )
    }
}
