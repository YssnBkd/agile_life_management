# Data Layer Testing

## Overview

The data layer is responsible for storing, retrieving, and manipulating application data. Proper testing of this layer ensures that:

1. Repositories correctly implement business logic
2. Data sources interact properly with their underlying data stores
3. Data transformations between layers are accurate
4. Error handling is robust and consistent

Since the data layer is the foundation of your application, thorough testing here provides confidence in the entire application's reliability.

## Testing Repositories

Repositories are the central component of the data layer and should be tested extensively.

### What to Test in Repositories

- **Data retrieval**: Verify data is correctly retrieved from data sources
- **Data manipulation**: Ensure data is properly created, updated, and deleted
- **Caching logic**: Test that caching strategies work as expected
- **Error handling**: Verify errors are properly propagated or handled
- **Business logic**: Test any business rules implemented in repositories
- **Source coordination**: Ensure proper coordination between local and remote sources

### Repository Test Example

```kotlin
class TaskRepositoryImplTest {
    // Mock dependencies
    private val localDataSource = mockk<TaskLocalDataSource>()
    private val remoteDataSource = mockk<TaskRemoteDataSource>()
    private val testDispatcher = StandardTestDispatcher()
    
    // System under test
    private lateinit var repository: TaskRepositoryImpl
    
    @Before
    fun setup() {
        repository = TaskRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            ioDispatcher = testDispatcher
        )
    }
    
    @Test
    fun `getTasks returns tasks from local data source`() = runTest {
        // Given
        val localTasks = listOf(
            TaskEntity("1", "Task 1", status = 1),
            TaskEntity("2", "Task 2", status = 2)
        )
        val expectedDomainTasks = localTasks.map { it.toDomain() }
        
        every { localDataSource.observeTasks() } returns flowOf(localTasks)
        
        // When
        val result = repository.getTasks().first()
        
        // Then
        verify { localDataSource.observeTasks() }
        assertEquals(expectedDomainTasks, result)
    }
    
    @Test
    fun `createTask updates local first then syncs with remote`() = runTest {
        // Given
        val task = Task("1", "Task 1", status = TaskStatus.TODO)
        val taskEntity = task.toEntity()
        
        coEvery { localDataSource.insertTask(taskEntity) } returns 1L
        coEvery { remoteDataSource.createTask(any()) } returns RemoteTask("1", "Task 1", 0)
        
        // When
        val result = repository.createTask(task)
        
        // Then
        coVerify { localDataSource.insertTask(taskEntity) }
        coVerify { remoteDataSource.createTask(any()) }
        assertTrue(result.isSuccess)
        assertEquals(task, result.getOrNull())
    }
    
    @Test
    fun `updateTaskStatus handles local error`() = runTest {
        // Given
        val taskId = "1"
        val status = TaskStatus.COMPLETED
        val exception = IllegalStateException("Database error")
        
        coEvery { 
            localDataSource.updateTaskStatus(taskId, status.toInt()) 
        } throws exception
        
        // When
        val result = repository.updateTaskStatus(taskId, status)
        
        // Then
        coVerify { localDataSource.updateTaskStatus(taskId, status.toInt()) }
        coVerify(exactly = 0) { remoteDataSource.updateTaskStatus(any(), any()) }
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
    
    @Test
    fun `updateTaskStatus handles remote error gracefully`() = runTest {
        // Given
        val taskId = "1"
        val status = TaskStatus.COMPLETED
        val taskEntity = TaskEntity(taskId, "Task 1", status = status.toInt())
        val domainTask = taskEntity.toDomain()
        
        coEvery { 
            localDataSource.updateTaskStatus(taskId, status.toInt()) 
        } returns taskEntity
        
        coEvery { 
            remoteDataSource.updateTaskStatus(any(), any()) 
        } throws IOException("Network error")
        
        // When
        val result = repository.updateTaskStatus(taskId, status)
        testScheduler.advanceUntilIdle() // Process the background coroutines
        
        // Then
        coVerify { localDataSource.updateTaskStatus(taskId, status.toInt()) }
        coVerify { remoteDataSource.updateTaskStatus(any(), any()) }
        
        // Local update should succeed even if remote fails
        assertTrue(result.isSuccess)
        assertEquals(domainTask, result.getOrNull())
    }
}
```

## Testing Data Sources

Data sources interact directly with the data stores (database, network, etc.) and should be tested accordingly.

### Local Data Source Testing

Use in-memory Room database for testing local data sources:

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskLocalDataSourceImplTest {
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var localDataSource: TaskLocalDataSourceImpl
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        taskDao = database.taskDao()
        localDataSource = TaskLocalDataSourceImpl(taskDao)
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndObserveTasks() = runTest {
        // Given
        val task1 = TaskEntity("1", "Task 1", status = 1)
        val task2 = TaskEntity("2", "Task 2", status = 2)
        
        // When
        localDataSource.insertTask(task1)
        localDataSource.insertTask(task2)
        
        // Then
        val tasks = localDataSource.observeTasks().first()
        assertEquals(2, tasks.size)
        assertTrue(tasks.contains(task1))
        assertTrue(tasks.contains(task2))
    }
    
    @Test
    fun updateTaskStatus() = runTest {
        // Given
        val task = TaskEntity("1", "Task 1", status = 1)
        localDataSource.insertTask(task)
        
        // When
        val updatedTask = localDataSource.updateTaskStatus("1", 2)
        
        // Then
        assertEquals(2, updatedTask.status)
        assertEquals("Task 1", updatedTask.title)
        
        // Verify in database
        val loadedTask = taskDao.getTaskById("1")
        assertEquals(2, loadedTask?.status)
    }
    
    @Test
    fun getTaskById_nonExistentId_returnsNull() = runTest {
        // When
        val result = localDataSource.getTaskById("non-existent")
        
        // Then
        assertNull(result)
    }
}
```

### Remote Data Source Testing

Use MockWebServer for testing remote data sources:

```kotlin
class TaskRemoteDataSourceImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var httpClient: HttpClient
    private lateinit var remoteDataSource: TaskRemoteDataSourceImpl
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
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
            install(DefaultRequest) {
                url(mockWebServer.url("/").toString())
            }
        }
        
        remoteDataSource = TaskRemoteDataSourceImpl(httpClient)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `getTasks returns tasks when successful`() = runTest {
        // Given
        val mockResponse = """
            [
                {"id": "1", "title": "Task 1", "status": 0},
                {"id": "2", "title": "Task 2", "status": 1}
            ]
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When
        val tasks = remoteDataSource.getTasks()
        
        // Then
        assertEquals(2, tasks.size)
        assertEquals("1", tasks[0].id)
        assertEquals("Task 1", tasks[0].title)
        assertEquals(0, tasks[0].status)
        
        // Verify request
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/tasks", request.path)
    }
    
    @Test(expected = IOException::class)
    fun `getTasks throws exception when server error`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse().setResponseCode(500)
        )
        
        // When
        remoteDataSource.getTasks()
        
        // Then: should throw exception
    }
}
```

## Integration Testing with Real Data Sources

Testing repositories with real (non-mock) data sources:

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskRepositoryIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var localDataSource: TaskLocalDataSource
    private lateinit var mockWebServer: MockWebServer
    private lateinit var httpClient: HttpClient
    private lateinit var remoteDataSource: TaskRemoteDataSource
    private lateinit var repository: TaskRepositoryImpl
    
    @Before
    fun setup() {
        // Set up database
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        taskDao = database.taskDao()
        localDataSource = TaskLocalDataSourceImpl(taskDao)
        
        // Set up mock server
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        httpClient = HttpClient(OkHttp) {
            // Configure client
        }
        
        remoteDataSource = TaskRemoteDataSourceImpl(httpClient)
        
        // Create repository with real data sources
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
    fun completeEndToEndCreateAndRetrieveTask() = runTest {
        // Given
        val taskToCreate = Task("1", "Integration Test", status = TaskStatus.TODO)
        
        // Mock server response
        val mockResponse = """
            {"id": "1", "title": "Integration Test", "status": 0}
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When - Create task
        val createResult = repository.createTask(taskToCreate)
        
        // Then - Task should be created successfully
        assertTrue(createResult.isSuccess)
        
        // When - Retrieve tasks
        val tasks = repository.getTasks().first()
        
        // Then - Created task should be in the list
        assertEquals(1, tasks.size)
        assertEquals("Integration Test", tasks[0].title)
        assertEquals(TaskStatus.TODO, tasks[0].status)
    }
}
```

## Testing Data Mapping

Test mapping between different data models:

```kotlin
class TaskMappingTest {
    @Test
    fun `map from entity to domain model`() {
        // Given
        val entity = TaskEntity(
            id = "1",
            title = "Test Task",
            description = "Description",
            status = 1,
            dueDate = 1621436400000 // 2021-05-19T10:00:00Z
        )
        
        // When
        val domain = entity.toDomain()
        
        // Then
        assertEquals("1", domain.id)
        assertEquals("Test Task", domain.title)
        assertEquals("Description", domain.description)
        assertEquals(TaskStatus.IN_PROGRESS, domain.status) // 1 maps to IN_PROGRESS
        assertEquals(LocalDateTime.of(2021, 5, 19, 10, 0, 0), domain.dueDate)
    }
    
    @Test
    fun `map from domain to entity model`() {
        // Given
        val domain = Task(
            id = "1",
            title = "Test Task",
            description = "Description",
            status = TaskStatus.COMPLETED,
            dueDate = LocalDateTime.of(2021, 5, 19, 10, 0, 0)
        )
        
        // When
        val entity = domain.toEntity()
        
        // Then
        assertEquals("1", entity.id)
        assertEquals("Test Task", entity.title)
        assertEquals("Description", entity.description)
        assertEquals(2, entity.status) // COMPLETED maps to 2
        assertEquals(1621436400000, entity.dueDate) // 2021-05-19T10:00:00Z
    }
    
    @Test
    fun `map from remote to domain model`() {
        // Given
        val remote = RemoteTask(
            id = "1",
            title = "Test Task",
            status = 0
        )
        
        // When
        val domain = remote.toDomain()
        
        // Then
        assertEquals("1", domain.id)
        assertEquals("Test Task", domain.title)
        assertEquals(TaskStatus.TODO, domain.status) // 0 maps to TODO
    }
}
```

## Testing with Hilt for Data Layer Components

```kotlin
@HiltAndroidTest
class HiltTaskRepositoryTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var taskRepository: TaskRepository
    
    @Before
    fun init() {
        hiltRule.inject()
    }
    
    @Test
    fun testTaskCreationAndRetrieval() = runTest {
        // Given
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = "Hilt Test Task",
            status = TaskStatus.TODO
        )
        
        // When
        val createResult = taskRepository.createTask(task)
        
        // Then
        assertTrue(createResult.isSuccess)
        
        // Verify retrieval
        val tasks = taskRepository.getTasks().first()
        assertTrue(tasks.any { it.title == "Hilt Test Task" })
    }
}

// Test modules for Hilt
@Module
@InstallIn(SingletonComponent::class)
object TestDatabaseModule {
    @Provides
    @Singleton
    fun provideInMemoryDb(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object TestNetworkModule {
    @Provides
    @Singleton
    fun provideMockHttpClient(): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    // Mock responses based on request
                    when (request.url.encodedPath) {
                        "/tasks" -> {
                            respond(
                                content = """[{"id":"1","title":"Mocked Task"}]""",
                                status = HttpStatusCode.OK,
                                headers = headersOf("Content-Type", "application/json")
                            )
                        }
                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
            install(ContentNegotiation) {
                json()
            }
        }
    }
}
```

## Testing Error Handling

```kotlin
@Test
fun `repository properly propagates network errors`() = runTest {
    // Given
    coEvery { remoteDataSource.getTasks() } throws IOException("Network error")
    
    // When
    val result = repository.refreshTasks()
    
    // Then
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is NetworkException)
}

@Test
fun `repository falls back to cache on network error`() = runTest {
    // Given
    val cachedTasks = listOf(
        TaskEntity("1", "Cached Task", status = 1)
    )
    every { localDataSource.observeTasks() } returns flowOf(cachedTasks)
    coEvery { remoteDataSource.getTasks() } throws IOException("Network error")
    
    // When
    val refreshResult = repository.refreshTasks()
    val tasks = repository.getTasks().first()
    
    // Then
    assertTrue(refreshResult.isFailure)
    assertEquals(1, tasks.size)
    assertEquals("Cached Task", tasks[0].title)
}
```

## Best Practices for Data Layer Testing

1. **Test offline-first behavior**: Verify local updates happen before remote
2. **Test synchronization logic**: Ensure data syncs correctly between sources
3. **Test error recovery**: Verify graceful handling of network/database errors
4. **Use appropriate test doubles**: Use fakes for complex behavior, mocks for simple verification
5. **Test real database operations**: Use in-memory Room database for true integration tests
6. **Test data mapping**: Verify correct transformation between different data models
7. **Test concurrency**: Verify thread-safety with concurrent operations
8. **Test error propagation**: Ensure errors are properly converted and propagated
9. **Test edge cases**: Test boundary conditions and unusual scenarios
10. **Maintain high test coverage**: Data layer should have 80-90% test coverage

## Resources

- [Room Testing Documentation](https://developer.android.com/training/data-storage/room/testing-db)
- [Ktor Client Testing](https://ktor.io/docs/http-client-testing.html)
- [Kotlin Coroutines Testing](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test)
- [Flow Testing with Turbine](https://github.com/cashapp/turbine)
- [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
- [Testing with Hilt](https://developer.android.com/training/dependency-injection/hilt-testing)
