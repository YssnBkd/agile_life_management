package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.remote.dto.SprintDto
import com.example.agilelifemanagement.domain.model.Sprint

fun Sprint.toDto(userId: String, currentTimeMillis: Long): SprintDto = SprintDto(
    id = id,
    name = name,
    summary = summary,
    description = description,
    start_date = startDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
    end_date = endDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
    is_active = isActive,
    is_completed = isCompleted,
    user_id = userId,
    created_at = currentTimeMillis,
    updated_at = currentTimeMillis
)
