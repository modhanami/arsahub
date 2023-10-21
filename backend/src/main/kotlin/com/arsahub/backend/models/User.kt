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

    @Column(name = "name", nullable = false)
    val name: String,

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    val members: MutableSet<Member> = mutableSetOf(),

//    @ManyToOne
//    @JoinColumn(name = "role_id", nullable = false)
//    val role: Role // Many users can have the same role

    @Column(name = "external_user_id", unique = true, nullable = false)
    val externalUserId: String,
) {
    override fun toString(): String {
        return "User(userId=$userId, username='$username', members=$members)"
    }
}
