# Material 3 Expressive Implementation

## Overview

Material 3 Expressive is an extension of Material 3 that allows for more creative freedom and customization while maintaining the core principles of Material Design. This document provides implementation guidelines for incorporating Material 3 Expressive into Android applications using Jetpack Compose.

## Key Concepts

### Dynamic Color and Theming

Material 3 Expressive builds upon Material 3's dynamic color system but allows for more customization:

- **Custom Color Schemes**: Create unique brand expressions while maintaining accessibility and coherence
- **Extended Color Palettes**: Access to additional tonal ranges beyond the standard Material 3 palette
- **Color Role Mapping**: Redefine how colors are applied to various UI components

### Typography System

- **Custom Type Scales**: Define unique typography scales that reflect brand identity
- **Variable Fonts Support**: Utilize variable fonts for more expressive typography
- **Typography Role Mapping**: Customize how type styles are applied to different UI elements

### Shape System

- **Custom Shape Scales**: Define unique corner styles and dimensions
- **Component-Specific Shapes**: Apply different shape treatments to specific components
- **Shape Role Mapping**: Redefine how shapes are applied across the UI

### Animation and Motion

- **Custom Motion Patterns**: Define unique animation patterns for transitions and interactions
- **Expressive Transitions**: Create more dynamic and engaging transitions between screens and states
- **Interactive Feedback**: Enhance user feedback through custom motion designs

## Implementation in Jetpack Compose

### Setting Up Material 3 Expressive Theme

```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine the color scheme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> customDarkColorScheme
        else -> customLightColorScheme
    }

    // Custom typography
    val typography = customTypography

    // Custom shapes
    val shapes = customShapes

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
```

### Custom Color Schemes

```kotlin
// Light theme custom colors
val customLightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005E),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1E192B),
    tertiary = Color(0xFF7E5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9E3),
    onTertiaryContainer = Color(0xFF31101D),
    // Add other colors as needed
)

// Dark theme custom colors
val customDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF371E73),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD9E3),
    // Add other colors as needed
)
```

### Custom Typography

```kotlin
val customTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    // Define other text styles
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    // Add other typography styles as needed
)
```

### Custom Shapes

```kotlin
val customShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)
```

## Component Customization

### Buttons

```kotlin
@Composable
fun ExpressiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = contentPadding,
        content = content
    )
}
```

### Cards

```kotlin
@Composable
fun ExpressiveCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
}
```

### Text Fields

```kotlin
@Composable
fun ExpressiveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    shape: Shape = MaterialTheme.shapes.small
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
```

## Integration with Architecture Components

### ViewModel Integration

```kotlin
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getTasksUseCase().collect { tasks ->
                    _uiState.update { 
                        it.copy(
                            tasks = tasks,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    ) 
                }
            }
        }
    }
    
    fun toggleTaskCompleted(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            try {
                val status = if (completed) TaskStatus.COMPLETED else TaskStatus.IN_PROGRESS
                updateTaskStatusUseCase(taskId, status)
                // The Flow collection in loadTasks() will pick up the change
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
```

### UI Component with Material 3 Expressive

```kotlin
@Composable
fun TasksScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    navigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Tasks") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add task */ },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        TasksContent(
            modifier = Modifier.padding(padding),
            tasksUiState = uiState,
            onTaskClick = navigateToDetail,
            onTaskCheckChanged = viewModel::toggleTaskCompleted
        )
    }
}

@Composable
private fun TasksContent(
    modifier: Modifier = Modifier,
    tasksUiState: TasksUiState,
    onTaskClick: (String) -> Unit,
    onTaskCheckChanged: (String, Boolean) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            tasksUiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            tasksUiState.error != null -> {
                ExpressiveErrorMessage(
                    message = tasksUiState.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            tasksUiState.tasks.isEmpty() -> {
                ExpressiveEmptyState(
                    message = "No tasks yet",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn {
                    items(tasksUiState.tasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = { onTaskClick(task.id) },
                            onCheckChange = { completed -> 
                                onTaskCheckChanged(task.id, completed)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit
) {
    ExpressiveCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onTaskClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.status == TaskStatus.COMPLETED,
                onCheckedChange = onCheckChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.status == TaskStatus.COMPLETED) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )
                
                task.description?.let { description ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            TaskStatusChip(status = task.status)
        }
    }
}

@Composable
fun TaskStatusChip(status: TaskStatus) {
    val (containerColor, contentColor, label) = when (status) {
        TaskStatus.TODO -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "TODO"
        )
        TaskStatus.IN_PROGRESS -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "IN PROGRESS"
        )
        TaskStatus.COMPLETED -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "COMPLETED"
        )
    }
    
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ExpressiveErrorMessage(
    message: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = message ?: "An unknown error occurred",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ExpressiveEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
```

## Animation Patterns

### Animated State Changes

```kotlin
@Composable
fun AnimatedTaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    
    // Animate color and alpha when completion status changes
    val backgroundColor by animateColorAsState(
        targetValue = if (isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "Background color"
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (isCompleted) 0.7f else 1f,
        label = "Content alpha"
    )
    
    ExpressiveCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onTaskClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onCheckChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = contentAlpha }
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )
                
                task.description?.let { description ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(Modifier.width(8.dp))
            
            TaskStatusChip(status = task.status)
        }
    }
}
```

### List Animations

```kotlin
@Composable
fun AnimatedTaskList(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onTaskCheckChanged: (String, Boolean) -> Unit
) {
    LazyColumn {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            AnimatedVisibility(
                visible = true,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                AnimatedTaskItem(
                    task = task,
                    onTaskClick = { onTaskClick(task.id) },
                    onCheckChange = { completed -> 
                        onTaskCheckChanged(task.id, completed)
                    }
                )
            }
        }
    }
}
```

## Accessibility Considerations

### Theming for Accessibility

```kotlin
@Composable
fun AccessibleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false, // User preference for high contrast
    largeText: Boolean = false, // User preference for large text
    content: @Composable () -> Unit
) {
    // Determine the color scheme
    val colorScheme = when {
        highContrast && darkTheme -> highContrastDarkColorScheme
        highContrast -> highContrastLightColorScheme
        darkTheme -> customDarkColorScheme
        else -> customLightColorScheme
    }
    
    // Adjust typography for large text
    val typography = if (largeText) {
        largeTextTypography
    } else {
        customTypography
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = customShapes,
        content = content
    )
}

// High contrast color schemes
val highContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF0000BB),
    onPrimary = Color(0xFFFFFFFF),
    // Other high contrast colors...
)

val highContrastDarkColorScheme = darkColorScheme(
    primary = Color(0xFFBBBBFF),
    onPrimary = Color(0xFF000000),
    // Other high contrast colors...
)

// Large text typography
val largeTextTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 68.sp,
        lineHeight = 76.sp,
        // Other properties...
    ),
    // Other text styles with increased size...
    bodyLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        // Other properties...
    )
)
```

### Semantic Properties

```kotlin
@Composable
fun AccessibleTaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    
    ExpressiveCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onTaskClick)
            .semantics {
                contentDescription = "Task: ${task.title}, Status: ${task.status.name}"
                stateDescription = if (isCompleted) "Completed" else "Not completed"
                
                // Make the entire card act as a toggle
                role = Role.Checkbox
                toggleableState = if (isCompleted) ToggleableState.On else ToggleableState.Off
                onClick {
                    onCheckChange(!isCompleted)
                    true
                }
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        // Card content similar to previous examples
    }
}
```

## Integration with Navigation

```kotlin
@Composable
fun AgileLifeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "tasks",
        modifier = modifier
    ) {
        composable("tasks") {
            TasksScreen(
                navigateToDetail = { taskId ->
                    navController.navigate("taskDetail/$taskId") {
                        // Configure animations
                        popUpTo("tasks")
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(
            route = "taskDetail/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

## Best Practices for Material 3 Expressive Implementation

1. **Follow Brand Guidelines**: Use custom colors and shapes that align with brand identity while maintaining accessibility
2. **Consistent Motion Design**: Define animation patterns and apply them consistently throughout the app
3. **Hierarchical Typography**: Use the type scale to establish clear visual hierarchy
4. **Responsive Design**: Design components to adapt to different screen sizes and orientations
5. **Accessibility First**: Always consider accessibility when customizing components
6. **Surface Layering**: Use elevation and surfaces to create meaningful spatial relationships
7. **Interactive States**: Clearly indicate interactive elements and their states
8. **Color Contrast**: Maintain proper contrast ratios between background and content
9. **Predictable Animations**: Use motion to guide users, not confuse them
10. **Unified Design System**: Create reusable component abstractions that maintain consistency

## Resources

- [Material 3 Design Guidelines](https://m3.material.io/)
- [Jetpack Compose Material 3 Documentation](https://developer.android.com/jetpack/compose/designsystems/material3)
- [Compose Animation Documentation](https://developer.android.com/jetpack/compose/animation)
- [Accessibility in Compose](https://developer.android.com/jetpack/compose/accessibility)
- [Material 3 Components](https://m3.material.io/components)
