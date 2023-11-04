package com.arsahub.backend.models

import jakarta.persistence.*

@Entity
@Table(name = "member")
class Member(
    @Id
    @Column(name = "member_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,

    @Column(name = "points", nullable = false)
    var points: Int = 0,
) {
    override fun toString(): String {
        return "Member(user=$user, event=$activity)"
    }
}