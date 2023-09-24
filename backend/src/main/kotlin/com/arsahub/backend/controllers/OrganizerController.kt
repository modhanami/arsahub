package com.arsahub.backend.controllers

import com.arsahub.backend.models.Organizer
import com.arsahub.backend.services.OrganizerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/organizers")
class OrganizerController(private val organizerService: OrganizerService) {

    @PostMapping
    fun createOrganizer(@RequestBody organizer: Organizer): Organizer {
        return organizerService.createOrganizer(organizer)
    }

    @PutMapping("/{organizerId}")
    fun updateOrganizer(@PathVariable organizerId: Long, @RequestBody updatedOrganizer: Organizer): Organizer {
        return organizerService.updateOrganizer(organizerId, updatedOrganizer)
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
