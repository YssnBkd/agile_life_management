package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.domain.usecase.task.CreateTaskUseCase
import com.example.agilelifemanagement.domain.usecase.task.DeleteTaskUseCase
import com.example.agilelifemanagement.domain.usecase.task.GetTaskByIdUseCase
import com.example.agilelifemanagement.domain.usecase.task.GetTasksUseCase
import com.example.agilelifemanagement.domain.usecase.task.UpdateTaskStatusUseCase
import com.example.agilelifemanagement.domain.usecase.task.UpdateTaskUseCase
import com.example.agilelifemanagement.ui.screens.task.TaskFilterChip
import com.example.agilelifemanagement.ui.screens.task.TaskFilterType
import com.example.agilelifemanagement.ui.screens.task.TaskSortCriteria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

/**
 * UI State for the Task screens, following the Unidirectional Data Flow pattern.
 */
data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val selectedTask: Task? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val filterType: TaskFilterType = TaskFilterType.ALL,
    val sortCriteria: TaskSortCriteria = TaskSortCriteria.DUE_DATE,
    val sortAscending: Boolean = true,
    val selectedFilters: List<TaskFilterChip> = emptyList(),
    val isTaskSaved: Boolean = false,
    val isTaskDeleted: Boolean = false
)

/**
 * ViewModel for Task-related screens, implementing Unidirectional Data Flow.
 * It handles loading tasks, filtering, sorting, and CRUD operations.
 */
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(TaskUiState())
    
    // Public immutable state flow for UI consumption
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    /**
     * Loads all tasks and applies the current filtering/sorting
     */
    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getTasksUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading tasks")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading tasks"
                        )
                    }
                }
                .collectLatest { tasks ->
                    _uiState.update { 
                        val filteredTasks = applyFiltersAndSort(tasks)
                        it.copy(
                            tasks = tasks,
                            filteredTasks = filteredTasks,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    /**
     * Loads a specific task by ID
     */
    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getTaskByIdUseCase(taskId)
                .catch { e ->
                    Timber.e(e, "Error loading task: $taskId")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading task"
                        )
                    }
                }
                .collectLatest { task ->
                    _uiState.update { 
                        it.copy(
                            selectedTask = task,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    /**
     * Creates a new task
     */
    fun createTask(task: Task) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isTaskSaved = false) }
            
            try {
                val result = createTaskUseCase(task)
                result.fold(
                    onSuccess = { createdTask ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedTask = createdTask,
                                isTaskSaved = true
                            )
                        }
                        // Refresh task list
                        loadTasks()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error creating task")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to create task",
                                isTaskSaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception creating task")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while creating task",
                        isTaskSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Updates an existing task
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isTaskSaved = false) }
            
            try {
                val result = updateTaskUseCase(task)
                result.fold(
                    onSuccess = { updatedTask ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedTask = updatedTask,
                                isTaskSaved = true
                            )
                        }
                        // Refresh task list
                        loadTasks()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error updating task")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to update task",
                                isTaskSaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception updating task")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while updating task",
                        isTaskSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Updates a task's status
     */
    fun updateTaskStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            
            try {
                val result = updateTaskStatusUseCase(taskId, status)
                result.fold(
                    onSuccess = { _ ->
                        // Refresh task list
                        loadTasks()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error updating task status")
                        _uiState.update { 
                            it.copy(
                                errorMessage = error.message ?: "Failed to update task status"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception updating task status")
                _uiState.update { 
                    it.copy(
                        errorMessage = e.message ?: "Unknown error occurred while updating task status"
                    )
                }
            }
        }
    }
    
    /**
     * Deletes a task
     */
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isTaskDeleted = false) }
            
            try {
                val result = deleteTaskUseCase(taskId)
                result.fold(
                    onSuccess = { _ ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isTaskDeleted = true,
                                selectedTask = null
                            )
                        }
                        // Refresh task list
                        loadTasks()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error deleting task")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to delete task",
                                isTaskDeleted = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception deleting task")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while deleting task",
                        isTaskDeleted = false
                    )
                }
            }
        }
    }
    
    /**
     * Sets the current filter type
     */
    fun setFilterType(filterType: TaskFilterType) {
        _uiState.update { currentState ->
            val filteredTasks = applyFiltersAndSort(currentState.tasks)
            currentState.copy(
                filterType = filterType,
                filteredTasks = filteredTasks
            )
        }
    }
    
    /**
     * Sets the current sort criteria
     */
    fun setSortCriteria(sortCriteria: TaskSortCriteria) {
        _uiState.update { currentState ->
            val filteredTasks = applyFiltersAndSort(currentState.tasks)
            currentState.copy(
                sortCriteria = sortCriteria,
                filteredTasks = filteredTasks
            )
        }
    }
    
    /**
     * Sets the current sort direction
     */
    fun setSortDirection(ascending: Boolean) {
        _uiState.update { currentState ->
            val filteredTasks = applyFiltersAndSort(currentState.tasks)
            currentState.copy(
                sortAscending = ascending,
                filteredTasks = filteredTasks
            )
        }
    }
    
    /**
     * Updates the selected filter chips
     */
    fun updateSelectedFilters(filter: TaskFilterChip, selected: Boolean) {
        _uiState.update { currentState ->
            val updatedFilters = if (selected) {
                currentState.selectedFilters + filter
            } else {
                currentState.selectedFilters - filter
            }
            
            val filteredTasks = applyFiltersAndSort(currentState.tasks)
            currentState.copy(
                selectedFilters = updatedFilters,
                filteredTasks = filteredTasks
            )
        }
    }
    
    /**
     * Refresh the tasks data
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadTasks()
    }
    
    /**
     * Resets the task saved/deleted state
     */
    fun resetTaskSavedState() {
        _uiState.update { it.copy(isTaskSaved = false, isTaskDeleted = false) }
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Helper function to apply filters and sorting to a list of tasks
     */
    private fun applyFiltersAndSort(tasks: List<Task>): List<Task> {
        val currentState = _uiState.value
        val now = LocalDate.now()
        
        // Apply base filter
        var filteredTasks = when (currentState.filterType) {
            TaskFilterType.ALL -> tasks
            TaskFilterType.ACTIVE -> tasks.filter { it.status != TaskStatus.COMPLETED }
            TaskFilterType.COMPLETED -> tasks.filter { it.status == TaskStatus.COMPLETED }
            TaskFilterType.TODAY -> tasks.filter { task -> 
                task.dueDate?.equals(now) == true && task.status != TaskStatus.COMPLETED
            }
            TaskFilterType.UPCOMING -> tasks.filter { task -> 
                (task.dueDate?.isAfter(now) == true) && task.status != TaskStatus.COMPLETED
            }
            TaskFilterType.OVERDUE -> tasks.filter { task -> 
                (task.dueDate?.isBefore(now) == true) && task.status != TaskStatus.COMPLETED
            }
        }
        
        // Apply additional filters from chips
        if (currentState.selectedFilters.isNotEmpty()) {
            filteredTasks = filteredTasks.filter { task ->
                currentState.selectedFilters.all { filterChip ->
                    when (filterChip) {
                        TaskFilterChip.HIGH_PRIORITY -> task.priority == "HIGH"
                        TaskFilterChip.MEDIUM_PRIORITY -> task.priority == "MEDIUM"
                        TaskFilterChip.LOW_PRIORITY -> task.priority == "LOW"
                        TaskFilterChip.HAS_DEADLINE -> task.dueDate != null
                        TaskFilterChip.NO_DEADLINE -> task.dueDate == null
                        TaskFilterChip.IN_PROGRESS -> task.status == TaskStatus.IN_PROGRESS
                        TaskFilterChip.NOT_STARTED -> task.status == TaskStatus.NOT_STARTED
                        // Add additional filters as needed
                    }
                }
            }
        }
        
        // Apply sorting
        return when (currentState.sortCriteria) {
            TaskSortCriteria.TITLE -> {
                if (currentState.sortAscending) {
                    filteredTasks.sortedBy { it.title }
                } else {
                    filteredTasks.sortedByDescending { it.title }
                }
            }
            TaskSortCriteria.DUE_DATE -> {
                if (currentState.sortAscending) {
                    filteredTasks.sortedWith(compareBy(nullsLast()) { it.dueDate })
                } else {
                    filteredTasks.sortedWith(compareByDescending(nullsLast()) { it.dueDate })
                }
            }
            TaskSortCriteria.PRIORITY -> {
                val priorityOrder = mapOf("HIGH" to 3, "MEDIUM" to 2, "LOW" to 1, "" to 0)
                if (currentState.sortAscending) {
                    filteredTasks.sortedBy { priorityOrder[it.priority] ?: 0 }
                } else {
                    filteredTasks.sortedByDescending { priorityOrder[it.priority] ?: 0 }
                }
            }
            TaskSortCriteria.STATUS -> {
                val statusOrder = mapOf(
                    TaskStatus.COMPLETED to 3,
                    TaskStatus.IN_PROGRESS to 2,
                    TaskStatus.NOT_STARTED to 1
                )
                if (currentState.sortAscending) {
                    filteredTasks.sortedBy { statusOrder[it.status] ?: 0 }
                } else {
                    filteredTasks.sortedByDescending { statusOrder[it.status] ?: 0 }
                }
            }
            TaskSortCriteria.CREATED_DATE -> {
                if (currentState.sortAscending) {
                    filteredTasks.sortedBy { it.createdDate }
                } else {
                    filteredTasks.sortedByDescending { it.createdDate }
                }
            }
        }
    }
    
    /**
     * Helper function to handle null values in comparisons
     */
    private fun <T : Comparable<T>> nullsLast(): Comparator<T?> = Comparator { a, b ->
        when {
            a == null && b == null -> 0
            a == null -> 1
            b == null -> -1
            else -> a.compareTo(b)
        }
    }
}
