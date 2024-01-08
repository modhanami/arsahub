package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.App

data class AppResponse(
    val id: Long,
    val name: String,
    val apiKey: String,
) {
    companion object {
        fun fromEntity(entity: App): AppResponse {
            return AppResponse(
                id = entity.id!!,
                name = entity.title!!,
                apiKey = entity.apiKey!!,
            )
        }
    }
}