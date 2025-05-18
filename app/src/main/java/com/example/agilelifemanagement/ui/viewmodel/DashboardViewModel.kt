package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.WellnessAnalytics
import com.example.agilelifemanagement.domain.usecase.day.activity.GetDayActivitiesUseCase
import com.example.agilelifemanagement.domain.usecase.goal.GetGoalsUseCase
import com.example.agilelifemanagement.domain.usecase.sprint.GetSprintsUseCase
import com.example.agilelifemanagement.domain.usecase.task.GetTasksByDateUseCase
import com.example.agilelifemanagement.domain.usecase.wellness.GetDailyCheckupUseCase
import com.example.agilelifemanagement.domain.usecase.wellness.GetWellnessAnalyticsUseCase
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
 * UI State for the Dashboard screen, following the Unidirectional Data Flow pattern.
 */
data class DashboardUiState(
    val todaysTasks: List<Task> = emptyList(),
    val activeSprint: Sprint? = null, 
    val sprintTasks: List<Task> = emptyList(),
    val activeGoals: List<Goal> = emptyList(),
    val todaysActivities: List<DayActivity> = emptyList(),
    val wellnessData: DailyCheckup? = null,
    val wellnessAnalytics: WellnessAnalytics? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val errorMessage: String? = null
)

/**
 * ViewModel for the Dashboard screen, implementing Unidirectional Data Flow.
 * It aggregates data from multiple sources to provide a comprehensive overview.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val getSprintsUseCase: GetSprintsUseCase,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getDayActivitiesUseCase: GetDayActivitiesUseCase,
    private val getDailyCheckupUseCase: GetDailyCheckupUseCase,
    private val getWellnessAnalyticsUseCase: GetWellnessAnalyticsUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(DashboardUiState())
    
    // Public immutable state flow for UI consumption
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    /**
     * Loads all dashboard data for the current date
     */
    fun loadDashboardData() {
        val today = LocalDate.now()
        _uiState.update { it.copy(isLoading = true, errorMessage = null, selectedDate = today) }
        
        loadTasksForDate(today)
        loadActiveSprint()
        loadActiveGoals()
        loadActivitiesForDate(today)
        loadWellnessData(today)
    }
    
    /**
     * Loads tasks for a specific date
     */
    private fun loadTasksForDate(date: LocalDate) {
        viewModelScope.launch {
            getTasksByDateUseCase(date)
                .catch { e ->
                    Timber.e(e, "Error loading tasks for date: $date")
                    _uiState.update { 
                        it.copy(
                            errorMessage = e.message ?: "Unknown error occurred while loading tasks"
                        )
                    }
                }
                .collectLatest { tasks ->
                    _uiState.update { 
                        it.copy(
                            todaysTasks = tasks,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }
    
    /**
     * Loads the active sprint and its tasks
     */
    private fun loadActiveSprint() {
        viewModelScope.launch {
            getSprintsUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading sprints")
                    _uiState.update { 
                        it.copy(
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
                            activeSprint = activeSprint
                        )
                    }
                }
        }
    }
    
    /**
     * Loads active goals
     */
    private fun loadActiveGoals() {
        viewModelScope.launch {
            getGoalsUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading goals")
                    _uiState.update { 
                        it.copy(
                            errorMessage = e.message ?: "Unknown error occurred while loading goals"
                        )
                    }
                }
                .collectLatest { goals ->
                    val today = LocalDate.now()
                    // Simplified goal filtering after May 2025 architecture change
                    // We'll temporarily show all goals for dashboard since we're rebuilding
                    val activeGoals = goals.take(5) // Limit to 5 goals for the dashboard
                    
                    _uiState.update { 
                        it.copy(
                            activeGoals = activeGoals
                        )
                    }
                }
        }
    }
    
    /**
     * Loads activities for a specific date
     */
    private fun loadActivitiesForDate(date: LocalDate) {
        viewModelScope.launch {
            getDayActivitiesUseCase(date)
                .catch { e ->
                    Timber.e(e, "Error loading activities for date: $date")
                    _uiState.update { 
                        it.copy(
                            errorMessage = e.message ?: "Unknown error occurred while loading activities"
                        )
                    }
                }
                .collectLatest { activities ->
                    _uiState.update { 
                        it.copy(
                            todaysActivities = activities
                        )
                    }
                }
        }
    }
    
    /**
     * Loads wellness data for a specific date
     */
    private fun loadWellnessData(date: LocalDate) {
        viewModelScope.launch {
            // Load daily checkup
            getDailyCheckupUseCase(date)
                .catch { e ->
                    Timber.e(e, "Error loading daily checkup for date: $date")
                    _uiState.update { 
                        it.copy(
                            errorMessage = e.message ?: "Unknown error occurred while loading wellness data"
                        )
                    }
                }
                .collectLatest { checkup ->
                    _uiState.update { 
                        it.copy(
                            wellnessData = checkup
                        )
                    }
                }
            
            // Load wellness analytics
            getWellnessAnalyticsUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading wellness analytics")
                    _uiState.update { 
                        it.copy(
                            errorMessage = e.message ?: "Unknown error occurred while loading wellness analytics"
                        )
                    }
                }
                .collectLatest { analytics ->
                    _uiState.update { 
                        it.copy(
                            wellnessAnalytics = analytics
                        )
                    }
                }
        }
    }
    
    /**
     * Refresh all dashboard data
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadDashboardData()
    }
    
    /**
     * Changes the selected date for dashboard data
     */
    fun changeDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadTasksForDate(date)
        loadActivitiesForDate(date)
        loadWellnessData(date)
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
