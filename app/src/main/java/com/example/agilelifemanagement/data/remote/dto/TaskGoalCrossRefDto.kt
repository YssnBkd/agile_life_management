package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.TaskGoalCrossRefEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Data Transfer Object for TaskGoalCrossRef in Supabase.
 * Represents the many-to-many relationship between tasks and goals.
 */
@Serializable
data class TaskGoalCrossRefDto(
    @SerialName("id") val id: String,
    @SerialName("task_id") val task_id: String,
    @SerialName("goal_id") val goal_id: String,
    @SerialName("created_at") val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): TaskGoalCrossRefEntity {
        return TaskGoalCrossRefEntity(
            id = id,
            taskId = task_id,
            goalId = goal_id,
            createdAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC)
        )
    }
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: TaskGoalCrossRefEntity): TaskGoalCrossRefDto {
            return TaskGoalCrossRefDto(
                id = entity.id,
                task_id = entity.taskId,
                goal_id = entity.goalId,
                created_at = entity.createdAt.toEpochSecond()
            )
        }
    }
}
