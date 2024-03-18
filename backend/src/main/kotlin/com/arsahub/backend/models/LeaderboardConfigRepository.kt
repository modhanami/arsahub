package com.arsahub.backend.models

import org.springframework.data.jpa.repository.JpaRepository

interface LeaderboardConfigRepository : JpaRepository<LeaderboardConfig, Long> {
    fun findByAppAndId(
        app: App,
        leaderboardId: Long,
    ): LeaderboardConfig?
}
