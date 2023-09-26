package com.arsahub.backend.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long = 0,

    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    val participations: MutableSet<Participation> = mutableSetOf(),

     @Column(name = "points", nullable = false)
     var points: Int = 0,
) {
    fun addPoints(points: Int) {
        this.points += points
    }
}