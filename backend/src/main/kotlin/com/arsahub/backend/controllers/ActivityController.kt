package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.ActivityResponse
import com.arsahub.backend.dtos.LeaderboardResponse
import com.arsahub.backend.dtos.MemberResponse
import com.arsahub.backend.services.ActivityService
import com.arsahub.backend.services.LeaderboardService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    private val activityService: ActivityService,
    private val leaderboardService: LeaderboardService
) {

//    @PostMapping
//    fun createEvent(
//        @CurrentUser user: CustomUserDetails,
//        @RequestBody eventCreateRequest: EventCreateRequest
//    ): EventResponse {
//        return eventService.createEvent(user, eventCreateRequest)
//    }
//
//    fun canCreateEvent(user: CustomUserDetails): Boolean {
//        return user.isOrganizer()
//    }

//    @PutMapping("/{activityId}")
//    fun updateEvent(
//        @CurrentUser user: CustomUserDetails,
//        @PathVariable activityId: Long,
//        @RequestBody eventUpdateRequest: EventUpdateRequest
//    ): EventResponse {
//        return eventService.updateEvent(user, activityId, eventUpdateRequest)
//    }

    //    @GetMapping("/{activityId}")
//    fun getEvent(@CurrentUser user: CustomUserDetails, @PathVariable activityId: Long): Activity? {
//        println(user)
//        return eventService.getEvent(activityId)
//    }
//
    @GetMapping
    fun listActivities(): List<ActivityResponse> {
        return activityService.listActivities()
    }

//    @DeleteMapping("/{activityId}")
//    fun deleteEvent(@CurrentUser user: CustomUserDetails, @PathVariable activityId: Long) {
//        eventService.deleteEvent(user, activityId)
//    }

    data class ActivityAddMembersRequest(val externalUserIds: List<String>)

    @PostMapping("/{activityId}/members")
    fun addMembers(@PathVariable activityId: Long, @RequestBody request: ActivityAddMembersRequest): ActivityResponse {
        return activityService.addMembers(activityId, request)
    }

//    @GetMapping("/joined")
//    fun listJoinedEvents(@CurrentUser user: CustomUserDetails): List<Activity> {
//        return activityService.listJoinedEvents(user)
//    }

    //    listMembers
    @GetMapping("/{activityId}/members")
    fun listMembers(@PathVariable activityId: Long): List<MemberResponse> {
        return activityService.listMembers(activityId)
    }

    //    trigger the defined effect
    data class ActivityTriggerRequest(val type: String, val params: Map<String, String>, val userId: String)

    @PostMapping("/{activityId}/trigger")
    fun trigger(@RequestBody request: ActivityTriggerRequest, @PathVariable activityId: Long) {
        return activityService.trigger(activityId, request)
    }

    // leaderboard
    @GetMapping("/{activityId}/leaderboard")
    fun leaderboard(@PathVariable activityId: Long, @RequestParam type: String): LeaderboardResponse {
        if (type == "total-points") {
            return leaderboardService.getTotalPointsLeaderboard(activityId)
        }
        return LeaderboardResponse(leaderboard = "total-points", entries = emptyList())
    }

    @GetMapping("/{activityId}/ws-request")
    fun requestWebsocketConnection(@PathVariable activityId: Long): String {
        return activityService.requestWebsocketConnection(activityId)
    }

    @GetMapping("/{activityId}/user-ws-request/{userId}")
    fun requestUserWebsocketConnection(@PathVariable activityId: Long, @PathVariable userId: String): String {
        return activityService.requestUserWebsocketConnection(activityId, userId)
    }
}
