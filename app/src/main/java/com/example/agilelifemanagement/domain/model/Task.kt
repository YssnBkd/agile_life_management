package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model representing a task in the AgileLifeManagement app.
 * A task is a unit of work that can be tracked, prioritized, and associated with sprints.
 */
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueDate: LocalDate?,
    val createdDate: LocalDate,
    val sprintId: String?,
    val tags: List<String> = emptyList()
)
