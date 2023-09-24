package com.arsahub.backend.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    val participations: MutableSet<Participation> = mutableSetOf()
)