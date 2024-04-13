package com.arsahub.backend.integration

import com.arsahub.backend.controllers.utils.AuthTestUtils.performWithAppAuth
import com.arsahub.backend.controllers.utils.AuthTestUtils.setupAuth
import com.arsahub.backend.dtos.request.AchievementCreateRequest
import com.arsahub.backend.models.UnlimitedRuleRepeatability
import com.arsahub.backend.repositories.AchievementRepository
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppUserAchievementRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.AchievementService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

class AchievementIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var appUserAchievementRepository: AppUserAchievementRepository

    @Autowired
    private lateinit var achievementService: AchievementService

    @Autowired
    private lateinit var appRepository: AppRepository

    @Autowired
    private lateinit var achievementRepository: AchievementRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    // Delete achievement: Only when no rules are using it and no users have it
    @Test
    fun `delete achievement - success`() {
        // Arrange
        val achievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))
        val achievementFromDB = achievementRepository.findById(achievement.achievementId!!)
        assertTrue(achievementFromDB.isPresent)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/achievements/${achievement.achievementId}"),
        )
            .andExpect(status().isNoContent)

        // Assert DB
        val achievements = achievementRepository.findById(achievement.achievementId!!)
        assertTrue(achievements.isEmpty)
    }

    @Test
    fun `delete achievement - failed - rules are using it`() {
        // Arrange
        val subjectAchievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))

        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    unlockAchievement(subjectAchievement.achievementId!!)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/achievements/${subjectAchievement.achievementId}"),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Achievement is used in rules or unlocked by users"))

        // Assert DB
        val achievements = achievementRepository.findById(subjectAchievement.achievementId!!)
        assertTrue(achievements.isPresent)
    }

    @Test
    fun `delete achievement - failed - users have it`() {
        // Arrange
        val subjectAchievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))

        val rule =
            createRule(authSetup.app) {
                title = "When workshop completed then add 100 points"
                trigger = createWorkshopCompletedTrigger(authSetup.app)
                action {
                    unlockAchievement(subjectAchievement.achievementId!!)
                }
                repeatability = UnlimitedRuleRepeatability
            }

        val user = createAppUser(authSetup.app)

        // trigger to activate the rule
        mockMvc.performWithAppAuth(
            post("/api/apps/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "key": "workshop_completed",
                        "userId": "${user.userId}"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        val appUserAchievementAfterTrigger =
            appUserAchievementRepository.findAll()
                .first { it.achievement!!.achievementId == subjectAchievement.achievementId }
        assertNotNull(appUserAchievementAfterTrigger)

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/achievements/${subjectAchievement.achievementId}"),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Achievement is used in rules or unlocked by users"))

        // Assert DB
        val achievements = achievementRepository.findById(subjectAchievement.achievementId!!)
        assertTrue(achievements.isPresent)

        val appUserAchievementAfterDelete =
            appUserAchievementRepository.findAll()
                .first { it.achievement!!.achievementId == subjectAchievement.achievementId }
        assertNotNull(appUserAchievementAfterDelete)
    }

    @Test
    fun `delete achievement - failed - different app`() {
        // Arrange
        val otherApp = setupAuth(userRepository, appRepository).app
        val achievement =
            achievementService.createAchievement(authSetup.app, AchievementCreateRequest("Workshop completed"))

        // Act & Assert HTTP
        mockMvc.performWithAppAuth(
            delete("/api/apps/achievements/${achievement.achievementId}"),
            app = otherApp,
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Achievement not found"))

        // Assert DB
        val achievementAfterFailedDelete =
            achievementRepository.findById(
                achievement
                    .achievementId!!,
            )
        assertTrue(achievementAfterFailedDelete.isPresent)
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
