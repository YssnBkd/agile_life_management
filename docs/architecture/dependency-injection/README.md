# Dependency Injection with Hilt

## Overview

Dependency Injection (DI) is a design pattern that allows classes to receive their dependencies from an external source rather than creating them internally. Hilt is the recommended DI solution for Android, built on top of Dagger to provide a simpler API specifically designed for Android.

## Benefits of Dependency Injection

- **Separation of concerns**: Classes focus on their core functionality, not on creating dependencies
- **Reusability**: Components can be reused in different contexts
- **Testability**: Dependencies can be easily mocked for testing
- **Flexibility**: Implementations can be swapped without changing consumer code
- **Boilerplate reduction**: Less code to instantiate and manage dependencies

## Hilt Setup

### Project Configuration

First, set up Hilt in your project:

```kotlin
// Project-level build.gradle
buildscript {
    dependencies {
        classpath "com.google.dagger:hilt-android-gradle-plugin:2.44"
    }
}

// App-level build.gradle
plugins {
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}

dependencies {
    implementation "com.google.dagger:hilt-android:2.44"
    kapt "com.google.dagger:hilt-android-compiler:2.44"
    
    // For instrumentation tests
    androidTestImplementation "com.google.dagger:hilt-android-testing:2.44"
    kaptAndroidTest "com.google.dagger:hilt-android-compiler:2.44"
    
    // For local unit tests
    testImplementation "com.google.dagger:hilt-android-testing:2.44"
    kaptTest "com.google.dagger:hilt-android-compiler:2.44"
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
```

### Application Setup

Create an Application class with the `@HiltAndroidApp` annotation:

```kotlin
@HiltAndroidApp
class AgileLifeManagementApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application-level initialization
    }
}
```

Update your manifest to use this application class:

```xml
<application
    android:name=".AgileLifeManagementApplication"
    ... >
    <!-- activities, services, etc. -->
</application>
```

## Hilt Components

Hilt provides components that follow the Android application structure:

| Component | Scope | Lifetime |
|-----------|-------|----------|
| `SingletonComponent` | `@Singleton` | Application lifetime |
| `ActivityRetainedComponent` | `@ActivityRetainedScoped` | Across configuration changes |
| `ViewModelComponent` | `@ViewModelScoped` | ViewModel lifetime |
| `ActivityComponent` | `@ActivityScoped` | Activity lifetime |
| `FragmentComponent` | `@FragmentScoped` | Fragment lifetime |
| `ViewComponent` | `@ViewScoped` | View lifetime |
| `ServiceComponent` | `@ServiceScoped` | Service lifetime |

## Injecting Dependencies

### Android Classes (Activities, Fragments, Views)

Use `@AndroidEntryPoint` to enable injection:

```kotlin
@AndroidEntryPoint
class TaskListActivity : AppCompatActivity() {
    // Inject ViewModel using by viewModels()
    private val viewModel: TaskViewModel by viewModels()
    
    // Field injection (less common with ViewModels)
    @Inject 
    lateinit var analyticsLogger: AnalyticsLogger
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use injected dependencies
    }
}

@AndroidEntryPoint
class TaskDetailFragment : Fragment() {
    private val viewModel: TaskDetailViewModel by viewModels()
    
    @Inject
    lateinit var taskFormatter: TaskFormatter
}
```

### ViewModels

Use `@HiltViewModel` for ViewModels:

```kotlin
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    // ViewModel implementation
}
```

### Non-Android Classes (Repositories, Use Cases, Etc.)

Use constructor injection for most cases:

```kotlin
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> = taskRepository.getTasks()
}

class TaskRepositoryImpl @Inject constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {
    // Repository implementation
}
```

## Hilt Modules

Modules define how to provide dependencies that cannot be constructor-injected (interfaces, third-party classes, etc.).

### Binding Interfaces to Implementations

Use `@Binds` to bind an interface to its implementation:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository
    
    @Singleton
    @Binds
    abstract fun bindSprintRepository(
        impl: SprintRepositoryImpl
    ): SprintRepository
    
    @Singleton
    @Binds
    abstract fun bindGoalRepository(
        impl: GoalRepositoryImpl
    ): GoalRepository
}
```

### Providing Instances

Use `@Provides` when constructor injection isn't possible:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Singleton
    @Provides
    fun provideTaskLocalDataSource(database: AppDatabase): TaskLocalDataSource {
        return TaskLocalDataSourceImpl(database.taskDao())
    }
    
    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d("Ktor: $message")
                    }
                }
                level = LogLevel.HEADERS
            }
            // Add other configurations
        }
    }
}
```

### Providing Database

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "agile_life_management_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Singleton
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Singleton
    @Provides
    fun provideSprintDao(database: AppDatabase): SprintDao {
        return database.sprintDao()
    }
}
```

### Providing Dispatchers for Coroutines

```kotlin
// Define qualifiers
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    
    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    
    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
```

## Scoping Dependencies

Scoping ensures dependencies have the appropriate lifecycle:

| Scope | Annotation | Appropriate for |
|-------|------------|----------------|
| Application | `@Singleton` | Database, network clients, repositories |
| Activity | `@ActivityScoped` | Activity-specific controllers |
| Fragment | `@FragmentScoped` | Fragment-specific controllers |
| ViewModel | `@ViewModelScoped` | ViewModel-specific dependencies |
| View | `@ViewScoped` | View-specific dependencies |
| Service | `@ServiceScoped` | Service-specific dependencies |

Example of scoping:

```kotlin
@Singleton // Lives as long as the application
class TaskRepositoryImpl @Inject constructor(...) : TaskRepository

@ActivityScoped // Lives as long as the activity
class TaskListController @Inject constructor(...) 

@FragmentScoped // Lives as long as the fragment
class TaskDetailFormatter @Inject constructor(...)

@ViewModelScoped // Lives as long as the ViewModel
class TaskAnalytics @Inject constructor(...)
```

## Using Qualifiers

Qualifiers distinguish between multiple bindings of the same type:

```kotlin
// Define qualifiers
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedApiClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthenticatedApiClient

// Provide instances with qualifiers
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @AuthenticatedApiClient
    @Provides
    fun provideAuthenticatedHttpClient(
        tokenProvider: TokenProvider
    ): HttpClient {
        return HttpClient(Android) {
            // Add auth interceptor
            install(DefaultRequest) {
                header("Authorization", "Bearer ${tokenProvider.getToken()}")
            }
        }
    }
    
    @Singleton
    @UnauthenticatedApiClient
    @Provides
    fun provideUnauthenticatedHttpClient(): HttpClient {
        return HttpClient(Android) {
            // Basic configuration without auth
        }
    }
}

// Inject with qualifier
class AuthRepository @Inject constructor(
    @AuthenticatedApiClient private val httpClient: HttpClient
)

class PublicRepository @Inject constructor(
    @UnauthenticatedApiClient private val httpClient: HttpClient
)
```

## Entry Points for Non-Hilt Code

For integrating with non-Hilt classes:

```kotlin
// Define the entry point interface
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AnalyticsEntryPoint {
    fun analyticsService(): AnalyticsService
}

// Use it in non-Hilt class
class ThirdPartyClass {
    fun logEvent(context: Context, event: String) {
        val analyticsEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AnalyticsEntryPoint::class.java
        )
        analyticsEntryPoint.analyticsService().logEvent(event)
    }
}
```

## Testing with Hilt

### Unit Testing with Hilt

```kotlin
@HiltAndroidTest
class TaskRepositoryTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var taskRepository: TaskRepository
    
    @Before
    fun setUp() {
        hiltRule.inject()
    }
    
    @Test
    fun testGetTasks() {
        // Test implementation
    }
}
```

### Using Test Modules

Replace real dependencies with test doubles:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object TestNetworkModule {
    @Singleton
    @Provides
    fun provideTestHttpClient(): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        "/tasks" -> {
                            respond(
                                content = """[{"id":"1","title":"Test Task"}]""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(
                                    "Content-Type" to listOf("application/json")
                                )
                            )
                        }
                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
        }
    }
}
```

### Using Fakes

For integration tests:

```kotlin
// A fake repository for testing
class FakeTaskRepository @Inject constructor() : TaskRepository {
    private val tasks = mutableListOf<Task>()
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    
    override fun getTasks(): Flow<List<Task>> = tasksFlow
    
    override suspend fun createTask(task: Task): Result<Task> {
        tasks.add(task)
        tasksFlow.value = tasks.toList()
        return Result.success(task)
    }
    
    // Other implementation methods...
    
    // Test helper methods
    fun addTasks(newTasks: List<Task>) {
        tasks.addAll(newTasks)
        tasksFlow.value = tasks.toList()
    }
}

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

## Best Practices

1. **Use constructor injection** when possible - it's simpler and more explicit.
2. **Keep modules focused** on a specific layer or feature.
3. **Use appropriate scopes** to manage object lifecycles.
4. **Define clear interfaces** between architectural layers.
5. **Keep dependency graph simple** - avoid circular dependencies.
6. **Use qualifiers** to disambiguate similar dependencies.
7. **Avoid injecting Android Context directly** - use `@ApplicationContext` when needed.
8. **Create separate modules for testing** to replace real implementations.

## Resources

- [Hilt documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Dependency Injection in Android](https://developer.android.com/training/dependency-injection)
- [Testing with Hilt](https://developer.android.com/training/dependency-injection/hilt-testing)
