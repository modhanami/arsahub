package com.arsahub.backend.models

sealed class RuleRepeatability(val key: String) {
    companion object {
        const val ONCE_PER_USER = "once_per_user"
        const val UNLIMITED = "unlimited"

        fun valueOf(key: String): RuleRepeatability {
            return when (key) {
                ONCE_PER_USER -> OncePerUserRuleRepeatability
                UNLIMITED -> UnlimitedRuleRepeatability
                else -> throw IllegalArgumentException("Unknown rule repeatability key: $key")
            }
        }
    }
}

data object OncePerUserRuleRepeatability : RuleRepeatability(
    key = ONCE_PER_USER
)

data object UnlimitedRuleRepeatability : RuleRepeatability(
    key = UNLIMITED
)