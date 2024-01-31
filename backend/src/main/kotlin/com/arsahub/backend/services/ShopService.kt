package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.RewardCreateRequest
import com.arsahub.backend.dtos.request.RewardRedeemRequest
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.Reward
import com.arsahub.backend.models.Transaction
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.RewardRepository
import com.arsahub.backend.repositories.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

class RewardNotFoundException : NotFoundException("Reward not found")

class RewardUnavailableException : IllegalArgumentException("Reward unavailable")

class InsufficientPointsException : IllegalArgumentException("Insufficient points")

class RewardInvalidPriceException : IllegalArgumentException("Price must be positive")

class RewardInvalidQuantityException : IllegalArgumentException("Quantity must be positive")

class RewardNameAlreadyExistsException : ConflictException("Reward with the same name already exists")

@Service
class ShopService(
    private val rewardRepository: RewardRepository,
    private val appUserRepository: AppUserRepository,
    private val appService: AppService,
    private val transactionRepository: TransactionRepository,
) {
    fun getRewards(currentApp: App): List<Reward> {
        return rewardRepository.findAllByApp(currentApp)
    }

    @Transactional
    fun redeemReward(
        currentApp: App,
        request: RewardRedeemRequest,
    ): Transaction {
        val reward =
            rewardRepository.findByIdAndApp(request.rewardId, currentApp)
                ?: throw RewardNotFoundException()
        val appUser =
            appService.getAppUserOrThrow(currentApp, request.userId)

        // check if out of stock
        // if quantity is null, assume infinite
        reward.quantity?.let {
            if (it <= 0) {
                throw RewardUnavailableException()
            } else {
                // deduct quantity
                reward.quantity = it - 1
                rewardRepository.save(reward)
            }
        }

        // check if enough points
        val appUserPoints = appUser.points ?: 0
        val rewardPrice = reward.price ?: 0
        if (appUserPoints < rewardPrice) {
            throw InsufficientPointsException()
        }
        // deduct points
        appUser.points = appUserPoints - rewardPrice
        appUserRepository.save(appUser)

        // create transaction
        val referenceNumber = UUID.randomUUID().toString()
        val transaction =
            Transaction(
                appUser = appUser,
                reward = reward,
                pointsSpent = rewardPrice,
                referenceNumber = referenceNumber,
                app = currentApp,
            )

        return transactionRepository.save(transaction)
    }

    fun createReward(
        app: App,
        request: RewardCreateRequest,
    ): Reward {
        // validate price
        if (request.price == null || request.price <= 0) {
            throw RewardInvalidPriceException()
        }

        // validate quantity
        if (request.quantity == null || request.quantity <= 0) {
            throw RewardInvalidQuantityException()
        }

        // validate uniqueness of name
        if (rewardRepository.findByAppAndName(app, request.name!!) != null) {
            throw RewardNameAlreadyExistsException()
        }

        val reward =
            Reward(
                app = app,
                name = request.name,
                description = request.description,
                price = request.price,
                quantity = request.quantity,
            )
        return rewardRepository.save(reward)
    }
}
