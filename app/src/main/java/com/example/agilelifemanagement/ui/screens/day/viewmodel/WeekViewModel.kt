package com.example.agilelifemanagement.ui.screens.day.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.usecase.day.GetWeekActivitiesUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.AddDayActivityUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.GetDayActivitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for WeekViewScreen.
 * Manages weekly calendar view and activities.
 * 
 * Following Clean Architecture principles, this ViewModel interacts with the domain layer
 * through use cases rather than directly with repositories.
 */
@HiltViewModel
class WeekViewModel @Inject constructor(
    private val getWeekActivitiesUseCase: GetWeekActivitiesUseCase,
    private val getDayActivitiesUseCase: GetDayActivitiesUseCase,
    private val addDayActivityUseCase: AddDayActivityUseCase
) : ViewModel() {

    // UI state for week view
    data class WeekUiState(
        val isLoading: Boolean = false,
        val currentWeekStart: LocalDate = LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1),
        val selectedDay: LocalDate = LocalDate.now(),
        val weekDays: List<LocalDate> = emptyList(),
        val selectedDayActivities: List<DayActivity> = emptyList(),
        val weekActivities: Map<LocalDate, List<DayActivity>> = emptyMap(),
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(WeekUiState())
    val uiState: StateFlow<WeekUiState> = _uiState.asStateFlow()

    // Initialize with current week
    init {
        initializeWeek()
    }

    // Set up the week view with the current week
    private fun initializeWeek() {
        val today = LocalDate.now()
        val weekStart = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
        setWeekRange(weekStart)
        selectDay(today)
    }

    // Set the week range to view
    fun setWeekRange(weekStart: LocalDate) {
        val weekDays = (0..6).map { weekStart.plusDays(it.toLong()) }
        
        _uiState.value = _uiState.value.copy(
            currentWeekStart = weekStart,
            weekDays = weekDays,
            isLoading = true,
            errorMessage = null
        )
        
        // Load activities for each day in the week
        loadWeekActivities(weekDays)
    }

    // Load activities for all days in the week using the GetWeekActivitiesUseCase
    private fun loadWeekActivities(days: List<LocalDate>) {
        viewModelScope.launch {
            try {
                val weekStart = days.first()
                
                getWeekActivitiesUseCase(weekStart)
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to load week activities: ${e.message}"
                        )
                    }
                    .collectLatest { activitiesMap ->
                        _uiState.value = _uiState.value.copy(
                            weekActivities = activitiesMap,
                            isLoading = false
                        )
                        
                        // Update selected day activities if it's part of this week
                        val selectedDay = _uiState.value.selectedDay
                        if (days.contains(selectedDay)) {
                            _uiState.value = _uiState.value.copy(
                                selectedDayActivities = activitiesMap[selectedDay] ?: emptyList()
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load week: ${e.message}"
                )
            }
        }
    }

    // Select a specific day in the week using the GetDayActivitiesUseCase
    fun selectDay(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedDay = date,
                isLoading = true
            )
            
            getDayActivitiesUseCase(date)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load day activities: ${e.message}"
                    )
                }
                .collectLatest { activities ->
                    _uiState.value = _uiState.value.copy(
                        selectedDayActivities = activities,
                        isLoading = false
                    )
                }
        }
    }

    // Navigate to previous week
    fun navigateToPreviousWeek() {
        val previousWeekStart = _uiState.value.currentWeekStart.minusWeeks(1)
        setWeekRange(previousWeekStart)
    }

    // Navigate to next week
    fun navigateToNextWeek() {
        val nextWeekStart = _uiState.value.currentWeekStart.plusWeeks(1)
        setWeekRange(nextWeekStart)
    }

    // Navigate to today
    fun navigateToToday() {
        val today = LocalDate.now()
        val currentWeekStart = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
        setWeekRange(currentWeekStart)
        selectDay(today)
    }

    // Add activity to the selected day using the AddDayActivityUseCase
    fun addActivity(activity: DayActivity) {
        viewModelScope.launch {
            when (val result = addDayActivityUseCase(activity)) {
                is Result.Success<DayActivity> -> {
                    // Refresh selected day activities
                    selectDay(_uiState.value.selectedDay)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to add activity: ${result.message}"
                    )
                }
            }
        }
    }
}
