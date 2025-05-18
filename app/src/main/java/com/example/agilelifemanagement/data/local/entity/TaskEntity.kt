package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agilelifemanagement.domain.model.TaskPriority
import com.example.agilelifemanagement.domain.model.TaskStatus
import java.time.LocalDate

/**
 * Room entity representing a task in the local database.
 * 
 * This entity follows Material 3 Expressive design principles by:
 * - Supporting various task states through the status field
 * - Including priority levels that can be visually represented
 * - Providing organization through sprints and categories
 * - Supporting deadline visibility with dueDate
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueDate: LocalDate?,
    val createdDate: LocalDate,
    val sprintId: String?
    // Tags will be handled through a join table relationship
)
