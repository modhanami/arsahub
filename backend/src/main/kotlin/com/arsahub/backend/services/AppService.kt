package com.arsahub.backend.services

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.dtos.request.AppUserCreateRequest
import com.arsahub.backend.dtos.request.TriggerSendRequest
import com.arsahub.backend.dtos.response.AchievementResponse
import com.arsahub.backend.dtos.socketio.AchievementUnlock
import com.arsahub.backend.dtos.socketio.LeaderboardUpdate
import com.arsahub.backend.dtos.socketio.PointsUpdate
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.arsahub.backend.services.ruleengine.RuleEngine
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

class AppUserNotFoundException(userId: String) : NotFoundException("App user with ID $userId not found")

class AppUserAlreadyExistsException : ConflictException("App user with this UID already exists")

class AppNotFoundException(appId: Long) : NotFoundException("App with ID $appId not found")

class AppNotFoundForUserException : Exception("App not found for this user")

class UserNotFoundException : NotFoundException("User with this ID not found")

@Service
class AppService(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val appUserRepository: AppUserRepository,
    private val socketIOService: SocketIOService,
    private val leaderboardService: LeaderboardService,
    private val ruleEngine: RuleEngine,
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

    fun getUserById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { UserNotFoundException() }
    }
}
