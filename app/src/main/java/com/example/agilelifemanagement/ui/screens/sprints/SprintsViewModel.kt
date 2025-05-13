package com.example.agilelifemanagement.ui.screens.sprints

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
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.domain.repository.SprintRepository
import com.example.agilelifemanagement.domain.repository.TagRepository
import java.time.LocalDate
import com.example.agilelifemanagement.domain.usecase.sprint.CreateSprintUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.UpdateSprintUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.DeleteSprintUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.GetActiveSprintUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.GetSprintByIdUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.GetSprintsUseCase

private const val TAG = "SprintsViewModel"

@HiltViewModel
class SprintsViewModel @Inject constructor(
    private val getSprintsUseCase: GetSprintsUseCase,
    private val getSprintByIdUseCase: GetSprintByIdUseCase,
    private val createSprintUseCase: CreateSprintUseCase,
    private val updateSprintUseCase: UpdateSprintUseCase,
    private val deleteSprintUseCase: DeleteSprintUseCase,
    private val getActiveSprintUseCase: GetActiveSprintUseCase,
    private val tagRepository: TagRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val sprints: List<Sprint>) : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
    }
    
    sealed class SprintDetailUiState {
        object Idle : SprintDetailUiState()
        object Loading : SprintDetailUiState()
        data class Success(val sprint: Sprint) : SprintDetailUiState()
        object NotFound : SprintDetailUiState()
        data class Error(val message: String) : SprintDetailUiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _selectedSprintState = MutableStateFlow<SprintDetailUiState>(SprintDetailUiState.Idle)
    val selectedSprintState: StateFlow<SprintDetailUiState> = _selectedSprintState.asStateFlow()
    
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

    var showCreateDialog by mutableStateOf(false)
        private set

    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    fun onAddSprintClicked() {
        showCreateDialog = true
    }

    fun clearSnackbar() {
        snackbarMessage = null
    }
    
    // For backward compatibility - delegates to selectedSprintState
    private val _selectedSprint = MutableStateFlow<Sprint?>(null)
    val selectedSprint: StateFlow<Sprint?> = _selectedSprint.asStateFlow()

    init {
        observeSprints()
        observeTags()
    }

    fun retry() {
        observeSprints()
        observeTags()
    }


    fun hideCreateDialog() {
        showCreateDialog = false
    }


    private fun observeSprints() {
        viewModelScope.launch {
            getSprintsUseCase()
                .onStart { _uiState.value = UiState.Loading }
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Unknown error")
                }
                .collect { sprints ->
                    _uiState.value = if (sprints.isEmpty()) UiState.Empty else UiState.Success(sprints)
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

    fun selectSprint(sprintId: String) {
        Log.d(TAG, "selectSprint called with sprintId: $sprintId")
        viewModelScope.launch {
            _selectedSprintState.value = SprintDetailUiState.Loading
            getSprintByIdUseCase(sprintId)
                .catch { e ->
                    Log.e(TAG, "Error fetching sprint with id: $sprintId: ${e.message}", e)
                    _selectedSprintState.value = SprintDetailUiState.Error(e.message ?: "Error loading sprint")
                    _selectedSprint.value = null // For backward compatibility
                }
                .collect { sprint ->
                    if (sprint == null) {
                        Log.w(TAG, "No sprint found for id: $sprintId")
                        _selectedSprintState.value = SprintDetailUiState.NotFound
                        _selectedSprint.value = null // For backward compatibility
                    } else {
                        Log.d(TAG, "Sprint loaded for id: $sprintId: ${sprint.id}")
                        _selectedSprintState.value = SprintDetailUiState.Success(sprint)
                        _selectedSprint.value = sprint // For backward compatibility
                    }
                }
        }
    }

    fun createSprint(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
            try {
                createSprintUseCase(
                    name = name,
                    startDate = startDate,
                    endDate = endDate
                )
                snackbarMessage = "Sprint created successfully"
                showCreateDialog = false
                observeSprints()
            } catch (e: Exception) {
                snackbarMessage = e.message ?: "Failed to create sprint"
                _uiState.value = UiState.Error(snackbarMessage!!)
            }
        }
    }

    fun updateSprint(
        sprint: Sprint,
        name: String,
        description: List<String>,
        startDate: LocalDate,
        endDate: LocalDate,
        isActive: Boolean,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            try {
                updateSprintUseCase(
                    id = sprint.id,
                    name = name,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                    isActive = isActive,
                    isCompleted = isCompleted
                )
                snackbarMessage = "Sprint updated successfully"
                observeSprints()
                // Reload the selected sprint to reflect changes
                selectSprint(sprint.id)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating sprint: ${sprint.id}", e)
                snackbarMessage = e.message ?: "Failed to update sprint"
                _uiState.value = UiState.Error(snackbarMessage!!)
            }
        }
    }
    
    // Simplified method for direct sprint update from UI
    fun updateSelectedSprint(sprint: Sprint) {
        Log.d(TAG, "updateSelectedSprint called for sprint: ${sprint.id}")
        viewModelScope.launch {
            try {
                // Get the current sprint to ensure we have the latest
                val currentSprint = when (val state = _selectedSprintState.value) {
                    is SprintDetailUiState.Success -> state.sprint
                    else -> null
                }
                
                if (currentSprint == null) {
                    Log.w(TAG, "Cannot update sprint: No sprint currently selected")
                    snackbarMessage = "Error: No sprint selected"
                    return@launch
                }
                
                updateSprintUseCase(
                    id = sprint.id,
                    name = sprint.name,
                    description = sprint.description,
                    startDate = sprint.startDate,
                    endDate = sprint.endDate,
                    isActive = sprint.isActive,
                    isCompleted = sprint.isCompleted
                )
                
                // Update the UI state
                _selectedSprintState.value = SprintDetailUiState.Success(sprint)
                _selectedSprint.value = sprint // For backward compatibility
                snackbarMessage = "Sprint updated successfully"
                observeSprints() // Refresh the sprints list
            } catch (e: Exception) {
                Log.e(TAG, "Error updating sprint: ${sprint.id}", e)
                snackbarMessage = e.message ?: "Failed to update sprint"
                _selectedSprintState.value = SprintDetailUiState.Error(snackbarMessage!!)
            }
        }
    }

    fun deleteSprint(sprintId: String) {
        viewModelScope.launch {
            try {
                deleteSprintUseCase(sprintId)
                snackbarMessage = "Sprint deleted successfully"
                _selectedSprintState.value = SprintDetailUiState.Idle
                _selectedSprint.value = null // For backward compatibility
                observeSprints()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting sprint: $sprintId", e)
                snackbarMessage = e.message ?: "Failed to delete sprint"
                _uiState.value = UiState.Error(snackbarMessage!!)
            }
        }
    }
    
    // Delete the currently selected sprint
    fun deleteSelectedSprint() {
        val currentState = _selectedSprintState.value
        if (currentState is SprintDetailUiState.Success) {
            deleteSprint(currentState.sprint.id)
        } else {
            snackbarMessage = "No sprint selected or sprint not loaded for deletion"
            Log.w(TAG, "deleteSelectedSprint called but no sprint is successfully loaded.")
        }
    }

    fun getActiveSprint() {
        viewModelScope.launch {
            getActiveSprintUseCase()
                .catch { _selectedSprint.value = null }
                .collect { sprint ->
                    _selectedSprint.value = sprint
                }
        }
    }

    fun onDialogDismiss() {
        showCreateDialog = false
    }
// Duplicate clearSnackbar removed above
}
