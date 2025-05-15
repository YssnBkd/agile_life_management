package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.GoalSprintCrossRefEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Data Transfer Object for GoalSprintCrossRef in Supabase.
 * Represents the many-to-many relationship between goals and sprints.
 */
@Serializable
data class GoalSprintCrossRefDto(
    @SerialName("id") val id: String,
    @SerialName("goal_id") val goal_id: String,
    @SerialName("sprint_id") val sprint_id: String,
    @SerialName("created_at") val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): GoalSprintCrossRefEntity {
        return GoalSprintCrossRefEntity(
            id = id,
            goalId = goal_id,
            sprintId = sprint_id,
            createdAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC)
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: GoalSprintCrossRefEntity): GoalSprintCrossRefDto {
            return GoalSprintCrossRefDto(
                id = entity.id,
                goal_id = entity.goalId,
                sprint_id = entity.sprintId,
                created_at = entity.createdAt.toEpochSecond()
            )
        }
    }
}
