package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model for Task.
 * Represents a task that can be assigned to a sprint.
 */
data class Task(
    val id: String = "",
    val title: String,
    val summary: String = "",
    val description: List<String> = emptyList(),
    val dueDate: LocalDate? = null,
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.TODO,
    val sprintId: String? = null,
    val goalId: String? = null,
    val estimatedEffort: Int = 0, // Story points or hours
    val tags: List<String> = emptyList()
) {
    enum class Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
    
    enum class Status {
        BACKLOG, TODO, IN_PROGRESS, BLOCKED, REVIEW, DONE
    }
}
