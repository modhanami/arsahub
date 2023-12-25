package com.arsahub.backend.services

import com.arsahub.backend.models.App
import org.springframework.stereotype.Service

@Service
class APIKeyService {
    data class APIKeyWithHashed(
        val apiKey: String,
        val hashedAPIKey: String
    )

    fun validateKeyForApp(
        app: App,
        key: String
    ): Boolean {
        return app.apiKey == key
    }
}