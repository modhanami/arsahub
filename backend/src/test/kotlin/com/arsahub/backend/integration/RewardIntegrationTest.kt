package com.arsahub.backend.integration

import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Reward
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.RewardRepository
import com.arsahub.backend.repositories.TransactionRepository
import com.arsahub.backend.repositories.UserRepository
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasEntry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import java.util.*

class RewardIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var appRepository: AppRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var rewardRepository: RewardRepository

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    // Redeem points
    @Test
    fun `redeem points - success`() {
        // Arrange
        val appUserWith100Points =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 100,
                ),
            )

        val reward10Points =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 10,
                    app = authSetup.app,
                ),
            )

        fun redeemReward(): ResultActions {
            return mockMvc.performWithAppAuth(
                post("/api/apps/shop/rewards/redeem")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "rewardId": ${reward10Points.id},
                            "userId": "${appUserWith100Points.userId}"
                        }
                        """.trimIndent(),
                    ),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pointsSpent").value(10))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.referenceNumber").exists())
        }

        // Act & Assert HTTP 1
        val resultActions = redeemReward()

        // Assert DB
        // Assert reward quantity
        val reward10PointsAfter = rewardRepository.findById(reward10Points.id!!).get()
        assertEquals(9, reward10PointsAfter.quantity)

        // Assert user points
        val appUserWith100PointsAfter = appUserRepository.findById(appUserWith100Points.id!!).get()
        assertEquals(90, appUserWith100PointsAfter.points)

        // Assert transaction created
        val transaction = transactionRepository.findAll().first()
        assertEquals(10, transaction.pointsSpent)

        // Assert reward redeemed
        val rewardRedeemed = transaction.reward!!
        assertEquals("10 Points", rewardRedeemed.name)

        // Assert transaction reference number
        val transactionReferenceNumber = transaction.referenceNumber
        assertDoesNotThrow { UUID.fromString(transactionReferenceNumber) }
        resultActions.andExpect(jsonPath("$.referenceNumber").value(transactionReferenceNumber))

        // Act & Assert HTTP 2
        val resultActions2 = redeemReward()

        // Assert DB
        // Assert reward quantity
        val reward10PointsAfter2 = rewardRepository.findById(reward10Points.id!!).get()
        assertEquals(8, reward10PointsAfter2.quantity)

        // Assert user points
        val appUserWith100PointsAfter2 = appUserRepository.findById(appUserWith100Points.id!!).get()
        assertEquals(80, appUserWith100PointsAfter2.points)

        // Assert transaction created
        val transaction2 = transactionRepository.findAll().sortedByDescending { it.createdAt }.first()
        assertEquals(10, transaction2.pointsSpent)

        // Assert reward redeemed
        val rewardRedeemed2 = transaction2.reward!!
        assertEquals("10 Points", rewardRedeemed2.name)

        // Assert transaction reference number
        val transactionReferenceNumber2 = transaction2.referenceNumber
        assertDoesNotThrow { UUID.fromString(transactionReferenceNumber2) }
        resultActions2.andExpect(jsonPath("$.referenceNumber").value(transactionReferenceNumber2))
    }

    @Test
    fun `redeem points - not enough points`() {
        // Arrange
        val appUserWith1Points =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 1,
                ),
            )

        val reward10Points =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 10,
                    app = authSetup.app,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "rewardId": ${reward10Points.id},
                        "userId": "${appUserWith1Points.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Insufficient points"))
    }

    @Test
    fun `redeem points - out of stock`() {
        // Arrange
        val appUserWith100Points =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 100,
                ),
            )

        val reward10Points =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 0,
                    app = authSetup.app,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "rewardId": ${reward10Points.id},
                        "userId": "${appUserWith100Points.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Reward unavailable"))
    }

    @Test
    fun `redeem points - invalid reward ID`() {
        // Arrange
        val appUserWith100Points =
            appUserRepository.save(
                AppUser(
                    userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                    displayName = "User1",
                    app = authSetup.app,
                    points = 100,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "rewardId": 999999999,
                        "userId": "${appUserWith100Points.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Reward not found"))
    }

    // Create reward

    @Test
    fun `create reward - success`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "price": 10,
                "quantity": 10
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("10 Points"))
            .andExpect(jsonPath("$.price").value(10))
            .andExpect(jsonPath("$.quantity").value(10))

        // Assert DB
        val rewards = rewardRepository.findAll()
        assertEquals(1, rewards.size)
        val reward = rewards[0]
        assertEquals("10 Points", reward.name)
        assertEquals(10, reward.price)
        assertEquals(10, reward.quantity)
    }

    @Test
    fun `create reward - success - name and description are trimmed`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": " 10 Points ",
                "description": " 10 Points ",
                "price": 10,
                "quantity": 10
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("10 Points"))
            .andExpect(jsonPath("$.description").value("10 Points"))
            .andExpect(jsonPath("$.price").value(10))
            .andExpect(jsonPath("$.quantity").value(10))

        // Assert DB
        val rewards = rewardRepository.findAll()
        assertEquals(1, rewards.size)
        val reward = rewards[0]
        assertEquals("10 Points", reward.name)
        assertEquals("10 Points", reward.description)
        assertEquals(10, reward.price)
        assertEquals(10, reward.quantity)
    }

    @Test
    fun `create reward - success - no quantity`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "description": "10 Points",
                "price": 10,
                "quantity": null
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("10 Points"))
            .andExpect(jsonPath("$.description").value("10 Points"))
            .andExpect(jsonPath("$.price").value(10))
            .andExpect(jsonPath("$.quantity").value(null))

        // Assert DB
        val rewards = rewardRepository.findAll()
        assertEquals(1, rewards.size)
        val reward = rewards[0]
        assertEquals("10 Points", reward.name)
        assertEquals("10 Points", reward.description)
        assertEquals(10, reward.price)
        assertEquals(null, reward.quantity)
    }

    @Test
    fun `create reward - failed - invalid price`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "description": "10 Points",
                "price": -1,
                "quantity": 10
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Price must be positive"))
    }

    @Test
    fun `create reward - failed - invalid quantity`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "description": "10 Points",
                "price": 10,
                "quantity": -1
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Quantity must be positive"))
    }

    @Test
    fun `create reward - failed - duplicate name`() {
        // Arrange
        val jsonBody =
            """
            {
                "name": "10 Points",
                "description": "10 Points",
                "price": 10,
                "quantity": 10
            }
            """.trimIndent()

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("10 Points"))
            .andExpect(jsonPath("$.description").value("10 Points"))
            .andExpect(jsonPath("$.price").value(10))
            .andExpect(jsonPath("$.quantity").value(10))

        // Assert DB
        val rewards = rewardRepository.findAll()
        assertEquals(1, rewards.size)
        val reward = rewards[0]
        assertEquals("10 Points", reward.name)
        assertEquals("10 Points", reward.description)
        assertEquals(10, reward.price)
        assertEquals(10, reward.quantity)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            post("/api/apps/shop/rewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Reward with the same name already exists"))
    }

    // Get rewards

    @Test
    fun `get rewards - success - one reward`() {
        // Arrange
        val reward =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 10,
                    app = authSetup.app,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            get("/api/apps/shop/rewards"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].name").value("10 Points"))
            .andExpect(jsonPath("$[0].description").value("10 Points"))
            .andExpect(jsonPath("$[0].price").value(10))
            .andExpect(jsonPath("$[0].quantity").value(10))
    }

    @Test
    fun `get rewards - success - only rewards for the given app`() {
        // Arrange
        val reward1 =
            rewardRepository.save(
                Reward(
                    name = "10 Points",
                    description = "10 Points",
                    price = 10,
                    quantity = 10,
                    app = authSetup.app,
                ),
            )

        val reward2 =
            rewardRepository.save(
                Reward(
                    name = "20 Points",
                    description = "20 Points",
                    price = 20,
                    quantity = 20,
                    app = authSetup.app,
                ),
            )

        val otherApp = setupAuth(userRepository, appRepository).app
        val otherAppReward =
            rewardRepository.save(
                Reward(
                    name = "30 Points",
                    description = "30 Points",
                    price = 30,
                    quantity = 30,
                    app = otherApp,
                ),
            )

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            get("/api/apps/shop/rewards"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(
                jsonPath(
                    "$",
                    containsInAnyOrder(
                        hasEntry(
                            "id",
                            reward1.id!!.toInt(),
                        ),
                        hasEntry(
                            "id",
                            reward2.id!!.toInt(),
                        ),
                    ),
                ),
            )
    }

    @BeforeEach
    fun setUp() {
        initIntegrationTest(postgres)
    }

    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<Nothing> =
            setupDBContainer().apply { start() }
    }
}
