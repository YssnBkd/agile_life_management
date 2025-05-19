# WorkManager Implementation

## Overview

WorkManager is a part of Android Jetpack that makes it easy to schedule deferrable, asynchronous tasks that are expected to run even if the app exits or the device restarts. It's the recommended solution for persistent work, and it's particularly useful for tasks like syncing data with a server, uploading logs, or periodic cleanup operations.

This guide covers the implementation of WorkManager in the context of the AgileLifeManagement project, focusing on background synchronization and scheduled tasks.

## Setting Up WorkManager

### Dependencies

Add the required dependencies to your app-level `build.gradle` file:

```kotlin
dependencies {
    // WorkManager with Kotlin support
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    
    // For Hilt integration with WorkManager
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    
    // For testing
    androidTestImplementation("androidx.work:work-testing:2.8.1")
}
```

### Basic Setup

Initialize WorkManager in your application class:

```kotlin
@HiltAndroidApp
class AgileLifeManagementApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
}
```

## Implementing Workers

### Basic Worker

Create a basic worker for simple background tasks:

```kotlin
class DataCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Perform cleanup operations
            // e.g., delete old cached files, etc.
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error during data cleanup")
            Result.retry()
        }
    }
}
```

### Hilt-Integrated Worker

Use Hilt to inject dependencies into your workers:

```kotlin
@HiltWorker
class SyncTasksWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val connectivityChecker: ConnectivityChecker
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        // Check if we have network connectivity
        if (!connectivityChecker.isNetworkAvailable()) {
            // No network, return retry result
            return Result.retry()
        }
        
        return try {
            // Sync tasks with remote server
            val syncResult = taskRepository.syncPendingTasks()
            
            if (syncResult.isSuccess) {
                // Return success result with number of synced tasks
                val syncedCount = syncResult.getOrNull() ?: 0
                val outputData = workDataOf("synced_count" to syncedCount)
                Result.success(outputData)
            } else {
                // Handle repository error
                val exception = syncResult.exceptionOrNull() 
                Timber.e(exception, "Repository error syncing tasks")
                
                if (exception is IOException) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing tasks")
            
            // Decide whether to retry based on the exception
            if (e is IOException) {
                // Network-related error, retry
                Result.retry()
            } else {
                // Other error, mark as failure
                Result.failure()
            }
        }
    }
}

// Connectivity checker
class ConnectivityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
```

### Worker with Input Data

Pass data to your workers:

```kotlin
@HiltWorker
class UpdateTaskStatusWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : CoroutineWorker(context, params) {
    
    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_STATUS = "status"
        
        fun createInputData(taskId: String, status: TaskStatus): Data {
            return workDataOf(
                KEY_TASK_ID to taskId,
                KEY_STATUS to status.ordinal
            )
        }
    }
    
    override suspend fun doWork(): Result {
        val taskId = inputData.getString(KEY_TASK_ID)
            ?: return Result.failure()
            
        val statusOrdinal = inputData.getInt(KEY_STATUS, -1)
        if (statusOrdinal == -1) {
            return Result.failure()
        }
        
        val status = TaskStatus.values()[statusOrdinal]
        
        return try {
            val result = updateTaskStatusUseCase(taskId, status)
            
            if (result.isSuccess) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating task status")
            Result.failure()
        }
    }
}
```

## Scheduling Work

### One-Time Work Requests

Schedule tasks that run once:

```kotlin
class TaskManager @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleTaskUpdate(taskId: String, status: TaskStatus) {
        val inputData = UpdateTaskStatusWorker.createInputData(taskId, status)
        
        val updateRequest = OneTimeWorkRequestBuilder<UpdateTaskStatusWorker>()
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
            
        workManager.enqueueUniqueWork(
            "update_task_$taskId",
            ExistingWorkPolicy.REPLACE,
            updateRequest
        )
    }
}
```

### Periodic Work Requests

Schedule tasks that run repeatedly:

```kotlin
class SyncManager @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedulePeriodicalSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncRequest = PeriodicWorkRequestBuilder<SyncTasksWorker>(
            15, TimeUnit.MINUTES, // Minimum interval is 15 minutes
            5, TimeUnit.MINUTES  // Flex interval
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

### Chained Work Requests

Schedule multiple workers that depend on each other:

```kotlin
class DataSyncManager @Inject constructor(
    private val workManager: WorkManager
) {
    fun performFullSync() {
        // First, sync tasks
        val syncTasksRequest = OneTimeWorkRequestBuilder<SyncTasksWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
            
        // Then, sync sprints
        val syncSprintsRequest = OneTimeWorkRequestBuilder<SyncSprintsWorker>()
            .build()
            
        // Finally, sync goals
        val syncGoalsRequest = OneTimeWorkRequestBuilder<SyncGoalsWorker>()
            .build()
            
        // Chain them together
        workManager
            .beginUniqueWork(
                "full_sync",
                ExistingWorkPolicy.REPLACE,
                syncTasksRequest
            )
            .then(syncSprintsRequest)
            .then(syncGoalsRequest)
            .enqueue()
    }
}
```

## Observing Work

### Observing Work Progress

Observe the state of work requests:

```kotlin
class SyncViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val syncManager: SyncManager
) : ViewModel() {
    
    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    fun startSync() {
        syncManager.performFullSync()
        observeSyncStatus()
    }
    
    private fun observeSyncStatus() {
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow("full_sync")
                .collect { workInfoList ->
                    // Find the last work info
                    val lastWorkInfo = workInfoList.lastOrNull()
                    
                    _syncState.value = when (lastWorkInfo?.state) {
                        WorkInfo.State.ENQUEUED -> SyncState.PENDING
                        WorkInfo.State.RUNNING -> SyncState.IN_PROGRESS
                        WorkInfo.State.SUCCEEDED -> SyncState.COMPLETED
                        WorkInfo.State.FAILED -> SyncState.FAILED
                        WorkInfo.State.BLOCKED -> SyncState.PENDING
                        WorkInfo.State.CANCELLED -> SyncState.IDLE
                        null -> SyncState.IDLE
                    }
                }
        }
    }
    
    enum class SyncState {
        IDLE, PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}
```

### Work Progress and Data

Provide progress updates from workers:

```kotlin
@HiltWorker
class DataImportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val importRepository: ImportRepository
) : CoroutineWorker(context, params) {
    
    companion object {
        const val PROGRESS = "progress"
        const val STATUS_MESSAGE = "status_message"
    }
    
    override suspend fun doWork(): Result {
        return try {
            // Get the file path from input data
            val filePath = inputData.getString("file_path") ?: return Result.failure()
            
            // Total items to import
            val totalItems = importRepository.countItemsInFile(filePath)
            var processedItems = 0
            
            // Process the items
            importRepository.importItems(filePath) { item ->
                processedItems++
                
                // Calculate progress percentage
                val progress = (processedItems * 100 / totalItems).toInt()
                
                // Set progress
                setProgress(workDataOf(
                    PROGRESS to progress,
                    STATUS_MESSAGE to "Importing item $processedItems of $totalItems"
                ))
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error during data import")
            Result.failure()
        }
    }
}

// In ViewModel or elsewhere
viewModelScope.launch {
    workManager.getWorkInfosForUniqueWorkFlow("data_import")
        .collect { workInfoList ->
            val workInfo = workInfoList.firstOrNull()
            
            workInfo?.let {
                if (it.state == WorkInfo.State.RUNNING) {
                    // Extract progress
                    val progress = it.progress.getInt(DataImportWorker.PROGRESS, 0)
                    val message = it.progress.getString(DataImportWorker.STATUS_MESSAGE) ?: ""
                    
                    _importProgress.value = progress
                    _importMessage.value = message
                }
            }
        }
}
```

## Integration with Repository Pattern

### Implementing Sync Logic in Repository

Implement sync logic that will be called by workers:

```kotlin
interface TaskRepository {
    // Other methods...
    
    suspend fun syncPendingTasks(): Result<Int>
    suspend fun markTasksForSync(taskIds: List<String>): Result<Unit>
}

class TaskRepositoryImpl @Inject constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {
    
    // Implementation of other methods...
    
    override suspend fun syncPendingTasks(): Result<Int> = withContext(ioDispatcher) {
        try {
            // Get all tasks marked for sync
            val tasksToSync = localDataSource.getTasksMarkedForSync()
            if (tasksToSync.isEmpty()) {
                return@withContext Result.success(0)
            }
            
            var syncedCount = 0
            
            // Sync each task
            tasksToSync.forEach { taskEntity ->
                try {
                    // Convert to DTO and sync
                    val result = remoteDataSource.updateTask(taskEntity.id, taskEntity.toDto())
                    
                    if (result.isSuccess) {
                        // Mark as synced
                        localDataSource.markTaskSynced(taskEntity.id)
                        syncedCount++
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing task ${taskEntity.id}")
                    // Continue with other tasks
                }
            }
            
            Result.success(syncedCount)
        } catch (e: Exception) {
            Timber.e(e, "Error during task sync")
            Result.failure(e)
        }
    }
    
    override suspend fun markTasksForSync(taskIds: List<String>): Result<Unit> = withContext(ioDispatcher) {
        try {
            localDataSource.markTasksForSync(taskIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error marking tasks for sync")
            Result.failure(e)
        }
    }
}
```

### Repository Methods for Scheduling Sync

Add methods to the repository to schedule sync operations:

```kotlin
interface TaskRepository {
    // Other methods...
    
    fun scheduleTaskSync(taskId: String)
    fun schedulePeriodicalSync()
}

class TaskRepositoryImpl @Inject constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val workManager: WorkManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {
    
    // Implementation of other methods...
    
    override fun scheduleTaskSync(taskId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val inputData = workDataOf("task_id" to taskId)
        
        val syncRequest = OneTimeWorkRequestBuilder<SyncTaskWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
            
        workManager.enqueueUniqueWork(
            "sync_task_$taskId",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
    
    override fun schedulePeriodicalSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncRequest = PeriodicWorkRequestBuilder<SyncTasksWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            "periodic_task_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
```

## Testing WorkManager

### Testing Workers

Test workers using the WorkManager test utilities:

```kotlin
@RunWith(AndroidJUnit4::class)
class SyncTasksWorkerTest {
    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var testDriver: WorkManagerTestInitHelper.TestDriver
    
    // Mock dependencies
    private val taskRepository = mockk<TaskRepository>()
    private val connectivityChecker = mockk<ConnectivityChecker>()
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
            
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
        testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
        
        // Set up WorkerFactory for Hilt dependencies
        val workerFactory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker? {
                return when (workerClassName) {
                    SyncTasksWorker::class.java.name -> 
                        SyncTasksWorker(
                            appContext,
                            workerParameters,
                            taskRepository,
                            connectivityChecker
                        )
                    else -> null
                }
            }
        }
        
        val config2 = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(context, config2)
    }
    
    @Test
    fun syncTasksWorker_syncSuccessful_returnsSuccess() {
        // Given
        every { connectivityChecker.isNetworkAvailable() } returns true
        coEvery { taskRepository.syncPendingTasks() } returns Result.success(5)
        
        // When - Create and enqueue the worker
        val request = OneTimeWorkRequestBuilder<SyncTasksWorker>()
            .build()
        
        // Enqueue and wait for execution
        val testWorkManager = WorkManager.getInstance(context)
        testWorkManager.enqueue(request).result.get()
        
        // Then - Process the worker
        testDriver.setAllConstraintsMet(request.id)
        
        // Get the WorkInfo and check the state
        val workInfo = testWorkManager.getWorkInfoById(request.id).get()
        assertEquals(WorkInfo.State.SUCCEEDED, workInfo.state)
        
        // Verify repository method was called
        coVerify { taskRepository.syncPendingTasks() }
    }
    
    @Test
    fun syncTasksWorker_noNetwork_returnsRetry() {
        // Given
        every { connectivityChecker.isNetworkAvailable() } returns false
        
        // When - Create and enqueue the worker
        val request = OneTimeWorkRequestBuilder<SyncTasksWorker>()
            .build()
        
        // Enqueue and wait for execution
        val testWorkManager = WorkManager.getInstance(context)
        testWorkManager.enqueue(request).result.get()
        
        // Then - Process the worker
        testDriver.setAllConstraintsMet(request.id)
        
        // Get the WorkInfo and check the state
        val workInfo = testWorkManager.getWorkInfoById(request.id).get()
        assertEquals(WorkInfo.State.ENQUEUED, workInfo.state)
        
        // Verify repository method was not called
        coVerify(exactly = 0) { taskRepository.syncPendingTasks() }
    }
}
```

### Testing Work Scheduling

Test that work is scheduled correctly:

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskRepositoryWorkSchedulingTest {
    private lateinit var context: Context
    private lateinit var workManager: TestWorkManager
    
    private lateinit var taskRepository: TaskRepositoryImpl
    private val localDataSource = mockk<TaskLocalDataSource>()
    private val remoteDataSource = mockk<TaskRemoteDataSource>()
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        
        // Initialize the test WorkManager
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context) as TestWorkManager
        
        // Create repository with test WorkManager
        taskRepository = TaskRepositoryImpl(
            localDataSource,
            remoteDataSource,
            workManager,
            Dispatchers.Unconfined
        )
    }
    
    @Test
    fun scheduleTaskSync_enqueuesUniqueWork() {
        // When
        taskRepository.scheduleTaskSync("task-123")
        
        // Then
        val workInfos = workManager.getWorkInfosForUniqueWork("sync_task_task-123").get()
        assertEquals(1, workInfos.size)
        
        val workInfo = workInfos[0]
        assertEquals(WorkInfo.State.ENQUEUED, workInfo.state)
        
        // Verify input data
        assertEquals("task-123", workInfo.inputData.getString("task_id"))
    }
    
    @Test
    fun schedulePeriodicalSync_enqueuesUniquePeriodicWork() {
        // When
        taskRepository.schedulePeriodicalSync()
        
        // Then
        val workInfos = workManager.getWorkInfosForUniqueWork("periodic_task_sync").get()
        assertEquals(1, workInfos.size)
        
        val workInfo = workInfos[0]
        assertEquals(WorkInfo.State.ENQUEUED, workInfo.state)
    }
}
```

## Dependency Injection with Hilt

Set up WorkManager with Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}

// HiltWorkerFactory module
@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerBindingModule {
    @Binds
    abstract fun bindWorkerFactory(
        workerFactory: AgileLifeManagementWorkerFactory
    ): WorkerFactory
}

// Create Assisted Factory interface for workers
interface SyncTasksWorkerFactory {
    fun create(context: Context, params: WorkerParameters): SyncTasksWorker
}
```

## Best Practices for WorkManager

1. **Use Constraints**: Apply appropriate constraints to ensure work runs under optimal conditions
2. **Handle Failures**: Implement proper error handling and retry logic
3. **Tag Work Requests**: Use tags to identify and manage groups of related work
4. **Observe Work State**: Monitor work state to update UI and provide feedback to users
5. **Test Workers**: Create comprehensive tests for your worker implementations
6. **Use Dependency Injection**: Leverage Hilt to inject dependencies into workers
7. **Chain Work**: Use work chaining for complex workflows with dependencies
8. **Consider Battery Impact**: Be mindful of battery usage and optimize work accordingly
9. **Respect System Doze**: Schedule work with consideration for Doze mode and battery optimizations
10. **Provide Progress Updates**: Show work progress to keep users informed

## Resources

- [WorkManager documentation](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Hilt integration with WorkManager](https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager)
- [Testing WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager/how-to/testing)
- [Advanced WorkManager usage](https://medium.com/androiddevelopers/workmanager-basics-beba51e94048)
