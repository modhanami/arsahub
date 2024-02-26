package com.arsahub.backend.dtos.supabase

data class SupabaseIdentity(
    val supabaseUserId: String,
    val googleUserId: String?,
    val email: String,
    val name: String,
)

data class UserIdentity(
    val internalUserId: Long,
    val externalUserId: String,
    val googleUserId: String?,
    val email: String,
    val name: String,
)
