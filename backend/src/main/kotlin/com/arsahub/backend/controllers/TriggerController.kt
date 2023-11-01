package com.arsahub.backend.controllers

import com.arsahub.backend.models.Trigger
import com.arsahub.backend.repositories.ActivityRepository
import com.arsahub.backend.repositories.TriggerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/triggers")
class TriggerController(
    private val triggerRepository: TriggerRepository,
    private val activityRepository: ActivityRepository
) {
    data class TriggerCreateRequest(
        val title: String,
        val description: String,
        val activityId: Long,
    )

    @PostMapping
    fun createTrigger(@RequestBody request: TriggerCreateRequest): Trigger {
        val activity = activityRepository.findByIdOrNull(request.activityId) ?: throw Exception("Activity not found")
        val trigger = Trigger(
            title = request.title,
            description = request.description,
            activity = activity
        )
        return triggerRepository.save(trigger)
    }
}