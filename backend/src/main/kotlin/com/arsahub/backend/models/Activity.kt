package com.arsahub.backend.models

import jakarta.persistence.*

@Entity
@Table(name = "activity")
class Activity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    val activityId: Long = 0,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String?,

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "organizer_id", nullable = false, insertable = false, updatable = false)
//    val organizer: Organizer? = null,
//
//    @Column(name = "organizer_id", nullable = false)
//    val organizerId: Long,

    @OneToMany(mappedBy = "activity")
//    @JsonIgnore
    val members: MutableSet<Member> = mutableSetOf(),
) {
    override fun toString(): String {
        return "Event(activityId=$activityId, title='$title')"
    }
}
