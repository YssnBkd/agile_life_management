# UI Layer

## Overview

The UI (User Interface) layer is responsible for displaying application data on the screen and serving as the primary point of user interaction. It acts as a visual representation of the application state retrieved from the data layer.

When data changes, whether due to user interaction (like pressing a button) or external input (like a network response), the UI updates to reflect those changes. The UI layer serves as the pipeline that converts application data changes into a form that can be presented to users.

## UI Layer Architecture Components

The UI layer consists of two main parts:

### 1. UI Elements

These are the components that render data on the screen:
- **Views**: Traditional Android View system (XML layouts)
- **Jetpack Compose**: Modern declarative UI toolkit

### 2. State Holders

Components that hold data, expose it to the UI, and handle logic:
- **ViewModels**: Primary state holders that survive configuration changes
- **Other State Holders**: UI Controllers, Composables with `remember`, etc.

## Material 3 Expressive UI Implementation

Material 3 Expressive is an extension of Material 3 that provides more creative freedom while maintaining core Material Design principles. The AgileLifeManagement app implements Material 3 Expressive to create a visually distinctive and engaging user experience.

### Color System Implementation

The app uses dynamic color to adapt to user preferences while maintaining a unique brand expression:

### Theme Implementation

The app implements a custom theme that:
- Supports both light and dark themes based on system settings
- Utilizes Android 12+ dynamic color capabilities when available
- Falls back to custom brand colors on older devices
- Implements custom typography that aligns with Material 3 guidelines
- Ensures consistent visual styling throughout the application


### Component Theming Structure

The app's theme utilizes Material 3's component theming structure:
- **Shapes**: Custom shape definitions for components like cards and buttons
- **Material Theme**: Centralized theme provider that supplies color, typography, and shape values

### Custom Color Schemes

The app defines distinct color schemes that represent the brand identity:

The color schemes were carefully designed to ensure:
- **Brand consistency** across light and dark themes
- **WCAG accessibility compliance** for text contrast
- **Visual hierarchy** to guide user attention
- **Emotional resonance** with the productivity-focused app purpose

### Typography System

The app implements a custom typography scale that enhances readability and visual hierarchy:

#### Typography Scales

The app uses a carefully crafted typography system with:
- **Display styles**: For large headlines and feature introductions
- **Headline styles**: For section headers and important text elements
- **Title styles**: For component titles and medium-emphasis headers
- **Body styles**: For primary content text with optimal readability
#### Typography Considerations

- **Font Family**: Uses both the system default font and a custom project font family
- **Font Weight**: Varies from normal to semi-bold for proper visual hierarchy
- **Font Size**: Follows a consistent scale from small (body) to large (display)
- **Line Height**: Carefully tuned for optimal readability at each size
- **Letter Spacing**: Adjusted for each text style to enhance legibility

### Custom Component Styles

The app implements expressive custom components that align with the brand identity:

```kotlin
@Composable
fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = when(task.priority) {
                TaskPriority.HIGH -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                TaskPriority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        // Card content
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = task.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Task metadata (due date, priority indicator, etc.)
        }
    }
}
```

### Animation and Motion

The app uses custom animations to enhance the user experience:

```kotlin
@Composable
fun TaskFilterChip(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = { Text(text = label) },
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null
    )
}
```

## Theme Mode Management

The app supports dynamic theme mode changes that are persisted using DataStore:

```kotlin
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class ThemePreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true
)

@Singleton
class ThemeManagerRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    val themePreferences: Flow<ThemePreferences> = dataStore.data
        .catch { exception ->
            Timber.e(exception, "Error reading theme preferences")
            emit(Preferences.defaultInstance)
        }
        .map { preferences ->
            val themeMode = when(preferences[THEME_MODE_KEY]) {
                0 -> ThemeMode.LIGHT
                1 -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
            ThemePreferences(
                themeMode = themeMode,
                useDynamicColor = preferences[USE_DYNAMIC_COLOR_KEY] ?: true
            )
        }
        .flowOn(ioDispatcher)
        
    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = when(themeMode) {
                ThemeMode.LIGHT -> 0
                ThemeMode.DARK -> 1
                ThemeMode.SYSTEM -> 2
            }
        }
    }
}
```

## UI State Definition

UI state represents all the data needed for the UI to render. It should be:

### Immutable

UI state classes should be immutable to avoid unexpected changes. Here's an example of a TasksUiState class that includes Material 3 Expressive UI properties:

```kotlin
data class TasksUiState(
    // Core data
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Material 3 Expressive UI properties
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true,
    val colorScheme: ColorScheme? = null,
    
    // Task grouping for enhanced visual organization
    val taskGroups: List<TaskGroup> = emptyList(),
    
    // Animation states for Material motion
    val expandedTaskIds: Set<String> = emptySet(),
    val recentlyAddedTaskId: String? = null,
    val animationInProgress: Boolean = false,
    
    // Filters state
    val selectedFilters: Set<TaskFilter> = emptySet(),
)

// Supporting classes for Material 3 Expressive UI
data class TaskGroup(
    val priority: TaskPriority,
    val tasks: List<Task>,
    val color: Color? = null,
    val expanded: Boolean = true
)

enum class TaskFilter {
    BY_PRIORITY, BY_DUE_DATE, COMPLETED_ONLY, INCOMPLETE_ONLY, RECENT_ONLY
}
```

### Immutable

Using immutable data classes with the following properties:
- Define UI state as a data class with `val` properties
- Use `copy()` to create new instances when state changes
- Prevent unexpected mutations

```kotlin
data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Complete

UI state should contain all data the UI needs to render, including:
- Content to display
- Loading states
- Error states
- User interaction states (selections, text entry, etc.)

## Unidirectional Data Flow (UDF)

The UI layer follows the Unidirectional Data Flow pattern:

1. **State flows down**: The state holder (ViewModel) exposes state to the UI
2. **Events flow up**: UI components send events to the state holder
3. **State is updated**: State holder processes events and updates state
4. **UI updates**: UI observes and renders the new state

### UDF Implementation

```kotlin
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {
    // UI state as a StateFlow
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            getTasksUseCase()
                .onStart {
                    // Optional additional loading indicator logic
                }
                .catch { exception ->
                    Timber.e(exception, "Error loading tasks")
                    _uiState.update { 
                        it.copy(isLoading = false, error = exception.message) 
                    }
                }
                .collect { result ->
                    _uiState.update { currentState ->
                        when (result) {
                            is Result.Success -> {
                                currentState.copy(
                                    tasks = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                            is Result.Error -> {
                                currentState.copy(
                                    isLoading = false,
                                    error = result.exception.message
                                )
                            }
                            is Result.Loading -> {
                                currentState.copy(isLoading = true)
                            }
                        }
                    }
                }
        }
    }
    
    // UI event handling using Material 3 Expressive styles
    fun toggleTaskCompleted(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            // Show optional progress indicator for the specific task
            _uiState.update { state ->
                state.copy(
                    tasks = state.tasks.map { task ->
                        if (task.id == taskId) task.copy(isUpdating = true) else task
                    }
                )
            }
            
            val result = updateTaskUseCase(taskId, completed)
            
            // Handle result
            when (result) {
                is Result.Success -> {
                    // Success is handled by the Flow collection in loadTasks()
                    // But we could also update the local state immediately for better UX
                    _uiState.update { state ->
                        state.copy(
                            tasks = state.tasks.map { task ->
                                if (task.id == taskId) {
                                    task.copy(completed = completed, isUpdating = false)
                                } else task
                            }
                        )
                    }
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Error updating task")
                    _uiState.update { state ->
                        state.copy(
                            error = result.exception.message,
                            tasks = state.tasks.map { task ->
                                if (task.id == taskId) task.copy(isUpdating = false) else task
                            }
                        )
                    }
                }
                else -> { /* Do nothing for other states */ }
            }
        }
    }
}
```

## Types of Logic in UI Layer

### UI Logic

Logic that processes and prepares data for display:
- Formatting data (dates, numbers, strings)
- Resource resolution
- Screen navigation

### UI Behavior Logic

Logic that handles user interactions:
- Click handlers
- Input validation
- Animation control

## Exposing UI State

### In Views (XML-based UI)

Using LiveData or StateFlow with lifecycle awareness:

```kotlin
// ViewModel
val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

// Activity/Fragment
viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
    .onEach { uiState -> updateUI(uiState) }
    .launchIn(lifecycleScope)

private fun updateUI(uiState: TasksUiState) {
    // Update UI based on state
    progressBar.isVisible = uiState.isLoading
    adapter.submitList(uiState.tasks)
    errorView.isVisible = uiState.error != null
    errorView.text = uiState.error
}
```

### In Jetpack Compose

Using `collectAsState()` to observe state:

```kotlin
@Composable
fun TasksScreen(viewModel: TasksViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    TasksContent(
        tasks = uiState.tasks,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onTaskCheckedChanged = { id, completed -> 
            viewModel.toggleTaskCompleted(id, completed) 
        }
    )
}

@Composable
fun TasksContent(
    tasks: List<Task>,
    isLoading: Boolean,
    error: String?,
    onTaskCheckedChanged: (String, Boolean) -> Unit
) {
    when {
        isLoading -> CircularProgressIndicator()
        error != null -> ErrorMessage(error)
        tasks.isEmpty() -> EmptyState()
        else -> TaskList(tasks, onTaskCheckedChanged)
    }
}
```

## Handling Additional Concerns

### Threading and Concurrency

- UI operations should happen on the main thread
- ViewModel should use `viewModelScope` for coroutines
- Long-running operations should be delegated to the domain or data layer
- Use appropriate dispatchers (`Dispatchers.Main` for UI)

### Navigation

Handle navigation through:
- Jetpack Navigation Component
- Navigation events in the UI state
- Deep linking capabilities

### Error Handling

Proper error management in the UI includes:
- Showing user-friendly error messages
- Offering retry options
- Distinguishing between different error types
- Handling edge cases (empty states, connectivity issues)

## Testing the UI Layer

### ViewModel Testing

Test ViewModels using:
- JUnit for unit tests
- Kotlin Coroutines Test for suspending functions
- Mock dependencies (use cases, repositories)
- Test UI state transformations

### Compose UI Testing

Test Compose UI using:
- ComposeTestRule
- Semantics testing
- Screenshot testing

## Resources

- [UI Layer documentation by Android Developers](https://developer.android.com/jetpack/guide/ui-layer)
- [ViewModel documentation](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Jetpack Compose documentation](https://developer.android.com/jetpack/compose)
- [State and Jetpack Compose](https://developer.android.com/jetpack/compose/state)
