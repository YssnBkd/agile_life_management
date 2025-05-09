package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.remote.dto.GoalDto
import com.example.agilelifemanagement.domain.model.Goal

private fun Goal.Category.toGoalCategory(): com.example.agilelifemanagement.domain.model.GoalCategory = when (this) {
    Goal.Category.PERSONAL -> com.example.agilelifemanagement.domain.model.GoalCategory.PERSONAL
    Goal.Category.PROFESSIONAL -> com.example.agilelifemanagement.domain.model.GoalCategory.PROFESSIONAL
    Goal.Category.HEALTH -> com.example.agilelifemanagement.domain.model.GoalCategory.HEALTH
    Goal.Category.FINANCIAL -> com.example.agilelifemanagement.domain.model.GoalCategory.FINANCIAL
    Goal.Category.LEARNING -> com.example.agilelifemanagement.domain.model.GoalCategory.LEARNING
    Goal.Category.OTHER -> com.example.agilelifemanagement.domain.model.GoalCategory.OTHER
}

fun Goal.toDto(userId: String, currentTimeMillis: Long): GoalDto = GoalDto(
    id = id,
    title = title,
    summary = summary.ifEmpty { null },
    description = if (description.isEmpty()) null else description,
    category = category.toGoalCategory(),
    deadline = deadline?.atStartOfDay(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
    is_completed = isCompleted,
    user_id = userId,
    created_at = currentTimeMillis,
    updated_at = currentTimeMillis
)
