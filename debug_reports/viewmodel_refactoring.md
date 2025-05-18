# ViewModel Refactoring

## Overview

This document details the refactoring process for ViewModels in the Agile Life Management application. As part of the architectural rebuild, we needed to update all ViewModels to work with the new domain models and temporary repositories.

## TaskViewModel Refactoring

### Key Issues Addressed
- Unclosed comment syntax error
- Duplicate TaskUiState definition
- Incorrect task priority comparisons
- Integration with temporary repository

### Implementation Changes

```kotlin
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // UI State definition
    data class TaskUiState(
        val tasks: List<Task> = emptyList(),
        val filteredTasks: List<Task> = emptyList(),
        val categories: List<Category> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val filterType: TaskFilterType = TaskFilterType.ALL,
        val sortType: TaskSortType = TaskSortType.DUE_DATE,
        val searchQuery: String = ""
    )

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
        loadCategories()
    }

    // Updated to use TaskPriority enum
    fun applyFiltersAndSort(tasks: List<Task>): List<Task> {
        var filteredTasks = tasks

        // Apply filter based on filter type
        filteredTasks = when (_uiState.value.filterType) {
            TaskFilterType.ALL -> tasks
            TaskFilterType.ACTIVE -> tasks.filter { it.status != TaskStatus.COMPLETED && it.status != TaskStatus.CANCELLED }
            TaskFilterType.COMPLETED -> tasks.filter { it.status == TaskStatus.COMPLETED }
            TaskFilterType.TODAY -> tasks.filter { it.dueDate == LocalDate.now() }
            TaskFilterType.UPCOMING -> tasks.filter { 
                it.dueDate != null && it.dueDate.isAfter(LocalDate.now()) 
            }
            TaskFilterType.OVERDUE -> tasks.filter { 
                it.dueDate != null && it.dueDate.isBefore(LocalDate.now()) && it.status != TaskStatus.COMPLETED 
            }
            TaskFilterType.HIGH_PRIORITY -> tasks.filter { it.priority == TaskPriority.HIGH || it.priority == TaskPriority.URGENT }
        }

        // Apply search query if present
        if (_uiState.value.searchQuery.isNotBlank()) {
            val query = _uiState.value.searchQuery.lowercase()
            filteredTasks = filteredTasks.filter { 
                it.title.lowercase().contains(query) || it.description.lowercase().contains(query) 
            }
        }

        // Sort based on sort type
        filteredTasks = when (_uiState.value.sortType) {
            TaskSortType.DUE_DATE -> filteredTasks.sortedWith(
                compareBy(
                    { it.dueDate == null },
                    { it.dueDate }
                )
            )
            TaskSortType.PRIORITY -> filteredTasks.sortedWith(
                compareByDescending { it.priority }
            )
            TaskSortType.TITLE -> filteredTasks.sortedBy { it.title.lowercase() }
            TaskSortType.CREATION_DATE -> filteredTasks.sortedByDescending { it.createdAt }
        }

        return filteredTasks
    }
}
```

### Error Resolution Strategy
1. Fixed unclosed comment with proper syntax
2. Removed duplicate TaskUiState definition
3. Updated priority comparisons to use `TaskPriority` enum 
4. Implemented proper filtering and sorting logic for tasks

## GoalViewModel Refactoring

### Key Issues Addressed
- Incorrect handling of goal completion status
- Progress tracking implementation
- Integration with temporary repository

### Implementation Changes

```kotlin
fun updateGoalCompletionStatus(goalId: String, isCompleted: Boolean) {
    viewModelScope.launch {
        val currentGoals = _uiState.value.goals
        val goalIndex = currentGoals.indexOfFirst { it.id == goalId }
        
        if (goalIndex != -1) {
            // Create an updated copy of the goal with changed completion status
            val updatedGoal = currentGoals[goalIndex].copy(
                isCompleted = isCompleted,
                completedAt = if (isCompleted) LocalDateTime.now() else null,
                // If marking as completed, set progress to 100%, otherwise leave it unchanged
                progress = if (isCompleted) 100 else currentGoals[goalIndex].progress
            )
            
            updateGoalUseCase(updatedGoal)
                .catch { e -> 
                    Timber.e(e, "Error updating goal completion status")
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { 
                            // Update the UI state with the updated goal
                            val updatedGoals = currentGoals.toMutableList().apply {
                                set(goalIndex, updatedGoal)
                            }
                            _uiState.update { it.copy(goals = updatedGoals) }
                        },
                        onError = { message, _ ->
                            _uiState.update { it.copy(errorMessage = message) }
                        }
                    )
                }
        }
    }
}
```

### Error Resolution Strategy
1. Fixed the goal completion status update logic
2. Ensured progress is set to 100% when a goal is marked complete
3. Added proper error handling using the Result pattern
4. Used immutable update patterns with copy() method

## DayActivityViewModel Refactoring

### Key Issues Addressed
- Duplicate state declarations
- Incorrect initialization of state
- Integration with temporary repository

### Implementation Changes

```kotlin
@HiltViewModel
class DayActivityViewModel @Inject constructor(
    private val getDayActivitiesUseCase: GetDayActivitiesUseCase,
    private val addDayActivityUseCase: AddDayActivityUseCase,
    private val updateDayActivityUseCase: UpdateDayActivityUseCase,
    private val deleteDayActivityUseCase: DeleteDayActivityUseCase,
    private val getActivityCategoriesUseCase: GetActivityCategoriesUseCase
) : ViewModel() {

    data class DayActivityUiState(
        val activities: List<DayActivity> = emptyList(),
        val categories: List<ActivityCategory> = emptyList(),
        val selectedDate: LocalDate = LocalDate.now(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(DayActivityUiState())
    val uiState: StateFlow<DayActivityUiState> = _uiState.asStateFlow()

    init {
        loadActivitiesForDate(LocalDate.now())
        loadCategories()
    }

    fun loadActivitiesForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDate = date) }
            getDayActivitiesUseCase(date)
                .catch { e -> 
                    Timber.e(e, "Error loading activities for date: $date")
                    _uiState.update { 
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
                .collectLatest { activities -> 
                    _uiState.update { 
                        it.copy(activities = activities, isLoading = false)
                    }
                }
        }
    }
}
```

### Error Resolution Strategy
1. Fixed duplicate state declaration
2. Corrected initialization of MutableStateFlow
3. Used consistent error handling pattern
4. Updated to use proper date handling for activities

## WellnessViewModel Refactoring

### Key Issues Addressed
- Integration with temporary repository
- Handling of rating scales
- Consistent date handling

### Implementation Changes

```kotlin
@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val getDailyCheckupUseCase: GetDailyCheckupUseCase,
    private val saveDailyCheckupUseCase: SaveDailyCheckupUseCase,
    private val getWellnessAnalyticsUseCase: GetWellnessAnalyticsUseCase
) : ViewModel() {

    data class WellnessUiState(
        val dailyCheckup: DailyCheckup? = null,
        val analytics: WellnessAnalytics? = null,
        val selectedDate: LocalDate = LocalDate.now(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(WellnessUiState())
    val uiState: StateFlow<WellnessUiState> = _uiState.asStateFlow()

    init {
        loadDailyCheckup(LocalDate.now())
        loadWellnessAnalytics(30) // Default to 30 days
    }

    fun loadDailyCheckup(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDate = date) }
            getDailyCheckupUseCase(date)
                .catch { e ->
                    Timber.e(e, "Error loading daily checkup for date: $date")
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { checkup ->
                            _uiState.update {
                                it.copy(dailyCheckup = checkup, isLoading = false)
                            }
                        },
                        onError = { message, _ ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = message)
                            }
                        }
                    )
                }
        }
    }
}
```

### Error Resolution Strategy
1. Updated to use the Result pattern for error handling
2. Fixed initialization with properly typed state
3. Ensured consistent date handling for daily records
4. Added validation for rating scales (1-5)

## SprintViewModel Refactoring

### Key Issues Addressed
- Integration with the new Sprint model
- Enum-based status handling
- Date range validation

### Implementation Changes

```kotlin
@HiltViewModel
class SprintViewModel @Inject constructor(
    private val getSprintsUseCase: GetSprintsUseCase,
    private val getSprintByIdUseCase: GetSprintByIdUseCase,
    private val addSprintUseCase: AddSprintUseCase,
    private val updateSprintUseCase: UpdateSprintUseCase,
    private val deleteSprintUseCase: DeleteSprintUseCase,
    private val getGoalsUseCase: GetGoalsUseCase
) : ViewModel() {

    data class SprintUiState(
        val sprints: List<Sprint> = emptyList(),
        val currentSprint: Sprint? = null,
        val availableGoals: List<Goal> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(SprintUiState())
    val uiState: StateFlow<SprintUiState> = _uiState.asStateFlow()

    init {
        loadSprints()
        loadAvailableGoals()
    }

    fun updateSprintStatus(sprintId: String, status: SprintStatus) {
        viewModelScope.launch {
            val currentSprints = _uiState.value.sprints
            val sprintIndex = currentSprints.indexOfFirst { it.id == sprintId }
            
            if (sprintIndex != -1) {
                val sprint = currentSprints[sprintIndex]
                val updatedSprint = sprint.copy(
                    status = status,
                    completedAt = if (status == SprintStatus.COMPLETED) LocalDateTime.now() else null
                )
                
                updateSprintUseCase(updatedSprint)
                    .catch { e ->
                        Timber.e(e, "Error updating sprint status")
                        _uiState.update { it.copy(errorMessage = e.message) }
                    }
                    .collectLatest { result ->
                        result.fold(
                            onSuccess = {
                                val updatedSprints = currentSprints.toMutableList().apply {
                                    set(sprintIndex, updatedSprint)
                                }
                                _uiState.update { it.copy(
                                    sprints = updatedSprints,
                                    currentSprint = if (_uiState.value.currentSprint?.id == sprintId) updatedSprint else _uiState.value.currentSprint
                                )}
                            },
                            onError = { message, _ ->
                                _uiState.update { it.copy(errorMessage = message) }
                            }
                        )
                    }
            }
        }
    }
}
```

### Error Resolution Strategy
1. Updated to use enum-based status handling
2. Implemented proper date range validation for sprints
3. Used immutable update patterns with copy() method
4. Added proper error handling with the Result pattern

## Common ViewModel Patterns Implemented

### State Management Pattern
All ViewModels now follow a consistent state management pattern:
1. Define a nested data class for UI state
2. Use MutableStateFlow for internal state management
3. Expose StateFlow for UI consumption
4. Use the update() method with copy() for immutable state updates

### Error Handling Pattern
A consistent error handling pattern was implemented across all ViewModels:
1. Use the Result class for representing success/error outcomes
2. Handle errors using catch blocks in flow operations
3. Update UI state with error messages when errors occur
4. Use fold() method to handle both success and error cases

### Asynchronous Operation Pattern
All asynchronous operations follow a consistent pattern:
1. Launch coroutines in the viewModelScope
2. Update UI state to show loading state
3. Call appropriate use case and collect results
4. Update UI state based on operation outcome
5. Handle any exceptions with proper error states

## Migration to Material 3 Guidelines

In accordance with the Material 3 Expressive UI guidelines, we've updated ViewModels to support:

### Dynamic Color Support
- ViewModels now prepare data in a way that's compatible with dynamic color theming
- State representations include semantic color roles rather than hardcoded colors

### Typography System Compatibility
- Text content is prepared with appropriate typography roles in mind
- ViewModels avoid making assumptions about specific font metrics

### Shape System Integration
- Data structures support the Material 3 shape system
- ViewModels don't make assumptions about specific corner radii

### Animation and Motion Support
- State transitions are designed to support animation
- ViewModels include the necessary data for expressive transitions

## Lessons Learned

### Effective ViewModel Design
1. **Single Responsibility**
   - Each ViewModel focuses on a specific domain area
   - Avoids becoming a "god object" with too many responsibilities

2. **Consistent State Representation**
   - All state is contained in a single data class
   - Updates are done atomically with immutable operations

3. **Proper Error Propagation**
   - Errors are captured and exposed through UI state
   - Error messages are user-friendly and actionable

4. **Lifecycle Management**
   - Resources are properly released in onCleared()
   - Coroutine scopes are tied to the ViewModel lifecycle

### Migration Strategies
1. **Incremental Approach**
   - Update one ViewModel at a time
   - Test each ViewModel thoroughly before moving to the next

2. **Consistent Patterns**
   - Apply the same patterns across all ViewModels
   - Makes code more predictable and maintainable

3. **Backward Compatibility**
   - Temporarily support old data structures during migration
   - Gradually phase out deprecated patterns
