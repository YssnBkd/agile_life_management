package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.TagEntity
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Tag in Supabase.
 */
@Serializable
data class TagDto(
    val id: String,
    val name: String,
    val color: String,
    val user_id: String,
    val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): TagEntity {
        return TagEntity(
            id = id,
            name = name,
            color = color,
            userId = user_id,
            createdAt = created_at
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: TagEntity): TagDto {
            return TagDto(
                id = entity.id,
                name = entity.name,
                color = entity.color,
                user_id = entity.userId,
                created_at = entity.createdAt
            )
        }
    }
}
