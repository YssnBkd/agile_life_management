# Domain Model Changes

## Overview

This document details the changes made to domain models during our debugging session for the Agile Life Management application. These changes were necessary to support the transition to a new architecture where the data layer is being rebuilt.

## Result Class Implementation

### Purpose
The `Result` class was created to provide a standardized error handling mechanism across the application. This replaces ad-hoc error handling with a consistent pattern.

### Implementation Details

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()

    fun <R> fold(
        onSuccess: (T) -> R,
        onError: (String, Throwable?) -> R
    ): R {
        return when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(message, cause)
        }
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(message: String, cause: Throwable? = null): Result<Nothing> = Error(message, cause)
    }
}
```

### Benefits
- Provides clear distinction between success and error states
- Enables type-safe handling with the `fold` method
- Reduces boilerplate in error handling code
- Facilitates consistent error reporting across the application

## Task Model Refactoring

### Previous Issues
- Task status and priority were represented as strings
- Inconsistent handling of optional fields
- Lack of clear domain semantics

### Updated Implementation

```kotlin
data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val status: TaskStatus = TaskStatus.NOT_STARTED,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: LocalDate? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,
    val categoryId: String? = null,
    val tags: List<String> = emptyList(),
    val estimatedTime: Int? = null, // In minutes
    val actualTime: Int? = null,    // In minutes
    val parentTaskId: String? = null,
    val subtasks: List<Task> = emptyList()
)

enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    BLOCKED,
    CANCELLED
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}
```

### Migration Impact
- Required updates to all ViewModels using tasks
- Necessitated changes to UI components for displaying status and priority
- Enhanced type safety with enum-based status and priority

## Sprint Model Implementation

### Purpose
The Sprint model supports agile sprint planning functionality within the application.

### Implementation Details

```kotlin
data class Sprint(
    val id: String,
    val name: String,
    val description: String = "",
    val startDate: LocalDate,
    val endDate: LocalDate,
    val goals: List<String> = emptyList(), // Goal IDs
    val status: SprintStatus = SprintStatus.PLANNED,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

enum class SprintStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
```

### Integration Challenges
- Required updates to SprintViewModel for handling enum-based status
- Needed modifications to UI components for displaying sprint status

## Goal Model Implementation

### Purpose
The Goal model facilitates goal tracking and management within the application.

### Implementation Details

```kotlin
data class Goal(
    val id: String,
    val title: String,
    val description: String = "",
    val category: GoalCategory? = null,
    val dueDate: LocalDate? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null,
    val isCompleted: Boolean = false,
    val progress: Int = 0, // 0-100%
    val priority: GoalPriority = GoalPriority.MEDIUM,
    val tags: List<String> = emptyList(),
    val milestones: List<GoalMilestone> = emptyList(),
    val parentGoalId: String? = null
)

data class GoalMilestone(
    val id: String,
    val goalId: String,
    val title: String,
    val description: String = "",
    val dueDate: LocalDate? = null,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null
)

enum class GoalPriority {
    LOW,
    MEDIUM,
    HIGH
}

enum class GoalCategory(val displayName: String) {
    PERSONAL("Personal"),
    PROFESSIONAL("Professional"),
    HEALTH("Health & Wellness"),
    FINANCIAL("Financial"),
    EDUCATIONAL("Educational"),
    SOCIAL("Social"),
    OTHER("Other")
}
```

### Integration Challenges
- Required updates to GoalViewModel for progress tracking
- Needed modifications for goal completion handling

## DailyCheckup Model

### Purpose
The DailyCheckup model enables wellness tracking functionality within the application.

### Implementation Details

```kotlin
data class DailyCheckup(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val date: LocalDate = LocalDate.now(),
    val moodRating: Int, // 1-5
    val sleepQuality: Int, // 1-5
    val stressLevel: Int, // 1-5
    val energyLevel: Int, // 1-5
    val notes: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
)
```

### Integration Challenges
- Required updates to WellnessViewModel for handling rating scales
- Needed consistent date handling for daily records

## WellnessAnalytics Model

### Purpose
The WellnessAnalytics model provides aggregated wellness data for analytics features.

### Implementation Details

```kotlin
data class WellnessAnalytics(
    val timeFrameDays: Int,
    val averageMood: Float,
    val averageSleep: Float,
    val averageStress: Float,
    val averageEnergy: Float,
    val moodTrend: List<Pair<LocalDate, Int>> = emptyList(),
    val sleepTrend: List<Pair<LocalDate, Int>> = emptyList(),
    val stressTrend: List<Pair<LocalDate, Int>> = emptyList(),
    val energyTrend: List<Pair<LocalDate, Int>> = emptyList()
)
```

### Integration Challenges
- Needed modifications to analytics display components
- Required temporal aggregation logic in the repository implementation

## Lessons Learned

### Effective Domain Model Design
1. **Use Enums for Limited Value Sets**
   - Provides compiler-enforced constraints
   - Enables exhaustiveness checking in when expressions
   - Makes code more self-documenting

2. **Consistent Date/Time Handling**
   - Use LocalDate for date-only values
   - Use LocalDateTime for timestamp values
   - Explicitly handle time zones when necessary

3. **Default Parameters for Optional Fields**
   - Reduces boilerplate in object creation
   - Makes intent clear for optional fields
   - Ensures reasonable defaults for common cases

4. **Immutable Data Classes**
   - Simplifies state management
   - Reduces bugs from unintended mutations
   - Works well with Kotlin's copy() method for updates

### Migration Strategies
1. **Incremental Type Changes**
   - Update models first, then ViewModels, then UI
   - Run frequent compilations to catch errors early
   - Use AI-assisted refactoring for consistent changes

2. **Comprehensive Documentation**
   - Document the purposes of domain model fields
   - Explain relationships between models
   - Note default values and their rationale

3. **Type-Safety First Approach**
   - Prioritize strong typing over flexibility
   - Use sealed classes for representing polymorphic concepts
   - Leverage Kotlin's type system for compile-time safety
