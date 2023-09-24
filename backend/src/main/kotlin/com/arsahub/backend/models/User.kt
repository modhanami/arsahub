package com.arsahub.backend.models

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_events",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "event_id")]
    )
    var joinedEvents: MutableSet<Event> = mutableSetOf()
)