package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.domain.model.GoalCategory
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Goal in Supabase.
 */
@Serializable
data class GoalDto(
    val id: String,
    val title: String,
    val summary: String? = null,
    val category: GoalCategory? = null,
    val deadline: Long? = null,
    val is_completed: Boolean,
    val user_id: String,
    val created_at: Long,
    val updated_at: Long,
    val description: List<String>? = null // New field for bullet points
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): GoalEntity {
        return GoalEntity(
            id = id,
            title = title,
            summary = summary,
            category = category?.ordinal,
            deadline = deadline,
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
        fun fromEntity(entity: GoalEntity): GoalDto {
            return GoalDto(
                id = entity.id,
                title = entity.title,
                summary = entity.summary,
                category = entity.category?.let { GoalCategory.fromInt(it) },
                deadline = entity.deadline,
                is_completed = entity.isCompleted,
                user_id = entity.userId,
                created_at = entity.createdAt,
                updated_at = entity.updatedAt,
                description = entity.description
            )
        }
    }
}
