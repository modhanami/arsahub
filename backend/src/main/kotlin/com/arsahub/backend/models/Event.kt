package com.arsahub.backend.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime

@Entity
@Table(name = "event")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    val eventId: Long? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String?,

    @Column(name = "location")
    var location: String?,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDateTime,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false, insertable = false, updatable = false)
    val organizer: Organizer? = null,

    @Column(name = "organizer_id", nullable = false)
    val organizerId: Long? = null,

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    val participations: MutableSet<Participation> = mutableSetOf()
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Event

        return eventId != null && eventId == other.eventId
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    fun isFinished(currentDateTime: LocalDateTime): Boolean {
        return currentDateTime.isAfter(endDate)
    }

    @Transient
    fun isValid(): Boolean {
        return startDate.isBefore(endDate)
    }
}
