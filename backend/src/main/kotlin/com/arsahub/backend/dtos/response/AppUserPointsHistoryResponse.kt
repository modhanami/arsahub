package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.AppUserPointsHistory
import java.time.Instant

class AppUserPointsHistoryResponse(
    val id: Long,
    val appId: Long,
    val points: Long,
    val pointsChange: Long,
    val fromRule: RuleResponse?,
    val createdAt: Instant,
) {
    companion object {
        fun fromEntity(entity: AppUserPointsHistory): AppUserPointsHistoryResponse {
            return AppUserPointsHistoryResponse(
                id = entity.id!!,
                appId = entity.app!!.id!!,
                points = entity.points!!,
                pointsChange = entity.pointsChange!!,
                fromRule = entity.fromRule?.let { RuleResponse.fromEntity(it) },
                createdAt = entity.createdAt!!,
            )
        }
    }
}
