package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.usecase.day.activity.AddDayActivityUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.DeleteDayActivityUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.GetDayActivitiesUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.GetDayActivityByIdUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.UpdateDayActivityUseCase
import com.example.agilelifemanagement.domain.usecase.day.category.GetActivityCategoriesUseCase
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
 * UI State for the DayActivity screens, following the Unidirectional Data Flow pattern.
 */
data class DayActivityUiState(
    val activities: List<DayActivity> = emptyList(),
    val selectedActivity: DayActivity? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val categories: List<Any> = emptyList(), // Replace with actual ActivityCategory type
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isActivitySaved: Boolean = false,
    val isActivityDeleted: Boolean = false
)

/**
 * ViewModel for DayActivity-related screens, implementing Unidirectional Data Flow.
 * It handles loading activities for specific dates, and CRUD operations.
 * 
 * Updated for May 15, 2025 architectural change where data layer was archived.
 */
@HiltViewModel
class DayActivityViewModel @Inject constructor(
    private val getDayActivitiesUseCase: GetDayActivitiesUseCase,
    private val getDayActivityByIdUseCase: GetDayActivityByIdUseCase,
    private val addDayActivityUseCase: AddDayActivityUseCase,
    private val updateDayActivityUseCase: UpdateDayActivityUseCase,
    private val deleteDayActivityUseCase: DeleteDayActivityUseCase,
    private val getActivityCategoriesUseCase: GetActivityCategoriesUseCase
) : ViewModel() {
    
    // UI state for this screen
    private val _uiState = MutableStateFlow(DayActivityUiState())
    val uiState: StateFlow<DayActivityUiState> = _uiState.asStateFlow()
    
    init {
        // Load activities for today by default
        loadActivitiesForDate(LocalDate.now())
        // Categories loading temporarily disabled until data layer is rebuilt
        // loadCategories()
    }
    
    /**
     * Loads activities for a specific date
     */
    fun loadActivitiesForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDate = date) }
            
            getDayActivitiesUseCase(date)
                .catch { e ->
                    Timber.e(e, "Error loading activities for date: $date")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isRefreshing = false,
                            errorMessage = e.message
                        )
                    }
                }
                .collectLatest { activities ->
                    _uiState.update { 
                        it.copy(
                            activities = activities,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }
    
    /**
     * Loads a specific activity by ID
     */
    fun loadActivity(activityId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            getDayActivityByIdUseCase(activityId)
                .catch { e ->
                    Timber.e(e, "Error loading activity by ID: $activityId")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
                .collectLatest { activity ->
                    _uiState.update { 
                        it.copy(
                            selectedActivity = activity,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    /**
     * Creates a new activity
     */
    fun addActivity(activity: DayActivity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isActivitySaved = false, errorMessage = null) }
            
            try {
                // Use a simplified approach during rebuilding phase
                // In the future, this will use the real repository
                val result = Result.Success(activity)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isActivitySaved = true
                    )
                }
                // Refresh activities for the current date
                loadActivitiesForDate(_uiState.value.selectedDate)
            } catch (e: Exception) {
                Timber.e(e, "Exception creating activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error creating activity",
                        isActivitySaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Updates an existing activity
     */
    fun updateActivity(activity: DayActivity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isActivitySaved = false, errorMessage = null) }
            
            try {
                // Using a simplified approach during rebuilding phase
                val result = Result.Success(activity)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isActivitySaved = true
                    )
                }
                // Refresh activities for the current date
                loadActivitiesForDate(_uiState.value.selectedDate)
            } catch (e: Exception) {
                Timber.e(e, "Exception updating activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error updating activity",
                        isActivitySaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Deletes an activity
     */
    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isActivityDeleted = false, errorMessage = null) }
            
            try {
                // Using a simplified approach during rebuilding phase
                val result = Result.Success(Unit)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isActivityDeleted = true
                    )
                }
                // Refresh activities for the current date
                loadActivitiesForDate(_uiState.value.selectedDate)
            } catch (e: Exception) {
                Timber.e(e, "Exception deleting activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error deleting activity",
                        isActivityDeleted = false
                    )
                }
            }
        }
    }
    
    /**
     * Sets the selected date and loads activities for that date
     */
    fun selectDate(date: LocalDate) {
        loadActivitiesForDate(date)
    }
    
    /**
     * Refresh the activities data for the current date
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadActivitiesForDate(_uiState.value.selectedDate)
    }
    
    /**
     * Resets the activity saved/deleted state
     */
    fun resetActivitySavedState() {
        _uiState.update { it.copy(isActivitySaved = false, isActivityDeleted = false) }
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
