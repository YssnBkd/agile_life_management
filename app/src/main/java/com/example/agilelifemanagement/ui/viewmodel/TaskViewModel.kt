package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.Result as DomainResult
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
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getSprintsUseCase: com.example.agilelifemanagement.domain.usecase.sprint.GetSprintsUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(TaskUiState())
    
    // Public immutable state flow for UI consumption
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
        loadSprints()
    }
    
    /**
     * Loads all available sprints for task assignment
     */
    fun loadSprints() {
        viewModelScope.launch {
            // Show loading state
            _uiState.update { it.copy(isLoadingSprints = true) }
            
            getSprintsUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading sprints for task assignment")
                    _uiState.update { 
                        it.copy(isLoadingSprints = false, errorMessage = "Failed to load sprints: ${e.message}")
                    }
                }
                .collectLatest { sprints ->
                    _uiState.update { 
                        it.copy(availableSprints = sprints, isLoadingSprints = false)
                    }
                }
        }
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
     * Assigns a task to a sprint
     * 
     * @param taskId ID of the task to assign
     * @param sprintId ID of the sprint to assign the task to, or null to remove from any sprint
     */
    fun assignTaskToSprint(taskId: String, sprintId: String?) {
        // Critical fix: First update UI state to show user action was received
        // and the operation is in progress
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        // Process the assignment in a background coroutine
        viewModelScope.launch {
            try {
                // Step 1: Get the task directly
                var task: Task? = null
                try {
                    // Use a more direct approach to get the task
                    // Directly access tasks from the current UI state if available
                    task = _uiState.value.tasks.find { it.id == taskId }
                        ?: _uiState.value.selectedTask?.takeIf { it.id == taskId }
                    
                    // If task wasn't found in the current state, try to load it
                    if (task == null) {
                        // Load all tasks first to make sure we have the latest data
                        val tasks = mutableListOf<Task>()
                        getTasksUseCase().collect { taskList ->
                            tasks.addAll(taskList)
                            // Break early if we found the task
                            if (tasks.any { it.id == taskId }) {
                                return@collect
                            }
                        }
                        
                        // Try to find the task in the loaded tasks
                        task = tasks.find { it.id == taskId }
                        
                        // If still not found, throw an exception
                        if (task == null) {
                            throw Exception("Task with ID $taskId not found in the system")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error finding task")
                    throw Exception("Could not find task: ${e.message}")
                }
                
                // Step 3: Check if assignment is different
                if (task.sprintId == sprintId) {
                    // No change needed, but still show success
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            operationSuccess = true,
                            errorMessage = null
                        )
                    }
                    return@launch
                }
                
                // Step 4: Create and update the task
                val updatedTask = task.copy(sprintId = sprintId)
                try {
                    val updateResult = updateTaskUseCase(updatedTask)
                    if (updateResult is DomainResult.Error) {
                        throw Exception(updateResult.message)
                    }
                    
                    // Step 5: Show success immediately
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            operationSuccess = true,
                            errorMessage = null
                        )
                    }
                    
                    // Step 6: Refresh data in the background (non-blocking)
                    viewModelScope.launch {
                        try {
                            loadTasks()
                            if (_uiState.value.selectedTask?.id == taskId) {
                                loadTask(taskId)
                            }
                        } catch (e: Exception) {
                            // Log error but don't update UI again
                            Timber.e(e, "Error refreshing tasks after sprint assignment")
                        }
                    }
                } catch (e: Exception) {
                    throw Exception("Failed to assign task to sprint: ${e.message}")
                }
            } catch (e: Exception) {
                // Final error handler - ensure loading state is always cleared
                Timber.e(e, "Exception assigning task to sprint")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while assigning task"
                    )
                }
            }
        }
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Resets the operation state
     */
    fun resetOperationState() {
        _uiState.update { it.copy(
            isTaskSaved = false,
            isTaskDeleted = false,
            operationSuccess = false
        )}
    }
    
    /**
     * Updates the search query and filters tasks accordingly
     */
    fun searchTasks(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredTasks = filterTasksBySearchQuery(currentState.tasks, query)
            )
        }
    }
    
    /**
     * Filters tasks by search query
     */
    private fun filterTasksBySearchQuery(tasks: List<Task>, query: String): List<Task> {
        if (query.isBlank()) {
            return applyFiltersAndSort(tasks)
        }
        
        return tasks.filter { task ->
            task.title.contains(query, ignoreCase = true) ||
            task.description.contains(query, ignoreCase = true)
        }
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