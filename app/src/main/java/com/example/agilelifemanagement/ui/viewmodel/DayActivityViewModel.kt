package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.DayActivity
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

    // Private mutable state flow
    private val _uiState = MutableStateFlow(DayActivityUiState())
    
    // Public immutable state flow for UI consumption
    val uiState: StateFlow<DayActivityUiState> = _uiState.asStateFlow()

    init {
        loadActivitiesForDate(LocalDate.now())
        loadCategories()
    }

    /**
     * Loads activities for a specific date
     */
    fun loadActivitiesForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, selectedDate = date) }
            
            getDayActivitiesUseCase(date)
                .catch { e ->
                    Timber.e(e, "Error loading activities for date: $date")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading activities"
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getDayActivityByIdUseCase(activityId)
                .catch { e ->
                    Timber.e(e, "Error loading activity: $activityId")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading activity"
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
     * Loads all activity categories
     */
    private fun loadCategories() {
        viewModelScope.launch {
            getActivityCategoriesUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading activity categories")
                    _uiState.update { 
                        it.copy(
                            errorMessage = e.message ?: "Unknown error occurred while loading categories"
                        )
                    }
                }
                .collectLatest { categories ->
                    _uiState.update { 
                        it.copy(
                            categories = categories
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isActivitySaved = false) }
            
            try {
                val result = addDayActivityUseCase(activity)
                result.fold(
                    onSuccess = { createdActivity ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedActivity = createdActivity,
                                isActivitySaved = true
                            )
                        }
                        // Refresh activities for the current date
                        loadActivitiesForDate(_uiState.value.selectedDate)
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error creating activity")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to create activity",
                                isActivitySaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception creating activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while creating activity",
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isActivitySaved = false) }
            
            try {
                val result = updateDayActivityUseCase(activity)
                result.fold(
                    onSuccess = { updatedActivity ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedActivity = updatedActivity,
                                isActivitySaved = true
                            )
                        }
                        // Refresh activities for the current date
                        loadActivitiesForDate(_uiState.value.selectedDate)
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error updating activity")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to update activity",
                                isActivitySaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception updating activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while updating activity",
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isActivityDeleted = false) }
            
            try {
                val result = deleteDayActivityUseCase(activityId)
                result.fold(
                    onSuccess = { _ ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isActivityDeleted = true,
                                selectedActivity = null
                            )
                        }
                        // Refresh activities for the current date
                        loadActivitiesForDate(_uiState.value.selectedDate)
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error deleting activity")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to delete activity",
                                isActivityDeleted = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception deleting activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while deleting activity",
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
        if (date != _uiState.value.selectedDate) {
            loadActivitiesForDate(date)
        }
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
