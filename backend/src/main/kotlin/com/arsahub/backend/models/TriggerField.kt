package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "trigger_field", schema = "public")
class TriggerField(

    @NotNull
    @Column(name = "key", nullable = false, length = Integer.MAX_VALUE)
    var key: String? = null,

    @NotNull
    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    var type: String? = null,

    @Column(name = "label", length = Integer.MAX_VALUE)
    var label: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trigger_id", nullable = false)
    var trigger: Trigger? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trigger_field_id", nullable = false)
    var id: Long? = null
}

enum class TriggerFieldType(val key: String) {
    INTEGER("integer"),
    TEXT("text");

    override fun toString(): String {
        return key
    }

    fun matchesKey(key: String): Boolean {
        return this.key == key.lowercase()
    }

    companion object {
        fun supports(key: String): Boolean {
            return entries.any { it.matchesKey(key) }
        }

        fun fromString(key: String): TriggerFieldType? {
            return entries.firstOrNull { it.matchesKey(key) }
        }
    }
}