package com.arsahub.backend.integration

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.dtos.request.AchievementCreateRequest
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.OncePerUserRuleRepeatability
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserAchievementRepository
import com.arsahub.backend.repositories.AppUserPointsHistoryRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.AchievementService
import com.corundumstudio.socketio.SocketIOServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

class AppUserIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var appUserPointsHistoryRepository: AppUserPointsHistoryRepository

    @Autowired
    private lateinit var appUserAchievementRepository: AppUserAchievementRepository

    @Autowired
    private lateinit var achievementService: AchievementService

    @Autowired
    private lateinit var appRepository: AppRepository

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    @Suppress("unused")
    private lateinit var socketIoServer: SocketIOServer // no-op

    @MockBean
    @Suppress("unused")
    private lateinit var socketIOService: SocketIOService // no-op

    @Test
    fun `returns list of app users with 200`() {
        // Arrange
        val appUser1 =
            AppUser(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                displayName = "User1",
                app = authSetup.app,
                points = 1000,
            )
        val appUser2 =
            AppUser(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000002").toString(),
                displayName = "User2",
                app = authSetup.app,
                points = 2000,
            )
        appUserRepository.saveAll(listOf(appUser1, appUser2))

        // Act & Assert
        mockMvc.performWithAppAuth(
            get("/api/apps/users"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value("00000000-0000-0000-0000-000000000001"))
            .andExpect(jsonPath("$[0].displayName").value("User1"))
            .andExpect(jsonPath("$[0].points").value(1000))
            .andExpect(jsonPath("$[1].userId").value("00000000-0000-0000-0000-000000000002"))
            .andExpect(jsonPath("$[1].displayName").value("User2"))
            .andExpect(jsonPath("$[1].points").value(2000))
    }

    @Test
    fun `creates app users - success`() {
        // Arrange
        val jsonBody =
            """
            [
                {
                    "uniqueId": "00000000-0000-0000-0000-000000000001",
                    "displayName": "User1"
                },
                {
                    "uniqueId": "00000000-0000-0000-0000-000000000002",
                    "displayName": "User2"
                }
            ]
            """.trimIndent()
        val otherApp = setupAuth(userRepository, appRepository).app
        val use1InOtherApp =
            AppUser(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                displayName = "User1",
                app = otherApp,
                points = 1000,
            )
        appUserRepository.save(use1InOtherApp)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/users/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value("00000000-0000-0000-0000-000000000001"))
            .andExpect(jsonPath("$[0].displayName").value("User1"))
            .andExpect(jsonPath("$[0].points").value(0))
            .andExpect(jsonPath("$[1].userId").value("00000000-0000-0000-0000-000000000002"))
            .andExpect(jsonPath("$[1].displayName").value("User2"))
            .andExpect(jsonPath("$[1].points").value(0))

        // Assert DB
        val appUsers = appUserRepository.findAllByApp(authSetup.app)
        assertEquals(2, appUsers.size)
        val appUser1 = appUsers.find { it.userId == "00000000-0000-0000-0000-000000000001" }
        assertNotNull(appUser1)
        assertEquals("User1", appUser1!!.displayName)
        assertEquals(0, appUser1.points)
        val appUser2 = appUsers.find { it.userId == "00000000-0000-0000-0000-000000000002" }
        assertNotNull(appUser2)
        assertEquals("User2", appUser2!!.displayName)
        assertEquals(0, appUser2.points)
    }

    @Test
    fun `creates app users - failed - some users already exist`() {
        // Arrange
        val appUser1 =
            AppUser(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000001").toString(),
                displayName = "User1",
                app = authSetup.app,
                points = 1000,
            )
        appUserRepository.save(appUser1)

        val jsonBody =
            """
            [
                {
                    "uniqueId": "00000000-0000-0000-0000-000000000001",
                    "displayName": "User1"
                },
                {
                    "uniqueId": "00000000-0000-0000-0000-000000000002",
                    "displayName": "User2"
                }
            ]
            """.trimIndent()

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/users/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Some users already exist"))

        // Assert DB
        val appUsers = appUserRepository.findAllByApp(authSetup.app)
        assertEquals(1, appUsers.size)
        val appUser = appUsers[0]
        assertEquals("User1", appUser.displayName)
        assertEquals(1000, appUser.points)
    }

    // Delete app user: Always allowed
    @Test
    fun `delete app user - success`() {
        // Arrange
        val appUser = createAppUser(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/users/${appUser.userId}"),
        )
            .andExpect(status().isNoContent)

        // Assert DB
        val appUsers = appUserRepository.findById(appUser.id!!)
        assertTrue(appUsers.isEmpty)
    }

    @Test
    fun `delete app user - with points and achievements - success`() {
        // Arrange
        val appUser = createAppUser(authSetup.app)

        val achievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))

        val workshopCompletedTrigger = createWorkshopCompletedTrigger(authSetup.app)

        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then unlock achievement"
                trigger = workshopCompletedTrigger
                action {
                    unlockAchievement(achievement.achievementId!!)
                }
                repeatability = OncePerUserRuleRepeatability
            }

        val rule2 =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = workshopCompletedTrigger
                action {
                    addPoints(100)
                }
                repeatability = OncePerUserRuleRepeatability
            }

        // trigger to activate the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "userId": "${appUser.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Preconditions
        val appUserAchievements = appUserAchievementRepository.findAll()
        assertEquals(1, appUserAchievements.size)
        val appUserAchievement = appUserAchievements[0]
        assertEquals(appUser.id, appUserAchievement.appUser?.id)
        assertEquals(achievement.achievementId, appUserAchievement.achievement?.achievementId)
        assertEquals(100, appUser.points)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/users/${appUser.userId}"),
        )
            .andExpect(status().isNoContent)

        // Assert DB
        val appUsersAfterDelete = appUserRepository.findById(appUser.id!!)
        assertTrue(appUsersAfterDelete.isEmpty)

        val appUserAchievementsAfterDelete = appUserAchievementRepository.findAll()
        assertEquals(0, appUserAchievementsAfterDelete.size)
    }

    @Test
    fun `delete app user - failed - different app`() {
        // Arrange
        val otherApp = setupAuth(userRepository, appRepository).app
        val appUser = createAppUser(authSetup.app)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/users/${appUser.userId}"),
            app = otherApp,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("App user not found"))

        // Assert DB
        val appUsers = appUserRepository.findById(appUser.id!!)
        assertTrue(appUsers.isPresent)
    }

    // Add (or remove) points directly to the user, without using a trigger
    @Test
    fun `add points directly to user - success`() {
        // Arrange
        val user = createAppUser(authSetup.app)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/users/${user.userId}/points/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "points": 100
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(100, userAfter.points)

        val pointsHistories = appUserPointsHistoryRepository.findAllByAppAndAppUser(authSetup.app, user)
        assertEquals(1, pointsHistories.size)
        assertEquals(100, pointsHistories[0].points)
        assertEquals(100, pointsHistories[0].pointsChange)
    }

    @Test
    fun `add points directly to user - success with maximum points`() {
        // Arrange
        val user = createAppUser(authSetup.app)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/users/${user.userId}/points/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "points": 2147483647
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(2147483647, userAfter.points)

        val pointsHistories = appUserPointsHistoryRepository.findAllByAppAndAppUser(authSetup.app, user)
        assertEquals(1, pointsHistories.size)
        assertEquals(2147483647, pointsHistories[0].points)
        assertEquals(2147483647, pointsHistories[0].pointsChange)
    }

    @Test
    fun `add points directly to user - success with negative points`() {
        // Arrange
        val user = createAppUser(authSetup.app)

        mockMvc.performWithAppAuth(
            post("/api/apps/users/${user.userId}/points/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "points": 100
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/users/${user.userId}/points/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "points": -20
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val userAfter = appUserRepository.findById(user.id!!).get()
        assertEquals(80, userAfter.points)

        val pointsHistories =
            appUserPointsHistoryRepository.findAllByAppAndAppUser(authSetup.app, user)
                .sortedByDescending { it.createdAt }
        assertEquals(2, pointsHistories.size)
        assertEquals(80, pointsHistories[0].points)
        assertEquals(-20, pointsHistories[0].pointsChange)
    }

    @Test
    fun `add points directly to user - failure with non-existing user`() {
        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/users/non-existing-user/points/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "points": 100
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isNotFound)
    }

    // Unlock achievement directly to the user, without using a trigger
    @Test
    fun `unlock achievement directly to user - success`() {
        // Arrange
        val user = createAppUser(authSetup.app)
        val achievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("You Joined!"))

        // Act & Assert
        mockMvc.performWithAppAuth(
            post("/api/apps/users/${user.userId}/achievements/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "achievementId": "${achievement.achievementId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val appUserAchievements = appUserAchievementRepository.findAll()
        assertEquals(1, appUserAchievements.size)
        assertEquals(user.id, appUserAchievements[0].appUser?.id)
        assertEquals(achievement.achievementId, appUserAchievements[0].achievement?.achievementId)
    }

    @Test
    fun `unlock achievement directly to user - success with existing achievement`() {
        val user = createAppUser(authSetup.app)
        val achievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("You Joined!"))

        mockMvc.performWithAppAuth(
            post("/api/apps/users/${user.userId}/achievements/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "achievementId": "${achievement.achievementId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val appUserAchievements = appUserAchievementRepository.findAll()
        assertEquals(1, appUserAchievements.size)
        assertEquals(user.id, appUserAchievements[0].appUser?.id)
        assertEquals(achievement.achievementId, appUserAchievements[0].achievement?.achievementId)
    }

    @Test
    fun `unlock achievement directly to user - failure with non-existing achievement`() {
        val user = createAppUser(authSetup.app)

        mockMvc.performWithAppAuth(
            post("/api/apps/users/${user.userId}/achievements/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "achievementId": 100
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `unlock achievement directly to user - failure with non-existing user`() {
        val achievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("You Joined!"))

        mockMvc.performWithAppAuth(
            post("/api/apps/users/non-existing-user/achievements/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "achievementId": "${achievement.achievementId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isNotFound)
    }
}
