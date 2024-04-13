package com.arsahub.backend.repositories

import com.arsahub.backend.dtos.response.RewardWithCount
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Reward
import com.arsahub.backend.models.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun countByAppUserAndReward(
        appUser: AppUser,
        reward: Reward,
    ): Int

    @Query(
        "SELECT NEW com.arsahub.backend.dtos.response.RewardWithCount(t.reward, COUNT(t)) " +
            "FROM Transaction t WHERE t.app = :app AND t.appUser = :appUser GROUP BY t.reward",
    )
    fun findRewardAndCountByAppAndAppUser(
        app: App,
        appUser: AppUser,
    ): List<RewardWithCount>
}
