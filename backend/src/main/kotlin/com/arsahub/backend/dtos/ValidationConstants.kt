package com.arsahub.backend.dtos

import com.fasterxml.jackson.annotation.JsonValue

// For typescript-generator
enum class ValidationMessages(val message: String) {
    TITLE_REQUIRED(Constants.TITLE_REQUIRED),
    TITLE_LENGTH(Constants.TITLE_LENGTH),
    TITLE_PATTERN(Constants.TITLE_PATTERN),
    NAME_REQUIRED(Constants.NAME_REQUIRED),
    NAME_LENGTH(Constants.NAME_LENGTH),
    NAME_PATTERN(Constants.NAME_PATTERN),
    DESCRIPTION_LENGTH(Constants.DESCRIPTION_LENGTH),
    KEY_REQUIRED(Constants.KEY_REQUIRED),
    KEY_LENGTH(Constants.KEY_LENGTH),
    KEY_PATTERN(Constants.KEY_PATTERN),
    PASSWORD_REQUIRED(Constants.PASSWORD_REQUIRED),
    PASSWORD_LENGTH(Constants.PASSWORD_LENGTH),
    TYPE_REQUIRED(Constants.TYPE_REQUIRED),
    LABEL_LENGTH(Constants.LABEL_LENGTH),
    REPEATABILITY_REQUIRED(Constants.REPEATABILITY_REQUIRED),

    APP_USER_UID_REQUIRED(Constants.APP_USER_UID_REQUIRED),
    APP_USER_UID_LENGTH(Constants.APP_USER_UID_LENGTH),
    APP_USER_UID_PATTERN(Constants.APP_USER_UID_PATTERN),
    APP_USER_DISPLAY_NAME_REQUIRED(Constants.APP_USER_DISPLAY_NAME_REQUIRED),
    APP_USER_DISPLAY_NAME_LENGTH(Constants.APP_USER_DISPLAY_NAME_LENGTH),
    APP_USER_DISPLAY_NAME_PATTERN(Constants.APP_USER_DISPLAY_NAME_PATTERN),

    ;

    override fun toString(): String {
        return message
    }

    object Constants {
        const val TITLE_REQUIRED = "Title is required"
        const val NAME_REQUIRED = "Name is required"
        const val TITLE_LENGTH =
            "Title must be between ${ValidationLengths.Constants.TITLE_MIN} and ${ValidationLengths.Constants.TITLE_MAX} characters"
        const val NAME_LENGTH =
            "Name must be between ${ValidationLengths.Constants.NAME_MIN} and ${ValidationLengths.Constants.NAME_MAX} characters"
        const val TITLE_PATTERN = "Title must contain only alphanumeric characters, spaces, underscores, and dashes"
        const val NAME_PATTERN = "Name must contain only alphanumeric characters, spaces, underscores, and dashes"
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

        const val APP_USER_UID_REQUIRED = "App user UID is required"
        const val APP_USER_UID_LENGTH =
            "UID must be between ${ValidationLengths.Constants.APP_USER_UID_MIN} and ${ValidationLengths.Constants.APP_USER_UID_MAX} characters"
        const val APP_USER_UID_PATTERN = "UID must contain only alphanumeric characters, underscores, and dashes"
        const val APP_USER_DISPLAY_NAME_REQUIRED = "Display name is required"
        const val APP_USER_DISPLAY_NAME_LENGTH =
            "Display name must be between ${ValidationLengths.Constants.APP_USER_DISPLAY_NAME_MIN} and ${ValidationLengths.Constants.APP_USER_DISPLAY_NAME_MAX} characters"
        const val APP_USER_DISPLAY_NAME_PATTERN =
            "Display name must contain only alphanumeric characters, underscores, and dashes"
    }
}

// For typescript-generator
enum class ValidationLengths(
    @JsonValue
    val value: Int,
) {
    TITLE_MIN_LENGTH(Constants.TITLE_MIN),
    TITLE_MAX_LENGTH(Constants.TITLE_MAX),
    NAME_MIN_LENGTH(Constants.NAME_MIN),
    NAME_MAX_LENGTH(Constants.NAME_MAX),
    DESCRIPTION_MAX_LENGTH(Constants.DESCRIPTION_MAX),
    KEY_MIN_LENGTH(Constants.KEY_MIN),
    KEY_MAX_LENGTH(Constants.KEY_MAX),
    PASSWORD_MIN_LENGTH(Constants.PASSWORD_MIN),
    PASSWORD_MAX_LENGTH(Constants.PASSWORD_MAX),
    LABEL_MIN_LENGTH(Constants.LABEL_MIN),
    LABEL_MAX_LENGTH(Constants.LABEL_MAX),
    APP_USER_UID_MIN_LENGTH(Constants.APP_USER_UID_MIN),
    APP_USER_UID_MAX_LENGTH(Constants.APP_USER_UID_MAX),
    APP_USER_DISPLAY_NAME_MIN_LENGTH(Constants.APP_USER_DISPLAY_NAME_MIN),
    APP_USER_DISPLAY_NAME_MAX_LENGTH(Constants.APP_USER_DISPLAY_NAME_MAX),
    ;

    object Constants {
        const val TITLE_MIN = 4
        const val TITLE_MAX = 200
        const val NAME_MIN = 4
        const val NAME_MAX = 200
        const val DESCRIPTION_MAX = 500
        const val KEY_MIN = 4
        const val KEY_MAX = 200
        const val PASSWORD_MIN = 8
        const val PASSWORD_MAX = 50
        const val LABEL_MIN = 4
        const val LABEL_MAX = 200
        const val APP_USER_UID_MIN = 4
        const val APP_USER_UID_MAX = 200
        const val APP_USER_DISPLAY_NAME_MIN = 4
        const val APP_USER_DISPLAY_NAME_MAX = 200
    }
}
