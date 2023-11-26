package com.arsahub.backend.dtos

import com.arsahub.backend.models.ExternalSystem

data class IntegrationResponse(
    val id: Long,
    val name: String,
) {
    companion object {
        fun fromEntity(entity: ExternalSystem): IntegrationResponse {
            return IntegrationResponse(
                id = entity.id!!,
                name = entity.title!!,
            )
        }
    }
}