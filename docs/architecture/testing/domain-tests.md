# Domain Layer Testing

## Overview

The domain layer contains business logic encapsulated in use cases (also called interactors). Testing this layer is critical because it contains the core business rules of your application. Domain layer tests should verify that:

1. Use cases correctly implement business rules
2. Use cases properly interact with repositories
3. Use cases handle errors appropriately
4. Complex business logic produces correct results

## Benefits of Domain Layer Testing

- **Business Rule Verification**: Ensures your app's core logic works correctly
- **Isolation**: Tests business logic without UI or data layer concerns
- **High Coverage**: Domain classes are easily testable with high coverage
- **Refactoring Safety**: Enables refactoring with confidence
- **Documentation**: Tests serve as executable documentation for business rules

## What to Test in Domain Layer

### For All Use Cases

- **Normal operation**: Correct behavior with valid inputs
- **Edge cases**: Behavior with boundary values
- **Error handling**: Proper handling of exceptions and error conditions
- **Parameter validation**: Validation of input parameters
- **Repository interactions**: Correct calls to repositories

### For Complex Business Logic

- **Calculations**: Correct mathematical or business calculations
- **Transformations**: Proper transformation of data
- **Filtering and sorting**: Correct filtering and sorting of data
- **State transitions**: Correct transitions between states
- **Workflow rules**: Correct implementation of workflow rules

## Testing Tools

- **JUnit**: Base testing framework
- **MockK/Mockito**: For mocking repositories and other dependencies
- **Kotlin Coroutines Test**: For testing suspending functions and Flow
- **Turbine**: For testing Flow emissions
- **Truth/AssertJ**: For fluent assertions

## Simple Use Case Test Examples

### Testing Use Case with Flow Return Type

```kotlin
class GetTasksUseCaseTest {
    // Mock dependencies
    private val taskRepository = mockk<TaskRepository>()
    
    // System under test
    private lateinit var getTasksUseCase: GetTasksUseCase
    
    @Before
    fun setup() {
        getTasksUseCase = GetTasksUseCase(taskRepository)
    }
    
    @Test
    fun `invoke returns tasks from repository`() = runTest {
        // Given
        val tasks = listOf(
            Task("1", "Task 1", status = TaskStatus.TODO),
            Task("2", "Task 2", status = TaskStatus.IN_PROGRESS)
        )
        every { taskRepository.getTasks() } returns flowOf(tasks)
        
        // When
        val result = getTasksUseCase().first()
        
        // Then
        verify { taskRepository.getTasks() }
        assertEquals(tasks, result)
    }
    
    @Test
    fun `emits updated tasks when repository emits updates`() = runTest {
        // Given
        val initialTasks = listOf(Task("1", "Task 1"))
        val updatedTasks = listOf(
            Task("1", "Task 1"),
            Task("2", "New Task")
        )
        
        val tasksFlow = MutableStateFlow(initialTasks)
        every { taskRepository.getTasks() } returns tasksFlow
        
        // When & Then
        getTasksUseCase().test {
            // Initial emission
            assertEquals(initialTasks, awaitItem())
            
            // Update the flow
            tasksFlow.value = updatedTasks
            
            // Should emit updated list
            assertEquals(updatedTasks, awaitItem())
            
            // Cancel the test
            cancel()
        }
    }
}
```

### Testing Use Case with Suspend Function

```kotlin
class UpdateTaskStatusUseCaseTest {
    // Mock dependencies
    private val taskRepository = mockk<TaskRepository>()
    
    // System under test
    private lateinit var updateTaskStatusUseCase: UpdateTaskStatusUseCase
    
    @Before
    fun setup() {
        updateTaskStatusUseCase = UpdateTaskStatusUseCase(taskRepository)
    }
    
    @Test
    fun `invoke updates task status in repository`() = runTest {
        // Given
        val taskId = "task-1"
        val newStatus = TaskStatus.COMPLETED
        val updatedTask = Task(taskId, "Task", status = newStatus)
        coEvery { taskRepository.updateTaskStatus(taskId, newStatus) } returns Result.success(updatedTask)
        
        // When
        val result = updateTaskStatusUseCase(taskId, newStatus)
        
        // Then
        coVerify { taskRepository.updateTaskStatus(taskId, newStatus) }
        assertTrue(result.isSuccess)
        assertEquals(updatedTask, result.getOrNull())
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val taskId = "task-1"
        val newStatus = TaskStatus.COMPLETED
        val exception = RuntimeException("Repository error")
        coEvery { taskRepository.updateTaskStatus(taskId, newStatus) } returns Result.failure(exception)
        
        // When
        val result = updateTaskStatusUseCase(taskId, newStatus)
        
        // Then
        coVerify { taskRepository.updateTaskStatus(taskId, newStatus) }
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
    
    @Test
    fun `invoke validates task ID`() = runTest {
        // Given
        val emptyTaskId = ""
        
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            updateTaskStatusUseCase(emptyTaskId, TaskStatus.COMPLETED)
        }
        
        assertEquals("Task ID cannot be empty", exception.message)
        
        // Verify repository not called with invalid ID
        coVerify(exactly = 0) { taskRepository.updateTaskStatus(any(), any()) }
    }
}
```

## Complex Business Logic Test Examples

### Testing Use Case with Parameter Transformation

```kotlin
class SearchTasksUseCaseTest {
    // Mock dependencies
    private val taskRepository = mockk<TaskRepository>()
    
    // System under test
    private lateinit var searchTasksUseCase: SearchTasksUseCase
    
    @Before
    fun setup() {
        searchTasksUseCase = SearchTasksUseCase(taskRepository)
    }
    
    @Test
    fun `invoke filters tasks by query`() = runTest {
        // Given
        val tasks = listOf(
            Task("1", "Meeting with team"),
            Task("2", "Write documentation"),
            Task("3", "Implement feature")
        )
        every { taskRepository.getTasks() } returns flowOf(tasks)
        
        // When
        val result = searchTasksUseCase("meet").first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }
    
    @Test
    fun `invoke handles case-insensitive search`() = runTest {
        // Given
        val tasks = listOf(
            Task("1", "Meeting with team"),
            Task("2", "Write documentation"),
            Task("3", "Important MEETING")
        )
        every { taskRepository.getTasks() } returns flowOf(tasks)
        
        // When
        val result = searchTasksUseCase("meeting").first()
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "1" })
        assertTrue(result.any { it.id == "3" })
    }
    
    @Test
    fun `invoke returns empty list when no matches`() = runTest {
        // Given
        val tasks = listOf(
            Task("1", "Meeting with team"),
            Task("2", "Write documentation")
        )
        every { taskRepository.getTasks() } returns flowOf(tasks)
        
        // When
        val result = searchTasksUseCase("project").first()
        
        // Then
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `invoke handles empty query`() = runTest {
        // Given
        val tasks = listOf(
            Task("1", "Meeting with team"),
            Task("2", "Write documentation")
        )
        every { taskRepository.getTasks() } returns flowOf(tasks)
        
        // When
        val result = searchTasksUseCase("").first()
        
        // Then
        assertEquals(tasks, result)
    }
}
```

### Testing Complex Business Logic

```kotlin
class CalculateProductivityMetricsUseCaseTest {
    // Mock dependencies
    private val taskRepository = mockk<TaskRepository>()
    private val activityRepository = mockk<ActivityRepository>()
    
    // System under test
    private lateinit var calculateProductivityMetricsUseCase: CalculateProductivityMetricsUseCase
    
    @Before
    fun setup() {
        calculateProductivityMetricsUseCase = CalculateProductivityMetricsUseCase(
            taskRepository,
            activityRepository
        )
    }
    
    @Test
    fun `calculate metrics with completed tasks and activities`() = runTest {
        // Given - Some completed and incomplete tasks and activities
        val tasks = listOf(
            Task("1", "Task 1", status = TaskStatus.COMPLETED),
            Task("2", "Task 2", status = TaskStatus.COMPLETED),
            Task("3", "Task 3", status = TaskStatus.IN_PROGRESS),
            Task("4", "Task 4", status = TaskStatus.TODO)
        )
        
        val activities = listOf(
            Activity("1", "Activity 1", isCompleted = true),
            Activity("2", "Activity 2", isCompleted = true),
            Activity("3", "Activity 3", isCompleted = true),
            Activity("4", "Activity 4", isCompleted = false)
        )
        
        coEvery { taskRepository.getTasksInPeriod(any(), any()) } returns tasks
        coEvery { activityRepository.getActivitiesInPeriod(any(), any()) } returns activities
        
        // When
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        val result = calculateProductivityMetricsUseCase(startDate, endDate)
        
        // Then - Verify metrics calculation
        assertEquals(0.5, result.taskCompletionRate, 0.01) // 2/4 tasks completed
        assertEquals(0.75, result.activityCompletionRate, 0.01) // 3/4 activities completed
        assertEquals(0.625, result.overallProductivity, 0.01) // Average: (0.5 + 0.75) / 2
    }
    
    @Test
    fun `calculate metrics with no tasks or activities`() = runTest {
        // Given - No tasks or activities
        coEvery { taskRepository.getTasksInPeriod(any(), any()) } returns emptyList()
        coEvery { activityRepository.getActivitiesInPeriod(any(), any()) } returns emptyList()
        
        // When
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        val result = calculateProductivityMetricsUseCase(startDate, endDate)
        
        // Then - Metrics should be zeros
        assertEquals(0.0, result.taskCompletionRate, 0.01)
        assertEquals(0.0, result.activityCompletionRate, 0.01)
        assertEquals(0.0, result.overallProductivity, 0.01)
    }
    
    @Test
    fun `validate date range`() = runTest {
        // Given - Invalid date range (end before start)
        val endDate = LocalDate.now().minusDays(14)
        val startDate = LocalDate.now().minusDays(7)
        
        // When & Then - Should throw exception
        val exception = assertThrows<IllegalArgumentException> {
            calculateProductivityMetricsUseCase(startDate, endDate)
        }
        
        assertEquals("End date must be after start date", exception.message)
        
        // Verify repositories not called with invalid dates
        coVerify(exactly = 0) { taskRepository.getTasksInPeriod(any(), any()) }
        coVerify(exactly = 0) { activityRepository.getActivitiesInPeriod(any(), any()) }
    }
}
```

## Testing Use Cases with Multiple Dependencies

When testing use cases that depend on multiple repositories or other use cases:

```kotlin
class SyncUserDataUseCaseTest {
    // Mock dependencies
    private val userRepository = mockk<UserRepository>()
    private val taskRepository = mockk<TaskRepository>()
    private val projectRepository = mockk<ProjectRepository>()
    
    // System under test
    private lateinit var syncUserDataUseCase: SyncUserDataUseCase
    
    @Before
    fun setup() {
        syncUserDataUseCase = SyncUserDataUseCase(
            userRepository,
            taskRepository,
            projectRepository
        )
    }
    
    @Test
    fun `sync calls all repositories for logged in user`() = runTest {
        // Given
        val userId = "user-1"
        val user = User(userId, "Test User")
        coEvery { userRepository.getCurrentUser() } returns user
        coEvery { taskRepository.syncTasks(userId) } returns Result.success(Unit)
        coEvery { projectRepository.syncProjects(userId) } returns Result.success(Unit)
        
        // When
        val result = syncUserDataUseCase()
        
        // Then
        coVerify { userRepository.getCurrentUser() }
        coVerify { taskRepository.syncTasks(userId) }
        coVerify { projectRepository.syncProjects(userId) }
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `sync fails when user not logged in`() = runTest {
        // Given
        coEvery { userRepository.getCurrentUser() } returns null
        
        // When
        val result = syncUserDataUseCase()
        
        // Then
        coVerify { userRepository.getCurrentUser() }
        coVerify(exactly = 0) { taskRepository.syncTasks(any()) }
        coVerify(exactly = 0) { projectRepository.syncProjects(any()) }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotLoggedInException)
    }
    
    @Test
    fun `sync fails when task sync fails`() = runTest {
        // Given
        val userId = "user-1"
        val user = User(userId, "Test User")
        val exception = IOException("Network error")
        
        coEvery { userRepository.getCurrentUser() } returns user
        coEvery { taskRepository.syncTasks(userId) } returns Result.failure(exception)
        
        // When
        val result = syncUserDataUseCase()
        
        // Then
        coVerify { userRepository.getCurrentUser() }
        coVerify { taskRepository.syncTasks(userId) }
        coVerify(exactly = 0) { projectRepository.syncProjects(any()) }
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
```

## Fake Dependencies for Testing

Creating fake implementations of repositories for more complex testing scenarios:

```kotlin
// A fake repository for testing
class FakeTaskRepository : TaskRepository {
    private val tasks = mutableListOf<Task>()
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    
    override fun getTasks(): Flow<List<Task>> = tasksFlow
    
    override suspend fun getTaskById(id: String): Result<Task> {
        val task = tasks.find { it.id == id }
        return if (task != null) {
            Result.success(task)
        } else {
            Result.failure(TaskNotFoundException(id))
        }
    }
    
    override suspend fun createTask(task: Task): Result<Task> {
        tasks.add(task)
        updateFlow()
        return Result.success(task)
    }
    
    override suspend fun updateTaskStatus(id: String, status: TaskStatus): Result<Task> {
        val taskIndex = tasks.indexOfFirst { it.id == id }
        if (taskIndex == -1) {
            return Result.failure(TaskNotFoundException(id))
        }
        
        val updated = tasks[taskIndex].copy(status = status)
        tasks[taskIndex] = updated
        updateFlow()
        
        return Result.success(updated)
    }
    
    // Test helper methods
    fun addTasks(newTasks: List<Task>) {
        tasks.addAll(newTasks)
        updateFlow()
    }
    
    private fun updateFlow() {
        tasksFlow.value = tasks.toList()
    }
}

// Using the fake in tests
class TaskOperationsUseCaseTest {
    // Create a fake repository
    private val fakeTaskRepository = FakeTaskRepository()
    
    // System under test
    private lateinit var getTasksUseCase: GetTasksUseCase
    private lateinit var updateTaskStatusUseCase: UpdateTaskStatusUseCase
    
    @Before
    fun setup() {
        getTasksUseCase = GetTasksUseCase(fakeTaskRepository)
        updateTaskStatusUseCase = UpdateTaskStatusUseCase(fakeTaskRepository)
        
        // Seed the fake repository with test data
        fakeTaskRepository.addTasks(
            listOf(
                Task("1", "Task 1", status = TaskStatus.TODO),
                Task("2", "Task 2", status = TaskStatus.IN_PROGRESS)
            )
        )
    }
    
    @Test
    fun `update task status affects subsequent task fetch`() = runTest {
        // Given - Initial state
        val tasks = getTasksUseCase().first()
        assertEquals(TaskStatus.TODO, tasks.first { it.id == "1" }.status)
        
        // When - Update a task status
        updateTaskStatusUseCase("1", TaskStatus.COMPLETED)
        
        // Then - The updated task should be reflected in subsequent fetch
        val updatedTasks = getTasksUseCase().first()
        assertEquals(TaskStatus.COMPLETED, updatedTasks.first { it.id == "1" }.status)
    }
}
```

## Best Practices for Domain Layer Testing

1. **Test business rules thoroughly**: Cover all aspects of business logic
2. **Mock dependencies**: Use mocks/fakes for repositories and other dependencies
3. **Test error conditions**: Verify error handling works correctly
4. **Test parameter validation**: Verify input validation is working
5. **Use descriptive test names**: Make test purposes clear
6. **Keep tests focused**: Test one behavior per test
7. **Test boundary conditions**: Verify behavior at the edges
8. **Avoid testing implementation details**: Focus on observable behavior
9. **Use test helpers**: Create helpers for common test operations
10. **Maintain high test coverage**: Aim for 80-90% coverage of the domain layer

## Testing with Hilt

For integration tests using Hilt's dependency injection:

```kotlin
@HiltAndroidTest
class TaskUseCasesIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    // Injected use cases
    @Inject
    lateinit var getTasksUseCase: GetTasksUseCase
    
    @Inject
    lateinit var updateTaskStatusUseCase: UpdateTaskStatusUseCase
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun testTaskStatusUpdate() = runTest {
        // Assuming we have test data in the repository
        val initialTasks = getTasksUseCase().first()
        val taskToUpdate = initialTasks.first()
        
        // Update status
        val newStatus = if (taskToUpdate.status == TaskStatus.COMPLETED) {
            TaskStatus.IN_PROGRESS
        } else {
            TaskStatus.COMPLETED
        }
        
        updateTaskStatusUseCase(taskToUpdate.id, newStatus)
        
        // Verify update
        val updatedTasks = getTasksUseCase().first()
        val updatedTask = updatedTasks.first { it.id == taskToUpdate.id }
        assertEquals(newStatus, updatedTask.status)
    }
}

// Test module to provide test dependencies
@Module
@InstallIn(SingletonComponent::class)
abstract class TestRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        fakeRepository: FakeTaskRepository
    ): TaskRepository
}
```

## Resources

- [Testing coroutines](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test)
- [Testing with Hilt](https://developer.android.com/training/dependency-injection/hilt-testing)
- [Flow testing with Turbine](https://github.com/cashapp/turbine)
- [Clean Architecture principles](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
