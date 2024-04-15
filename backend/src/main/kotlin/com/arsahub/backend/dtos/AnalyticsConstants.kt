package com.arsahub.backend.dtos

// For typescript-generator
enum class AnalyticsConstants(val message: String) {
    TOTAL_UNLOCKED_ACHIEVEMENTS(Constants.TOTAL_UNLOCKED_ACHIEVEMENTS),
    TOP_10_ACHIEVEMENTS(Constants.TOP_10_ACHIEVEMENTS),
    TOP_10_TRIGGERS(Constants.TOP_10_TRIGGERS),
    ;

    override fun toString(): String {
        return message
    }

    object Constants {
        const val TOTAL_UNLOCKED_ACHIEVEMENTS = "total-unlocked-achievements"
        const val TOP_10_ACHIEVEMENTS = "top-10-achievements"
        const val TOP_10_TRIGGERS = "top-10-triggers"
    }
}
