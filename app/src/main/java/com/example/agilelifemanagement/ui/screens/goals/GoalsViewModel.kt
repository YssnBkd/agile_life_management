package com.example.agilelifemanagement.ui.screens.goals

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
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.domain.repository.GoalRepository
import com.example.agilelifemanagement.domain.repository.TagRepository
import java.time.LocalDate

private const val TAG = "GoalsViewModel"

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val goals: List<Goal>) : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class GoalDetailUiState {
        object Idle : GoalDetailUiState()
        object Loading : GoalDetailUiState()
        data class Success(val goal: Goal) : GoalDetailUiState()
        object NotFound : GoalDetailUiState()
        data class Error(val message: String) : GoalDetailUiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedGoalState = MutableStateFlow<GoalDetailUiState>(GoalDetailUiState.Idle)
    val selectedGoalState: StateFlow<GoalDetailUiState> = _selectedGoalState.asStateFlow()

    // Direct access for goals list
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

    private var _activeFilters = MutableStateFlow(GoalFilters())
    val activeFilters: StateFlow<GoalFilters> = _activeFilters.asStateFlow()

    init {
        loadGoals()
        loadTags()
    }

    fun loadGoals() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                goalRepository.getGoals().collect { goals ->
                    _goals.value = goals
                    _uiState.value = if (goals.isEmpty()) UiState.Empty else UiState.Success(goals)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading goals", e)
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadGoalDetail(goalId: String) {
        viewModelScope.launch {
            _selectedGoalState.value = GoalDetailUiState.Loading
            try {
                goalRepository.getGoalById(goalId).collect { goal ->
                    if (goal != null) {
                        _selectedGoalState.value = GoalDetailUiState.Success(goal)
                    } else {
                        _selectedGoalState.value = GoalDetailUiState.NotFound
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading goal detail", e)
                _selectedGoalState.value = GoalDetailUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            try {
                tagRepository.getTags().collect { tags ->
                    _tags.value = tags
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tags", e)
            }
        }
    }

    fun createGoal(
        title: String,
        summary: String,
        category: Goal.Category,
        deadline: LocalDate? = null
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            try {
                val newGoal = Goal(
                    title = title,
                    summary = summary,
                    category = category,
                    deadline = deadline
                )
                goalRepository.insertGoal(newGoal)
                loadGoals()
                _showCreateDialog.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Error creating goal", e)
            }
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            try {
                goalRepository.updateGoal(goal)
                loadGoals()
                // If this is the selected goal, reload its details
                if (_selectedGoalState.value is GoalDetailUiState.Success &&
                    (_selectedGoalState.value as GoalDetailUiState.Success).goal.id == goal.id
                ) {
                    loadGoalDetail(goal.id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating goal", e)
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            try {
                goalRepository.deleteGoal(goalId)
                _selectedGoalState.value = GoalDetailUiState.Idle
                loadGoals()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting goal", e)
            }
        }
    }

    fun setShowCreateDialog(show: Boolean) {
        _showCreateDialog.value = show
    }

    fun updateFilters(filters: GoalFilters) {
        _activeFilters.value = filters
        // Apply filters to goals list
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            try {
                val filters = _activeFilters.value
                goalRepository.getGoals().collect { allGoals ->
                    val filteredGoals = allGoals.filter { goal ->
                        var include = true
                        
                        // Filter by category
                        if (filters.category != null && goal.category != filters.category) {
                            include = false
                        }
                        
                        // Filter by completion status
                        if (filters.showCompleted != null && goal.isCompleted != filters.showCompleted) {
                            include = false
                        }
                        
                        // Filter by deadline
                        if (filters.deadlineBefore != null && (goal.deadline == null || goal.deadline.isAfter(filters.deadlineBefore))) {
                            include = false
                        }
                        
                        include
                    }
                    
                    _goals.value = filteredGoals
                    _uiState.value = if (filteredGoals.isEmpty()) UiState.Empty else UiState.Success(filteredGoals)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error applying filters", e)
            }
        }
    }

    data class GoalFilters(
        val category: Goal.Category? = null,
        val showCompleted: Boolean? = null,
        val deadlineBefore: LocalDate? = null
    )
}
