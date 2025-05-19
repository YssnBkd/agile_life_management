package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.SprintStatus
import com.example.agilelifemanagement.domain.usecase.sprint.CreateSprintUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.DeleteSprintUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.GetSprintByIdUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.GetSprintsUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.UpdateSprintUseCase
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
import java.util.UUID
import javax.inject.Inject
import com.example.agilelifemanagement.domain.model.Result as DomainResult

/**
 * ViewModel for Sprint-related screens, implementing Unidirectional Data Flow.
 * It handles loading sprints and CRUD operations.
 */
@HiltViewModel
class SprintViewModel @Inject constructor(
    private val getSprintsUseCase: GetSprintsUseCase,
    private val getSprintByIdUseCase: GetSprintByIdUseCase,
    private val createSprintUseCase: CreateSprintUseCase,
    private val updateSprintUseCase: UpdateSprintUseCase,
    private val deleteSprintUseCase: DeleteSprintUseCase
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
            
            try {
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
                        _uiState.update { 
                            it.copy(
                                sprints = sprints,
                                filteredSprints = sprints,
                                isLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error loading sprints")
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.message ?: "Unknown error occurred while loading sprints"
                    )
                }
            }
        }
    }

    /**
     * Loads a specific sprint by ID
     */
    fun loadSprint(sprintId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                getSprintByIdUseCase(sprintId)
                    .catch { e ->
                        Timber.e(e, "Error getting sprint: $sprintId")
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessage = e.message ?: "Failed to load sprint"
                            )
                        }
                    }
                    .collectLatest { sprint ->
                        if (sprint != null) {
                            _uiState.update { 
                                it.copy(
                                    selectedSprint = sprint,
                                    isLoading = false
                                )
                            }
                        } else {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false, 
                                    errorMessage = "Sprint not found"
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error loading sprint: $sprintId")
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.message ?: "Unknown error occurred while loading sprint"
                    )
                }
            }
        }
    }
    
    /**
     * Loads a specific sprint by ID
     */
    fun loadSprintById(sprintId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getSprintByIdUseCase(sprintId)
                .catch { e ->
                    Timber.e(e, "Error loading sprint $sprintId")
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
                            isLoading = false,
                            selectedSprint = sprint
                        )
                    }
                }
        }
    }
    
    /**
     * Filters sprints based on status
     */
    fun filterSprints(statusFilter: Set<SprintStatus>) {
        viewModelScope.launch {
            val currentSprints = _uiState.value.sprints
            
            val filtered = if (statusFilter.isEmpty()) {
                currentSprints
            } else {
                currentSprints.filter { it.status in statusFilter }
            }
            
            _uiState.update { 
                it.copy(
                    filteredSprints = filtered
                )
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
                val sprintWithId = if (sprint.id.isBlank()) {
                    sprint.copy(id = UUID.randomUUID().toString(), createdDate = LocalDate.now())
                } else {
                    sprint
                }
                
                val result = createSprintUseCase(sprintWithId)
                when (result) {
                    is DomainResult.Success<String> -> {
                        val newSprintId = result.data
                        Timber.d("Sprint created successfully")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSprintSaved = true,
                                operationSuccess = true,
                                selectedSprint = sprint.copy(id = newSprintId)
                            )
                        }
                        // Refresh sprint list
                        loadSprints()
                    }
                    is DomainResult.Error -> {
                        Timber.e(result.cause, "Error creating sprint: ${result.message}")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message,
                                isSprintSaved = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error creating sprint")
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
                when (result) {
                    is DomainResult.Success<Unit> -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSprintSaved = true,
                                operationSuccess = true
                            )
                        }
                        // Refresh sprint list including the updated sprint
                        loadSprints()
                        // Load the updated sprint to ensure we have the latest version
                        loadSprintById(sprint.id)
                    }
                    is DomainResult.Error -> {
                        Timber.e(result.cause, "Error updating sprint: ${result.message}")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message,
                                isSprintSaved = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error updating sprint")
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
                when (result) {
                    is DomainResult.Success<Unit> -> {
                        // Delete successful
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedSprint = null,
                                isSprintDeleted = true
                            )
                        }
                        // Refresh sprint list
                        loadSprints()
                    }
                    is DomainResult.Error -> {
                        Timber.e(result.cause, "Error deleting sprint: ${result.message}")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message,
                                isSprintDeleted = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error deleting sprint")
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
     * Resets the operation state for proper navigation
     */
    fun resetOperationState() {
        _uiState.update { it.copy(
            isSprintSaved = false,
            isSprintDeleted = false,
            operationSuccess = false
        )}
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
