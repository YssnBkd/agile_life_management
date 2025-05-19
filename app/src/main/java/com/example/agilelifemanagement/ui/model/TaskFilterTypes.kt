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
    OVERDUE;
    
    val displayName: String
        get() = when (this) {
            ALL -> "All Tasks"
            ACTIVE -> "Active"
            COMPLETED -> "Completed"
            TODAY -> "Due Today"
            UPCOMING -> "Upcoming"
            OVERDUE -> "Overdue"
        }
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
    NOT_STARTED;
    
    val displayName: String
        get() = when (this) {
            HIGH_PRIORITY -> "High Priority"
            MEDIUM_PRIORITY -> "Medium Priority"
            LOW_PRIORITY -> "Low Priority"
            HAS_DEADLINE -> "Has Deadline"
            NO_DEADLINE -> "No Deadline"
            IN_PROGRESS -> "In Progress"
            NOT_STARTED -> "Not Started"
        }
}

/**
 * Represents sort criteria options for tasks.
 */
enum class TaskSortCriteria {
    TITLE,
    DUE_DATE,
    PRIORITY,
    STATUS,
    CREATED_DATE;
    
    val displayName: String
        get() = when (this) {
            TITLE -> "Title"
            DUE_DATE -> "Due Date"
            PRIORITY -> "Priority"
            STATUS -> "Status"
            CREATED_DATE -> "Created Date"
        }
}
