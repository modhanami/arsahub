package com.arsahub.backend.models

import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(

    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @OneToMany(mappedBy = "user")
    var userActivities: MutableSet<UserActivity> = mutableSetOf(),
) : AuditedEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long? = 0

}
