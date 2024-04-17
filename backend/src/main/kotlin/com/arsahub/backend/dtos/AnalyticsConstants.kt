package com.arsahub.backend.dtos

// For typescript-generator
enum class AnalyticsConstants(val message: String) {
    TOTAL_UNLOCKED_ACHIEVEMENTS(Constants.TOTAL_UNLOCKED_ACHIEVEMENTS),
    TOP_10_ACHIEVEMENTS(Constants.TOP_10_ACHIEVEMENTS),
    TOP_10_TRIGGERS(Constants.TOP_10_TRIGGERS),
    TOTAL_APP_USERS(Constants.TOTAL_APP_USERS),
    TOTAL_POINTS_EARNED(Constants.TOTAL_POINTS_EARNED),
    ;

    override fun toString(): String {
        return message
    }

    companion object {
        fun fromString(value: String): AnalyticsConstants? {
            return entries.find { it.message == value }
        }
    }

    object Constants {
        const val TOTAL_UNLOCKED_ACHIEVEMENTS = "total-unlocked-achievements"
        const val TOP_10_ACHIEVEMENTS = "top-10-achievements"
        const val TOP_10_TRIGGERS = "top-10-triggers"
        const val TOTAL_APP_USERS = "total-app-users"
        const val TOTAL_POINTS_EARNED = "total-points-earned"
    }
}
