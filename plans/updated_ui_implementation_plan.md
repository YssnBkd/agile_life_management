# Updated AgileLifeManagement UI Implementation Plan

## Overview

This document provides an updated implementation plan for the AgileLifeManagement app UI, aligning our existing ViewModel implementations with the Material 3 Expressive design system and the original UI vision. This plan focuses on creating a cohesive, intuitive user experience that leverages our implemented data layer while following Material 3 design guidelines.

## Current Implementation Status

We've successfully implemented:

1. **ViewModels with Unidirectional Data Flow**:
   - TaskViewModel
   - SprintViewModel
   - GoalViewModel
   - DayActivityViewModel
   - WellnessViewModel
   - CategoryViewModel
   - DashboardViewModel

2. **Example Screen Implementations**:
   - TaskListScreenWithViewModel
   - SprintListScreenWithViewModel
   - DashboardScreenWithViewModel

However, these implementations need to be aligned with the original UI vision in terms of:
- Visual components matching Material 3 Expressive guidelines
- Feature completeness according to the original plan
- Proper navigation structure

## Tech Stack & Architecture (Unchanged)

- **UI Framework**: Jetpack Compose with Material 3 Expressive
- **Architecture**: MVVM with Clean Architecture principles
- **Navigation**: Jetpack Navigation Compose
- **Dependency Injection**: Hilt
- **Data Layer**: Room Database with Repository pattern
- **State Management**: Unidirectional Data Flow with ViewModels and StateFlow

## Implementation Refinement Strategy

### 1. Theme Implementation

First, we need to create a proper Material 3 Expressive theme that will be applied consistently across all screens:

```kotlin
@Composable
fun AgileLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AgileLifeDarkColors
        else -> AgileLifeLightColors
    }
    
    val typography = AgileLifeTypography
    val shapes = AgileLifeShapes

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
```

### 2. Navigation Structure

Implement a bottom navigation structure with the following destinations:
1. Dashboard (Home)
2. Sprints
3. Day
4. Tasks

```kotlin
@Composable
fun AgileLifeApp() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            AgileLifeBottomNavigation(navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") { DashboardScreen(navController) }
            composable("sprints") { SprintListScreen(navController) }
            composable("day") { DayTimelineScreen(navController) }
            composable("tasks") { TaskBacklogScreen(navController) }
            
            // Detail and editor screens
            composable("sprints/{sprintId}") { backStackEntry ->
                val sprintId = backStackEntry.arguments?.getString("sprintId")
                SprintDetailScreen(sprintId = sprintId ?: "", navController)
            }
            // Add more routes as needed
        }
    }
}
```

### 3. Screen Implementations

Each screen needs to be implemented according to the original design vision, while utilizing our existing ViewModels:

#### Dashboard Screen (Highest Priority)

The Dashboard screen should be a central hub showing:
- Current sprint summary
- Today's schedule timeline
- Active goals summary
- Quick actions FAB

```kotlin
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = { HomeTopAppBar() },
        floatingActionButton = { QuickActionFAB(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Current Sprint Summary Card
            item {
                uiState.activeSprint?.let { sprint ->
                    SprintSummaryCard(
                        sprint = sprint,
                        taskCount = uiState.sprintTasks.size,
                        completedTaskCount = uiState.sprintTasks.count { it.isCompleted },
                        onClick = { navController.navigate("sprints/${sprint.id}") }
                    )
                }
            }
            
            // Today's Timeline Card
            item {
                DayTimelineCard(
                    activities = uiState.todaysActivities,
                    tasks = uiState.todaysTasks,
                    onClick = { navController.navigate("day") }
                )
            }
            
            // Active Goals
            item {
                ActiveGoalsCard(
                    goals = uiState.activeGoals,
                    onGoalClick = { goalId -> navController.navigate("goals/$goalId") },
                    onViewAllClick = { navController.navigate("goals") }
                )
            }
            
            // Wellness Summary
            item {
                uiState.wellnessData?.let { checkup ->
                    WellnessSummaryCard(
                        dailyCheckup = checkup,
                        onClick = { navController.navigate("wellness") }
                    )
                }
            }
        }
    }
}
```

#### Sprint Module

The Sprint module should include:
- Sprint list with filtering options
- Sprint detail view with tabs (Overview, Calendar, Backlog)
- Sprint creation/editing
- Sprint review functionality

#### Day Module

The Day module should include:
- Timeline view with scheduled activities and tasks
- Morning check-in for mood and energy tracking
- Evening check-in for reflection
- Journaling capabilities

#### Task Management

The Task module should include:
- Backlog view with filtering and sorting
- Task details with dependencies and tags
- Task creation and editing
- Sprint and day assignment

### 4. Custom Components

The following custom components need to be implemented to support the UI vision:

#### Timeline Component
For visualizing day schedule with time blocks for activities and tasks

#### Calendar Component
For visualizing sprint timeline and task distribution

#### Mood & Energy Components
For wellness tracking with intuitive, visually expressive inputs

#### Expandable FAB
For providing quick actions from any screen

## Implementation Priority

1. **Dashboard Screen**: Central hub that ties all features together
2. **Task Management**: Core functionality for task tracking
3. **Sprint Module**: For sprint planning and management
4. **Day Module**: For daily planning and wellness tracking

## Material 3 Expressive Extensions

All components should follow these Expressive styling principles:

1. **Generous Spacing**: More breathing room between elements
2. **Dynamic Elevation**: Cards and surfaces with context-aware elevation
3. **Expressive Animations**: Natural, physics-based motion for transitions
4. **Rich Color Palette**: Utilize extended color roles beyond standard Material 3
5. **Contextual Emphasis**: Visual hierarchy that adapts to content importance

## Navigation Experience

The navigation should provide:
1. **Fluid Transitions**: Shared element transitions between related screens
2. **Contextual Navigation**: "Back" behavior that respects user journey context
3. **Deep Linking**: Support for direct navigation to specific tasks, sprints, etc.
4. **State Preservation**: Maintain scroll position and UI state during navigation

## Next Steps

1. Implement the AgileLifeTheme with proper Material 3 Expressive styling
2. Create the navigation structure with bottom bar
3. Start with the Dashboard screen implementation
4. Continue with Task Management screens
5. Implement Sprint Management
6. Implement Day Planning and Wellness tracking

## Conclusion

This updated plan aligns our existing ViewModel implementations with the original UI vision. By focusing on core screens first and incrementally adding more specialized features, we can deliver a cohesive and intuitive user experience that follows Material 3 Expressive design principles.
