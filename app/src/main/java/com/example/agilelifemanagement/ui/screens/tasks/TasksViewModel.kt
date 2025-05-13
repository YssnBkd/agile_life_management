package com.example.agilelifemanagement.ui.screens.tasks

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import com.example.agilelifemanagement.domain.repository.TagRepository
import com.example.agilelifemanagement.domain.model.Tag
import java.time.LocalDate

private const val TAG = "TasksViewModel"

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val tasks: List<Task>) : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class TaskDetailUiState {
        object Idle : TaskDetailUiState()
        object Loading : TaskDetailUiState()
        data class Success(val task: Task) : TaskDetailUiState()
        object NotFound : TaskDetailUiState()
        data class Error(val message: String) : TaskDetailUiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedTaskState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState.Idle)
    val selectedTaskState: StateFlow<TaskDetailUiState> = _selectedTaskState.asStateFlow()

    var showCreateDialog by mutableStateOf(false)
        private set

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    init {
        observeTasks()
        observeTags()
    }

    fun retry() {
        observeTasks()
    }

    fun selectTask(taskId: String) {
        Log.d(TAG, "selectTask called with taskId: $taskId")
        viewModelScope.launch {
            _selectedTaskState.value = TaskDetailUiState.Loading
            taskRepository.getTaskById(taskId)
                .catch { e ->
                    Log.e(TAG, "Error fetching task $taskId", e)
                    _selectedTaskState.value = TaskDetailUiState.Error(e.message ?: "Unknown error selecting task")
                }
                .collect { task ->
                    if (task != null) {
                        Log.d(TAG, "Task $taskId fetched successfully: ${task.title}")
                        _selectedTaskState.value = TaskDetailUiState.Success(task)
                    } else {
                        Log.d(TAG, "Task $taskId not found")
                        _selectedTaskState.value = TaskDetailUiState.NotFound
                    }
                }
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
            taskRepository.getTasks()
                .onStart { _uiState.value = UiState.Loading }
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Unknown error")
                }
                .collect { tasks ->
                    if (tasks.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(tasks)
                    }
                }
        }
    }

    private fun observeTags() {
        viewModelScope.launch {
            tagRepository.getTags()
                .catch { _tags.value = emptyList() }
                .collect { tags -> _tags.value = tags }
        }
    }

    fun onAddTaskClicked() {
        showCreateDialog = true
    }

    fun onDialogDismiss() {
        showCreateDialog = false
    }

    fun createTask(
        title: String,
        summary: String,
        priority: Task.Priority,
        dueDate: LocalDate?,
        selectedTagIds: List<String>
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                snackbarMessage = "Title is required"
                return@launch
            }
            if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
                snackbarMessage = "Due date cannot be in the past"
                return@launch
            }
            try {
                val newTask = Task(
                    title = title,
                    summary = summary,
                    priority = priority,
                    dueDate = dueDate,
                    tags = selectedTagIds
                )
                taskRepository.insertTask(newTask)
                snackbarMessage = "Task created successfully"
                showCreateDialog = false
            } catch (e: Exception) {
                snackbarMessage = e.message ?: "Failed to create task"
                _uiState.value = UiState.Error(snackbarMessage!!)
            }
        }
    }

    fun updateSelectedTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(task)
                snackbarMessage = "Task updated successfully"
                // Refresh the selected task state
                _selectedTaskState.value = TaskDetailUiState.Success(task)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task ${task.id}", e)
                snackbarMessage = e.message ?: "Failed to update task"
                _selectedTaskState.value = TaskDetailUiState.Error(snackbarMessage!!)
            }
        }
    }

    fun deleteSelectedTask() {
        val currentTaskState = _selectedTaskState.value
        if (currentTaskState is TaskDetailUiState.Success) {
            viewModelScope.launch {
                try {
                    taskRepository.deleteTask(currentTaskState.task.id)
                    snackbarMessage = "Task deleted successfully"
                    _selectedTaskState.value = TaskDetailUiState.Idle // Reset state or navigate away
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting task ${currentTaskState.task.id}", e)
                    snackbarMessage = e.message ?: "Failed to delete task"
                    // Optionally, keep the error state for the detail screen to show
                    _selectedTaskState.value = TaskDetailUiState.Error(snackbarMessage!!)
                }
            }
        } else {
            snackbarMessage = "No task selected or task not loaded for deletion"
            Log.w(TAG, "deleteSelectedTask called but no task is successfully loaded.")
        }
    }

    fun clearSnackbar() {
        snackbarMessage = null
    }
}
