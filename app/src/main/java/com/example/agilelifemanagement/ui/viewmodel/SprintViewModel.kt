package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.usecase.sprint.CreateSprintUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.DeleteSprintUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.GetSprintByIdUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.GetSprintsUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.UpdateSprintUseCase
import com.example.agilelifemanagement.domain.usecase.task.GetTasksBySprintUseCase
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
 * UI State for the Sprint screens, following the Unidirectional Data Flow pattern.
 */
data class SprintUiState(
    val sprints: List<Sprint> = emptyList(),
    val activeSprint: Sprint? = null,
    val selectedSprint: Sprint? = null,
    val sprintTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isSprintSaved: Boolean = false,
    val isSprintDeleted: Boolean = false
)

/**
 * ViewModel for Sprint-related screens, implementing Unidirectional Data Flow.
 * It handles loading sprints and their tasks, and CRUD operations.
 */
@HiltViewModel
class SprintViewModel @Inject constructor(
    private val getSprintsUseCase: GetSprintsUseCase,
    private val getSprintByIdUseCase: GetSprintByIdUseCase,
    private val createSprintUseCase: CreateSprintUseCase,
    private val updateSprintUseCase: UpdateSprintUseCase,
    private val deleteSprintUseCase: DeleteSprintUseCase,
    private val getTasksBySprintUseCase: GetTasksBySprintUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(SprintUiState())
    
    // Public immutable state flow for UI consumption
    val uiState: StateFlow<SprintUiState> = _uiState.asStateFlow()

    init {
        loadSprints()
    }

    /**
     * Loads all sprints
     */
    fun loadSprints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getSprintsUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading sprints")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading sprints"
                        )
                    }
                }
                .collectLatest { sprints ->
                    val today = LocalDate.now()
                    val activeSprint = sprints.find { sprint ->
                        sprint.startDate.isBefore(today) && sprint.endDate.isAfter(today)
                    }
                    
                    _uiState.update { 
                        it.copy(
                            sprints = sprints,
                            activeSprint = activeSprint,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                    
                    // If we have an active sprint, load its tasks
                    activeSprint?.let { loadSprintTasks(it.id) }
                }
        }
    }

    /**
     * Loads a specific sprint by ID
     */
    fun loadSprint(sprintId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getSprintByIdUseCase(sprintId)
                .catch { e ->
                    Timber.e(e, "Error loading sprint: $sprintId")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading sprint"
                        )
                    }
                }
                .collectLatest { sprint ->
                    _uiState.update { 
                        it.copy(
                            selectedSprint = sprint,
                            isLoading = false
                        )
                    }
                    
                    // Load tasks for this sprint
                    if (sprint != null) {
                        loadSprintTasks(sprint.id)
                    }
                }
        }
    }
    
    /**
     * Loads tasks for a specific sprint
     */
    private fun loadSprintTasks(sprintId: String) {
        viewModelScope.launch {
            getTasksBySprintUseCase(sprintId)
                .catch { e ->
                    Timber.e(e, "Error loading tasks for sprint: $sprintId")
                    _uiState.update { 
                        it.copy(
                            errorMessage = e.message ?: "Unknown error occurred while loading sprint tasks"
                        )
                    }
                }
                .collectLatest { tasks ->
                    _uiState.update { 
                        it.copy(
                            sprintTasks = tasks
                        )
                    }
                }
        }
    }
    
    /**
     * Creates a new sprint
     */
    fun createSprint(sprint: Sprint) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSprintSaved = false) }
            
            try {
                val result = createSprintUseCase(sprint)
                result.fold(
                    onSuccess = { createdSprint ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedSprint = createdSprint,
                                isSprintSaved = true
                            )
                        }
                        // Refresh sprint list
                        loadSprints()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error creating sprint")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to create sprint",
                                isSprintSaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception creating sprint")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while creating sprint",
                        isSprintSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Updates an existing sprint
     */
    fun updateSprint(sprint: Sprint) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSprintSaved = false) }
            
            try {
                val result = updateSprintUseCase(sprint)
                result.fold(
                    onSuccess = { result ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedSprint = sprint, // Use the sprint we sent to update
                                isSprintSaved = true
                            )
                        }
                        // Refresh sprint list
                        loadSprints()
                    },
                    onError = { errorMsg, cause ->
                        Timber.e(cause, "Error updating sprint: $errorMsg")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = errorMsg,
                                isSprintSaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception updating sprint")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while updating sprint",
                        isSprintSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Deletes a sprint
     */
    fun deleteSprint(sprintId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSprintDeleted = false) }
            
            try {
                val result = deleteSprintUseCase(sprintId)
                result.fold(
                    onSuccess = { _ ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSprintDeleted = true,
                                selectedSprint = null,
                                sprintTasks = emptyList()
                            )
                        }
                        // Refresh sprint list
                        loadSprints()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error deleting sprint")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to delete sprint",
                                isSprintDeleted = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception deleting sprint")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while deleting sprint",
                        isSprintDeleted = false
                    )
                }
            }
        }
    }
    
    /**
     * Refresh the sprints data
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadSprints()
    }
    
    /**
     * Resets the sprint saved/deleted state
     */
    fun resetSprintSavedState() {
        _uiState.update { it.copy(isSprintSaved = false, isSprintDeleted = false) }
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
