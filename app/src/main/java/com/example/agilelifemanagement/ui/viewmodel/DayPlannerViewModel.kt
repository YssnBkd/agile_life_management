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
// Sprint use case to be added in the future
import com.example.agilelifemanagement.domain.usecase.task.GetTasksByDateUseCase
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel for the Day Planner screen, implementing Unidirectional Data Flow pattern.
 * Manages day activities with enhanced timeline functionality and integration with
 * sprints and tasks.
 */
@HiltViewModel
class DayPlannerViewModel @Inject constructor(
    private val getDayActivitiesUseCase: GetDayActivitiesUseCase,
    private val getDayActivityByIdUseCase: GetDayActivityByIdUseCase,
    private val addDayActivityUseCase: AddDayActivityUseCase,
    private val updateDayActivityUseCase: UpdateDayActivityUseCase,
    private val deleteDayActivityUseCase: DeleteDayActivityUseCase,
    private val getTasksByDateUseCase: GetTasksByDateUseCase
    // Sprint use case to be added when implemented
    // private val getSprintForDateUseCase: GetSprintForDateUseCase
) : ViewModel() {

    // UI state representing the current state of the Day Planner screen
    private val _uiState = MutableStateFlow(DayPlannerUiState())
    val uiState: StateFlow<DayPlannerUiState> = _uiState.asStateFlow()

    init {
        // Load activities for today by default
        handleEvent(DayPlannerEvent.DateSelected(LocalDate.now()))
    }

    /**
     * Handles events from the UI layer
     */
    fun handleEvent(event: DayPlannerEvent) {
        when (event) {
            is DayPlannerEvent.DateSelected -> loadDayData(event.date)
            is DayPlannerEvent.ActivityAdded -> addActivity(event.activity)
            is DayPlannerEvent.ActivityUpdated -> updateActivity(event.activity)
            is DayPlannerEvent.ActivityDeleted -> deleteActivity(event.activityId)
            is DayPlannerEvent.ActivityRescheduled -> rescheduleActivity(
                event.activityId, 
                event.newStartTime, 
                event.duration
            )
            is DayPlannerEvent.ActivityCompletionToggled -> toggleActivityCompletion(event.activityId)
            is DayPlannerEvent.RefreshRequested -> refreshDayData()
        }
    }

    /**
     * Loads all data needed for a specific date including activities, related tasks and sprint
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadDayData(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDate = date) }
            
            try {
                // Get day activities for the selected date
                getDayActivitiesUseCase(date)
                    .catch { e ->
                        Timber.e(e, "Error loading activities for date: $date")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to load activities"
                            )
                        }
                    }
                    .collectLatest { activities ->
                        // Convert domain activities to TimeBlock UI models
                        val timeBlocks = activities.map { activity ->
                            TimeBlock.fromDayActivity(activity)
                        }
                        
                        // Update UI state with activities
                        _uiState.update { state ->
                            state.copy(
                                timeBlocks = timeBlocks,
                                isLoading = false,
                                completionRate = calculateCompletionRate(activities)
                            )
                        }
                        
                        // Load related data (tasks and sprint)
                        loadRelatedData(date)
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error in loadDayData: $date")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }
    
    /**
     * Loads related tasks and sprint information for the selected date
     */
    private fun loadRelatedData(date: LocalDate) {
        viewModelScope.launch {
            try {
                // Load related tasks
                getTasksByDateUseCase(date)
                    .catch { e ->
                        Timber.e(e, "Error loading tasks for date: $date")
                    }
                    .collectLatest { tasks ->
                        _uiState.update { it.copy(relatedTasks = tasks) }
                    }
                
                // Sprint information loading to be implemented when use case is available
                // For now, we'll just use a null value for currentSprint
                _uiState.update { it.copy(currentSprint = null) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading related data for date: $date")
            }
        }
    }
    
    /**
     * Refreshes all data for the current date
     */
    private fun refreshDayData() {
        loadDayData(_uiState.value.selectedDate)
    }

    /**
     * Adds a new activity to the selected date
     */
    private fun addActivity(activityUiModel: DayActivityUiModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, actionInProgress = true) }
            
            try {
                // Convert UI model to domain model
                val activity = activityUiModel.toDomainModel(_uiState.value.selectedDate)
                
                // Add activity
                addDayActivityUseCase(activity)
                
                // Refresh the activities list
                refreshDayData()
                
                _uiState.update { 
                    it.copy(
                        actionInProgress = false,
                        actionSuccess = true,
                        actionMessage = "Activity added successfully"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error adding activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        actionInProgress = false,
                        actionSuccess = false,
                        error = e.message ?: "Failed to add activity",
                        actionMessage = "Failed to add activity"
                    )
                }
            }
        }
    }

    /**
     * Updates an existing activity
     */
    private fun updateActivity(activityUiModel: DayActivityUiModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, actionInProgress = true) }
            
            try {
                // Convert UI model to domain model
                val activity = activityUiModel.toDomainModel(_uiState.value.selectedDate)
                
                // Update activity
                updateDayActivityUseCase(activity)
                
                // Refresh the activities list
                refreshDayData()
                
                _uiState.update { 
                    it.copy(
                        actionInProgress = false,
                        actionSuccess = true,
                        actionMessage = "Activity updated successfully"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error updating activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        actionInProgress = false,
                        actionSuccess = false,
                        error = e.message ?: "Failed to update activity",
                        actionMessage = "Failed to update activity"
                    )
                }
            }
        }
    }

    /**
     * Deletes an activity
     */
    private fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, actionInProgress = true) }
            
            try {
                // Delete activity
                deleteDayActivityUseCase(activityId)
                
                // Refresh the activities list
                refreshDayData()
                
                _uiState.update { 
                    it.copy(
                        actionInProgress = false,
                        actionSuccess = true,
                        actionMessage = "Activity deleted successfully"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error deleting activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        actionInProgress = false,
                        actionSuccess = false,
                        error = e.message ?: "Failed to delete activity",
                        actionMessage = "Failed to delete activity"
                    )
                }
            }
        }
    }

    /**
     * Reschedules an activity by updating its start time and duration
     */
    private fun rescheduleActivity(activityId: String, newStartTime: LocalTime, duration: Int) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, actionInProgress = true) }
        
        try {
            // Get the current activity
            getDayActivityByIdUseCase(activityId)
                .catch { e ->
                    Timber.e(e, "Error getting activity for rescheduling: $activityId")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            actionInProgress = false,
                            actionSuccess = false,
                            error = e.message ?: "Failed to find activity",
                            actionMessage = "Failed to reschedule activity"
                        )
                    }
                }
                .collectLatest { activity ->
                    // Update with new times, with null safety check
                    activity?.let { act ->
                        val updatedActivity = act.copy(
                            scheduledTime = newStartTime,
                            duration = duration
                        )
                    
                        // Save the updated activity
                        updateDayActivityUseCase(updatedActivity)
                        
                        // Refresh the activities list
                        refreshDayData()
                        
                            refreshDayData()
                            
                            _uiState.update { 
                                it.copy(
                                    actionInProgress = false,
                                    actionSuccess = true,
                                    actionMessage = "Activity rescheduled successfully"
                                )
                            }
                        } ?: run {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    actionInProgress = false,
                                    actionSuccess = false,
                                    error = "Activity not found",
                                    actionMessage = "Failed to reschedule activity"
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error rescheduling activity")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        actionInProgress = false,
                        actionSuccess = false,
                        error = e.message ?: "Failed to reschedule activity",
                        actionMessage = "Failed to reschedule activity"
                    )
                }
            }
        }
    }

    /**
     * Toggles the completion status of an activity
     */
    private fun toggleActivityCompletion(activityId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionInProgress = true) }
            
            try {
                // Get the current activity
                getDayActivityByIdUseCase(activityId)
                    .catch { e ->
                        Timber.e(e, "Error getting activity for toggling completion: $activityId")
                        _uiState.update { 
                            it.copy(
                                actionInProgress = false,
                                actionSuccess = false,
                                error = e.message ?: "Failed to find activity",
                                actionMessage = "Failed to update completion status"
                            )
                        }
                    }
                    .collectLatest { activity ->
                        // Toggle completion status with null safety check
                        activity?.let { act ->
                            val updatedActivity = act.copy(
                                completed = !act.completed
                            )
                            
                            // Save the updated activity
                            updateDayActivityUseCase(updatedActivity)
                            
                            // Refresh the activities list
                            refreshDayData()
                            
                            _uiState.update { 
                                it.copy(
                                    actionInProgress = false,
                                    actionSuccess = true,
                                    actionMessage = if (updatedActivity.completed) 
                                        "Activity marked as complete" 
                                    else 
                                        "Activity marked as incomplete"
                                )
                            }
                        } ?: run {
                            _uiState.update { 
                                it.copy(
                                    actionInProgress = false,
                                    actionSuccess = false,
                                    error = "Activity not found",
                                    actionMessage = "Failed to update completion status"
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling activity completion")
                _uiState.update { 
                    it.copy(
                        actionInProgress = false,
                        actionSuccess = false,
                        error = e.message ?: "Failed to update completion status",
                        actionMessage = "Failed to update completion status"
                    )
                }
            }
        }
    }

    /**
     * Calculates the completion rate of activities for the current day
     */
    private fun calculateCompletionRate(activities: List<DayActivity>): Float {
        if (activities.isEmpty()) return 0f
        
        val completedCount = activities.count { it.completed }
        return completedCount.toFloat() / activities.size
    }
}

/**
 * UI State for the Day Planner screen
 */
data class DayPlannerUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val timeBlocks: List<TimeBlock> = emptyList(),
    val relatedTasks: List<Any> = emptyList(), // Replace with TaskUiModel when available
    val currentSprint: Any? = null, // Replace with SprintUiModel when available
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionInProgress: Boolean = false,
    val actionSuccess: Boolean = false,
    val actionMessage: String? = null,
    val completionRate: Float = 0f
)

/**
 * Events that can be triggered from the UI
 */
sealed class DayPlannerEvent {
    data class DateSelected(val date: LocalDate) : DayPlannerEvent()
    data class ActivityAdded(val activity: DayActivityUiModel) : DayPlannerEvent()
    data class ActivityUpdated(val activity: DayActivityUiModel) : DayPlannerEvent()
    data class ActivityDeleted(val activityId: String) : DayPlannerEvent()
    data class ActivityRescheduled(
        val activityId: String, 
        val newStartTime: LocalTime,
        val duration: Int
    ) : DayPlannerEvent()
    data class ActivityCompletionToggled(val activityId: String) : DayPlannerEvent()
    object RefreshRequested : DayPlannerEvent()
}

/**
 * UI Model for Day Activity with only the necessary information for UI display
 * Helps decouple UI from domain model changes
 */
data class DayActivityUiModel(
    val id: String,
    val title: String,
    val description: String,
    val startTime: LocalTime,
    val duration: Int,
    val isCompleted: Boolean,
    val categoryId: String
) {
    /**
     * Converts UI model to domain model
     */
    fun toDomainModel(date: LocalDate): DayActivity {
        return DayActivity(
            id = id,
            title = title,
            description = description,
            date = date,
            scheduledTime = startTime,
            duration = duration,
            completed = isCompleted,
            categoryId = categoryId
        )
    }
    
    companion object {
        /**
         * Creates a UI model from domain model
         */
        fun fromDomainModel(activity: DayActivity): DayActivityUiModel {
            return DayActivityUiModel(
                id = activity.id,
                title = activity.title,
                description = activity.description,
                startTime = activity.scheduledTime,
                duration = activity.duration,
                isCompleted = activity.completed,
                categoryId = activity.categoryId
            )
        }
    }
}
