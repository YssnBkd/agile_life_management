# Integration Testing

## Overview

Integration testing verifies that different components of your application work correctly together. Unlike unit tests that focus on isolated components, integration tests examine the interactions between multiple parts of your system.

In Android architecture, integration tests typically involve:
- Testing multiple layers together (e.g., ViewModel + Use Case + Repository)
- Testing components with their real dependencies rather than mocks
- Verifying end-to-end flows across architectural boundaries

## Benefits of Integration Testing

- **Validates architectural interactions**: Ensures different layers work together properly
- **Catches interface mismatches**: Identifies issues between components that unit tests might miss
- **Tests real-world behavior**: Provides confidence that the system works in real scenarios
- **Validates asynchronous flows**: Tests complete asynchronous processes across components
- **Reduces over-mocking**: Avoids issues caused by incorrect mock behavior

## Types of Integration Tests

### Cross-Layer Integration Tests

These tests focus on validating that multiple layers of the architecture work together correctly:

- **ViewModel + Use Case + Repository**: Testing the entire feature flow
- **Repository + Data Sources**: Testing data operations across local and remote sources
- **Use Case + Multiple Repositories**: Testing domain logic that coordinates multiple data sources

### End-to-End Flow Tests

These tests validate complete user scenarios from UI interaction to data persistence:

- **UI + ViewModel + Use Case + Repository + Database**: Testing full feature flows
- **Service + Repository + Database**: Testing background operations
- **Worker + Repository + Network**: Testing scheduled tasks

## Testing Tools for Integration Tests

- **JUnit**: Base testing framework
- **Hilt Testing**: For dependency injection in tests
- **Coroutines Test**: For testing asynchronous flows
- **Room In-Memory Database**: For database testing
- **Turbine**: For testing Flow emissions across components
- **MockWebServer**: For simulating backend responses

## Cross-Layer Integration Test Examples

### Testing ViewModel with Real Use Cases

```kotlin
@HiltAndroidTest
class TaskViewModelIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    // Inject real use cases (not mocks)
    @Inject
    lateinit var getTasksUseCase: GetTasksUseCase
    
    @Inject
    lateinit var updateTaskUseCase: UpdateTaskUseCase
    
    // Inject test repositories
    @Inject
    lateinit var taskRepository: TaskRepository
    
    // System under test
    private lateinit var viewModel: TaskViewModel
    
    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = TaskViewModel(getTasksUseCase, updateTaskUseCase)
    }
    
    @Test
    fun taskUpdatesAreReflectedInUIState() = runTest {
        // Precondition: Seed the repository with test data
        val task = Task("1", "Integration Test", status = TaskStatus.TODO)
        taskRepository.createTask(task)
        
        // Use real view model to perform an action
        viewModel.toggleTaskCompleted(task.id, true)
        
        // Verify that the UI state is updated correctly
        val tasks = viewModel.uiState.value.tasks
        val updatedTask = tasks.find { it.id == task.id }
        
        assertNotNull(updatedTask)
        assertEquals(TaskStatus.COMPLETED, updatedTask?.status)
    }
}
```

### Testing Repository with Real Data Sources

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskRepositoryDataSourceIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var localDataSource: TaskLocalDataSource
    private lateinit var mockWebServer: MockWebServer
    private lateinit var httpClient: HttpClient
    private lateinit var remoteDataSource: TaskRemoteDataSource
    
    // System under test
    private lateinit var repository: TaskRepositoryImpl
    
    @Before
    fun setup() {
        // Set up database and local data source
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        taskDao = database.taskDao()
        localDataSource = TaskLocalDataSourceImpl(taskDao)
        
        // Set up mock server and remote data source
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val baseUrl = mockWebServer.url("/").toString()
        
        httpClient = HttpClient(OkHttp) {
            engine {
                preconfigured = OkHttpClient.Builder().build()
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            defaultRequest {
                url(baseUrl)
            }
        }
        
        remoteDataSource = TaskRemoteDataSourceImpl(httpClient)
        
        // Create repository with real dependencies
        repository = TaskRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            ioDispatcher = Dispatchers.IO
        )
    }
    
    @After
    fun tearDown() {
        database.close()
        mockWebServer.shutdown()
    }
    
    @Test
    fun createTaskUpdatesLocalAndRemote() = runTest {
        // Given
        val task = Task("1", "Integration Test", status = TaskStatus.TODO)
        
        // Mock remote response
        val mockResponse = """
            {"id": "1", "title": "Integration Test", "status": 0}
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When
        val result = repository.createTask(task)
        
        // Then
        // 1. Check the result
        assertTrue(result.isSuccess)
        
        // 2. Verify local storage was updated
        val localTask = taskDao.getTaskById(task.id)
        assertNotNull(localTask)
        assertEquals("Integration Test", localTask?.title)
        
        // 3. Verify remote request was made
        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertTrue(request.path?.contains("/tasks") == true)
    }
    
    @Test
    fun offlineFirstStrategyWorksWhenRemoteFails() = runTest {
        // Given
        val task = Task("1", "Offline Test", status = TaskStatus.TODO)
        
        // Configure remote to fail
        mockWebServer.enqueue(
            MockResponse().setResponseCode(500)
        )
        
        // When
        val result = repository.createTask(task)
        
        // Then
        // Local operation should succeed even if remote fails
        assertTrue(result.isSuccess)
        
        // Verify local storage was updated
        val localTask = taskDao.getTaskById(task.id)
        assertNotNull(localTask)
        assertEquals("Offline Test", localTask?.title)
    }
    
    @Test
    fun repositoryPropagatesLocalChanges() = runTest {
        // Given - Create a task in the repository
        val task = Task("1", "Flow Test", status = TaskStatus.TODO)
        repository.createTask(task)
        
        // Set up collector to test flow emissions
        val emissions = mutableListOf<List<Task>>()
        val job = launch {
            repository.getTasks().collect { tasks ->
                emissions.add(tasks)
            }
        }
        
        // Wait for initial emission
        yield()
        
        // When - Update task
        repository.updateTaskStatus("1", TaskStatus.IN_PROGRESS)
        
        // Wait for update to propagate
        yield()
        
        // Then
        job.cancel()
        
        // Should have at least 2 emissions: initial and after update
        assertTrue(emissions.size >= 2)
        
        // Last emission should contain updated task
        val lastEmission = emissions.last()
        val updatedTask = lastEmission.find { it.id == "1" }
        assertNotNull(updatedTask)
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask?.status)
    }
}
```

## End-to-End Flow Testing

### Testing Complete Feature Flow

```kotlin
@HiltAndroidTest
class TaskFeatureEndToEndTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var taskRepository: TaskRepository
    
    @Inject
    lateinit var database: AppDatabase
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @After
    fun tearDown() {
        runBlocking {
            // Clean up database
            database.clearAllTables()
        }
    }
    
    @Test
    fun completeTaskLifecycleFlow() = runTest {
        // 1. Create a task
        val taskTitle = "E2E Test Task"
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = taskTitle,
            status = TaskStatus.TODO
        )
        
        val createResult = taskRepository.createTask(task)
        assertTrue(createResult.isSuccess)
        
        // 2. Retrieve the task
        val tasks = taskRepository.getTasks().first()
        val retrievedTask = tasks.find { it.title == taskTitle }
        assertNotNull(retrievedTask)
        
        // 3. Update the task status
        val updateResult = taskRepository.updateTaskStatus(
            retrievedTask!!.id,
            TaskStatus.IN_PROGRESS
        )
        assertTrue(updateResult.isSuccess)
        
        // 4. Verify update
        val updatedTasks = taskRepository.getTasks().first()
        val updatedTask = updatedTasks.find { it.id == retrievedTask.id }
        assertNotNull(updatedTask)
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask?.status)
        
        // 5. Delete the task
        val deleteResult = taskRepository.deleteTask(retrievedTask.id)
        assertTrue(deleteResult.isSuccess)
        
        // 6. Verify deletion
        val finalTasks = taskRepository.getTasks().first()
        assertNull(finalTasks.find { it.id == retrievedTask.id })
    }
}
```

## UI End-to-End Testing

For complete end-to-end tests including UI interactions:

```kotlin
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TasksScreenEndToEndTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Inject
    lateinit var taskRepository: TaskRepository
    
    @Before
    fun setup() {
        hiltRule.inject()
        
        // Seed database with test data
        runBlocking {
            taskRepository.createTask(Task("1", "E2E UI Test Task", status = TaskStatus.TODO))
        }
    }
    
    @Test
    fun taskCompletionWorkflow() {
        // Navigate to tasks screen (if needed)
        // ...
        
        // Find and verify task is displayed
        composeTestRule.onNodeWithText("E2E UI Test Task").assertIsDisplayed()
        
        // Check the checkbox to mark task as completed
        composeTestRule.onNode(
            hasTestTag("task_checkbox") and 
            hasAnyAncestor(hasText("E2E UI Test Task"))
        ).performClick()
        
        // Verify task shows as completed (checks the implementation details of how completion is displayed)
        composeTestRule.onNode(
            hasTestTag("task_completed_indicator") and 
            hasAnyAncestor(hasText("E2E UI Test Task"))
        ).assertIsDisplayed()
        
        // Verify in the database that the task was updated
        runBlocking {
            val tasks = taskRepository.getTasks().first()
            val task = tasks.find { it.title == "E2E UI Test Task" }
            assertNotNull(task)
            assertEquals(TaskStatus.COMPLETED, task?.status)
        }
    }
}
```

## Testing with Hilt in Integration Tests

### Test Modules for Integration Testing

```kotlin
// Provide test doubles for integration testing
@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    @Provides
    @Singleton
    fun provideInMemoryDb(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }
    
    @Provides
    @Singleton
    fun provideMockWebServer(): MockWebServer {
        val server = MockWebServer()
        server.start()
        return server
    }
    
    @Provides
    @Singleton
    fun provideHttpClient(mockWebServer: MockWebServer): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                preconfigured = OkHttpClient.Builder().build()
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            defaultRequest {
                url(mockWebServer.url("/").toString())
            }
        }
    }
    
    // Provide test dispatchers
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = StandardTestDispatcher()
    
    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = StandardTestDispatcher()
    
    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = StandardTestDispatcher()
}
```

### Test Application for Hilt

```kotlin
// Custom test application for Hilt
@HiltAndroidApp
class TestApplication : Application()

// Custom test runner using custom application
class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}

// Update app/build.gradle
android {
    defaultConfig {
        // ...
        testInstrumentationRunner "com.example.agilelifemanagement.CustomTestRunner"
    }
}
```

## Handling Asynchronous Operations in Integration Tests

```kotlin
@Test
fun taskSynchronizationWithBackground() = runTest {
    // Given
    val task = Task("1", "Sync Test", status = TaskStatus.TODO)
    repository.createTask(task)
    
    // Mock remote response for sync
    mockWebServer.enqueue(
        MockResponse()
            .setResponseCode(200)
            .setBody("""{"result": "success"}""")
            .addHeader("Content-Type", "application/json")
    )
    
    // When - Trigger background sync
    repository.syncPendingChanges()
    
    // Advance virtual time to let background coroutines complete
    testScheduler.advanceUntilIdle()
    
    // Then
    // Verify sync request was made
    val request = mockWebServer.takeRequest()
    assertEquals("POST", request.method)
    assertTrue(request.path?.contains("/sync") == true)
    
    // Verify sync status was updated in database
    val syncStatus = database.syncStatusDao().getSyncStatusForTask("1")
    assertNotNull(syncStatus)
    assertTrue(syncStatus?.synced == true)
}
```

## Best Practices for Integration Testing

1. **Focus on critical paths**: Test the most important user and system flows
2. **Minimize test doubles**: Use real implementations when possible
3. **Use in-memory databases**: For faster, reliable testing
4. **Control external systems**: Use MockWebServer for network testing
5. **Test resilience**: Test offline operation and error handling
6. **Control coroutine execution**: Use TestDispatcher to control asynchronous execution
7. **Clean up resources**: Properly close databases, servers, and other resources
8. **Setup test data clearly**: Make test data setup explicit and relevant
9. **Test complete flows**: Test end-to-end workflows, not just individual operations
10. **Keep tests deterministic**: Avoid flaky tests by controlling all external factors

## Common Challenges and Solutions

### Handling Background Work

Use test dispatchers and virtual time control:

```kotlin
@Test
fun backgroundWorkCompletes() = runTest {
    // Configure repository with test dispatcher
    val testDispatcher = StandardTestDispatcher(testScheduler)
    repository = TaskRepositoryImpl(
        localDataSource = localDataSource,
        remoteDataSource = remoteDataSource,
        ioDispatcher = testDispatcher
    )
    
    // Perform operation that triggers background work
    repository.refreshTasks()
    
    // Advance virtual time to let background work complete
    testScheduler.advanceUntilIdle()
    
    // Verify work completed
    val syncStatus = database.syncStatusDao().getLatestSyncStatus()
    assertNotNull(syncStatus)
    assertEquals(SyncStatus.COMPLETED, syncStatus?.status)
}
```

### Testing Flow Collection

Use Turbine to simplify Flow testing:

```kotlin
@Test
fun repositoryEmitsUpdates() = runTest {
    // Given
    val task = Task("1", "Flow Test", status = TaskStatus.TODO)
    repository.createTask(task)
    
    // When & Then
    repository.getTasks().test {
        // Initial emission
        val initialTasks = awaitItem()
        assertTrue(initialTasks.any { it.id == "1" })
        
        // Update task
        repository.updateTaskStatus("1", TaskStatus.IN_PROGRESS)
        
        // Next emission should contain updated task
        val updatedTasks = awaitItem()
        val updatedTask = updatedTasks.find { it.id == "1" }
        assertNotNull(updatedTask)
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask?.status)
        
        cancelAndIgnoreRemainingEvents()
    }
}
```

### Handling Dependencies Between Tests

Ensure proper isolation between tests:

```kotlin
class IsolatedIntegrationTests {
    @Before
    fun setup() {
        // Create fresh dependencies for each test
        database = createInMemoryDatabase()
        server = MockWebServer()
        server.start()
        // ...
    }
    
    @After
    fun tearDown() {
        // Clean up after each test
        database.close()
        server.shutdown()
    }
    
    @Test
    fun firstTest() {
        // This test has fresh dependencies
        // ...
    }
    
    @Test
    fun secondTest() {
        // This test also has fresh dependencies
        // ...
    }
}
```

## Resources

- [Testing Coroutines](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test)
- [Testing with Hilt](https://developer.android.com/training/dependency-injection/hilt-testing)
- [Room Database Testing](https://developer.android.com/training/data-storage/room/testing-db)
- [Compose UI Testing](https://developer.android.com/jetpack/compose/testing)
- [Turbine for Flow testing](https://github.com/cashapp/turbine)
- [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
