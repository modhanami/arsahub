package com.arsahub.backend.repositories;

import com.arsahub.backend.models.Participation
import org.springframework.data.jpa.repository.JpaRepository

interface ParticipationRepository : JpaRepository<Participation, Long>
