package com.example.agilelifemanagement.ui.viewmodel

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.ui.model.TaskFilterChip
import com.example.agilelifemanagement.ui.model.TaskFilterType
import com.example.agilelifemanagement.ui.model.TaskSortCriteria

/**
 * UI State for Task screens.
 * This follows the Unidirectional Data Flow pattern.
 */
data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val selectedTask: Task? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isTaskSaved: Boolean = false,
    val isTaskDeleted: Boolean = false,
    
    // Filtering and sorting state
    val filterType: TaskFilterType = TaskFilterType.ALL,
    val selectedFilters: Set<TaskFilterChip> = emptySet(),
    val sortCriteria: TaskSortCriteria = TaskSortCriteria.DUE_DATE,
    val sortAscending: Boolean = true,
    val searchQuery: String = "",
    
    // Group options
    val groupTasks: Boolean = false,
    val groupedTasks: Map<String, List<Task>> = emptyMap()
)
