package com.example.agilelifemanagement.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Helper models and enums for Task entity in the AgileLifeManagement app.
 * The primary Task model is defined in Task.kt
 * The primary TaskStatus enum is defined in TaskStatus.kt
 * The primary TaskPriority enum is defined in TaskPriority.kt
 */

// TaskStatus enum is defined in TaskStatus.kt

// TaskPriority enum is defined in TaskPriority.kt

/**
 * Represents the filter options for task due date.
 */
enum class TaskDueFilter {
    ALL,
    TODAY,
    UPCOMING,
    OVERDUE,
    COMPLETED
}

/**
 * Represents the sorting options for tasks.
 */
enum class TaskSortOption {
    TITLE,
    DUE_DATE,
    PRIORITY,
    STATUS,
    CREATED_DATE
}

/**
 * Represents task grouping options.
 */
enum class TaskGroupOption {
    NONE,
    STATUS,
    PRIORITY,
    DUE_DATE
}

/**
 * Represents task filtering by deadline existence.
 */
enum class TaskDeadlineFilter {
    ALL,
    HAS_DEADLINE,
    NO_DEADLINE
}

/**
 * Represents task filtering by progress.
 */
enum class TaskProgressFilter {
    ALL,
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}
