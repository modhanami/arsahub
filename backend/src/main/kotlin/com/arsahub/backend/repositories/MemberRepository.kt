package com.arsahub.backend.repositories;

import com.arsahub.backend.models.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findAllByActivity_ActivityId(activityId: Long): List<Member>
}
