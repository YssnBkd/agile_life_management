package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.domain.model.TaskPriority
import com.example.agilelifemanagement.domain.model.TaskStatus
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Task in Supabase.
 */
@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val summary: String? = null,
    val due_date: Long? = null,
    val priority: TaskPriority,
    val status: TaskStatus,
    val estimated_effort: Int? = null,
    val actual_effort: Int? = null,
    val is_recurring: Boolean,
    val recurring_pattern: String? = null,
    val user_id: String,
    val created_at: Long,
    val updated_at: Long,
    val description: List<String>? = null // New field for bullet points
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            summary = summary,
            dueDate = due_date,
            priority = priority.ordinal,
            status = status.ordinal,
            estimatedEffort = estimated_effort,
            actualEffort = actual_effort,
            isRecurring = is_recurring,
            recurringPattern = recurring_pattern,
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
        fun fromEntity(entity: TaskEntity): TaskDto {
            return TaskDto(
                id = entity.id,
                title = entity.title,
                summary = entity.summary,
                due_date = entity.dueDate,
                priority = TaskPriority.fromInt(entity.priority),
                status = TaskStatus.fromInt(entity.status),
                estimated_effort = entity.estimatedEffort,
                actual_effort = entity.actualEffort,
                is_recurring = entity.isRecurring,
                recurring_pattern = entity.recurringPattern,
                user_id = entity.userId,
                created_at = entity.createdAt,
                updated_at = entity.updatedAt,
                description = entity.description
            )
        }
    }
}
