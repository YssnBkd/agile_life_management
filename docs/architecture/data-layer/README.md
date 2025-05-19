# Data Layer

## Overview

The data layer contains application data and business logic. Business logic defines how data is created, stored, and modified according to real-world business rules. The data layer serves as the source of truth for all application data.

This layer is designed to:
- Be independent of the UI layer
- Be reusable across multiple screens and components
- Handle data operations and business logic
- Support testing in isolation

## Data Layer Architecture

The data layer consists of two main components:

### 1. Repositories

Repositories are the core component of the data layer that:
- Expose data to the rest of the app
- Centralize changes to data
- Resolve conflicts between multiple data sources
- Abstract data sources from the rest of the app
- Contain business logic
- Serve as the Single Source of Truth (SSOT)

### 2. Data Sources

Each data source is responsible for working with a single source of data:
- **Local data sources**: Room database, preferences, files
- **Remote data sources**: Network APIs, cloud services
- **In-memory cache**: Temporary data storage

## Repository Implementation

### Repository Design

Each data type should have its own repository class with methods for:
- Continuous data streams (using Flow)
- One-shot operations (using suspend functions)

### Repository Implementation

A repository implementation should:
- Implement the repository interface
- Take data sources and utility classes as dependencies
- Use an offline-first strategy where appropriate
- Handle network connectivity changes
- Schedule background work when needed
- Use proper threading with coroutine dispatchers
    
### One-shot Operations

One-shot operations should:
- Use suspend functions for asynchronous behavior
- Perform local updates first for immediate UI feedback
- Sync with remote data sources when possible
- Handle errors appropriately
- Return meaningful results to callers

## Offline-First Approach

For optimal user experience, the data layer should implement an offline-first approach:

1. **Local Updates First**: Update the local database immediately for responsive UI
2. **Background Synchronization**: Schedule sync operations when connectivity is limited
3. **Conflict Resolution**: Handle conflicts between local and remote data
4. **Change Tracking**: Keep track of local changes that need to be synced

## Background Work and Synchronization

The data layer should handle background work efficiently:

1. **Scheduled Sync**: Use WorkManager to schedule sync operations with appropriate constraints
2. **Network Awareness**: Only perform network operations when connectivity is available
3. **Batched Operations**: Group multiple sync operations to reduce network usage
4. **Error Handling**: Implement retry mechanisms for failed operations
5. **Targeted Sync**: Allow syncing specific data items or performing full synchronization

## Naming Conventions

For clear and consistent code:
- Repository interfaces: {Entity}Repository (e.g., TaskRepository)
- Repository implementations: {Entity}RepositoryImpl (e.g., TaskRepositoryImpl)
- Local data sources: {Entity}LocalDataSource (e.g., TaskLocalDataSource)
- Remote data sources: {Entity}RemoteDataSource (e.g., TaskRemoteDataSource)

## Repository Patterns

### Single Source of Truth (SSOT)

In offline-first applications:
- The local database serves as the SSOT
- Data flows from the database to the UI
- Updates go to the database first, then sync to remote sources
- This ensures the app works regardless of network conditions

### Repository Layers

In complex apps, multiple repository layers may be needed:
1. **Feature repositories**: Handle specific use cases for features
2. **Core repositories**: Provide basic data operations on entities
3. **Off-device repositories**: Abstract remote operations

## Types of Data Operations

### UI-Oriented Operations

Operations triggered by UI interactions:
- Create, read, update, delete (CRUD)
- Search and filter
- Pagination
- Refresh data

### App-Oriented Operations

Operations triggered by the app itself:
- Background syncing
- Prefetching data
- Cache maintenance
- Periodic updates

### Business-Oriented Operations

Operations implementing business rules:
- Validation
- Transformations
- Calculations
- Access control

## Threading

Repositories should be main-safe and handle their own threading:
- Use appropriate threading abstractions to specify the dispatcher
- Specify the dispatcher for Flow operations
- Make repository methods safe to call from any thread
- Use appropriate dispatchers (IO for disk/network, Default for CPU-intensive work)

## Error Handling

### Result Pattern

Use Kotlin's Result class to handle success and failure cases, providing:
- Clear error semantics
- Type-safe error handling
- Consistent error reporting across the app

### Error Types

Consider defining different error types for different failure scenarios:
- Network errors
- Database errors
- Business rule violations
- Validation errors

## WorkManager Integration

Use WorkManager for background tasks that need to survive process death:
- Scheduled synchronization
- Data cleanup
- Periodic operations
- Chained tasks
                } catch (e: Exception) {
                    // Handle error, possibly queue for retry
                }
            }
        }
        
        Result.success(localTask)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Domain-Specific Errors

Create custom exceptions for specific error cases:

```kotlin
class TaskNotFoundException(taskId: String) : 
    Exception("Task with ID $taskId not found")

class InvalidTaskStateException(message: String) : 
    Exception(message)
```

## Common Tasks

### Making Network Requests

Use Ktor Client for network operations:

```kotlin
class TaskRemoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient
) : TaskRemoteDataSource {
    override suspend fun getTasks(): List<TaskDto> {
        return httpClient.get("$BASE_URL/tasks") {
            contentType(ContentType.Application.Json)
        }.body()
    }
}
```

### Implementing In-Memory Caching

For frequently accessed data that doesn't change often:

```kotlin
@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserPreferencesRepository {
    // In-memory cache
    private val cachedPreferences = AtomicReference<UserPreferences?>(null)
    
    override fun getUserPreferences(): Flow<UserPreferences> {
        return dataStore.data
            .catch { exception ->
    override suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }
}
```

### Scheduling Background Tasks

Using WorkManager:

```kotlin
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            taskRepository.syncPendingTasks()
            Result.success()
        } catch (e: Exception) {
            // Retry with backoff for transient errors
            Result.retry()
        }
    }
}

// Scheduling the work
class SyncManager @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedulePeriodicalSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
```

## Testing the Data Layer

### Unit Testing Repositories

```kotlin
class TaskRepositoryTest {
    // Mock dependencies
    private val localDataSource = mockk<TaskLocalDataSource>()
    private val remoteDataSource = mockk<TaskRemoteDataSource>()
    private val testDispatcher = TestCoroutineDispatcher()
    
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
        val tasks = listOf(Task("1", "Task 1"), Task("2", "Task 2"))
        every { localDataSource.observeTasks() } returns flowOf(tasks)
        
        // When
        val result = repository.getTasks().first()
        
        // Then
        assertEquals(tasks, result)
    }
}
```

### Integration Testing

Using in-memory Room database:

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        taskDao = database.taskDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetTask() = runTest {
        // Given
        val task = TaskEntity("1", "Task 1", "Description", TaskStatus.TODO)
        
        // When
        taskDao.insertTask(task)
        val loaded = taskDao.getTaskById("1")
        
        // Then
        assertEquals(task, loaded)
    }
}
```

## Resources

- [Data Layer documentation by Android Developers](https://developer.android.com/jetpack/guide/data-layer)
- [Room Database documentation](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines and Flow](https://developer.android.com/kotlin/flow)
- [WorkManager documentation](https://developer.android.com/topic/libraries/architecture/workmanager)
