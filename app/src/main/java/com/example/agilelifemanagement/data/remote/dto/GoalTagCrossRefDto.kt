package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.GoalTagCrossRefEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Data Transfer Object for GoalTagCrossRef in Supabase.
 * Represents the many-to-many relationship between goals and tags.
 */
@Serializable
data class GoalTagCrossRefDto(
    @SerialName("id") val id: String,
    @SerialName("goal_id") val goal_id: String,
    @SerialName("tag_id") val tag_id: String,
    @SerialName("created_at") val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): GoalTagCrossRefEntity {
        return GoalTagCrossRefEntity(
            id = id,
            goalId = goal_id,
            tagId = tag_id,
            createdAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC)
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: GoalTagCrossRefEntity): GoalTagCrossRefDto {
            return GoalTagCrossRefDto(
                id = entity.id,
                goal_id = entity.goalId,
                tag_id = entity.tagId,
                created_at = entity.createdAt.toEpochSecond()
            )
        }
    }
}
