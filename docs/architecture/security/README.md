# Security Implementation Guide

## Overview

This guide outlines the security implementations in the AgileLifeManagement application. Security is a critical aspect of any application, especially those that handle sensitive user data. This document provides comprehensive guidance on implementing security best practices across all layers of the application architecture.

## Authentication & Authorization

### JWT Authentication with Supabase

The application uses JWT (JSON Web Token) based authentication through Supabase:

```kotlin
interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Session>
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    fun isUserAuthenticated(): Flow<Boolean>
    suspend fun refreshSession(): Result<Session>
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val secureStorage: SecureStorage,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {
    
    private val _currentUserFlow = MutableStateFlow<User?>(null)
    
    init {
        externalScope.launch {
            secureStorage.getToken()?.let { token ->
                if (!token.isExpired()) {
                    // Validate and load user from token
                    val user = token.extractUser()
                    _currentUserFlow.value = user
                } else {
                    // Token expired, try to refresh
                    refreshSession()
                }
            }
        }
    }

    // Add application scope provider
    @Module
    @InstallIn(SingletonComponent::class)
    object CoroutineScopesModule {
        @Provides
        @Singleton
        @ApplicationScope
        fun providesApplicationScope(): CoroutineScope {
            return CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }
    }
    
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ApplicationScope
    
    override suspend fun signIn(email: String, password: String): Result<Session> = withContext(ioDispatcher) {
        try {
            val result = authRemoteDataSource.signIn(email, password)
            if (result.isSuccess) {
                result.getOrNull()?.let { session ->
                    // Store token securely
                    secureStorage.saveToken(session.accessToken, session.refreshToken)
                    _currentUserFlow.value = session.user
                }
            }
            result
        } catch (e: Exception) {
            Timber.e(e, "Sign in failed")
            Result.failure(e)
        }
    }
    
    override suspend fun signOut(): Result<Unit> = withContext(ioDispatcher) {
        try {
            // Clear tokens
            secureStorage.clearTokens()
            _currentUserFlow.value = null
            
            // Sign out from remote
            authRemoteDataSource.signOut()
        } catch (e: Exception) {
            Timber.e(e, "Sign out failed")
            Result.failure(e)
        }
    }
    
    override fun getCurrentUser(): Flow<User?> = _currentUserFlow
    
    override fun isUserAuthenticated(): Flow<Boolean> = _currentUserFlow.map { it != null }
    
    override suspend fun refreshSession(): Result<Session> = withContext(ioDispatcher) {
        try {
            val refreshToken = secureStorage.getRefreshToken() ?: return@withContext Result.failure(
                Exception("No refresh token available")
            )
            
            val result = authRemoteDataSource.refreshToken(refreshToken)
            if (result.isSuccess) {
                result.getOrNull()?.let { session ->
                    secureStorage.saveToken(session.accessToken, session.refreshToken)
                    _currentUserFlow.value = session.user
                }
            }
            result
        } catch (e: Exception) {
            Timber.e(e, "Token refresh failed")
            Result.failure(e)
        }
    }
}
```

### Ktor Client Authentication Configuration

Configure Ktor client to handle JWT authentication:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideHttpClient(
        secureStorage: SecureStorage
    ): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            install(Logging) {
                level = LogLevel.HEADERS
            }
            
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = secureStorage.getToken() ?: "",
                            refreshToken = secureStorage.getRefreshToken() ?: ""
                        )
                    }
                    
                    refreshTokens {
                        // Return null to indicate that refresh failed
                        val refreshToken = secureStorage.getRefreshToken() ?: return@refreshTokens null
                        
                        try {
                            val response = client.post("${ApiConfig.BASE_URL}/auth/refresh") {
                                contentType(ContentType.Application.Json)
                                setBody(mapOf("refresh_token" to refreshToken))
                            }
                            
                            if (response.status.isSuccess()) {
                                val refreshResponse = response.body<RefreshTokenResponse>()
                                secureStorage.saveToken(
                                    refreshResponse.accessToken, 
                                    refreshResponse.refreshToken
                                )
                                
                                BearerTokens(
                                    accessToken = refreshResponse.accessToken,
                                    refreshToken = refreshResponse.refreshToken
                                )
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Token refresh failed in Ktor client")
                            null
                        }
                    }
                }
            }
            
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }
            
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }
}
```

## Secure Storage

### Encrypted Storage for Sensitive Data

Implementation of secure storage for tokens and other sensitive information:

```kotlin
interface SecureStorage {
    suspend fun saveToken(accessToken: String, refreshToken: String)
    suspend fun getToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
    
    suspend fun saveEncryptedData(key: String, data: String)
    suspend fun getEncryptedData(key: String): String?
    suspend fun removeEncryptedData(key: String)
}

@Singleton
class SecureStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SecureStorage {
    
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val encryptedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    private val encryptedDataStore by lazy {
        context.createDataStore(
            name = "encrypted_data_store",
            dataStoreFactory = DataStoreFactory.create(
                serializer = TokenSerializer(),
                produceFile = { context.cryptoFile("encrypted_tokens.pb") },
                corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
            )
        )
    }
    
    override suspend fun saveToken(accessToken: String, refreshToken: String) = withContext(ioDispatcher) {
        encryptedDataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }
    
    override suspend fun getToken(): String? = withContext(ioDispatcher) {
        encryptedDataStore.data.first()[ACCESS_TOKEN]
    }
    
    override suspend fun getRefreshToken(): String? = withContext(ioDispatcher) {
        encryptedDataStore.data.first()[REFRESH_TOKEN]
    }
    
    override suspend fun clearTokens() = withContext(ioDispatcher) {
        encryptedDataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
        }
    }
    
    override suspend fun saveEncryptedData(key: String, data: String) = withContext(ioDispatcher) {
        encryptedPreferences.edit {
            putString(key, data)
        }
    }
    
    override suspend fun getEncryptedData(key: String): String? = withContext(ioDispatcher) {
        encryptedPreferences.getString(key, null)
    }
    
    override suspend fun removeEncryptedData(key: String) = withContext(ioDispatcher) {
        encryptedPreferences.edit {
            remove(key)
        }
    }
    
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}

// Extension function to create encrypted files
fun Context.cryptoFile(fileName: String): File {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    return EncryptedFile.Builder(
        File(this.filesDir, fileName),
        this,
        masterKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build().file
}
```

### Biometric Authentication

Implement biometric authentication for accessing sensitive features:

```kotlin
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val biometricManager = BiometricManager.from(context)
    
    fun canAuthenticate(): Boolean {
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }
    
    fun createPrompt(
        title: String,
        subtitle: String,
        negativeButtonText: String
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }
    
    fun createBiometricPrompt(
        fragment: Fragment,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (Int, CharSequence) -> Unit,
        onFailed: () -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        
        return BiometricPrompt(
            fragment,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess(result)
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errorCode, errString)
                }
                
                override fun onAuthenticationFailed() {
                    onFailed()
                }
            }
        )
    }
}

// Usage in a Fragment
@AndroidEntryPoint
class SecureContentFragment : Fragment() {
    
    @Inject
    lateinit var biometricAuthManager: BiometricAuthManager
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (biometricAuthManager.canAuthenticate()) {
            authenticateUser()
        } else {
            // Fall back to password authentication
            showPasswordPrompt()
        }
    }
    
    private fun authenticateUser() {
        val promptInfo = biometricAuthManager.createPrompt(
            title = "Authenticate to Continue",
            subtitle = "Verify your identity to access secure content",
            negativeButtonText = "Use Password"
        )
        
        val biometricPrompt = biometricAuthManager.createBiometricPrompt(
            fragment = this,
            onSuccess = { result ->
                // Authentication successful, proceed to show secure content
                showSecureContent()
            },
            onError = { errorCode, errString ->
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        // User chose to use password instead
                        showPasswordPrompt()
                    }
                    else -> {
                        // Handle other errors
                        Toast.makeText(
                            requireContext(),
                            "Authentication error: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            onFailed = {
                // Authentication failed (e.g., fingerprint not recognized)
                Toast.makeText(
                    requireContext(),
                    "Authentication failed, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    private fun showPasswordPrompt() {
        // Show password authentication dialog
    }
    
    private fun showSecureContent() {
        // Show secure content after successful authentication
    }
}
```

## Network Security

### API Security

Implementing secure API communication:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    
    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add(ApiConfig.HOST, "sha256/...")
            .add(ApiConfig.HOST, "sha256/...")
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSecureHttpClient(
        certificatePinner: CertificatePinner,
        secureStorage: SecureStorage
    ): HttpClient {
        return HttpClient(Android) {
            // Other configurations...
            
            engine {
                config {
                    // Certificate pinning
                    certificatePinner(certificatePinner)
                    
                    // TLS settings
                    sslSocketFactory = SSLConfigurationFactory.create()
                }
            }
        }
    }
}

object SSLConfigurationFactory {
    fun create(): SSLSocketFactory {
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(null as KeyStore?)
        
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(
            null,
            trustManagerFactory.trustManagers,
            SecureRandom()
        )
        
        return sslContext.socketFactory
    }
}
```

### Network Security Configuration

Create a network security configuration XML file at `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <domain-config>
        <domain includeSubdomains="true">api.example.com</domain>
        <pin-set>
            <!-- Backup pins -->
            <pin digest="SHA-256">...</pin>
            <pin digest="SHA-256">...</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

And reference it in the AndroidManifest.xml:

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
    <!-- ... -->
</application>
```

## Data Encryption

### At-Rest Encryption

Implement encryption for sensitive data stored on the device:

```kotlin
interface EncryptionManager {
    fun encrypt(data: String): String
    fun decrypt(encryptedData: String): String
}

@Singleton
class EncryptionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : EncryptionManager {
    
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    
    private val encryptionKey: SecretKey by lazy { getOrCreateKey() }
    
    private fun getOrCreateKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        existingKey?.secretKey?.let { return it }
        
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()
        
        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }
    
    override fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)
        
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        
        val result = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, result, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, result, iv.size, encryptedBytes.size)
        
        return Base64.encodeToString(result, Base64.DEFAULT)
    }
    
    override fun decrypt(encryptedData: String): String {
        val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivSpec = GCMParameterSpec(128, encryptedBytes, 0, IV_SIZE)
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, ivSpec)
        
        val decryptedBytes = cipher.doFinal(
            encryptedBytes,
            IV_SIZE,
            encryptedBytes.size - IV_SIZE
        )
        
        return String(decryptedBytes, Charsets.UTF_8)
    }
    
    companion object {
        private const val KEY_ALIAS = "agile_life_encryption_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12 // GCM recommended IV size
    }
}
```

### In-Transit Encryption

Ensure all network communications are encrypted:

```kotlin
// In your API service interface
interface TaskApiService {
    @GET("tasks")
    suspend fun getTasks(
        @Header("X-API-Key") apiKey: String
    ): List<TaskDto>
    
    @POST("tasks")
    suspend fun createTask(
        @Body task: TaskDto,
        @Header("X-API-Key") apiKey: String
    ): TaskDto
}

// In your remote data source
class TaskRemoteDataSourceImpl @Inject constructor(
    private val apiService: TaskApiService,
    private val apiKeyProvider: ApiKeyProvider,
    private val secureStorage: SecureStorage
) : TaskRemoteDataSource {
    
    override suspend fun getTasks(): Result<List<TaskDto>> {
        return try {
            val apiKey = apiKeyProvider.getApiKey()
            val tasks = apiService.getTasks(apiKey)
            Result.success(tasks)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching tasks")
            Result.failure(e)
        }
    }
    
    // Other implementations...
}

// API key provider that safely stores and retrieves API keys
class ApiKeyProvider @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val secureStorage: SecureStorage
) {
    suspend fun getApiKey(): String {
        return secureStorage.getEncryptedData(API_KEY_ALIAS)?.let { encryptedKey ->
            encryptionManager.decrypt(encryptedKey)
        } ?: throw SecurityException("API key not found")
    }
    
    suspend fun storeApiKey(apiKey: String) {
        val encryptedKey = encryptionManager.encrypt(apiKey)
        secureStorage.saveEncryptedData(API_KEY_ALIAS, encryptedKey)
    }
    
    companion object {
        private const val API_KEY_ALIAS = "api_key"
    }
}
```

## Input Validation & Sanitization

Implement proper validation of user inputs:

```kotlin
// Domain-level validation
class TaskValidator @Inject constructor() {
    fun validateTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Error("Title cannot be empty")
            title.length < 3 -> ValidationResult.Error("Title must be at least 3 characters")
            title.length > 100 -> ValidationResult.Error("Title must be at most 100 characters")
            else -> ValidationResult.Success
        }
    }
    
    fun validateDescription(description: String?): ValidationResult {
        return when {
            description != null && description.length > 1000 -> 
                ValidationResult.Error("Description must be at most 1000 characters")
            else -> ValidationResult.Success
        }
    }
    
    fun validateDueDate(dueDate: LocalDateTime?): ValidationResult {
        return when {
            dueDate != null && dueDate.isBefore(LocalDateTime.now()) -> 
                ValidationResult.Error("Due date must be in the future")
            else -> ValidationResult.Success
        }
    }
    
    // Other validation methods...
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

// Use in UseCase
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskValidator: TaskValidator
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        dueDate: LocalDateTime?,
        priority: TaskPriority
    ): Result<Task> {
        // Validate inputs
        val titleValidation = taskValidator.validateTitle(title)
        if (titleValidation is ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(titleValidation.message))
        }
        
        val descriptionValidation = taskValidator.validateDescription(description)
        if (descriptionValidation is ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(descriptionValidation.message))
        }
        
        val dueDateValidation = taskValidator.validateDueDate(dueDate)
        if (dueDateValidation is ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(dueDateValidation.message))
        }
        
        // Create task if validation passes
        return taskRepository.createTask(
            title = title,
            description = description,
            dueDate = dueDate,
            priority = priority
        )
    }
}
```

### Handling User Input in UI Layer

```kotlin
@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {
    
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    
    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()
    
    // Other state flows for task fields...
    
    fun updateTitle(newTitle: String) {
        _title.value = newTitle.trim()
        // Immediate validation feedback
        _titleError.value = when {
            newTitle.isBlank() -> "Title cannot be empty"
            newTitle.length < 3 -> "Title must be at least 3 characters"
            newTitle.length > 100 -> "Title must be at most 100 characters"
            else -> null
        }
    }
    
    // Other update methods for task fields...
    
    fun createTask() {
        viewModelScope.launch {
            val result = createTaskUseCase(
                title = _title.value,
                description = _description.value.takeIf { it.isNotBlank() },
                dueDate = _dueDate.value,
                priority = _priority.value
            )
            
            // Handle result...
        }
    }
}

// In your Compose UI
@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val titleError by viewModel.titleError.collectAsState()
    
    // Other state variables...
    
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.updateTitle(it) },
            label = { Text("Title") },
            isError = titleError != null,
            supportingText = {
                titleError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Other input fields...
        
        Button(
            onClick = { viewModel.createTask() },
            enabled = titleError == null && /* other validation checks */,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Task")
        }
    }
}
```

## Security Testing

### Code Analysis Tools

Configure static analysis tools in your gradle file:

```kotlin
dependencies {
    // Static analysis tools
    debugImplementation("com.google.android.gms:play-services-safetynet:18.0.1")
    
    // Leak detection
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
    
    // Security testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
}
```

### Security Tests

Create tests for security implementations:

```kotlin
@RunWith(AndroidJUnit4::class)
class SecurityImplementationTest {
    
    @Inject
    lateinit var encryptionManager: EncryptionManager
    
    @Inject
    lateinit var secureStorage: SecureStorage
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Before
    fun setUp() {
        hiltRule.inject()
    }
    
    @Test
    fun encryptionManager_encryptAndDecrypt_returnsSameValue() {
        // Given
        val original = "highly sensitive data"
        
        // When
        val encrypted = encryptionManager.encrypt(original)
        val decrypted = encryptionManager.decrypt(encrypted)
        
        // Then
        assertNotEquals("Encrypted data should be different from original", original, encrypted)
        assertEquals("Decrypted data should match original", original, decrypted)
    }
    
    @Test
    suspend fun secureStorage_saveAndRetrieveToken_returnsSameValue() = runTest {
        // Given
        val accessToken = "access.token.123"
        val refreshToken = "refresh.token.456"
        
        // When
        secureStorage.saveToken(accessToken, refreshToken)
        val retrievedAccess = secureStorage.getToken()
        val retrievedRefresh = secureStorage.getRefreshToken()
        
        // Then
        assertEquals("Retrieved access token should match saved token", accessToken, retrievedAccess)
        assertEquals("Retrieved refresh token should match saved token", refreshToken, retrievedRefresh)
        
        // Cleanup
        secureStorage.clearTokens()
    }
}
```

## Security Best Practices

### Securing User Data

1. **Minimize Data Collection**: Only collect data that is strictly necessary for your app's functionality
2. **Secure Storage**: Use encrypted storage for all sensitive data
3. **Data Masking**: Mask sensitive information in logs and UI
4. **Data Validation**: Validate all user inputs on client and server side
5. **Secure Communication**: Use TLS for all network requests
6. **Certificate Pinning**: Implement certificate pinning to prevent MITM attacks
7. **Token Security**: Store authentication tokens securely using encryption
8. **Biometric Authentication**: Use biometric authentication for sensitive operations
9. **Session Management**: Implement proper session management with token refresh
10. **Secure Backup**: Ensure sensitive data is not included in backups

### Code Security

1. **Proguard/R8**: Use code obfuscation to make reverse engineering more difficult
2. **Secure Coding**: Follow secure coding practices to prevent common vulnerabilities
3. **Regular Updates**: Keep all dependencies up to date to address security vulnerabilities
4. **Static Analysis**: Use static analysis tools to detect security issues
5. **Security Testing**: Conduct regular security testing
6. **Root/Jailbreak Detection**: Implement root and jailbreak detection
7. **Emulator Detection**: Detect if the app is running in an emulator
8. **Tamper Detection**: Detect if the app has been tampered with
9. **App Permissions**: Request minimal permissions and explain why they are needed
10. **Secure Dependencies**: Use only trusted dependencies and verify their integrity

## Security Checklist

- [ ] Implement secure authentication (JWT, OAuth)
- [ ] Store tokens and sensitive data securely
- [ ] Implement certificate pinning
- [ ] Add TLS for all network communications
- [ ] Validate all user inputs
- [ ] Encrypt sensitive data at rest
- [ ] Add biometric authentication for sensitive operations
- [ ] Implement proper session management
- [ ] Configure ProGuard/R8 for code obfuscation
- [ ] Add root/jailbreak detection
- [ ] Implement secure logging (no sensitive data in logs)
- [ ] Conduct regular security testing
- [ ] Keep all dependencies updated
- [ ] Use static analysis tools to detect security issues
- [ ] Implement proper error handling without exposing sensitive information
- [ ] Configure network security in AndroidManifest.xml
- [ ] Add expiry time for authentication tokens
- [ ] Implement proper permission handling
- [ ] Add secure backup mechanisms
- [ ] Include security in your CI/CD pipeline

## Resources

- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [OWASP Mobile Security Testing Guide](https://owasp.org/www-project-mobile-security-testing-guide/)
- [Jetpack Security](https://developer.android.com/topic/security/data)
- [Biometric Authentication](https://developer.android.com/training/sign-in/biometric-auth)
- [Proguard Configuration](https://developer.android.com/studio/build/shrink-code)
- [Network Security Configuration](https://developer.android.com/training/articles/security-config)
