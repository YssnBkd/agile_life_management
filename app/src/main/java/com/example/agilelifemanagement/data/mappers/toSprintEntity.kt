package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.domain.model.Sprint

fun Sprint.toEntity(userId: String, currentTimeMillis: Long): SprintEntity = SprintEntity(
    id = id,
    name = name,
    summary = summary.ifEmpty { null },
    description = if (description.isEmpty()) null else description,
    startDate = startDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
    endDate = endDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
    isActive = isActive,
    isCompleted = isCompleted,
    userId = userId,
    createdAt = currentTimeMillis,
    updatedAt = currentTimeMillis
)
