package com.arsahub.backend.dtos.request

import jakarta.validation.constraints.NotEmpty

class WebhookCreateRequest(
    @NotEmpty
    val url: String,
)
