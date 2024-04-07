package com.arsahub.backend.services

import com.arsahub.backend.dtos.response.LeaderboardResponse
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.LeaderboardConfigRepository
import com.arsahub.backend.models.LeaderboardTypes
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserPointsHistoryRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.services.DateTimeUtils.getMonthStartEndTimes
import com.arsahub.backend.services.DateTimeUtils.getWeekStartEndTimes
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

class LeaderboardConfigNotFoundException() : NotFoundException("Leaderboard not found")

@Service
class LeaderboardService(
    private val appUserRepository: AppUserRepository,
    private val appRepository: AppRepository,
    private val leaderboardConfigRepository: LeaderboardConfigRepository,
    private val appUserPointsHistoryRepository: AppUserPointsHistoryRepository,
) {
    fun getTotalPointsLeaderboard(appId: Long): LeaderboardResponse {
        val app = appRepository.findById(appId).orElseThrow { AppNotFoundException(appId) }
        val entries =
            appUserRepository.findAllByApp(app)
                .sortedByDescending { it.points }
                .mapIndexed { index, appUser ->
                    if (appUser.id != null && appUser.points != null) {
                        LeaderboardResponse.Entry(
                            userId = appUser.userId!!,
                            memberName = appUser.displayName!!,
                            score = appUser.points!!,
                            rank = index + 1,
                        )
                    } else {
                        null
                    }
                }
                .filterNotNull()
        return LeaderboardResponse(
            leaderboard = "total-points",
            entries,
        )
    }

//    create table leaderboard_config
// (
//    leaderboard_config_id bigserial
//        constraint leaderboard_config_pk
//            primary key,
//    app_id                bigint   not null
//        constraint leaderboard_config_app_app_id_fk
//            references app,
//    leaderboard_type_id   smallint not null
//        constraint leaderboard_config_leaderboard_type_leaderboard_type_id_fk
//            references leaderboard_type,
//    start_day             smallint, // 1 - 7 (Sunday - Saturday)
//    reset_day             smallint, // day of the month (1 - 31)
//    reset_time            time with time zone // 00:00:00 - 23:59:59 (with optional postgres timezone, e.g. 00:00:00+00)
// );

    // in-memory cache for leaderboard
    // TODO: use Redis for caching
    private val leaderboardCache = mutableMapOf<Long, LeaderboardResponse>()

    fun getLeaderboard(
        app: App,
        leaderboardId: Long,
        currentTime: ZonedDateTime = ZonedDateTime.now(),
    ): LeaderboardResponse {
        val leaderboardConfig =
            leaderboardConfigRepository.findByAppAndId(app, leaderboardId)
                ?: throw LeaderboardConfigNotFoundException()

        val leaderboardType = leaderboardConfig.leaderboardType
        requireNotNull(leaderboardType) { "Leaderboard type is not set" }

        val resetTime = leaderboardConfig.resetTime
        val timezone = leaderboardConfig.timezone.let { ZoneId.of(it) }
        requireNotNull(resetTime) { "Reset time is not set" }
        requireNotNull(timezone) { "Timezone is not set" }

        val zonedCurrentTime = currentTime.withZoneSameInstant(timezone)

        // weekly or monthly
        val leaderboard =
            // TODO: save snapshot of leaderboard from previous week/month to database (leaderboard_history)
            when (leaderboardType) {
                LeaderboardTypes.WEEKLY -> {
                    // check if current day is the start day and the time is after the reset time
                    val startDay = leaderboardConfig.startDay // 1 - 7 (Sunday - Saturday)
                    requireNotNull(startDay) { "Start day is not set" }
                    val dayOfWeek = DayOfWeek.of(startDay.toInt())

                    val (weekStartTime, weekEndTime) =
                        getWeekStartEndTimes(
                            dayOfWeek,
                            resetTime,
                            zonedCurrentTime,
                        )

                    getLeaderboard(app, weekStartTime, weekEndTime)
                }

                LeaderboardTypes.MONTHLY -> {
                    // check if current day is the reset day and the time is after the reset time
                    val resetDay = leaderboardConfig.resetDay
                    requireNotNull(resetDay) { "Reset day is not set" }
                    val (monthStartTime, monthEndTime) =
                        getMonthStartEndTimes(
                            resetDay.toInt(),
                            resetTime,
                            zonedCurrentTime,
                        )

                    getLeaderboard(app, monthStartTime, monthEndTime)
                }

                else -> {
                    throw IllegalArgumentException("Unknown leaderboard type: ${leaderboardType.name}")
                }
            }

        return LeaderboardResponse(
            leaderboard = "total-points",
            listOf(),
        )
    }

    private fun getLeaderboard(
        app: App,
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
    ) = appUserPointsHistoryRepository.findLatestPointsByStartTimeInclusiveEndTimeExclusive(
        appId = app.id!!,
        startTime = startTime.toInstant(),
        endTime = endTime.toInstant(),
    )
        .mapIndexed { index, latestPointsProjection ->
            LeaderboardResponse.Entry(
                userId = latestPointsProjection.getAppUser().userId!!,
                memberName = latestPointsProjection.getAppUser().displayName!!,
                score = latestPointsProjection.getPoints().toInt(),
                rank = index + 1,
            )
        }
}

object DateTimeUtils {
    fun getWeekStartEndTimes(
        startDay: DayOfWeek,
        resetTime: LocalTime,
        currentTime: ZonedDateTime,
    ): Pair<ZonedDateTime, ZonedDateTime> {
        val weekStartTime =
            currentTime.with(TemporalAdjusters.previousOrSame(startDay)).with(resetTime).let {
                if (it.isAfter(currentTime)) {
                    it.minusWeeks(1)
                } else {
                    it
                }
            }
        val weekEndTime = weekStartTime.plusWeeks(1)
        return weekStartTime to weekEndTime
    }

    fun getMonthStartEndTimes(
        resetDay: Int, // 1 - 31
        resetTime: LocalTime,
        currentTime: ZonedDateTime,
    ): Pair<ZonedDateTime, ZonedDateTime> {
        val daysInMonth = YearMonth.of(currentTime.year, currentTime.month).lengthOfMonth()
        val monthStartTime =
            currentTime
                .withDayOfMonth(resetDay.coerceAtMost(daysInMonth))
                .with(resetTime)
                .let {
                    if (it.isAfter(currentTime)) {
                        it.minusMonths(1)
                    } else {
                        it
                    }
                }
        val monthEndTime = monthStartTime.plusMonths(1)

        return monthStartTime to monthEndTime
    }
}
