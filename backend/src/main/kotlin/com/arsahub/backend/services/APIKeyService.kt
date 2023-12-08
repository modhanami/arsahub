package com.arsahub.backend.services

import com.arsahub.backend.models.App
import org.springframework.stereotype.Service

@Service
class APIKeyService {
    data class APIKeyWithHashed(
        val apiKey: String,
        val hashedAPIKey: String
    )

    fun generateKey(
    ): APIKeyWithHashed {
        val uuid = java.util.UUID.randomUUID().toString()
        return APIKeyWithHashed(uuid, "")
    }

    fun validateKeyForApp(
        app: App,
        key: String
    ): Boolean {
        return app.apiKey == key
    }
}