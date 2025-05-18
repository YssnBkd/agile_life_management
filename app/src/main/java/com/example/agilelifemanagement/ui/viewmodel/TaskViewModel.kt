package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskPriority
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.domain.usecase.task.CreateTaskUseCase
import com.example.agilelifemanagement.domain.usecase.task.DeleteTaskUseCase
import com.example.agilelifemanagement.domain.usecase.task.GetTaskByIdUseCase
import com.example.agilelifemanagement.domain.usecase.task.GetTasksUseCase
import com.example.agilelifemanagement.domain.usecase.task.UpdateTaskStatusUseCase
import com.example.agilelifemanagement.domain.usecase.task.UpdateTaskUseCase
import com.example.agilelifemanagement.ui.model.TaskFilterChip
import com.example.agilelifemanagement.ui.model.TaskFilterType
import com.example.agilelifemanagement.ui.model.TaskSortCriteria
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
 * UI State is defined in TaskViewUiState.kt

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
     * Applies filters and sorting to tasks (simplified for temporary implementation)
     * 
     * Note: This is a temporary implementation after the May 15, 2025 architectural change
     * where the data layer was archived for rebuilding.
     */
    private fun applyFiltersAndSort(tasks: List<Task>): List<Task> {
        // During rebuilding phase, we'll just return the original tasks without filtering
        // This will be properly implemented when the data layer is rebuilt
        return tasks
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
    // End of TaskViewModel class
}
*/