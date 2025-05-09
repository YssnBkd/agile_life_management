package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.domain.model.Task
import java.time.LocalDate

fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    summary = summary ?: "",
    description = description ?: emptyList(),
    dueDate = dueDate?.let { LocalDate.ofEpochDay(it) },
    priority = Task.Priority.values().getOrElse(priority) { Task.Priority.MEDIUM },
    status = Task.Status.values().getOrElse(status) { Task.Status.TODO },
    estimatedEffort = estimatedEffort ?: 0
    // TODO: Map tags, sprintId, goalId, dependencies, etc. when available
)
