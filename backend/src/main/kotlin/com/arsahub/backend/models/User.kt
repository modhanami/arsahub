package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long = 0,

    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "external_user_id", unique = true, nullable = false)
    val externalUserId: String,
) {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "external_system_id", nullable = false)
    var externalSystem: ExternalSystem? = null

    @OneToMany(mappedBy = "user")
    var userActivities: MutableSet<UserActivity> = mutableSetOf()

}
