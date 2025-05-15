package com.example.agilelifemanagement.auth.domain.model

/**
 * Domain model representing a user authenticated via Supabase.
 * This should match the agile_life.users schema and reference Supabase Auth user id.
 */
data class User(
    val id: String, // UUID from Supabase Auth
    val name: String?,
    val email: String,
    val profileImageUrl: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// Extension function to map User to UserEntity
fun User.toUserEntity(): com.example.agilelifemanagement.data.local.entity.UserEntity {
    return com.example.agilelifemanagement.data.local.entity.UserEntity(
        id = this.id,
        name = this.name ?: "",
        email = this.email,
        profileImageUrl = this.profileImageUrl,
        createdAt = this.createdAt?.toLongOrNull() ?: System.currentTimeMillis(),
        updatedAt = this.updatedAt?.toLongOrNull() ?: System.currentTimeMillis()
    )
}
