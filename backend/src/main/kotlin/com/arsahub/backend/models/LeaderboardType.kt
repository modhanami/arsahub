package com.arsahub.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "leaderboard_type")
class LeaderboardType(
    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    var name: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leaderboard_type_id", nullable = false)
    var id: Long? = null
}

object LeaderboardTypes {
    val DAILY = LeaderboardType("Daily").apply { id = 1 }
    val WEEKLY = LeaderboardType("Weekly").apply { id = 2 }
    val MONTHLY = LeaderboardType("Monthly").apply { id = 3 }
}
