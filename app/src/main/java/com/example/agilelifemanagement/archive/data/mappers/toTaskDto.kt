package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.remote.dto.TaskDto
import com.example.agilelifemanagement.domain.model.Task

private fun Task.Priority.toTaskPriority(): com.example.agilelifemanagement.domain.model.TaskPriority = when (this) {
    Task.Priority.LOW -> com.example.agilelifemanagement.domain.model.TaskPriority.LOW
    Task.Priority.MEDIUM -> com.example.agilelifemanagement.domain.model.TaskPriority.MEDIUM
    Task.Priority.HIGH -> com.example.agilelifemanagement.domain.model.TaskPriority.HIGH
    Task.Priority.URGENT -> com.example.agilelifemanagement.domain.model.TaskPriority.URGENT
}

private fun Task.Status.toTaskStatus(): com.example.agilelifemanagement.domain.model.TaskStatus = when (this) {
    Task.Status.TODO -> com.example.agilelifemanagement.domain.model.TaskStatus.TODO
    Task.Status.IN_PROGRESS -> com.example.agilelifemanagement.domain.model.TaskStatus.IN_PROGRESS
    Task.Status.DONE -> com.example.agilelifemanagement.domain.model.TaskStatus.DONE
    Task.Status.BLOCKED -> com.example.agilelifemanagement.domain.model.TaskStatus.BLOCKED
    Task.Status.BACKLOG -> com.example.agilelifemanagement.domain.model.TaskStatus.BACKLOG
    Task.Status.REVIEW -> com.example.agilelifemanagement.domain.model.TaskStatus.REVIEW
}

fun Task.toDto(userId: String, currentTimeMillis: Long): TaskDto = TaskDto(
    id = id,
    title = title,
    summary = summary.ifEmpty { null },
    description = if (description.isEmpty()) null else description,
    due_date = dueDate?.toEpochDay(),
    priority = priority.toTaskPriority(),
    status = status.toTaskStatus(),
    estimated_effort = estimatedEffort,
    actual_effort = null, // TODO: Add if needed
    is_recurring = false, // TODO: Support recurring
    recurring_pattern = null, // TODO: Support recurring
    user_id = userId,
    created_at = currentTimeMillis,
    updated_at = currentTimeMillis
)
