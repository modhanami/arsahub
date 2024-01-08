package com.arsahub.backend.services

import com.arsahub.backend.models.App
import org.springframework.stereotype.Service

@Service
class APIKeyService {

    fun validateKeyForApp(
        app: App,
        key: String
    ): Boolean {
        return app.apiKey == key
    }

    fun generateAPIKey(): String {
        return java.util.UUID.randomUUID().toString()
    }
}