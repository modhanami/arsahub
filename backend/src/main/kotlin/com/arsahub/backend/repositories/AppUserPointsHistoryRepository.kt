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
        value = """
        SELECT p.app_user_id, p.points, p.created_at
        FROM app_user_points_history p
        INNER JOIN (
            SELECT app_user_id, MAX(created_at) AS max_created_at
            FROM app_user_points_history 
            WHERE app_id = :appId
            AND created_at >= :startTime
            AND created_at < :endTime
            GROUP BY app_user_id
        ) latest ON p.app_user_id = latest.app_user_id AND p.created_at = latest.max_created_at
        ORDER BY p.points DESC
    """,
        nativeQuery = true,
    )
    fun findLatestPointsByStartTimeInclusiveEndTimeExclusive(
        @Param("appId") appId: Long,
        @Param("startTime") startTime: Instant,
        @Param("endTime") endTime: Instant,
    ): List<LatestPointsProjection>

    interface LatestPointsProjection {
        fun getAppUser(): AppUser

        fun getPoints(): Long

        fun getCreatedAt(): Instant
    }
}
