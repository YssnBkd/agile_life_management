package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.TaskSprintCrossRefEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Data Transfer Object for TaskSprintCrossRef in Supabase.
 * Represents the many-to-many relationship between tasks and sprints.
 */
@Serializable
data class TaskSprintCrossRefDto(
    @SerialName("id") val id: String,
    @SerialName("task_id") val task_id: String,
    @SerialName("sprint_id") val sprint_id: String,
    @SerialName("created_at") val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): TaskSprintCrossRefEntity {
        return TaskSprintCrossRefEntity(
            id = id,
            taskId = task_id,
            sprintId = sprint_id,
            createdAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC)
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: TaskSprintCrossRefEntity): TaskSprintCrossRefDto {
            return TaskSprintCrossRefDto(
                id = entity.id,
                task_id = entity.taskId,
                sprint_id = entity.sprintId,
                created_at = entity.createdAt.toEpochSecond()
            )
        }
    }
}
