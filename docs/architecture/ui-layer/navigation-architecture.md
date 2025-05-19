# Navigation Architecture

## Overview

This guide outlines the navigation implementation in the AgileLifeManagement application using Jetpack Navigation and Compose Navigation. Modern Android applications require a robust navigation system that handles deep linking, arguments passing, transitions, and back stack management. This document provides comprehensive guidance on implementing navigation using best practices across the application.

## Navigation Components

### Jetpack Navigation with Compose

The AgileLifeManagement application uses the Jetpack Navigation Compose library to manage navigation between screens:

```kotlin
dependencies {
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.0")
    
    // Hilt Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    
    // Testing
    testImplementation("androidx.navigation:navigation-testing:2.7.0")
}
```

## Navigation Structure

### Main Navigation Graph

Define the main navigation graph for the application:

```kotlin
@Composable
fun AgileLifeNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Dashboard screen
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onTaskClick = { taskId ->
                    navController.navigate("${Screen.TaskDetail.route}/$taskId")
                },
                onAddTaskClick = {
                    navController.navigate(Screen.AddTask.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // Task detail screen with arguments
        composable(
            route = "${Screen.TaskDetail.route}/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(
                taskId = taskId,
                onNavigateUp = { navController.navigateUp() },
                onEditClick = { navController.navigate("${Screen.EditTask.route}/$taskId") }
            )
        }
        
        // Add task screen
        composable(route = Screen.AddTask.route) {
            AddTaskScreen(
                onNavigateUp = { navController.navigateUp() },
                onTaskAdded = { navController.navigateUp() }
            )
        }
        
        // Edit task screen with arguments
        composable(
            route = "${Screen.EditTask.route}/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            EditTaskScreen(
                taskId = taskId,
                onNavigateUp = { navController.navigateUp() },
                onTaskUpdated = { navController.navigateUp() }
            )
        }
        
        // Settings screen
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

// Screen routes
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object TaskDetail : Screen("task_detail")
    object AddTask : Screen("add_task")
    object EditTask : Screen("edit_task")
    object Settings : Screen("settings")
}
```

### Nested Navigation Graphs

For more complex navigation flows, we can use nested navigation:

```kotlin
@Composable
fun AgileLifeNavHost(
    navController: NavHostController,
    startDestination: String = Graph.MAIN,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Main graph
        navigation(
            startDestination = Screen.Dashboard.route,
            route = Graph.MAIN
        ) {
            // Main screens...
            composable(route = Screen.Dashboard.route) { /* ... */ }
            composable(route = "${Screen.TaskDetail.route}/{taskId}") { /* ... */ }
            // More main screens...
        }
        
        // Authentication graph
        navigation(
            startDestination = AuthScreen.Login.route,
            route = Graph.AUTH
        ) {
            composable(route = AuthScreen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.popBackStack()
                        navController.navigate(Graph.MAIN)
                    },
                    onSignUpClick = {
                        navController.navigate(AuthScreen.SignUp.route)
                    }
                )
            }
            
            composable(route = AuthScreen.SignUp.route) {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.popBackStack()
                        navController.navigate(Graph.MAIN)
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        // Settings graph
        navigation(
            startDestination = SettingsScreen.Main.route,
            route = Graph.SETTINGS
        ) {
            composable(route = SettingsScreen.Main.route) { /* ... */ }
            composable(route = SettingsScreen.Account.route) { /* ... */ }
            composable(route = SettingsScreen.Notifications.route) { /* ... */ }
            // More settings screens...
        }
    }
}

// Navigation graphs
object Graph {
    const val MAIN = "main_graph"
    const val AUTH = "auth_graph"
    const val SETTINGS = "settings_graph"
}

// Auth screens
sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object SignUp : AuthScreen("sign_up")
    object ForgotPassword : AuthScreen("forgot_password")
}

// Settings screens
sealed class SettingsScreen(val route: String) {
    object Main : SettingsScreen("settings_main")
    object Account : SettingsScreen("settings_account")
    object Notifications : SettingsScreen("settings_notifications")
    object Theme : SettingsScreen("settings_theme")
}
```

## Application Structure

### Main Activity and Compose Entry Point

Setup the main activity as the entry point with the navigation controller:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val navController = rememberNavController()
            
            // Observe authentication state
            val viewModel: MainViewModel = hiltViewModel()
            val isAuthenticated by viewModel.isAuthenticated.collectAsState(initial = false)
            
            // Set start destination based on authentication
            val startDestination = if (isAuthenticated) Graph.MAIN else Graph.AUTH
            
            AppTheme {
                AgileLifeNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    val isAuthenticated: Flow<Boolean> = authRepository.isUserAuthenticated()
}
```

## Navigation State Management

### Using a NavigationViewModel

For more complex navigation scenarios, use a ViewModel to manage navigation state:

```kotlin
@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // Navigation events as StateFlow to observe in Composables
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()
    
    // Track current screen
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()
    
    // Previous screens stack
    private val backStack = mutableListOf<Screen>()
    
    // Authentication check
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    init {
        viewModelScope.launch {
            authRepository.isUserAuthenticated().collect { isAuthenticated ->
                _isAuthenticated.value = isAuthenticated
                
                // Navigate to appropriate screen based on authentication
                if (!isAuthenticated) {
                    navigateTo(NavigationEvent.ToLogin)
                }
            }
        }
    }
    
    fun navigateTo(event: NavigationEvent) {
        viewModelScope.launch {
            when (event) {
                is NavigationEvent.ToTaskDetail -> {
                    // Check authentication before navigating
                    if (_isAuthenticated.value) {
                        backStack.add(_currentScreen.value)
                        _currentScreen.value = Screen.TaskDetail
                        _navigationEvent.emit(event)
                    } else {
                        _navigationEvent.emit(NavigationEvent.ToLogin)
                    }
                }
                is NavigationEvent.ToAddTask -> {
                    if (_isAuthenticated.value) {
                        backStack.add(_currentScreen.value)
                        _currentScreen.value = Screen.AddTask
                        _navigationEvent.emit(event)
                    } else {
                        _navigationEvent.emit(NavigationEvent.ToLogin)
                    }
                }
                // Handle other navigation events...
                is NavigationEvent.Back -> {
                    if (backStack.isNotEmpty()) {
                        _currentScreen.value = backStack.removeLast()
                        _navigationEvent.emit(NavigationEvent.Back)
                    }
                }
            }
        }
    }
}

// Navigation events
sealed class NavigationEvent {
    object ToLogin : NavigationEvent()
    object ToSignUp : NavigationEvent()
    object ToDashboard : NavigationEvent()
    data class ToTaskDetail(val taskId: String) : NavigationEvent()
    object ToAddTask : NavigationEvent()
    data class ToEditTask(val taskId: String) : NavigationEvent()
    object ToSettings : NavigationEvent()
    object Back : NavigationEvent()
}

// In your composable
@Composable
fun MyApp(viewModel: NavigationViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    
    // Listen for navigation events
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToLogin -> navController.navigate(Graph.AUTH)
                is NavigationEvent.ToDashboard -> navController.navigate(Screen.Dashboard.route)
                is NavigationEvent.ToTaskDetail -> 
                    navController.navigate("${Screen.TaskDetail.route}/${event.taskId}")
                is NavigationEvent.ToAddTask -> navController.navigate(Screen.AddTask.route)
                is NavigationEvent.ToEditTask -> 
                    navController.navigate("${Screen.EditTask.route}/${event.taskId}")
                is NavigationEvent.ToSettings -> navController.navigate(Graph.SETTINGS)
                is NavigationEvent.Back -> navController.navigateUp()
                // Handle other events...
            }
        }
    }
    
    AgileLifeNavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Graph.MAIN else Graph.AUTH
    )
}
```

## Deep Linking

### Implementing Deep Links

Configure deep links to allow direct navigation to specific screens:

```kotlin
@Composable
fun AgileLifeNavHost(
    navController: NavHostController,
    startDestination: String = Graph.MAIN,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Main graph
        navigation(
            startDestination = Screen.Dashboard.route,
            route = Graph.MAIN
        ) {
            composable(
                route = Screen.Dashboard.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "agilelife://dashboard"
                        action = Intent.ACTION_VIEW
                    }
                )
            ) {
                DashboardScreen(/* ... */)
            }
            
            composable(
                route = "${Screen.TaskDetail.route}/{taskId}",
                arguments = listOf(
                    navArgument("taskId") { type = NavType.StringType }
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "agilelife://tasks/{taskId}"
                        action = Intent.ACTION_VIEW
                    }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                TaskDetailScreen(taskId = taskId, /* ... */)
            }
            
            // More screens...
        }
        
        // Other navigation graphs...
    }
}
```

And add intent filters to your AndroidManifest.xml:

```xml
<activity
    android:name=".MainActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
    
    <!-- Deep linking -->
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        
        <data
            android:scheme="agilelife"
            android:host="dashboard" />
    </intent-filter>
    
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        
        <data
            android:scheme="agilelife"
            android:host="tasks"
            android:pathPattern="/.*" />
    </intent-filter>
</activity>
```

## ViewModel Integration with Navigation

### Using SavedStateHandle for Arguments

Get navigation arguments in ViewModels using SavedStateHandle:

```kotlin
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskUseCase: GetTaskUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val taskId: String = checkNotNull(savedStateHandle["taskId"])
    
    private val _uiState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState.Loading)
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadTask()
    }
    
    private fun loadTask() {
        viewModelScope.launch {
            _uiState.value = TaskDetailUiState.Loading
            
            try {
                getTaskUseCase(taskId).collect { task ->
                    _uiState.value = if (task != null) {
                        TaskDetailUiState.Success(task)
                    } else {
                        TaskDetailUiState.Error("Task not found")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = TaskDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class TaskDetailUiState {
    object Loading : TaskDetailUiState()
    data class Success(val task: Task) : TaskDetailUiState()
    data class Error(val message: String) : TaskDetailUiState()
}
```

### Passing Complex Objects

For passing complex objects between destinations, use a NavType.StringType argument and serialize/deserialize the object:

```kotlin
// Navigation graph setup
composable(
    route = "${Screen.TaskPreview.route}/{taskJson}",
    arguments = listOf(
        navArgument("taskJson") {
            type = NavType.StringType
        }
    )
) { backStackEntry ->
    val taskJson = backStackEntry.arguments?.getString("taskJson") ?: ""
    TaskPreviewScreen(
        taskJson = taskJson,
        onNavigateUp = { navController.navigateUp() }
    )
}

// Serializing and navigating
val task = Task(id = "123", title = "Sample Task", /* other properties */)
val taskJson = Uri.encode(Json.encodeToString(task))
navController.navigate("${Screen.TaskPreview.route}/$taskJson")

// In the destination ViewModel
@HiltViewModel
class TaskPreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskJson: String = checkNotNull(savedStateHandle["taskJson"])
    
    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()
    
    init {
        try {
            val decodedJson = Uri.decode(taskJson)
            _task.value = Json.decodeFromString<Task>(decodedJson)
        } catch (e: Exception) {
            Timber.e(e, "Error parsing task JSON")
        }
    }
}
```

## Animations and Transitions

### Navigation Animations

Add animations to navigation transitions:

```kotlin
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedAgileLifeNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route,
    modifier: Modifier = Modifier
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = Screen.Dashboard.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            DashboardScreen(/* ... */)
        }
        
        composable(
            route = "${Screen.TaskDetail.route}/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            ),
            enterTransition = { 
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            },
            exitTransition = { 
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = { 
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = { 
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(taskId = taskId, /* ... */)
        }
        
        // More animated screens...
    }
}
```

### Shared Element Transitions

Implement shared element transitions for a more polished UX:

```kotlin
// First, add the dependency
implementation("androidx.compose.animation:animation:1.5.0")

// Create a shared transition state
class SharedElementsRootScope : SharedElementsScope {
    val state = SharedElementsTransitionState()
    override val transition get() = state
}

// In your composable 
@Composable
fun MyApp() {
    val rootScope = remember { SharedElementsRootScope() }
    val navController = rememberNavController()
    
    CompositionLocalProvider(
        LocalSharedElementsScope provides rootScope
    ) {
        AnimatedAgileLifeNavHost(navController = navController)
    }
}

// In your source screen
@Composable
fun TaskListItem(
    task: Task,
    onClick: () -> Unit
) {
    val localScope = LocalSharedElementsScope.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            .shared(task.id) // Mark for shared element transition
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = task.dueDate?.toString() ?: "No due date",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// In your destination screen
@Composable
fun TaskDetailScreen(task: Task, /* other params */) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .shared(task.id) // Same key as source element
    ) {
        // Screen content
    }
}
```

## Navigation Testing

### Testing Navigation Components

Write tests for navigation flows:

```kotlin
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testNavigationToDashboard() {
        // Set up UI
        composeTestRule.setContent {
            val navController = rememberNavController()
            AgileLifeNavHost(navController = navController)
        }
        
        // Verify current screen is Dashboard
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
    }
    
    @Test
    fun testNavigationToTaskDetail() {
        // Set up UI with mocked tasks
        composeTestRule.setContent {
            val navController = rememberNavController()
            CompositionLocalProvider(
                LocalTaskProvider provides FakeTaskRepository()
            ) {
                AgileLifeNavHost(navController = navController)
            }
        }
        
        // Find and click a task
        composeTestRule.onNodeWithText("Sample Task").performClick()
        
        // Verify navigation to task detail
        composeTestRule.onNodeWithText("Task Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sample Task").assertIsDisplayed()
    }
}

// Fake task repository for testing
class FakeTaskRepository : TaskRepository {
    override fun getTasks(): Flow<List<Task>> = flowOf(
        listOf(
            Task(id = "1", title = "Sample Task", description = "Sample description"),
            Task(id = "2", title = "Another Task", description = "Another description")
        )
    )
    
    override fun getTask(id: String): Flow<Task?> = flowOf(
        when (id) {
            "1" -> Task(id = "1", title = "Sample Task", description = "Sample description")
            "2" -> Task(id = "2", title = "Another Task", description = "Another description")
            else -> null
        }
    )
    
    // Other repository methods...
}
```

### Testing with Hilt and Navigation

For testing navigation with Hilt:

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationWithHiltTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    
    @Inject
    lateinit var taskRepository: TaskRepository
    
    @Before
    fun setup() {
        hiltRule.inject()
        
        // Set up UI
        composeTestRule.setContent {
            val navController = rememberNavController()
            AgileLifeNavHost(navController = navController)
        }
    }
    
    @Test
    fun testNavigationFlow() {
        // Verify dashboard is displayed
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        
        // Click add task button
        composeTestRule.onNodeWithContentDescription("Add Task").performClick()
        
        // Verify add task screen is displayed
        composeTestRule.onNodeWithText("Add Task").assertIsDisplayed()
        
        // Fill task details
        composeTestRule.onNodeWithText("Title").performTextInput("New Test Task")
        composeTestRule.onNodeWithText("Description").performTextInput("New Test Description")
        
        // Click save button
        composeTestRule.onNodeWithText("Save Task").performClick()
        
        // Verify navigation back to dashboard
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        
        // Verify new task is displayed
        composeTestRule.onNodeWithText("New Test Task").assertIsDisplayed()
    }
}
```

## Navigation Best Practices

1. **Single Activity**: Use a single activity with multiple composable destinations
2. **Type Safety**: Use sealed classes for route definitions to ensure type safety
3. **Argument Validation**: Always validate arguments using `checkNotNull` or safe calls
4. **ViewModel Scope**: Keep ViewModels scoped to the appropriate navigation destinations
5. **Deep Links**: Implement deep links for important screens
6. **Transitions**: Use meaningful transitions between screens
7. **State Handling**: Properly handle process death by saving and restoring navigation state
8. **Back Stack Management**: Be mindful of the back stack and use `popUpTo` and `inclusive` flags when appropriate
9. **Navigation Events**: Use events (SharedFlow) for one-time navigation actions
10. **Testing**: Write comprehensive tests for navigation flows

## Handling Configuration Changes

Preserving navigation state during configuration changes:

```kotlin
// In your Activity or composable
val savedStateHandle = rememberSaveable(saver = NavControllerSaver()) {
    mutableStateOf(
        NavHostController(LocalContext.current)
    )
}
val navController = savedStateHandle.value

// NavController saver
class NavControllerSaver(private val context: Context) : Saver<NavHostController, Bundle> {
    override fun restore(value: Bundle): NavHostController {
        val controller = NavHostController(context)
        controller.restoreState(value)
        return controller
    }
    
    override fun SaverScope.save(value: NavHostController): Bundle? {
        return value.saveState()
    }
}

// Usage with proper context
@Composable
fun rememberNavControllerWithSaver(): NavHostController {
    val context = LocalContext.current
    return rememberSaveable(saver = NavControllerSaver(context)) {
        NavHostController(context)
    }
}
```

## Resources

- [Jetpack Navigation Compose documentation](https://developer.android.com/jetpack/compose/navigation)
- [Advanced Navigation Patterns](https://medium.com/androiddevelopers/animations-in-navigation-compose-36d48870776b)
- [Testing Navigation Components](https://developer.android.com/jetpack/compose/testing-cheatsheet)
- [Deep linking with Compose Navigation](https://developer.android.com/jetpack/compose/navigation#deeplinks)
- [Hilt with Navigation Compose](https://developer.android.com/jetpack/compose/libraries#hilt-navigation)
