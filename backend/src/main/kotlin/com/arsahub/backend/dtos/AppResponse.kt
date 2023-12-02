package com.arsahub.backend.dtos

import com.arsahub.backend.models.App

data class AppResponse(
    val id: Long,
    val name: String,
) {
    companion object {
        fun fromEntity(entity: App): AppResponse {
            return AppResponse(
                id = entity.id!!,
                name = entity.title!!,
            )
        }
    }
}