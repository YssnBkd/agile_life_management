package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.SprintReviewEntity
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for SprintReview in Supabase.
 */
@Serializable
data class SprintReviewDto(
    val id: String,
    val sprint_id: String,
    val date: Long,
    val rating: Int,
    val user_id: String,
    val created_at: Long,
    val updated_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): SprintReviewEntity {
        return SprintReviewEntity(
            id = id,
            sprintId = sprint_id,
            date = date,
            rating = rating,
            userId = user_id,
            createdAt = created_at,
            updatedAt = updated_at
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: SprintReviewEntity): SprintReviewDto {
            return SprintReviewDto(
                id = entity.id,
                sprint_id = entity.sprintId,
                date = entity.date,
                rating = entity.rating,
                user_id = entity.userId,
                created_at = entity.createdAt,
                updated_at = entity.updatedAt
            )
        }
    }
}
