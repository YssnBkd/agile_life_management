package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.usecase.goal.CreateGoalUseCase
import com.example.agilelifemanagement.domain.usecase.goal.DeleteGoalUseCase
import com.example.agilelifemanagement.domain.usecase.goal.GetGoalByIdUseCase
import com.example.agilelifemanagement.domain.usecase.goal.GetGoalsUseCase
import com.example.agilelifemanagement.domain.usecase.goal.UpdateGoalUseCase
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
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * UI State for the Goal screens, following the Unidirectional Data Flow pattern.
 */
data class GoalUiState(
    val goals: List<Goal> = emptyList(),
    val activeGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val selectedGoal: Goal? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isGoalSaved: Boolean = false,
    val isGoalDeleted: Boolean = false
)

/**
 * ViewModel for Goal-related screens, implementing Unidirectional Data Flow.
 * It handles loading goals and CRUD operations.
 */
@HiltViewModel
class GoalViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getGoalByIdUseCase: GetGoalByIdUseCase,
    private val createGoalUseCase: CreateGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(GoalUiState())
    
    // Public immutable state flow for UI consumption
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    /**
     * Loads all goals and separates them into active and completed
     */
    fun loadGoals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getGoalsUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading goals")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading goals"
                        )
                    }
                }
                .collectLatest { goals ->
                    val today = LocalDate.now()
                    // Since we're rebuilding the data layer (May 15, 2025), partition all goals as active for now
                    val active = goals
                    val completed = emptyList<Goal>()
                    
                    _uiState.update { 
                        it.copy(
                            goals = goals,
                            activeGoals = active,
                            completedGoals = completed,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    /**
     * Loads a specific goal by ID
     */
    fun loadGoal(goalId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getGoalByIdUseCase(goalId)
                .catch { e ->
                    Timber.e(e, "Error loading goal: $goalId")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading goal"
                        )
                    }
                }
                .collectLatest { goal ->
                    _uiState.update { 
                        it.copy(
                            selectedGoal = goal,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    /**
     * Creates a new goal
     */
    fun createGoal(goal: Goal) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isGoalSaved = false) }
            
            try {
                val result = createGoalUseCase(goal)
                result.fold(
                    onSuccess = { createdGoal ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedGoal = createdGoal,
                                isGoalSaved = true
                            )
                        }
                        // Refresh goal list
                        loadGoals()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error creating goal")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to create goal",
                                isGoalSaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception creating goal")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while creating goal",
                        isGoalSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Updates an existing goal
     */
    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isGoalSaved = false) }
            
            try {
                val result = updateGoalUseCase(goal)
                result.fold(
                    onSuccess = { updatedGoal ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedGoal = updatedGoal,
                                isGoalSaved = true
                            )
                        }
                        // Refresh goal list
                        loadGoals()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error updating goal")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to update goal",
                                isGoalSaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception updating goal")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while updating goal",
                        isGoalSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Toggles the completion status of a goal
     * 
     * Note: This is a temporary implementation after the May 15, 2025 architectural change
     * where the data layer was archived for rebuilding.
     */
    fun updateGoalCompletionStatus(goalId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            val currentGoal = _uiState.value.selectedGoal
            if (currentGoal == null) {
                loadGoal(goalId)
                return@launch
            }
            
            if (currentGoal.id != goalId) {
                loadGoal(goalId)
                return@launch
            }
            
            // Since we're rebuilding the architecture (May 15, 2025), just use a temporary implementation
            // that doesn't modify any fields directly - just return the current goal
            // This will be properly implemented when the data layer is rebuilt
            val updatedGoal = currentGoal // Use original goal temporarily
            updateGoal(updatedGoal)
        }
    }
    
    /**
     * Deletes a goal
     */
    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isGoalDeleted = false) }
            
            try {
                val result = deleteGoalUseCase(goalId)
                result.fold(
                    onSuccess = { _ ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isGoalDeleted = true,
                                selectedGoal = null
                            )
                        }
                        // Refresh goal list
                        loadGoals()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error deleting goal")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to delete goal",
                                isGoalDeleted = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception deleting goal")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while deleting goal",
                        isGoalDeleted = false
                    )
                }
            }
        }
    }
    
    /**
     * Refresh the goals data
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadGoals()
    }
    
    /**
     * Resets the goal saved/deleted state
     */
    fun resetGoalSavedState() {
        _uiState.update { it.copy(isGoalSaved = false, isGoalDeleted = false) }
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
