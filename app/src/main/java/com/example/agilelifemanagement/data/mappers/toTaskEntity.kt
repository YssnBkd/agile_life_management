package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.domain.model.Task

fun Task.toEntity(userId: String, currentTimeMillis: Long): TaskEntity = TaskEntity(
    id = id,
    title = title,
    summary = summary.ifEmpty { null },
    description = if (description.isEmpty()) null else description,
    dueDate = dueDate?.toEpochDay(),
    priority = priority.ordinal,
    status = status.ordinal,
    estimatedEffort = estimatedEffort,
    actualEffort = null, // TODO: Add if needed
    isRecurring = false, // TODO: Support recurring
    recurringPattern = null, // TODO: Support recurring
    userId = userId,
    createdAt = currentTimeMillis,
    updatedAt = currentTimeMillis
)
