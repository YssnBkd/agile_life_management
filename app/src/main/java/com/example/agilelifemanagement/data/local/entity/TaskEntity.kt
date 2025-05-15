package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agilelifemanagement.domain.model.TaskPriority
import com.example.agilelifemanagement.domain.model.TaskStatus
import java.time.LocalDate

/**
 * Room entity representing a task in the local database.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueDate: LocalDate?,
    val createdDate: LocalDate,
    val sprintId: String?
    // Tags will be handled through a join table relationship
)
