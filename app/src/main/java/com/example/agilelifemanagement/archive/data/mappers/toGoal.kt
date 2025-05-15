package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.domain.model.Goal

fun GoalEntity.toGoal(): Goal = Goal(
    id = id,
    title = title,
    summary = summary ?: "",
    description = description ?: emptyList(),
    category = category?.let { Goal.Category.values().getOrElse(it) { Goal.Category.PERSONAL } } ?: Goal.Category.PERSONAL,
    deadline = deadline?.let { java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate() },
    isCompleted = isCompleted,
    // Add other fields as needed
)
