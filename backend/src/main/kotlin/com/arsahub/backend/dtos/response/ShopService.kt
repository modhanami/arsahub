package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.Reward
import com.arsahub.backend.models.Transaction

data class RewardResponse(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Int? = null,
    val quantity: Int? = null,
) {
    companion object {
        fun fromEntity(reward: Reward): RewardResponse {
            return RewardResponse(
                id = reward.id,
                name = reward.name,
                description = reward.description,
                price = reward.price,
                quantity = reward.quantity,
            )
        }
    }
}

data class TransactionResponse(
    val id: Long? = null,
    val pointsSpent: Int? = null,
    val createdAt: Long? = null,
    val referenceNumber: String? = null,
) {
    companion object {
        fun fromEntity(transaction: Transaction): TransactionResponse {
            return TransactionResponse(
                id = transaction.id,
                pointsSpent = transaction.pointsSpent,
                createdAt = transaction.createdAt?.toEpochMilli(),
                referenceNumber = transaction.referenceNumber,
            )
        }
    }
}
