package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.Reward
import org.springframework.data.jpa.repository.JpaRepository

interface RewardRepository : JpaRepository<Reward, Long> {
    fun findAllByApp(app: App): List<Reward>

    fun findByAppAndName(
        app: App,
        name: String,
    ): Reward?

    fun findByIdAndApp(
        rewardId: Long,
        currentApp: App,
    ): Reward?
}
