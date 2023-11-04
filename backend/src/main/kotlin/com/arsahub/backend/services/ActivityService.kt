package com.arsahub.backend.services

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.ActivityController
import com.arsahub.backend.dtos.ActivityResponse
import com.arsahub.backend.dtos.MemberResponse
import com.arsahub.backend.models.AchievementProgress
import com.arsahub.backend.models.Member
import com.arsahub.backend.repositories.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

interface ActivityService {
    //    fun createEvent(currentUser: CustomUserDetails, eventCreateRequest: EventCreateRequest): EventResponse
//    fun updateEvent(
//        currentUser: CustomUserDetails,
//        activityId: Long,
//        eventUpdateRequest: EventUpdateRequest
//    ): EventResponse
//
//    fun getEvent(activityId: Long): Activity?
//    fun listEvents(): List<Activity>
//    fun deleteEvent(currentUser: CustomUserDetails, activityId: Long)
    fun addMembers(activityId: Long, request: ActivityController.ActivityAddMembersRequest): ActivityResponse
    fun listMembers(activityId: Long): List<MemberResponse>
    fun listActivities(): List<ActivityResponse>
    fun trigger(activityId: Long, request: ActivityController.ActivityTriggerRequest)
//    fun listJoinedEvents(currentUser: CustomUserDetails): List<Activity>
}

@Service
class ActivityServiceImpl(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val memberRepository: MemberRepository,
    private val achievementRepository: AchievementRepository,
    private val achievementProgressRepository: AchievementProgressRepository,
    private val socketIOService: SocketIOService,
    private val leaderboardService: LeaderboardService,
) :
    ActivityService {

//    override fun createEvent(currentUser: CustomUserDetails, eventCreateRequest: EventCreateRequest): EventResponse {
//        val activityToSave = Activity(
//            title = eventCreateRequest.title,
//            description = eventCreateRequest.description,
//            points = eventCreateRequest.points,
//            organizerId = currentUser.userId
//        )
//        val savedEvent = eventRepository.save(activityToSave)
//        return EventResponse.fromEntity(savedEvent)
//    }
//
//    override fun updateEvent(
//        currentUser: CustomUserDetails,
//        activityId: Long,
//        eventUpdateRequest: EventUpdateRequest
//    ): EventResponse {
//        println("updateEvent")
//        val existingEvent = eventRepository.findByIdOrNull(activityId)
//            ?: throw EntityNotFoundException("Activity with ID $activityId not found")
//
//        if (!canUpdateEvent(currentUser, existingEvent)) {
//            throw AccessDeniedException("Cannot update event with ID $activityId")
//        }
//
//        val updatedEvent = updateEvent(existingEvent, eventUpdateRequest)
//        return EventResponse.fromEntity(eventRepository.save(updatedEvent))
//    }
//
//    fun canUpdateEvent(currentUser: CustomUserDetails, activity: Activity): Boolean {
//        if (currentUser.isAdmin()) {
//            return true
//        }
//        return activity.organizer?.organizerId == currentUser.userId
//    }
//
//
//    private fun updateEvent(existingActivity: Activity, eventUpdateRequest: EventUpdateRequest): Activity {
//        if (eventUpdateRequest.title != null) {
//            existingActivity.title = eventUpdateRequest.title
//        }
//        if (eventUpdateRequest.description != null) {
//            existingActivity.description = eventUpdateRequest.description
//        }
//        return existingActivity
//    }
//
//    override fun getEvent(activityId: Long): Activity? {
//        return eventRepository.findByIdOrNull(activityId)
//    }
//
//    override fun listEvents(): List<Activity> {
//        return eventRepository.findAll()
//    }
//
//    override fun deleteEvent(currentUser: CustomUserDetails, activityId: Long) {
//        if (!eventRepository.existsById(activityId)) {
//            throw EntityNotFoundException("Activity with ID $activityId not found")
//        }
//        eventRepository.deleteById(activityId)
//    }

    override fun addMembers(
        activityId: Long,
        request: ActivityController.ActivityAddMembersRequest
    ): ActivityResponse {
        val existingEvent = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        val existingMemberUserIds = existingEvent.members.map { it.user.externalUserId }.toSet()
        val newMemberUserIds = request.externalUserIds.filter { !existingMemberUserIds.contains(it) }
        val newMemberUsers = userRepository.findAll().filter { newMemberUserIds.contains(it.externalUserId) }
        newMemberUsers.forEach { println(it.externalUserId) }
        val newMembers = newMemberUsers.map { user ->
            Member(
                user = user,
                activity = existingEvent,
            )
        }
        memberRepository.saveAllAndFlush(newMembers)

        return ActivityResponse.fromEntity(
            activityRepository.findByIdOrNull(activityId)
                ?: throw EntityNotFoundException("Activity with ID $activityId not found")
        )
    }

    //    get all member of an activity
    override fun listMembers(activityId: Long): List<MemberResponse> {
        val existingEvent = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        return existingEvent.members.map { MemberResponse.fromEntity(it) }.toList()
    }

//    override fun listJoinedEvents(currentUser: CustomUserDetails): List<Activity> {
//        val existingUser = userRepository.findByIdOrNull(currentUser.userId)
//            ?: throw EntityNotFoundException("User with ID ${currentUser.userId} not found")
//
//        return existingUser.members.map { it.activity }
//    }
//
//    override fun releasePoints(
//        user: CustomUserDetails,
//        activityId: Long,
//        request: ActivityController.EventReleasePointsRequest
//    ): Activity {
//        val existingEvent = eventRepository.findByIdOrNull(activityId)
//            ?: throw EntityNotFoundException("Activity with ID $activityId not found")
//
//        val existingMemberUsers = existingEvent.members.map { it.user }
//        val mapOfExistingMemberUsers = existingMemberUsers.associateBy { it.userId }
//        request.userIds.forEach {
//            mapOfExistingMemberUsers[it]?.addPoints(existingEvent.points)
//        }
//
//        return memberRepository.saveAllAndFlush(existingEvent.members).firstOrNull()?.activity
//            ?: throw EntityNotFoundException("Activity with ID $activityId not found")
//     }

    override fun listActivities(): List<ActivityResponse> {
        return activityRepository.findAll().map { ActivityResponse.fromEntity(it) }
    }

    override fun trigger(activityId: Long, request: ActivityController.ActivityTriggerRequest) {
        val existingActivity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        val triggerType = request.type
        val triggerParams = request.params
        val userId = request.userId

        val existingMember = existingActivity.members.find { it.user.externalUserId == userId }
            ?: throw Exception("User not found")

        when (triggerType) {
            "add-points" -> {
                val points = triggerParams["points"]?.toIntOrNull() ?: throw Exception("Invalid points")
                existingMember.points += points
                memberRepository.saveAndFlush(existingMember)

                val totalPointsLeaderboard = leaderboardService.getTotalPointsLeaderboard(existingActivity.activityId)
                socketIOService.broadcastToActivityRoom(
                    activityId,
                    SocketIOService.LeaderboardUpdate(totalPointsLeaderboard)
                )
                socketIOService.broadcastToActivityRoom(
                    activityId,
                    SocketIOService.PointsUpdate(
                        userId = userId,
                        points = existingMember.points
                    )
                )
            }

            "progress-achievement" -> {
                val achievementId =
                    triggerParams["achievementId"]?.toLongOrNull() ?: throw Exception("Invalid achievementId")
                val progress = triggerParams["progress"]?.toIntOrNull() ?: 1
                val existingAchievement = achievementRepository.findByIdOrNull(achievementId)
                    ?: throw Exception("Achievement not found")
                // find existing achievement progress or create new one
                val achievementProgress =
                    existingAchievement.achievementProgresses.find { it.member == existingMember }
                        ?: AchievementProgress(
                            member = existingMember,
                            achievement = existingAchievement,
                        )
                achievementProgress.progress += progress

                if (achievementProgress.progress > existingAchievement.requiredProgress) {
//                    return
                    achievementProgress.progress = existingAchievement.requiredProgress
                }
                if (achievementProgress.progress == existingAchievement.requiredProgress) {
                    achievementProgress.completedAt = Instant.now()
                    println("${existingMember.user.externalUserId} completed achievement ${existingAchievement.title}")
                } else {
                    println("${existingMember.user.externalUserId} progress achievement ${existingAchievement.title}")
                }

                achievementProgressRepository.saveAndFlush(achievementProgress)

                socketIOService.broadcastToActivityRoom(
                    activityId,
                    SocketIOService.AchievementUpdate(
                        userId = userId,
                        achievementId = achievementId,
                        progress = achievementProgress.progress
                    )
                )
            }

            else -> {
                throw Exception("Invalid trigger type")
            }
        }
    }
}
