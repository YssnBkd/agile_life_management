package com.example.agilelifemanagement.ui.screens.tasks

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

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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

    fun clearSnackbar() {
        snackbarMessage = null
    }
}

