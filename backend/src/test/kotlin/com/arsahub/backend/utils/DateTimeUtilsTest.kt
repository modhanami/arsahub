package com.arsahub.backend.utils

import com.arsahub.backend.services.DateTimeUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.stream.Stream

class DateTimeUtilsTest {
    companion object {
        @JvmStatic
        fun weekTestCases(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    Named.of(
                        "reset day of week and time < current time => start time of the current week & end time of the next week",
                        DayOfWeek.SUNDAY,
                    ),
                    "05:00",
                    "2024-04-07T12:34:56Z",
                    "2024-04-07T05:00:00Z",
                    "2024-04-14T05:00:00Z",
                ),
                Arguments.of(
                    Named.of(
                        "reset day of week and time = current time => start time of the current week & end time of the next week",
                        DayOfWeek.SUNDAY,
                    ),
                    "05:00",
                    "2024-04-07T05:00:00Z",
                    "2024-04-07T05:00:00Z",
                    "2024-04-14T05:00:00Z",
                ),
                Arguments.of(
                    Named.of(
                        "reset day of week and time > current time => start time of the previous week & end time of the current week",
                        DayOfWeek.SUNDAY,
                    ),
                    "05:00",
                    "2024-04-06T12:34:56Z",
                    "2024-03-31T05:00:00Z",
                    "2024-04-07T05:00:00Z",
                ),
            )

        @JvmStatic
        fun monthsTestCases(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    Named.of(
                        "reset day and time < current time => start time of the current month & end time of the next month",
                        10,
                    ),
                    "05:00",
                    "2024-04-12T12:34:56Z",
                    "2024-04-10T05:00:00Z",
                    "2024-05-10T05:00:00Z",
                ),
                Arguments.of(
                    Named.of(
                        "reset day and time = current time => start time of the current month & end time of the next month",
                        10,
                    ),
                    "05:00",
                    "2024-04-10T05:00:00Z",
                    "2024-04-10T05:00:00Z",
                    "2024-05-10T05:00:00Z",
                ),
                Arguments.of(
                    Named.of(
                        "reset day and time > current time => start time of the previous month & end time of the current month",
                        10,
                    ),
                    "05:00",
                    "2024-04-07T12:34:56Z",
                    "2024-03-10T05:00:00Z",
                    "2024-04-10T05:00:00Z",
                ),
            )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("weekTestCases")
    fun `getWeekStartEndTimes returns correct start and end times`(
        startDay: DayOfWeek,
        resetTime: String,
        currentTime: String,
        expectedStartTime: String,
        expectedEndTime: String,
    ) {
        // Arrange
        val parsedResetTime = LocalTime.parse(resetTime)
        val parsedCurrentTime = ZonedDateTime.parse(currentTime)

        // Act
        val (startTime, endTime) = DateTimeUtils.getWeekStartEndTimes(startDay, parsedResetTime, parsedCurrentTime)

        // Assert
        assertEquals(ZonedDateTime.parse(expectedStartTime), startTime)
        assertEquals(ZonedDateTime.parse(expectedEndTime), endTime)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("monthsTestCases")
    fun `getMonthStartEndTimes returns correct start and end times`(
        resetDay: Int,
        resetTime: String,
        currentTime: String,
        expectedStartTime: String,
        expectedEndTime: String,
    ) {
        // Arrange
        val parsedResetTime = LocalTime.parse(resetTime)
        val parsedCurrentTime = ZonedDateTime.parse(currentTime)

        // Act
        val (startTime, endTime) = DateTimeUtils.getMonthStartEndTimes(resetDay, parsedResetTime, parsedCurrentTime)

        // Assert
        assertEquals(ZonedDateTime.parse(expectedStartTime), startTime)
        assertEquals(ZonedDateTime.parse(expectedEndTime), endTime)
    }

    @ParameterizedTest
    @MethodSource("monthStartEndTimesTestCases")
    @Disabled
    fun `getMonthStartEndTimes handles daylight saving time correctly`(
        resetDay: Int,
        resetTime: String,
        currentTime: String,
        expectedStartTime: String,
        expectedEndTime: String,
    ) {
    }
}
