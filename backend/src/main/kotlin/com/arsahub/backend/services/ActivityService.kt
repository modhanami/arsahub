package com.arsahub.backend.services

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.ActivityController
import com.arsahub.backend.dtos.ActivityResponse
import com.arsahub.backend.dtos.MemberResponse
import com.arsahub.backend.models.Activity
import com.arsahub.backend.models.RuleProgressTime
import com.arsahub.backend.models.TriggerType
import com.arsahub.backend.models.UserActivity
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
    fun getActivity(activityId: Long): Activity?

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
    private val userActivityRepository: UserActivityRepository,
    private val triggerRepository: TriggerRepository,
    private val actionRepository: ActionRepository,
    private val achievementRepository: AchievementRepository,
    private val userActivityAchievementRepository: UserActivityAchievementRepository,
    private val socketIOService: SocketIOService,
    private val leaderboardService: LeaderboardService,
    private val ruleProgressTimeRepository: RuleProgressTimeRepository
) : ActivityService {


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
    override fun getActivity(activityId: Long): Activity? {
        return activityRepository.findByIdOrNull(activityId)
    }
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
        activityId: Long, request: ActivityController.ActivityAddMembersRequest
    ): ActivityResponse {
        val existingEvent = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        val existingMemberUserIds = existingEvent.members.map { it.user?.externalUserId }.toSet()
        val newMemberUserIds = request.externalUserIds.filter { !existingMemberUserIds.contains(it) }
        val newMemberUsers = userRepository.findAll().filter { newMemberUserIds.contains(it.externalUserId) }
        newMemberUsers.forEach { println(it.externalUserId) }
        val newMembers = newMemberUsers.map { user ->
            UserActivity(
                user = user,
                activity = existingEvent,
            )
        }
        userActivityRepository.saveAllAndFlush(newMembers)

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
        val allActivity = activityRepository.findAll()
        println("${allActivity.size} activities found")
        val existingActivity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        val userId = request.userId

        val existingMember =
            existingActivity.members.find { it.user?.externalUserId == userId } ?: throw Exception("User not found")

        val trigger = triggerRepository.findByKey(request.key) ?: throw Exception("Trigger not found")

        val actionMessages = mutableListOf<String>()
        existingActivity.rules.filter { it.trigger?.key == trigger.key }.forEach { rule ->
            val action = rule.action ?: throw Exception("Action not found")

            val triggerType: TriggerType? = rule.triggerType
            if (triggerType == null) {
                println("Trigger type not found for rule ${rule.title} (${rule.id}), repeating")
            }

            // handle pre-built trigger types and rule user progress
            var isProgressCompleted = false
            when (triggerType?.key) {
                "times" -> {
                    val expectedCount =
                        rule.triggerTypeParams?.get("count")?.toString()?.toInt() ?: throw Exception("Count not found")
                    var ruleProgress = ruleProgressTimeRepository.findByRuleAndUserActivity(rule, existingMember)
                    if (ruleProgress == null) {
                        ruleProgress = RuleProgressTime(
                            rule = rule, userActivity = existingMember, progress = 0
                        )
                    } else {
                        if (ruleProgress.completedAt != null) {
                            return@forEach
                        }
                    }

                    ruleProgress.progress = ruleProgress.progress?.plus(1)
                    if (ruleProgress.progress == expectedCount) {
                        ruleProgress.completedAt = Instant.now()
                        isProgressCompleted = true
                        println("User ${existingMember.user?.userId} (${existingMember.user?.externalUserId}) has completed rule ${rule.title} (${rule.id}) with progress ${ruleProgress.progress}/${expectedCount}")
                    } else {
                        println("User ${existingMember.user?.userId} (${existingMember.user?.externalUserId}) has progress ${ruleProgress.progress}/${expectedCount} for rule ${rule.title} (${rule.id})")
                    }

                    ruleProgressTimeRepository.save(ruleProgress)
                }
            }

            if (triggerType != null && !isProgressCompleted) {
                return@forEach
            }

            // handle pre-built actions
            when (action.key) {
                "add_points" -> {
                    val points =
                        rule.actionParams?.get("value")?.toString()?.toInt() ?: throw Exception("Points not found")
                    existingMember.addPoints(points)
                    userActivityRepository.save(existingMember)

                    actionMessages.add("User `${existingMember.user?.userId}` (${existingMember.user?.externalUserId}) received `$points` points for activity `${existingActivity.title}` (${existingActivity.activityId}) from rule `${rule.title}` (${rule.id})")
                }

                "unlock_achievement" -> {
                    val achievementId =
                        rule.actionParams?.get("achievementId")?.toString()?.toLong()
                            ?: throw Exception("Achievement ID not found")
                    val achievement =
                        achievementRepository.findByIdOrNull(achievementId) ?: throw Exception("Achievement not found")

                    // precondition: user must not have unlocked the achievement
                    if (existingMember.userActivityAchievements.any { it.achievement?.achievementId == achievementId }) {
                        actionMessages.add("User already unlocked achievement")
                        return@forEach
                    }

                    existingMember.addAchievement(achievement)
//                    userActivityRepository.save(existingMember)
                    // save from the owning side
                    userActivityAchievementRepository.saveAll(existingMember.userActivityAchievements)

                    actionMessages.add("User `${existingMember.user?.userId}` (${existingMember.user?.externalUserId}) unlocked achievement `${achievement.title}` (${achievement.achievementId}) for activity `${existingActivity.title}` (${existingActivity.activityId}) from rule `${rule.title}` (${rule.id})")
                }
            }
        }

        actionMessages.forEach { println(it) }

    }
}
