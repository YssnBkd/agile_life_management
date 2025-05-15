package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import com.example.agilelifemanagement.domain.model.DailyCheckup
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

fun DailyCheckupEntity.toDailyCheckup(): DailyCheckup = DailyCheckup(
    id = id,
    date = Instant.ofEpochSecond(date).atZone(ZoneOffset.UTC).toLocalDate(),
    sprintId = sprintId,
    notes = notes ?: ""
    // accomplishments, plannedTasks, blockers would be filled by querying CheckupEntryEntity by checkupId and type
)
