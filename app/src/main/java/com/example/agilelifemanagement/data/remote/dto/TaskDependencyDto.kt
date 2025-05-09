package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.TaskDependencyEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Data Transfer Object for TaskDependency in Supabase.
 * Represents the dependency relationship between tasks.
 */
@Serializable
data class TaskDependencyDto(
    @SerialName("id") val id: String,
    @SerialName("task_id") val task_id: String,
    @SerialName("depends_on_task_id") val depends_on_task_id: String,
    @SerialName("created_at") val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): TaskDependencyEntity {
        return TaskDependencyEntity(
            id = id,
            taskId = task_id,
            dependsOnTaskId = depends_on_task_id,
            createdAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC)
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: TaskDependencyEntity): TaskDependencyDto {
            return TaskDependencyDto(
                id = entity.id,
                task_id = entity.taskId,
                depends_on_task_id = entity.dependsOnTaskId,
                created_at = entity.createdAt.toEpochSecond()
            )
        }
    }
}
