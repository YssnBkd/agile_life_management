# Networking with Ktor Client

## Overview

Ktor Client is a multiplatform HTTP client written in Kotlin that allows you to make network requests in a concise, flexible, and idiomatic way. It's a perfect fit for Android applications built with clean architecture principles.

This guide covers the implementation of Ktor Client in the context of the AgileLifeManagement project, which uses Supabase as the backend service.

## Setting Up Ktor Client

### Dependencies

Add the required dependencies to your app-level `build.gradle.kts` file:

```kotlin
dependencies {
    // Ktor core
    implementation("io.ktor:ktor-client-core:2.3.0")
    implementation("io.ktor:ktor-client-android:2.3.0")
    
    // JSON serialization
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    
    // Logging
    implementation("io.ktor:ktor-client-logging:2.3.0")
    
    // Auth
    implementation("io.ktor:ktor-client-auth:2.3.0")
}
```

### Basic Configuration

Create a Ktor client with a basic configuration:

```kotlin
val ktorHttpClient = HttpClient(Android) {
    // Install plugins
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
    
    // Configure client defaults
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = "api.example.com" // Replace with your API URL
        }
        contentType(ContentType.Application.Json)
    }
    
    // Configure timeouts
    engine {
        connectTimeout = 15_000 // 15 seconds
        socketTimeout = 15_000 // 15 seconds
    }
}
```

## Implementing Ktor with Supabase

### Supabase API Configuration

Configure Ktor client for use with Supabase:

```kotlin
@Singleton
class SupabaseKtorClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Get Supabase configuration from resources
    private val supabaseUrl = context.getString(R.string.supabase_url)
    private val supabaseKey = context.getString(R.string.supabase_anon_key)
    
    // Create Ktor client
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = false
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.tag("SupabaseKtor").d(message)
                }
            }
            level = LogLevel.HEADERS
        }
        
        install(DefaultRequest) {
            header("apikey", supabaseKey)
            contentType(ContentType.Application.Json)
        }
        
        engine {
            connectTimeout = 15_000
            socketTimeout = 15_000
        }
    }
}
```

### Remote Data Source Implementation

Implement a remote data source using Ktor:

```kotlin
interface TaskRemoteDataSource {
    suspend fun getTasks(): List<TaskDto>
    suspend fun getTaskById(taskId: String): TaskDto?
    suspend fun createTask(task: TaskCreateDto): TaskDto
    suspend fun updateTask(taskId: String, task: TaskUpdateDto): TaskDto
    suspend fun updateTaskStatus(taskId: String, status: Int): TaskDto
    suspend fun deleteTask(taskId: String): Boolean
}

class TaskRemoteDataSourceImpl @Inject constructor(
    private val supabaseKtorClient: SupabaseKtorClient
) : TaskRemoteDataSource {
    private val client get() = supabaseKtorClient.client
    private val baseUrl = "tasks"
    
    override suspend fun getTasks(): List<TaskDto> {
        return client.get("$baseUrl") {
            url {
                parameters.append("select", "*")
            }
        }.body()
    }
    
    override suspend fun getTaskById(taskId: String): TaskDto? {
        return try {
            client.get("$baseUrl") {
                url {
                    parameters.append("id", "eq.$taskId")
                    parameters.append("select", "*")
                }
            }.body<List<TaskDto>>().firstOrNull()
        } catch (e: Exception) {
            Timber.e(e, "Error getting task by ID")
            null
        }
    }
    
    override suspend fun createTask(task: TaskCreateDto): TaskDto {
        return client.post("$baseUrl") {
            contentType(ContentType.Application.Json)
            setBody(task)
            header("Prefer", "return=representation")
        }.body<List<TaskDto>>().first()
    }
    
    override suspend fun updateTask(taskId: String, task: TaskUpdateDto): TaskDto {
        return client.patch("$baseUrl") {
            url {
                parameters.append("id", "eq.$taskId")
            }
            contentType(ContentType.Application.Json)
            setBody(task)
            header("Prefer", "return=representation")
        }.body<List<TaskDto>>().first()
    }
    
    override suspend fun updateTaskStatus(taskId: String, status: Int): TaskDto {
        return client.patch("$baseUrl") {
            url {
                parameters.append("id", "eq.$taskId")
            }
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status, "updated_at" to System.currentTimeMillis()))
            header("Prefer", "return=representation")
        }.body<List<TaskDto>>().first()
    }
    
    override suspend fun deleteTask(taskId: String): Boolean {
        val response = client.delete("$baseUrl") {
            url {
                parameters.append("id", "eq.$taskId")
            }
        }
        return response.status.isSuccess()
    }
}
```

### Data Models

Define DTOs (Data Transfer Objects) for network operations:

```kotlin
@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: Int,
    @SerialName("due_date")
    val dueDate: Long? = null,
    @SerialName("sprint_id")
    val sprintId: String? = null,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
)

@Serializable
data class TaskCreateDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: Int,
    @SerialName("due_date")
    val dueDate: Long? = null,
    @SerialName("sprint_id")
    val sprintId: String? = null,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
)

@Serializable
data class TaskUpdateDto(
    val title: String? = null,
    val description: String? = null,
    val status: Int? = null,
    @SerialName("due_date")
    val dueDate: Long? = null,
    @SerialName("sprint_id")
    val sprintId: String? = null,
    @SerialName("updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Mapping Data Models

Create mappers to convert between DTOs and domain models:

```kotlin
fun TaskDto.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        status = when (status) {
            0 -> TaskStatus.TODO
            1 -> TaskStatus.IN_PROGRESS
            2 -> TaskStatus.COMPLETED
            else -> TaskStatus.TODO
        },
        dueDate = dueDate?.let { 
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) 
        },
        sprintId = sprintId,
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt), 
            ZoneId.systemDefault()
        ),
        updatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(updatedAt), 
            ZoneId.systemDefault()
        )
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        status = when (status) {
            TaskStatus.TODO -> 0
            TaskStatus.IN_PROGRESS -> 1
            TaskStatus.COMPLETED -> 2
        },
        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        sprintId = sprintId,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

fun Task.toCreateDto(): TaskCreateDto {
    return TaskCreateDto(
        id = id,
        title = title,
        description = description,
        status = when (status) {
            TaskStatus.TODO -> 0
            TaskStatus.IN_PROGRESS -> 1
            TaskStatus.COMPLETED -> 2
        },
        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        sprintId = sprintId,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

fun Task.toUpdateDto(): TaskUpdateDto {
    return TaskUpdateDto(
        title = title,
        description = description,
        status = when (status) {
            TaskStatus.TODO -> 0
            TaskStatus.IN_PROGRESS -> 1
            TaskStatus.COMPLETED -> 2
        },
        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        sprintId = sprintId,
        updatedAt = System.currentTimeMillis()
    )
}
```

## Authentication with Ktor

### JWT Authentication

Implement JWT authentication for Supabase:

```kotlin
@Singleton
class AuthManager @Inject constructor(
    private val supabaseKtorClient: SupabaseKtorClient,
    private val tokenStorage: TokenStorage,
    @ApplicationContext private val context: Context
) {
    private val supabaseUrl = context.getString(R.string.supabase_url)
    private val supabaseKey = context.getString(R.string.supabase_anon_key)
    
    suspend fun signIn(email: String, password: String): Result<UserSession> {
        return try {
            val response = supabaseKtorClient.client.post("$supabaseUrl/auth/v1/token") {
                contentType(ContentType.Application.Json)
                setBody(SignInRequest(email, password))
                header("apikey", supabaseKey)
            }
            
            val authResponse = response.body<AuthResponse>()
            val userSession = UserSession(
                accessToken = authResponse.accessToken,
                refreshToken = authResponse.refreshToken,
                userId = authResponse.user.id,
                email = authResponse.user.email
            )
            
            // Store tokens
            tokenStorage.saveTokens(userSession)
            
            Result.success(userSession)
        } catch (e: Exception) {
            Timber.e(e, "Error during sign in")
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        try {
            val accessToken = tokenStorage.getAccessToken()
            if (accessToken != null) {
                supabaseKtorClient.client.post("$supabaseUrl/auth/v1/logout") {
                    header("Authorization", "Bearer $accessToken")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during sign out")
        } finally {
            tokenStorage.clearTokens()
        }
    }
    
    suspend fun refreshToken(): Boolean {
        val refreshToken = tokenStorage.getRefreshToken() ?: return false
        
        return try {
            val response = supabaseKtorClient.client.post("$supabaseUrl/auth/v1/token") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
                header("apikey", supabaseKey)
            }
            
            val authResponse = response.body<AuthResponse>()
            val userSession = UserSession(
                accessToken = authResponse.accessToken,
                refreshToken = authResponse.refreshToken,
                userId = authResponse.user.id,
                email = authResponse.user.email
            )
            
            // Store updated tokens
            tokenStorage.saveTokens(userSession)
            true
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing token")
            false
        }
    }
    
    // Data classes for request/response
    @Serializable
    data class SignInRequest(
        val email: String,
        val password: String
    )
    
    @Serializable
    data class RefreshTokenRequest(
        @SerialName("refresh_token")
        val refreshToken: String
    )
    
    @Serializable
    data class AuthResponse(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("refresh_token")
        val refreshToken: String,
        @SerialName("expires_in")
        val expiresIn: Int,
        @SerialName("token_type")
        val tokenType: String,
        val user: UserDto
    )
    
    @Serializable
    data class UserDto(
        val id: String,
        val email: String
    )
}
```

### Token Storage

Implement secure token storage:

```kotlin
interface TokenStorage {
    suspend fun saveTokens(userSession: UserSession)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}

class DataStoreTokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenStorage {
    private val dataStore = context.createDataStore(name = "auth_tokens")
    
    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val EMAIL = stringPreferencesKey("email")
    }
    
    override suspend fun saveTokens(userSession: UserSession) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = userSession.accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = userSession.refreshToken
            preferences[PreferencesKeys.USER_ID] = userSession.userId
            preferences[PreferencesKeys.EMAIL] = userSession.email
        }
    }
    
    override suspend fun getAccessToken(): String? {
        return dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
    }
    
    override suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[PreferencesKeys.REFRESH_TOKEN]
    }
    
    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
```

## Error Handling

### Implementing Error Handling Middleware

Create a custom exception handler for Ktor:

```kotlin
class KtorExceptionHandler {
    // Network exceptions
    class NetworkException(
        message: String = "Network error occurred",
        cause: Throwable? = null
    ) : IOException(message, cause)
    
    // Server errors (5xx)
    class ServerException(
        val statusCode: Int,
        message: String = "Server error occurred"
    ) : Exception(message)
    
    // Client errors (4xx)
    class ClientException(
        val statusCode: Int,
        message: String = "Client error occurred"
    ) : Exception(message)
    
    // Authentication errors
    class AuthenticationException(
        message: String = "Authentication failed"
    ) : Exception(message)
    
    // Parse the error response from server
    suspend fun handleResponseException(e: ResponseException): Exception {
        return when (e.response.status.value) {
            in 500..599 -> ServerException(e.response.status.value)
            
            401 -> AuthenticationException()
            
            in 400..499 -> {
                try {
                    val errorResponse = e.response.body<ErrorResponse>()
                    ClientException(e.response.status.value, errorResponse.message)
                } catch (parseError: Exception) {
                    ClientException(e.response.status.value)
                }
            }
            
            else -> e
        }
    }
    
    @Serializable
    data class ErrorResponse(
        val code: String? = null,
        val message: String = "Unknown error"
    )
}

// Extension function to convert Ktor exceptions to application-specific exceptions
suspend fun <T> executeWithErrorHandling(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: ResponseException) {
        val handler = KtorExceptionHandler()
        Result.failure(handler.handleResponseException(e))
    } catch (e: IOException) {
        Result.failure(KtorExceptionHandler.NetworkException(cause = e))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Using the Error Handler in Remote Data Source

Apply the error handler to the remote data source:

```kotlin
override suspend fun getTasks(): Result<List<TaskDto>> {
    return executeWithErrorHandling {
        client.get("$baseUrl") {
            url {
                parameters.append("select", "*")
            }
        }.body()
    }
}

override suspend fun createTask(task: TaskCreateDto): Result<TaskDto> {
    return executeWithErrorHandling {
        client.post("$baseUrl") {
            contentType(ContentType.Application.Json)
            setBody(task)
            header("Prefer", "return=representation")
        }.body<List<TaskDto>>().first()
    }
}
```

## Dependency Injection with Hilt

Set up Ktor and remote data sources with Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideSupabaseKtorClient(
        @ApplicationContext context: Context
    ): SupabaseKtorClient {
        return SupabaseKtorClient(context)
    }
    
    @Singleton
    @Provides
    fun provideTokenStorage(
        @ApplicationContext context: Context
    ): TokenStorage {
        return DataStoreTokenStorage(context)
    }
    
    @Singleton
    @Provides
    fun provideAuthManager(
        supabaseKtorClient: SupabaseKtorClient,
        tokenStorage: TokenStorage,
        @ApplicationContext context: Context
    ): AuthManager {
        return AuthManager(supabaseKtorClient, tokenStorage, context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {
    @Singleton
    @Provides
    fun provideTaskRemoteDataSource(
        supabaseKtorClient: SupabaseKtorClient
    ): TaskRemoteDataSource {
        return TaskRemoteDataSourceImpl(supabaseKtorClient)
    }
    
    @Singleton
    @Provides
    fun provideSprintRemoteDataSource(
        supabaseKtorClient: SupabaseKtorClient
    ): SprintRemoteDataSource {
        return SprintRemoteDataSourceImpl(supabaseKtorClient)
    }
}
```

## Testing Ktor Client

### Mock Ktor Client for Testing

Create a mock HTTP client for testing:

```kotlin
class MockEngine(private val responses: Map<String, MockResponse>) : HttpClientEngineFactory<HttpClientEngineConfig> {
    override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine {
        return io.ktor.client.engine.mock.MockEngine { request ->
            val url = request.url.toString()
            val mockResponse = responses[url] ?: error("Unhandled request: $url")
            
            respond(
                content = mockResponse.content,
                status = mockResponse.status,
                headers = mockResponse.headers
            )
        }
    }
    
    data class MockResponse(
        val content: String,
        val status: HttpStatusCode = HttpStatusCode.OK,
        val headers: Headers = headersOf("Content-Type", "application/json")
    )
}

// Usage in tests
val mockResponses = mapOf(
    "https://api.example.com/tasks" to MockEngine.MockResponse(
        content = """
            [
                {"id":"1","title":"Task 1","status":0},
                {"id":"2","title":"Task 2","status":1}
            ]
        """.trimIndent()
    )
)

val mockClient = HttpClient(MockEngine(mockResponses)) {
    install(ContentNegotiation) {
        json()
    }
}
```

### Testing Remote Data Source

Test the remote data source with a mock Ktor client:

```kotlin
class TaskRemoteDataSourceTest {
    private lateinit var mockClient: HttpClient
    private lateinit var remoteDataSource: TaskRemoteDataSourceImpl
    
    @Before
    fun setup() {
        val mockResponses = mapOf(
            "https://api.example.com/tasks?select=*" to MockEngine.MockResponse(
                content = """
                    [
                        {"id":"1","title":"Task 1","status":0,"created_at":1620000000000,"updated_at":1620000000000},
                        {"id":"2","title":"Task 2","status":1,"created_at":1620000000000,"updated_at":1620000000000}
                    ]
                """.trimIndent()
            ),
            "https://api.example.com/tasks?id=eq.1&select=*" to MockEngine.MockResponse(
                content = """
                    [
                        {"id":"1","title":"Task 1","status":0,"created_at":1620000000000,"updated_at":1620000000000}
                    ]
                """.trimIndent()
            )
        )
        
        mockClient = HttpClient(MockEngine(mockResponses)) {
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                url("https://api.example.com/")
            }
        }
        
        // Inject the mock client
        val supabaseKtorClient = mock<SupabaseKtorClient>()
        whenever(supabaseKtorClient.client).thenReturn(mockClient)
        
        remoteDataSource = TaskRemoteDataSourceImpl(supabaseKtorClient)
    }
    
    @Test
    fun `getTasks returns list of tasks`() = runTest {
        // When
        val result = remoteDataSource.getTasks()
        
        // Then
        assertTrue(result.isSuccess)
        val tasks = result.getOrNull()
        assertNotNull(tasks)
        assertEquals(2, tasks?.size)
        assertEquals("Task 1", tasks?.get(0)?.title)
        assertEquals("Task 2", tasks?.get(1)?.title)
    }
    
    @Test
    fun `getTaskById returns task when found`() = runTest {
        // When
        val result = remoteDataSource.getTaskById("1")
        
        // Then
        assertTrue(result.isSuccess)
        val task = result.getOrNull()
        assertNotNull(task)
        assertEquals("1", task?.id)
        assertEquals("Task 1", task?.title)
    }
}
```

## Best Practices for Ktor

1. **Use Coroutines**: Leverage Kotlin Coroutines for asynchronous networking
2. **Proper Error Handling**: Implement robust error handling and conversion to domain-specific exceptions
3. **Content Negotiation**: Configure proper serialization/deserialization for your API
4. **Logging**: Set up appropriate logging for debugging
5. **Authentication**: Implement secure authentication handling
6. **Timeout Configuration**: Configure appropriate timeouts for your network operations
7. **Dependency Injection**: Use Hilt to provide HTTP client and data sources
8. **Testability**: Design for testability with mock engines
9. **DTOs**: Use separate data transfer objects for network operations
10. **Repository Pattern**: Integrate Ktor data sources with the repository pattern

## Resources

- [Ktor Client documentation](https://ktor.io/docs/client.html)
- [Supabase REST API documentation](https://supabase.io/docs/reference/javascript/supabase-js)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Testing HTTP clients](https://ktor.io/docs/http-client-testing.html)
