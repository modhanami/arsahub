package com.arsahub.backend.services

import com.arsahub.backend.controllers.utils.AuthSetup
import com.arsahub.backend.controllers.utils.AuthTestUtils
import com.arsahub.backend.dtos.request.AppUserCreateRequest
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.AppUserPointsHistory
import com.arsahub.backend.models.LeaderboardConfig
import com.arsahub.backend.models.LeaderboardConfigRepository
import com.arsahub.backend.models.LeaderboardTypes
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserPointsHistoryRepository
import com.arsahub.backend.repositories.UserRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.ext.ScriptUtils
import org.testcontainers.jdbc.JdbcDatabaseDelegate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Testcontainers
@ActiveProfiles("dev", "test")
@Transactional
@EmbeddedKafka
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class LeaderboardServiceTest {
    @Autowired
    private lateinit var appService: AppService

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var appUserPointsHistoryRepository: AppUserPointsHistoryRepository

    @Autowired
    private lateinit var leaderboardService: LeaderboardService

    @Autowired
    private lateinit var leaderboardConfigRepository: LeaderboardConfigRepository

    @Autowired
    private lateinit var appRepository: AppRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var authSetup: AuthSetup

    @BeforeEach
    fun setUp() {
        ScriptUtils.runInitScript(JdbcDatabaseDelegate(postgres, ""), "pre-schema.sql")
        ScriptUtils.runInitScript(JdbcDatabaseDelegate(postgres, ""), "schema.sql")
        ScriptUtils.runInitScript(JdbcDatabaseDelegate(postgres, ""), "data.sql")

        authSetup =
            AuthTestUtils.setupAuth(
                userRepository,
                appRepository,
            )
    }

    fun createAppUser(
        app: App,
        userId: String = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
    ): AppUser {
        val appUser =
            appService.addUser(
                app,
                AppUserCreateRequest(
                    uniqueId = userId,
                    displayName = "User $userId",
                ),
            )

        return appUser
    }

    @Test
    fun getLeaderboardReturnsLatestRanking_Weekly() {
        val leaderboardConfig =
            leaderboardConfigRepository.save(
                LeaderboardConfig(
                    app = authSetup.app,
                    leaderboardType = LeaderboardTypes.WEEKLY,
                    startDay = 7, // Sunday
                    resetTime = LocalTime.parse("05:00"),
                    timezone = "Etc/UTC",
                    name = "Weekly Leaderboard",
                ),
            )

        val currentTime = ZonedDateTime.parse("2024-04-07T02:34:56Z") // Sunday
        val instant = currentTime.toInstant()

        fun assertLeaderboard(rankedPoints: List<Long>) {
            val leaderboardResponse =
                leaderboardService.getLeaderboard(
                    authSetup.app,
                    leaderboardId = leaderboardConfig.id!!,
                    currentTime = currentTime,
                )

            assertEquals(rankedPoints.size, leaderboardResponse.entries.size)
            rankedPoints.forEachIndexed { index, points ->
                assertEquals(points, leaderboardResponse.entries[index].score)
                assertEquals((index + 1).toLong(), leaderboardResponse.entries[index].rank)
            }
        }

        val appUser1 = createAppUser(authSetup.app)
        val appUser2 = createAppUser(authSetup.app, userId = UUID.randomUUID().toString())

        // Act & Assert - gradually add points starting from the past
        // not included
        addPointsHistory(appUser1, 110, instant.minus(7, ChronoUnit.DAYS))
        addPointsHistory(appUser2, 2110, instant.minus(7, ChronoUnit.DAYS))
        addPointsHistory(
            appUser1,
            100,
            currentTime.minus(7, ChronoUnit.DAYS).with(LocalTime.parse("04:59")).toInstant(),
        )
        addPointsHistory(
            appUser2,
            2100,
            currentTime.minus(7, ChronoUnit.DAYS).with(LocalTime.parse("04:59")).toInstant(),
        )
        assertLeaderboard(listOf())

        // included
        addPointsHistory(
            appUser1,
            90,
            currentTime.minus(7, ChronoUnit.DAYS).with(LocalTime.parse("05:00")).toInstant(),
        )
        addPointsHistory(
            appUser2,
            2090,
            currentTime.minus(7, ChronoUnit.DAYS).with(LocalTime.parse("05:00")).toInstant(),
        )
        assertLeaderboard(listOf(2090, 90))

        addPointsHistory(appUser1, 80, instant.minus(1, ChronoUnit.DAYS))
        addPointsHistory(appUser2, 2080, instant.minus(1, ChronoUnit.DAYS))
        assertLeaderboard(listOf(2080, 80))

        addPointsHistory(appUser1, 70, currentTime.with(LocalTime.parse("04:59")).toInstant())
        addPointsHistory(appUser2, 2070, currentTime.with(LocalTime.parse("04:59")).toInstant())
        assertLeaderboard(listOf(2070, 70))

        // non included (note the future time)
        addPointsHistory(appUser1, 60, currentTime.with(LocalTime.parse("05:00")).toInstant())
        addPointsHistory(appUser2, 2060, currentTime.with(LocalTime.parse("05:00")).toInstant())
        assertLeaderboard(listOf(2070, 70))
    }

    private fun addPointsHistory(
        appUser: AppUser,
        points: Long,
        createdAt: Instant,
    ): AppUserPointsHistory {
        val pointsHistory =
            appUserPointsHistoryRepository.save(
                AppUserPointsHistory(
                    app = authSetup.app,
                    appUser = appUser,
                    pointsChange = 0,
                    points = points,
                ),
            )

        // raw query override the createdAt from @CreatedDate
        val updateQuery =
            """
                UPDATE app_user_points_history
                SET created_at = '$createdAt'
                WHERE app_user_points_history_id = ${pointsHistory.id}
                """
        entityManager.createNativeQuery(updateQuery).executeUpdate()
        entityManager.refresh(pointsHistory)

        return pointsHistory
    }

    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<Nothing> =
            PostgreSQLContainer<Nothing>("postgres:16-alpine")
                .withReuse(true)
    }
}
