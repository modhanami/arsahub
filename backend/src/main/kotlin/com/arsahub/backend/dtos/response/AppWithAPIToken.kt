package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.App

data class AppWithAPIToken(
    val app: App,
    val apiKey: String,
)
