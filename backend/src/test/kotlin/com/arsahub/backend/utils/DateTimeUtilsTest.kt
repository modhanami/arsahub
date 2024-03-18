package com.arsahub.backend.utils

import com.arsahub.backend.services.DateTimeUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Stream

class DateTimeUtilsTest {
    companion object {
        @JvmStatic
        fun weekStartEndTimesTestCases(): Stream<Arguments> =
            Stream.of(
                Arguments.of(1, "12:00", "2023-03-20T12:34:56Z", "2023-03-20T12:00:00Z", "2023-03-27T12:00:00Z"),
                Arguments.of(2, "09:30", "2023-03-14T00:00:00Z", "2023-03-14T09:30:00Z", "2023-03-21T09:30:00Z"),
                Arguments.of(3, "18:45", "2023-03-15T10:20:30Z", "2023-03-15T18:45:00Z", "2023-03-22T18:45:00Z"),
                Arguments.of(4, "00:00", "2023-03-16T23:59:59Z", "2023-03-16T00:00:00Z", "2023-03-23T00:00:00Z"),
                Arguments.of(5, "06:15", "2023-03-17T08:00:00Z", "2023-03-17T06:15:00Z", "2023-03-24T06:15:00Z"),
                Arguments.of(6, "20:30", "2023-03-18T16:45:30Z", "2023-03-18T20:30:00Z", "2023-03-25T20:30:00Z"),
                Arguments.of(7, "03:00", "2023-03-19T22:00:00Z", "2023-03-19T03:00:00Z", "2023-03-26T03:00:00Z"),
            )

        @JvmStatic
        fun monthStartEndTimesTestCases(): Stream<Arguments> =
//            //        parsedCurrentTime: 2023-03-20T05:34:56-07:00[America/Los_Angeles]
// // parsedExpectedStartTime: 2023-03-01T00:00-08:00[America/Los_Angeles]
// // parsedExpectedEndTime: 2023-03-31T17:00-07:00[America/Los_Angeles]
//
// //        parsedCurrentTime: 2023-03-10T00:20:30-08:00[America/Los_Angeles]
// // parsedExpectedStartTime: 2023-03-15T12:00-07:00[America/Los_Angeles]
// // parsedExpectedEndTime: 2023-04-15T05:00-07:00[America/Los_Angeles]
//
// //        parsedCurrentTime: 2023-03-25T11:45-07:00[America/Los_Angeles]
// // parsedExpectedStartTime: 2023-03-31T16:59-07:00[America/Los_Angeles]
// // parsedExpectedEndTime: 2023-04-30T16:59-07:00[America/Los_Angeles]
            Stream.of(
                Arguments.of(
                    1,
                    "00:00",
                    "2023-03-20T05:34:56-07:00[America/Los_Angeles]",
                    "2023-03-01T00:00-08:00[America/Los_Angeles]",
                    "2023-03-31T17:00-07:00[America/Los_Angeles]",
                ),
                Arguments.of(
                    15,
                    "12:00",
                    "2023-03-10T00:20:30-08:00[America/Los_Angeles]",
                    "2023-03-15T12:00-07:00[America/Los_Angeles]",
                    "2023-04-15T05:00-07:00[America/Los_Angeles]",
                ),
                Arguments.of(
                    31,
                    "23:59",
                    "2023-03-25T11:45-07:00[America/Los_Angeles]",
                    "2023-03-31T16:59-07:00[America/Los_Angeles]",
                    "2023-04-30T16:59-07:00[America/Los_Angeles]",
                ),
            )
    }

    @ParameterizedTest
    @MethodSource("weekStartEndTimesTestCases")
    fun `getWeekStartEndTimes returns correct start and end times`(
        startDay: Int,
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

    @ParameterizedTest
    @MethodSource("monthStartEndTimesTestCases")
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
    fun `getMonthStartEndTimes handles daylight saving time correctly`(
        resetDay: Int,
        resetTime: String,
        currentTime: String,
        expectedStartTime: String,
        expectedEndTime: String,
    ) {
        // Arrange
        val parsedResetTime = LocalTime.parse(resetTime)
        val parsedCurrentTime = ZonedDateTime.parse(currentTime).withZoneSameInstant(ZoneId.of("America/Los_Angeles"))

        // Act
        val (startTime, endTime) = DateTimeUtils.getMonthStartEndTimes(resetDay, parsedResetTime, parsedCurrentTime)

        println(
            """
            parsedCurrentTime: $parsedCurrentTime
            parsedExpectedStartTime: ${
                ZonedDateTime.parse(expectedStartTime).withZoneSameInstant(ZoneId.of("America/Los_Angeles"))
            }
            parsedExpectedEndTime: ${
                ZonedDateTime.parse(expectedEndTime).withZoneSameInstant(ZoneId.of("America/Los_Angeles"))
            }
            """.trimIndent(),
        )
        // Assert
        assertEquals(
            ZonedDateTime.parse(expectedStartTime).withZoneSameInstant(ZoneId.of("America/Los_Angeles")),
            startTime,
        )
        assertEquals(
            ZonedDateTime.parse(expectedEndTime).withZoneSameInstant(ZoneId.of("America/Los_Angeles")),
            endTime,
        )

//        parsedCurrentTime: 2023-03-20T05:34:56-07:00[America/Los_Angeles]
// parsedExpectedStartTime: 2023-02-28T16:00-08:00[America/Los_Angeles]
// parsedExpectedEndTime: 2023-03-31T17:00-07:00[America/Los_Angeles]

//        parsedCurrentTime: 2023-03-10T00:20:30-08:00[America/Los_Angeles]
// parsedExpectedStartTime: 2023-03-15T05:00-07:00[America/Los_Angeles]
// parsedExpectedEndTime: 2023-04-15T05:00-07:00[America/Los_Angeles]

//        parsedCurrentTime: 2023-03-25T11:45-07:00[America/Los_Angeles]
// parsedExpectedStartTime: 2023-03-31T16:59-07:00[America/Los_Angeles]
// parsedExpectedEndTime: 2023-04-30T16:59-07:00[America/Los_Angeles]
    }
}
