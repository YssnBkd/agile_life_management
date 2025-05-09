package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.UserEntity
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for User in Supabase.
 */
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val profile_image_url: String? = null,
    val created_at: Long,
    val updated_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            email = email,
            profileImageUrl = profile_image_url,
            createdAt = created_at,
            updatedAt = updated_at
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: UserEntity): UserDto {
            return UserDto(
                id = entity.id,
                name = entity.name,
                email = entity.email,
                profile_image_url = entity.profileImageUrl,
                created_at = entity.createdAt,
                updated_at = entity.updatedAt
            )
        }
    }
}
