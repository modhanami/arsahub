package com.arsahub.backend.dtos

/**
 * DTO for {@link com.arsahub.backend.models.CustomUnit}
 */
data class CustomUnitResponse(val name: String?, val key: String?, val id: Long?) {
    companion object {
        fun fromEntity(customUnit: com.arsahub.backend.models.CustomUnit): CustomUnitResponse {
            return CustomUnitResponse(customUnit.name!!, customUnit.key!!, customUnit.id!!)
        }
    }
}