package com.example.agilelifemanagement.ui.screens.day.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.DaySchedule
import kotlin.Result
import kotlin.runCatching
import com.example.agilelifemanagement.domain.usecase.day.GetDayScheduleUseCase
import com.example.agilelifemanagement.domain.usecase.day.UpdateDayScheduleUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.AddDayActivityUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.DeleteDayActivityUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.GetDayActivitiesUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.ToggleActivityCompletionUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.UpdateDayActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for day-related screens (DayDetailScreen, DayPlannerScreen).
 * Manages day schedule data and activities.
 * 
 * Following Clean Architecture principles, this ViewModel interacts with the domain layer
 * through use cases rather than directly with repositories.
 */
@HiltViewModel
class DayViewModel @Inject constructor(
    private val getDayScheduleUseCase: GetDayScheduleUseCase,
    private val updateDayScheduleUseCase: UpdateDayScheduleUseCase,
    private val getDayActivitiesUseCase: GetDayActivitiesUseCase,
    private val addDayActivityUseCase: AddDayActivityUseCase,
    private val updateDayActivityUseCase: UpdateDayActivityUseCase,
    private val deleteDayActivityUseCase: DeleteDayActivityUseCase,
    private val toggleActivityCompletionUseCase: ToggleActivityCompletionUseCase
) : ViewModel() {

    // UI state for day screens
    data class DayUiState(
        val isLoading: Boolean = false,
        val currentDate: LocalDate = LocalDate.now(),
        val daySchedule: DaySchedule? = null,
        val activities: List<DayActivity> = emptyList(),
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(DayUiState())
    val uiState: StateFlow<DayUiState> = _uiState.asStateFlow()

    // Function to load a specific day's data using the appropriate use cases
    fun loadDay(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                currentDate = date,
                errorMessage = null
            )

            // Load day schedule using GetDayScheduleUseCase
            getDayScheduleUseCase(date)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load schedule: ${e.message}"
                    )
                }
                .collectLatest { schedule ->
                    _uiState.value = _uiState.value.copy(
                        daySchedule = schedule,
                        isLoading = false
                    )
                }

            // Load day activities using GetDayActivitiesUseCase
            getDayActivitiesUseCase(date)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load activities: ${e.message}"
                    )
                }
                .collectLatest { activities ->
                    _uiState.value = _uiState.value.copy(
                        activities = activities,
                        isLoading = false
                    )
                }
        }
    }

    // Add a new activity using AddDayActivityUseCase
    fun addActivity(activity: DayActivity) {
        viewModelScope.launch {
            val result = addDayActivityUseCase(activity)
            if (result.isSuccess) {
                // Refresh activities
                loadDay(_uiState.value.currentDate)
            } else {
                // Simply show generic error - we can't access exception details in this environment
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add activity"
                )
            }
        }
    }

    // Update an existing activity using UpdateDayActivityUseCase
    fun updateActivity(activity: DayActivity) {
        viewModelScope.launch {
            val result = updateDayActivityUseCase(activity)
            if (result.isSuccess) {
                // Refresh activities
                loadDay(_uiState.value.currentDate)
            } else {
                // Simply show generic error - we can't access exception details in this environment
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update activity"
                )
            }
        }
    }

    // Delete an activity using DeleteDayActivityUseCase
    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            val result = deleteDayActivityUseCase(activityId)
            if (result.isSuccess) {
                // Refresh activities
                loadDay(_uiState.value.currentDate)
            } else {
                // Simply show generic error - we can't access exception details in this environment
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete activity"
                )
            }
        }
    }

    // Toggle completion status of an activity using ToggleActivityCompletionUseCase
    fun toggleActivityCompletion(activityId: String, completed: Boolean) {
        viewModelScope.launch {
            // Note: The use case now handles the completion toggling internally
            val result = toggleActivityCompletionUseCase(activityId)
            if (result.isSuccess) {
                // Refresh activities
                loadDay(_uiState.value.currentDate)
            } else {
                // Simply show generic error - we can't access exception details in this environment
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update activity status"
                )
            }
        }
    }

    // Update day notes using UpdateDayScheduleUseCase
    fun updateNotes(notes: String) {
        viewModelScope.launch {
            val currentSchedule = _uiState.value.daySchedule
            if (currentSchedule != null) {
                val updatedSchedule = currentSchedule.copy(notes = notes)
                val result = updateDayScheduleUseCase(updatedSchedule)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        daySchedule = updatedSchedule
                    )
                } else {
                    // Simply show generic error - we can't access exception details in this environment
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to update notes"
                    )
                }
            }
        }
    }

    // Navigate to next/previous day
    fun navigateToDay(date: LocalDate) {
        loadDay(date)
    }

    // Initialize ViewModel
    init {
        loadDay()
    }
}
