package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.AppUserPointsHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface AppUserPointsHistoryRepository : JpaRepository<AppUserPointsHistory, Long> {
    fun findAllByAppAndAppUser(
        app: App,
        appUser: AppUser,
    ): List<AppUserPointsHistory>

    @Query(
        """
        SELECT p
        FROM AppUserPointsHistory p
        WHERE p.app = :app
        AND p.createdAt >= :startTime
        AND p.createdAt < :endTime
        """,
    )
    fun findByStartTimeInclusiveEndTimeExclusive(
        app: App,
        startTime: Instant,
        endTime: Instant,
    ): List<AppUserPointsHistory>

    // TODO: use QueryDSL to make this query type-safe
    @Query(
        """
        SELECT p.appUser.id AS appUserId, p.points AS points, p.createdAt AS createdAt
        FROM AppUserPointsHistory p
        WHERE p.app.id = :appId
            AND p.createdAt = (
                SELECT MAX(p2.createdAt)
                FROM AppUserPointsHistory p2
                WHERE p2.appUser.id = p.appUser.id
                    AND p2.app.id = :appId
                    AND p2.createdAt >= :startTime
                    AND p2.createdAt < :endTime
            )
        """,
    )
    fun findLatestPointsByStartTimeInclusiveEndTimeExclusive(
        @Param("appId") appId: Long,
        @Param("startTime") startTime: Instant,
        @Param("endTime") endTime: Instant,
    ): List<LatestPointsProjection>

    interface LatestPointsProjection {
        fun getAppUserId(): Long

        fun getPoints(): Long

        fun getCreatedAt(): Instant
    }
}
