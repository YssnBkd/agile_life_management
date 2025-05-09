package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.TaskTagCrossRefEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Data Transfer Object for TaskTagCrossRef in Supabase.
 * Represents the many-to-many relationship between tasks and tags.
 */
@Serializable
data class TaskTagCrossRefDto(
    @SerialName("id") val id: String,
    @SerialName("task_id") val task_id: String,
    @SerialName("tag_id") val tag_id: String,
    @SerialName("created_at") val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): TaskTagCrossRefEntity {
        return TaskTagCrossRefEntity(
            id = id,
            taskId = task_id,
            tagId = tag_id,
            createdAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC)
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: TaskTagCrossRefEntity): TaskTagCrossRefDto {
            return TaskTagCrossRefDto(
                id = entity.id,
                task_id = entity.taskId,
                tag_id = entity.tagId,
                created_at = entity.createdAt.toEpochSecond()
            )
        }
    }
}
