package com.example.agilelifemanagement.ui.viewmodel

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.ui.model.TaskFilterChip
import com.example.agilelifemanagement.ui.model.TaskFilterType
import com.example.agilelifemanagement.ui.model.TaskSortCriteria

/**
 * UI State for Task-related screens, following the single-state pattern.
 * This data class captures all possible states of the Task screens.
 */
data class TaskUiState(
    // Task collection
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    
    // Single task detail
    val selectedTask: Task? = null,
    
    // Sprint assignment
    val availableSprints: List<Sprint> = emptyList(),
    val isLoadingSprints: Boolean = false,
    
    // Loading states
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    
    // Error state
    val errorMessage: String? = null,
    
    // Task operation states
    val isTaskSaved: Boolean = false,
    val isTaskDeleted: Boolean = false,
    val operationSuccess: Boolean = false,
    
    // Filtering
    val filterType: TaskFilterType = TaskFilterType.ALL,
    val selectedFilters: Set<TaskFilterChip> = emptySet(),
    
    // Sorting
    val sortCriteria: TaskSortCriteria = TaskSortCriteria.DUE_DATE,
    val sortAscending: Boolean = true,
    
    // Search
    val searchQuery: String = ""
)
