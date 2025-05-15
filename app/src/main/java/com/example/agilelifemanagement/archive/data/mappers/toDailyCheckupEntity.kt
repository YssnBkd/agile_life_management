package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import com.example.agilelifemanagement.data.remote.dto.DailyCheckupDto

fun DailyCheckupDto.toDailyCheckupEntity(): DailyCheckupEntity = DailyCheckupEntity(
    id = id,
    date = date,
    sprintId = sprint_id,
    userId = user_id,
    notes = notes,
    createdAt = created_at,
    updatedAt = updated_at
)
