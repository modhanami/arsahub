package com.arsahub.backend.dtos.request

import jakarta.validation.constraints.NotEmpty
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.hibernate.validator.constraints.URL

class WebhookCreateRequest(
    url: String?,
) {
    @NotEmpty
    @URL(message = "Invalid URL")
    val url =
        url?.trim()?.toHttpUrlOrNull()?.toString()
}
