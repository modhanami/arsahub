package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.RewardCreateRequest
import com.arsahub.backend.dtos.request.RewardRedeemRequest
import com.arsahub.backend.dtos.request.RewardSetImageRequest
import com.arsahub.backend.dtos.response.RewardWithCount
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.Reward
import com.arsahub.backend.models.Transaction
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.RewardRepository
import com.arsahub.backend.repositories.TransactionRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.util.*

class RewardNotFoundException : NotFoundException("Reward not found")

class RewardUnavailableException : IllegalArgumentException("Reward unavailable")

class RewardAlreadyRedeemedException : IllegalArgumentException("Reward already redeemed")

class InsufficientPointsException : IllegalArgumentException("Insufficient points")

class RewardInvalidPriceException : IllegalArgumentException("Price must be positive")

class RewardInvalidQuantityException : IllegalArgumentException("Quantity must be positive")

class RewardNameAlreadyExistsException : ConflictException("Reward with the same name already exists")

class RewardInvalidMaxUserRedemptions : IllegalArgumentException("Max user redemptions must be positive")

@Service
class ShopService(
    private val rewardRepository: RewardRepository,
    private val appUserRepository: AppUserRepository,
    private val appService: AppService,
    private val transactionRepository: TransactionRepository,
    private val properties: MyServiceProperties,
) {
    private val logger = KotlinLogging.logger {}

    private val s3Client =
        S3Client.builder()
            .endpointOverride(
                java.net.URI.create("https://176727395c7e97ac98fb6d497684940a.r2.cloudflarestorage.com"),
            )
            .region(Region.US_EAST_1) // auto
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        properties.auth.accessKeyId,
                        properties.auth.secretAccessKey,
                    ),
                ),
            )
            .build()

    fun getRewards(currentApp: App): List<Reward> {
        return rewardRepository.findAllByApp(currentApp)
    }

    @Transactional
    fun redeemReward(
        currentApp: App,
        request: RewardRedeemRequest,
    ): Transaction {
        val reward = getRewardOrThrow(request.rewardId, currentApp)
        val appUser =
            appService.getAppUserOrThrow(currentApp, request.userId)

        // check max user redemptions
        reward.maxUserRedemptions?.let {
            val userRedemptions = transactionRepository.countByAppUserAndReward(appUser, reward)
            if (userRedemptions >= it) {
                throw RewardAlreadyRedeemedException()
            }
        }

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

        request.quantity?.let {
            if (it <= 0) {
                throw RewardInvalidQuantityException()
            }
        }

        request.maxUserRedemptions?.let {
            if (it <= 0) {
                throw RewardInvalidMaxUserRedemptions()
            }
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
                maxUserRedemptions = request.maxUserRedemptions,
            )
        return rewardRepository.save(reward)
    }

    fun getRewardOrThrow(
        id: Long,
        app: App,
    ): Reward {
        return rewardRepository.findByIdAndApp(id, app) ?: throw RewardNotFoundException()
    }

    fun setImageForReward(
        app: App,
        request: RewardSetImageRequest,
    ): Reward {
        val rewardId = request.rewardId
        val image = request.image
        val reward = getRewardOrThrow(rewardId, app)

        if (reward.imageKey != null) {
            throw ConflictException("Reward already has an image")
        }

        val file = image.originalFilename?.let { File(it) }
        val uuid = UUID.randomUUID()
        val key = "apps/${app.id}/rewards/${reward.id}/$uuid"
        val imageBytes = image.bytes
        val contentType = image.contentType

        logger.info {
            "Uploading image for reward ${reward.id}: " +
                "size=${imageBytes.size}, contentType=$contentType, " +
                "name=${image.name}, originalFilename=${file?.name}"
        }

        val metadata = mutableMapOf<String, Any>()

        file?.name?.let { metadata["originalFilename"] = it }
        contentType?.let { metadata["contentType"] = it }
        metadata["size"] = imageBytes.size

        val putObjectRequest =
            PutObjectRequest.builder()
                .bucket(properties.bucket)
                .key(key)
                .contentType(contentType)
                .metadata(
                    metadata.mapValues { it.value.toString() },
                )
                .build()

        val putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes))
        logger.debug { "Uploaded image for reward ${reward.id}: $putObjectResponse" }

        reward.imageKey = key
        reward.imageMetadata = metadata

        return rewardRepository.save(reward)
    }

    fun getRewardsForUser(
        app: App,
        userId: String,
    ): List<RewardWithCount> {
        val appUser = appService.getAppUserOrThrow(app, userId)
        val findRewardAndCountByAppAndAppUser = transactionRepository.findRewardAndCountByAppAndAppUser(app, appUser)
        println(findRewardAndCountByAppAndAppUser)
        return findRewardAndCountByAppAndAppUser
    }
}
