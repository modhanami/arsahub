package com.arsahub.backend.repositories

import com.arsahub.backend.dtos.response.AchievementWithUnlockCount
import com.arsahub.backend.dtos.response.TriggerWithTriggerCount
import com.arsahub.backend.models.App
import com.arsahub.backend.models.QAppUserAchievement
import com.arsahub.backend.models.Transaction
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

class TimeRange(
    val startInclusive: Instant,
    val endExclusive: Instant,
)

interface AnalyticsRepository : JpaRepository<Transaction, Long>, AnalyticsRepositoryCustom {
    @Query(
        "SELECT NEW com.arsahub.backend.dtos.response.AchievementWithUnlockCount(t.achievement, COUNT(t)) " +
            "FROM AppUserAchievement t " +
            "WHERE t.app = :app " +
            "AND t.completedAt >= :#{#timeRange.startInclusive} AND t.completedAt < :#{#timeRange.endExclusive} " +
            "GROUP BY t.achievement " +
            "ORDER BY COUNT(t) DESC " +
            "LIMIT 10",
    )
    fun getAchievementsWithUnlockedCount(
        app: App,
        timeRange: TimeRange,
    ): List<AchievementWithUnlockCount>

    @Query(
        "SELECT NEW com.arsahub.backend.dtos.response.TriggerWithTriggerCount(t.trigger, COUNT(t)) " +
            "FROM TriggerLog t " +
            "WHERE t.app = :app " +
            "AND t.createdAt >= :#{#timeRange.startInclusive} AND t.createdAt < :#{#timeRange.endExclusive} " +
            "GROUP BY t.trigger " +
            "ORDER BY COUNT(t) DESC " +
            "LIMIT 10",
    )
    fun getTriggersWithTriggerCount(
        app: App,
        timeRange: TimeRange,
    ): List<TriggerWithTriggerCount>

    // total app users in a time range
    @Query(
        "SELECT COUNT(DISTINCT t) " +
            "FROM AppUser t " +
            "WHERE t.app = :app " +
            "AND t.createdAt >= :#{#timeRange.startInclusive} AND t.createdAt < :#{#timeRange.endExclusive}",
    )
    fun getTotalAppUsers(
        app: App,
        timeRange: TimeRange,
    ): Int?

    // total points earned in a time range
    @Query(
        "SELECT SUM(t.points) " +
            "FROM AppUserPointsHistory t " +
            "WHERE t.app = :app " +
            "AND t.createdAt >= :#{#timeRange.startInclusive} AND t.createdAt < :#{#timeRange.endExclusive}",
    )
    fun getTotalPointsEarned(
        app: App,
        timeRange: TimeRange,
    ): Long?
}

interface AnalyticsRepositoryCustom {
    fun getTotalUnlockedAchievements(
        app: App,
        timeRange: TimeRange,
    ): Int
}

@Repository
class AnalyticsRepositoryCustomImpl(private val entityManager: EntityManager) : AnalyticsRepositoryCustom {
    override fun getTotalUnlockedAchievements(
        app: App,
        timeRange: TimeRange,
    ): Int {
        val achievement = QAppUserAchievement.appUserAchievement
        val query =
            JPAQuery<Int>(entityManager)
                .from(achievement)
                .where(achievement.app.eq(app))
                .where(achievement.completedAt.goe(timeRange.startInclusive))
                .where(achievement.completedAt.lt(timeRange.endExclusive))
                .select(achievement.count())
        return (query.fetchOne() ?: 0).toInt()
    }
}
