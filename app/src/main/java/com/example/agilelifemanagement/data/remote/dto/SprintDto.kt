package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.SprintEntity
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Sprint in Supabase.
 */
@Serializable
data class SprintDto(
    val id: String,
    val name: String,
    val summary: String? = null,
    val start_date: Long,
    val end_date: Long,
    val is_active: Boolean,
    val is_completed: Boolean,
    val user_id: String,
    val created_at: Long,
    val updated_at: Long,
    val description: List<String>? = null // New field for bullet points
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): SprintEntity {
        return SprintEntity(
            id = id,
            name = name,
            summary = summary,
            startDate = start_date,
            endDate = end_date,
            isActive = is_active,
            isCompleted = is_completed,
            userId = user_id,
            createdAt = created_at,
            updatedAt = updated_at,
            description = description
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: SprintEntity): SprintDto {
            return SprintDto(
                id = entity.id,
                name = entity.name,
                summary = entity.summary,
                start_date = entity.startDate,
                end_date = entity.endDate,
                is_active = entity.isActive,
                is_completed = entity.isCompleted,
                user_id = entity.userId,
                created_at = entity.createdAt,
                updated_at = entity.updatedAt,
                description = entity.description
            )
        }
    }
}
