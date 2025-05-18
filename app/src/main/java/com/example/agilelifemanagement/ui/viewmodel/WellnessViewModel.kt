package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.model.WellnessAnalytics
import com.example.agilelifemanagement.domain.usecase.wellness.GetDailyCheckupUseCase
import com.example.agilelifemanagement.domain.usecase.wellness.GetWellnessAnalyticsUseCase
import com.example.agilelifemanagement.domain.repository.temporary.TempWellnessRepository
import com.example.agilelifemanagement.domain.usecase.wellness.SaveDailyCheckupUseCase
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
 * UI State for the Wellness screens, following the Unidirectional Data Flow pattern.
 */
data class WellnessUiState(
    val dailyCheckup: DailyCheckup? = null,
    val recentCheckups: List<DailyCheckup> = emptyList(),
    val wellnessAnalytics: WellnessAnalytics? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isCheckupSaved: Boolean = false
)

/**
 * ViewModel for Wellness-related screens, implementing Unidirectional Data Flow.
 * It handles loading and saving daily wellness checkups and wellness analytics.
 */
@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val getDailyCheckupUseCase: GetDailyCheckupUseCase,
    private val saveDailyCheckupUseCase: SaveDailyCheckupUseCase,
    private val getWellnessAnalyticsUseCase: GetWellnessAnalyticsUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(WellnessUiState())
    
    // Public immutable state flow for UI consumption
    val uiState: StateFlow<WellnessUiState> = _uiState.asStateFlow()

    init {
        loadDailyCheckup(LocalDate.now())
        loadWellnessAnalytics()
    }

    /**
     * Loads a daily checkup for a specific date
     */
    fun loadDailyCheckup(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, selectedDate = date) }
            
            getDailyCheckupUseCase(date)
                .catch { e ->
                    Timber.e(e, "Error loading daily checkup for date: $date")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading daily checkup"
                        )
                    }
                }
                .collectLatest { checkup ->
                    _uiState.update { 
                        it.copy(
                            dailyCheckup = checkup,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    /**
     * Loads wellness analytics data
     */
    private fun loadWellnessAnalytics() {
        viewModelScope.launch {
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
     * Saves a daily wellness checkup
     */
    fun saveDailyCheckup(checkup: DailyCheckup) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isCheckupSaved = false) }
            
            try {
                val result = saveDailyCheckupUseCase(checkup)
                result.fold(
                    onSuccess = { savedCheckup ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                dailyCheckup = savedCheckup,
                                isCheckupSaved = true
                            )
                        }
                        // Refresh analytics
                        loadWellnessAnalytics()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error saving daily checkup")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to save daily checkup",
                                isCheckupSaved = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception saving daily checkup")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred while saving daily checkup",
                        isCheckupSaved = false
                    )
                }
            }
        }
    }
    
    /**
     * Updates the mood rating for the current daily checkup
     */
    fun updateMoodLevel(moodLevel: Int) {
        val currentCheckup = _uiState.value.dailyCheckup
        if (currentCheckup != null) {
            val updatedCheckup = currentCheckup.copy(moodRating = moodLevel)
            saveDailyCheckup(updatedCheckup)
        } else {
            // Create a new checkup if none exists
            val newCheckup = DailyCheckup(
                id = "",
                date = _uiState.value.selectedDate,
                moodRating = moodLevel,
                stressLevel = 0,
                sleepQuality = 0,
                energyLevel = 0,
                notes = ""
            )
            saveDailyCheckup(newCheckup)
        }
    }
    
    /**
     * Updates the stress level for the current daily checkup
     */
    fun updateStressLevel(stressLevel: Int) {
        val currentCheckup = _uiState.value.dailyCheckup
        if (currentCheckup != null) {
            val updatedCheckup = currentCheckup.copy(stressLevel = stressLevel)
            saveDailyCheckup(updatedCheckup)
        } else {
            // Create a new checkup if none exists
            val newCheckup = DailyCheckup(
                id = "",
                date = _uiState.value.selectedDate,
                moodRating = 0,
                stressLevel = stressLevel,
                sleepQuality = 0,
                energyLevel = 0,
                notes = ""
            )
            saveDailyCheckup(newCheckup)
        }
    }
    
    /**
     * Updates the sleep quality for the current daily checkup
     */
    fun updateSleepQuality(sleepQuality: Int) {
        val currentCheckup = _uiState.value.dailyCheckup
        if (currentCheckup != null) {
            val updatedCheckup = currentCheckup.copy(sleepQuality = sleepQuality)
            saveDailyCheckup(updatedCheckup)
        } else {
            // Create a new checkup if none exists
            val newCheckup = DailyCheckup(
                id = "",
                date = _uiState.value.selectedDate,
                moodRating = 0,
                stressLevel = 0,
                sleepQuality = sleepQuality,
                energyLevel = 0,
                notes = ""
            )
            saveDailyCheckup(newCheckup)
        }
    }
    
    /**
     * Updates the notes for the current daily checkup
     */
    fun updateNotes(notes: String) {
        val currentCheckup = _uiState.value.dailyCheckup
        if (currentCheckup != null) {
            val updatedCheckup = currentCheckup.copy(notes = notes)
            saveDailyCheckup(updatedCheckup)
        } else {
            // Create a new checkup if none exists
            val newCheckup = DailyCheckup(
                id = "",
                date = _uiState.value.selectedDate,
                moodRating = 0,
                stressLevel = 0,
                sleepQuality = 0,
                energyLevel = 0,
                notes = notes
            )
            saveDailyCheckup(newCheckup)
        }
    }
    
    /**
     * Sets the selected date and loads daily checkup for that date
     */
    fun selectDate(date: LocalDate) {
        if (date != _uiState.value.selectedDate) {
            loadDailyCheckup(date)
        }
    }
    
    /**
     * Refresh the daily checkup data for the current date
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadDailyCheckup(_uiState.value.selectedDate)
        loadWellnessAnalytics()
    }
    
    /**
     * Resets the checkup saved state
     */
    fun resetCheckupSavedState() {
        _uiState.update { it.copy(isCheckupSaved = false) }
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
