# UI Layer Testing

## Overview

Testing the UI layer involves verifying that:
1. ViewModels properly transform data for UI consumption
2. UI components correctly display data and respond to user interactions
3. UI state changes are correctly propagated through the system

## ViewModel Testing

ViewModels serve as the bridge between the UI and the domain/data layers, making them critical components to test.

### What to Test in ViewModels

- Initial state setup
- State transformations in response to events
- Error handling
- Loading state management
- Event handling
- Navigation triggers

### Testing Tools for ViewModels

- JUnit for unit tests
- Kotlin Coroutines Test for testing suspend functions and Flow
- MockK/Mockito for mocking dependencies (Use Cases, Repositories)
- Turbine for testing Flow emissions

### ViewModel Test Example

```kotlin
@ExperimentalCoroutinesApi
class TaskViewModelTest {
    // Set the main dispatcher to a test dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    // For testing Flow collection
    @get:Rule
    val turbineRule = TurbineRule()
    
    // Mock dependencies
    private val getTasksUseCase = mockk<GetTasksUseCase>()
    private val updateTaskUseCase = mockk<UpdateTaskUseCase>()
    
    // System under test
    private lateinit var viewModel: TaskViewModel
    
    @Before
    fun setup() {
        // Setup default responses for mocks
        every { getTasksUseCase() } returns flowOf(Result.Loading())
        coEvery { updateTaskUseCase(any(), any()) } returns Result.Success(mockk())
        
        // Initialize viewModel after setting up mocks
        viewModel = TaskViewModel(getTasksUseCase, updateTaskUseCase)
    }
    
    @Test
    fun `initial state is loading`() {
        // When ViewModel is initialized
        
        // Then initial state should show loading
        val initialState = viewModel.uiState.value
        assertEquals(true, initialState.isLoading)
        assertEquals(emptyList<Task>(), initialState.tasks)
        assertEquals(null, initialState.error)
        // For Material 3 Expressive UI: ensure proper theme state
        assertEquals(ThemeMode.SYSTEM, initialState.themeMode) 
    }
    
    @Test
    fun `when getTasks emits success result, state is updated correctly`() = runTest {
        // Given
        val tasks = listOf(
            Task("1", "Task 1", priority = TaskPriority.HIGH), 
            Task("2", "Task 2", priority = TaskPriority.MEDIUM)
        )
        
        // Setup flow that emits loading followed by success
        val resultsFlow = flow {
            emit(Result.Loading<List<Task>>())
            emit(Result.Success(tasks))
        }
        
        every { getTasksUseCase() } returns resultsFlow
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem() // Initial loading state
            assertTrue(state.isLoading)
            
            val loadedState = awaitItem() // State after loading
            assertFalse(loadedState.isLoading)
            assertEquals(tasks, loadedState.tasks)
            assertNull(loadedState.error)
            
            // No more emissions
            expectNoEvents()
        }
    }
    
    @Test
    fun `error during loading updates error state`() = runTest {
        // Given
        val exception = IOException("Network error")
        val errorFlow = flow<Result<List<Task>>> {
            emit(Result.Loading())
            emit(Result.Error(exception))
        }
        every { getTasksUseCase() } returns errorFlow
        
        // When
        viewModel = TaskViewModel(getTasksUseCase, updateTaskUseCase)
        
        // Then
        viewModel.uiState.test {
            val initialState = awaitItem() // Loading state
            assertTrue(initialState.isLoading)
            
            val errorState = awaitItem() // Error state
            assertFalse(errorState.isLoading)
            assertEquals("Network error", errorState.error)
            assertEquals(emptyList<Task>(), errorState.tasks)
            
            expectNoEvents()
        }
    }
    
    @Test
    fun `toggle task completed calls use case and updates state`() = runTest {
        // Given
        val taskId = "task-1"
        val completed = true
        val updatedTask = Task(taskId, "Task 1", completed = completed)
        
        // Setup mock responses
        every { getTasksUseCase() } returns flowOf(listOf(updatedTask))
        coEvery { updateTaskUseCase(taskId, completed) } returns Result.success(updatedTask)
        
        // When
        viewModel = TaskViewModel(getTasksUseCase, updateTaskUseCase)
        viewModel.toggleTaskCompleted(taskId, completed)
        
        // Then
        coVerify { updateTaskUseCase(taskId, completed) }
    }
}

// Test rule for main dispatcher
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

## Jetpack Compose UI Testing

Testing Compose UI requires a different approach focused on UI components and their behavior.

### What to Test in Compose UI

- Content visibility and properties
- UI behavior in response to state changes
- User interactions (clicks, text input, etc.)
- Screen navigation
- Error states and loading indicators

### Testing Tools for Compose UI

- ComposeTestRule for controlling composition
- Semantics testing for finding and interacting with elements
- Snapshot testing for visual regression testing

### Compose UI Test Example

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskListScreenTest {
    // Create a test rule for Compose
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun taskList_whenLoading_showsLoadingIndicator() {
        // Given - Loading state
        val uiState = TasksUiState(isLoading = true)
        
        // When - Composing the UI with this state
        composeTestRule.setContent {
            MaterialTheme {
                TasksContent(
                    uiState = uiState,
                    onTaskClick = {},
                    onTaskCheckChanged = { _, _ -> }
                )
            }
        }
        
        // Then - Loading indicator should be visible
        composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()
        composeTestRule.onNodeWithTag("taskList").assertDoesNotExist()
    }
    
    @Test
    fun taskList_whenHasTasks_showsTasks() {
        // Given - State with tasks
        val tasks = listOf(
            Task("1", "Task 1"),
            Task("2", "Task 2")
        )
        val uiState = TasksUiState(tasks = tasks, isLoading = false)
        
        // When - Composing the UI with this state
        composeTestRule.setContent {
            MaterialTheme {
                TasksContent(
                    uiState = uiState,
                    onTaskClick = {},
                    onTaskCheckChanged = { _, _ -> }
                )
            }
        }
        
        // Then - Task list should be visible with tasks
        composeTestRule.onNodeWithTag("taskList").assertIsDisplayed()
        composeTestRule.onNodeWithText("Task 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Task 2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("loadingIndicator").assertDoesNotExist()
    }
    
    @Test
    fun taskList_whenTaskClicked_callsOnTaskClick() {
        // Given - State with tasks and a click tracker
        val tasks = listOf(Task("1", "Task 1"))
        val uiState = TasksUiState(tasks = tasks, isLoading = false)
        var clickedTaskId: String? = null
        
        // When - Composing the UI and clicking a task
        composeTestRule.setContent {
            MaterialTheme {
                TasksContent(
                    uiState = uiState,
                    onTaskClick = { taskId -> clickedTaskId = taskId },
                    onTaskCheckChanged = { _, _ -> }
                )
            }
        }
        
        // Perform the click
        composeTestRule.onNodeWithText("Task 1").performClick()
        
        // Then - Click callback should be called with correct ID
        assertEquals("1", clickedTaskId)
    }
    
    @Test
    fun taskList_whenError_showsErrorMessage() {
        // Given - Error state
        val errorMessage = "Failed to load tasks"
        val uiState = TasksUiState(error = errorMessage)
        
        // When - Composing the UI with error state
        composeTestRule.setContent {
            MaterialTheme {
                TasksContent(
                    uiState = uiState,
                    onTaskClick = {},
                    onTaskCheckChanged = { _, _ -> }
                )
            }
        }
        
        // Then - Error message should be visible
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithTag("taskList").assertDoesNotExist()
    }
}
```

## Traditional View (XML) UI Testing

For projects using traditional XML-based views, Espresso is the primary testing tool.

### What to Test in Traditional Views

- View visibility and properties
- User interactions (clicks, input, etc.)
- RecyclerView content and scrolling
- Navigation between screens
- Error states and loading indicators

### Testing Tools for Traditional Views

- Espresso for UI testing
- ActivityScenarioRule for activity lifecycle management
- Hamcrest matchers for assertions

### Traditional View Test Example

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskListActivityTest {
    // Create a test rule for the Activity
    @get:Rule
    val activityRule = ActivityScenarioRule(TaskListActivity::class.java)
    
    @Test
    fun taskList_whenLoading_showsLoadingIndicator() {
        // Set the activity to loading state (using IdlingResource or test ViewModel)
        // ...
        
        // Check that progress bar is visible and RecyclerView is gone
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
    }
    
    @Test
    fun taskList_whenHasTasks_showsTasks() {
        // Set the activity to state with tasks
        // ...
        
        // Check that RecyclerView is visible and progress bar is gone
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        
        // Check RecyclerView content
        onView(withId(R.id.recyclerView))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(atPosition(0, hasDescendant(withText("Task 1")))))
        
        onView(withId(R.id.recyclerView))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(atPosition(1, hasDescendant(withText("Task 2")))))
    }
    
    @Test
    fun taskList_whenTaskClicked_navigatesToDetail() {
        // Set the activity to state with tasks
        // ...
        
        // Click on first task
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        
        // Verify navigation to detail screen
        onView(withId(R.id.taskDetailTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.taskDetailTitle)).check(matches(withText("Task 1")))
    }
    
    @Test
    fun taskList_whenError_showsErrorMessage() {
        // Set the activity to error state
        // ...
        
        // Check that error view is visible with correct message
        onView(withId(R.id.errorView)).check(matches(isDisplayed()))
        onView(withId(R.id.errorView)).check(matches(withText("Failed to load tasks")))
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
    }
}
```

## Best Practices for UI Testing

1. **Test state, not implementation**: Focus on what the user sees, not how it's implemented
2. **Use test doubles**: Mock dependencies to isolate UI layer
3. **Test navigation**: Verify navigation between screens
4. **Test error states**: Ensure error handling works correctly
5. **Test loading states**: Verify loading indicators appear appropriately
6. **Test accessibility**: Ensure UI is accessible to all users
7. **Minimize flakiness**: Make tests deterministic and reliable
8. **Use tags**: Add testTags to Compose elements to make them easier to find
9. **Separate UI from logic**: Keep UI components focused on rendering, not logic
10. **Test different device configurations**: Test on different screen sizes, orientations, etc.

## Resources

- [Testing Jetpack Compose](https://developer.android.com/jetpack/compose/testing)
- [Espresso testing](https://developer.android.com/training/testing/espresso)
- [ViewModel testing](https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics#0)
- [Coroutines testing](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test)
