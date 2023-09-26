package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.OrganizerCreateRequest
import com.arsahub.backend.dtos.OrganizerResponse
import com.arsahub.backend.dtos.OrganizerUpdateRequest
import com.arsahub.backend.models.Organizer
import com.arsahub.backend.services.OrganizerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/organizers")
class OrganizerController(private val organizerService: OrganizerService) {

    @PostMapping
    fun createOrganizer(@RequestBody organizerCreateRequest: OrganizerCreateRequest): OrganizerResponse {
        return organizerService.createOrganizer(organizerCreateRequest)
    }

    @PatchMapping("/{organizerId}")
    fun updateOrganizer(
        @PathVariable organizerId: Long,
        @RequestBody organizerUpdateRequest: OrganizerUpdateRequest
    ): OrganizerResponse {
        return organizerService.updateOrganizer(organizerId, organizerUpdateRequest)
    }

    @GetMapping("/{organizerId}")
    fun getOrganizer(@PathVariable organizerId: Long): Organizer? {
        return organizerService.getOrganizer(organizerId)
    }

    @GetMapping
    fun listOrganizers(): List<Organizer> {
        return organizerService.listOrganizers()
    }

    @DeleteMapping("/{organizerId}")
    fun deleteOrganizer(@PathVariable organizerId: Long) {
        organizerService.deleteOrganizer(organizerId)
    }
}
