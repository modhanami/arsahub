package com.arsahub.backend.models;

import org.springframework.data.jpa.repository.JpaRepository

interface ParticipationRepository : JpaRepository<Participation, Long> {
    fun findByUser(user: User): List<Participation>
    fun findByEvent(event: Event): List<Participation>
}