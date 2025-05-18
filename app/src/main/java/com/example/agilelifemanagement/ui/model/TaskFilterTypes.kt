package com.example.agilelifemanagement.ui.model

/**
 * Represents the primary filter types for tasks in the UI.
 */
enum class TaskFilterType {
    ALL,
    ACTIVE,
    COMPLETED,
    TODAY,
    UPCOMING,
    OVERDUE
}

/**
 * Represents filter chip options for additional task filtering.
 */
enum class TaskFilterChip {
    HIGH_PRIORITY,
    MEDIUM_PRIORITY,
    LOW_PRIORITY,
    HAS_DEADLINE,
    NO_DEADLINE,
    IN_PROGRESS,
    NOT_STARTED
}

/**
 * Represents sort criteria options for tasks.
 */
enum class TaskSortCriteria {
    TITLE,
    DUE_DATE,
    PRIORITY,
    STATUS,
    CREATED_DATE
}
