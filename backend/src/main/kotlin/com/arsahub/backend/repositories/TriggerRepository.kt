package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.Trigger
import org.springframework.data.jpa.repository.JpaRepository

interface TriggerRepository : JpaRepository<Trigger, Long> {
    fun findByKeyAndApp(
        key: String,
        app: App,
    ): Trigger?

    fun findByTitleAndApp(
        title: String,
        app: App,
    ): Trigger?

    fun findAllByAppId(appId: Long): List<Trigger>

    // Built-in triggers don't belong to any app
    fun findAllByAppIdOrAppIdIsNull(appId: Long): List<Trigger>

    fun findAllByAppIdIsNull(): List<Trigger>

    fun findByKey(key: String): Trigger?
}
