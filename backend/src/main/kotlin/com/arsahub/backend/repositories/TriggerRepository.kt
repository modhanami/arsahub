package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.Trigger
import org.springframework.data.jpa.repository.JpaRepository

interface TriggerRepository : JpaRepository<Trigger, Long> {
    fun findByKeyAndApp(
        key: String,
        app: App,
    ): Trigger?

    fun findAllByAppId(appId: Long): List<Trigger>
}
