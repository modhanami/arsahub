package com.arsahub.backend.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.Instant

@Entity
@Table(name = "event")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    val eventId: Long = 0,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String?,

    @Column(name = "location")
    var location: String?,

    @Column(name = "start_time", nullable = false)
    var startTime: Instant,

    @Column(name = "end_time", nullable = false)
    var endTime: Instant,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false, insertable = false, updatable = false)
    val organizer: Organizer? = null,

    @Column(name = "organizer_id", nullable = false)
    val organizerId: Long,

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    val participations: MutableSet<Participation> = mutableSetOf(),

    @Column(name = "completed")
    var completed: Boolean = false,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completed_at")
    var completedAt: Instant? = null,

    @Column(name = "points", nullable = false)
    val points: Int,
) {
    override fun toString(): String {
        return "Event(eventId=$eventId, title='$title', startTime=$startTime, endTime=$endTime)"
    }

    fun markAsCompleted(completedAt: Instant) {
        completed = true
        this.completedAt = completedAt
    }
}
