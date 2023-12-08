package com.arsahub.backend.dtos

import com.arsahub.backend.models.App

data class AppCreateResponse(
    val id: Long,
    val name: String,
    val apiKey: String? = null,
) {
    companion object {
        fun fromEntity(entity: App, apiKey: String?): AppCreateResponse {
            return AppCreateResponse(
                id = entity.id!!,
                name = entity.title!!,
                apiKey = apiKey
            )
        }
    }
}