package com.arsahub.backend.dtos

import com.fasterxml.jackson.annotation.JsonValue

// For typescript-generator
enum class ValidationMessages(val message: String) {
    TITLE_REQUIRED(Constants.TITLE_REQUIRED),
    TITLE_LENGTH(Constants.TITLE_LENGTH),
    TITLE_PATTERN(Constants.TITLE_PATTERN),
    DESCRIPTION_LENGTH(Constants.DESCRIPTION_LENGTH),
    KEY_REQUIRED(Constants.KEY_REQUIRED),
    KEY_LENGTH(Constants.KEY_LENGTH),
    KEY_PATTERN(Constants.KEY_PATTERN),
    PASSWORD_REQUIRED(Constants.PASSWORD_REQUIRED),
    PASSWORD_LENGTH(Constants.PASSWORD_LENGTH),
    TYPE_REQUIRED(Constants.TYPE_REQUIRED),
    LABEL_LENGTH(Constants.LABEL_LENGTH),
    REPEATABILITY_REQUIRED(Constants.REPEATABILITY_REQUIRED),
    ;

    override fun toString(): String {
        return message
    }

    object Constants {
        const val TITLE_REQUIRED = "Title is required"
        const val TITLE_LENGTH =
            "Title must be between ${ValidationLengths.Constants.TITLE_MIN} and ${ValidationLengths.Constants.TITLE_MAX} characters"
        const val TITLE_PATTERN = "Title must contain only alphanumeric characters, spaces, underscores, and dashes"
        const val DESCRIPTION_LENGTH =
            "Description cannot be longer than ${ValidationLengths.Constants.DESCRIPTION_MAX} characters"
        const val KEY_REQUIRED = "Key is required"
        const val KEY_LENGTH =
            "Key must be between ${ValidationLengths.Constants.KEY_MIN} and ${ValidationLengths.Constants.KEY_MAX} characters"
        const val KEY_PATTERN = "Key must contain only alphanumeric characters, underscores, and dashes"
        const val PASSWORD_REQUIRED = "Password is required"
        const val PASSWORD_LENGTH =
            "Password must be between ${ValidationLengths.Constants.PASSWORD_MIN} and ${ValidationLengths.Constants.PASSWORD_MAX} characters"
        const val TYPE_REQUIRED = "Type is required"
        const val LABEL_LENGTH =
            "Label must be between ${ValidationLengths.Constants.LABEL_MIN} and ${ValidationLengths.Constants.LABEL_MAX} characters"
        const val REPEATABILITY_REQUIRED = "Repeatability is required"
    }
}

// For typescript-generator
enum class ValidationLengths(
    @JsonValue
    val value: Int,
) {
    TITLE_MIN_LENGTH(Constants.TITLE_MIN),
    TITLE_MAX_LENGTH(Constants.TITLE_MAX),
    DESCRIPTION_MAX_LENGTH(Constants.DESCRIPTION_MAX),
    KEY_MIN_LENGTH(Constants.KEY_MIN),
    KEY_MAX_LENGTH(Constants.KEY_MAX),
    PASSWORD_MIN_LENGTH(Constants.PASSWORD_MIN),
    PASSWORD_MAX_LENGTH(Constants.PASSWORD_MAX),
    LABEL_MIN_LENGTH(Constants.LABEL_MIN),
    LABEL_MAX_LENGTH(Constants.LABEL_MAX),
    ;

    object Constants {
        const val TITLE_MIN = 4
        const val TITLE_MAX = 200
        const val DESCRIPTION_MAX = 500
        const val KEY_MIN = 4
        const val KEY_MAX = 200
        const val PASSWORD_MIN = 8
        const val PASSWORD_MAX = 50
        const val LABEL_MIN = 4
        const val LABEL_MAX = 200
    }
}
