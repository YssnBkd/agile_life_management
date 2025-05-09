package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.domain.model.Goal

fun Goal.toEntity(userId: String, currentTimeMillis: Long): GoalEntity = GoalEntity(
    id = id,
    title = title,
    summary = summary.ifEmpty { null },
    description = if (description.isEmpty()) null else description,
    category = category.ordinal,
    deadline = deadline?.atStartOfDay(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
    isCompleted = isCompleted,
    userId = userId,
    createdAt = currentTimeMillis,
    updatedAt = currentTimeMillis
)
