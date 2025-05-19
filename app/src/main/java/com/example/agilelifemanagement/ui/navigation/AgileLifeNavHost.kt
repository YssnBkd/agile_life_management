package com.example.agilelifemanagement.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.agilelifemanagement.ui.screens.dashboard.DashboardScreen
import com.example.agilelifemanagement.ui.screens.day.DayDetailScreenSimple
import com.example.agilelifemanagement.ui.screens.day.DayPlannerScreenSimple
import com.example.agilelifemanagement.ui.screens.day.EnhancedDayPlannerScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintDetailScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintEditorScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintListScreen
import com.example.agilelifemanagement.ui.screens.task.TaskBacklogScreen
import com.example.agilelifemanagement.ui.screens.task.TaskDetailScreen
import com.example.agilelifemanagement.ui.screens.task.TaskEditorScreen
import com.example.agilelifemanagement.ui.screens.wellness.WellnessScreen

/**
 * Duration for the animated transitions between destinations
 */
private const val NAVIGATION_ANIMATION_DURATION = 300

/**
 * Navigation host for the AgileLife app
 * Implements Material 3 Expressive motion patterns with shared element transitions
 */
@Composable
fun AgileLifeNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavDestination.Dashboard.route,
        modifier = modifier.padding(paddingValues),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(NAVIGATION_ANIMATION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION)
            ) + fadeOut(animationSpec = tween(NAVIGATION_ANIMATION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(NAVIGATION_ANIMATION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION)
            ) + fadeOut(animationSpec = tween(NAVIGATION_ANIMATION_DURATION))
        }
    ) {
        // Main bottom navigation destinations
        addBottomNavigationDestinations(navController)
        
        // Task related screens
        taskNavigation(navController)
        
        // Sprint related screens
        sprintNavigation(navController)
        
        // Day and wellness screens
        dayNavigation(navController)
    }
}

/**
 * Add the main bottom navigation destinations to the NavGraphBuilder
 */
private fun NavGraphBuilder.addBottomNavigationDestinations(navController: NavController) {
    composable(BottomNavDestination.Dashboard.route) {
        DashboardScreen(
            onTaskClick = { taskId -> navController.navigate("tasks/$taskId") },
            onSprintClick = { sprintId -> navController.navigate("sprints/$sprintId") },
            onGoalClick = { goalId -> navController.navigate("goals/$goalId") },
            onDayActivityClick = { date -> navController.navigate("day/timeline?date=$date") },
            onWellnessClick = { navController.navigate("wellness") },
            onAllTasksClick = { navController.navigate(BottomNavDestination.Tasks.route) },
            onAllSprintsClick = { navController.navigate("sprints") },
            onAllGoalsClick = { navController.navigate("goals") }
        )
    }
    
    composable(BottomNavDestination.Tasks.route) {
        // Using our new Material 3 Expressive TaskBacklogScreen implementation
        com.example.agilelifemanagement.ui.screens.task.TaskBacklogScreen(
            navController = navController
        )
    }
    
    composable(BottomNavDestination.Sprints.route) {
        // Using the proper ViewModel implementation for Sprints
        com.example.agilelifemanagement.ui.screens.sprint.SprintListScreen(
            onSprintClick = { sprintId -> navController.navigate("sprints/$sprintId") },
            onAddSprintClick = { navController.navigate("sprints/create") },
            onBackClick = { navController.navigateUp() }
        )
    }
    
    composable(BottomNavDestination.Day.route) {
        // Using our enhanced day planner as the main day screen
        EnhancedDayPlannerScreen(
            navController = navController,
            selectedDate = java.time.LocalDate.now()
        )
    }
}

/**
 * Task related navigation destinations
 */
private fun NavGraphBuilder.taskNavigation(navController: NavController) {
    composable(
        route = "tasks/{taskId}",
        arguments = listOf(navArgument("taskId") { type = NavType.StringType })
    ) { backStackEntry ->
        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
        // Use our Material 3 Expressive TaskDetailScreen
        com.example.agilelifemanagement.ui.screens.task.TaskDetailScreen(
            navController = navController,
            taskId = taskId
        )
    }
    
    composable("tasks/create") {
        // Use our Material 3 Expressive TaskEditorScreen
        com.example.agilelifemanagement.ui.screens.task.TaskEditorScreen(
            navController = navController,
            taskId = null
        )
    }
    
    composable(
        route = "tasks/edit/{taskId}",
        arguments = listOf(navArgument("taskId") { type = NavType.StringType })
    ) { backStackEntry ->
        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
        // Use our Material 3 Expressive TaskEditorScreen
        com.example.agilelifemanagement.ui.screens.task.TaskEditorScreen(
            navController = navController,
            taskId = taskId
        )
    }
}

/**
 * Sprint related navigation destinations
 */
private fun NavGraphBuilder.sprintNavigation(navController: NavController) {
    composable(
        route = "sprints/{sprintId}",
        arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
    ) { backStackEntry ->
        val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
        SprintDetailScreen(
            navController = navController,
            sprintId = sprintId
        )
    }
    
    composable("sprints/create") {
        SprintEditorScreen(
            navController = navController,
            sprintId = null
        )
    }
    
    composable(
        route = "sprints/edit/{sprintId}",
        arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
    ) { backStackEntry ->
        val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
        SprintEditorScreen(
            navController = navController,
            sprintId = sprintId
        )
    }
}

/**
 * Day and wellness related navigation destinations
 */
private fun NavGraphBuilder.dayNavigation(navController: NavController) {
    // Day timeline screen with optional date parameter
    composable(
        route = "day/timeline?date={date}",
        arguments = listOf(
            navArgument("date") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val dateString = backStackEntry.arguments?.getString("date")
        val selectedDate = dateString?.let {
            java.time.LocalDate.parse(it)
        } ?: java.time.LocalDate.now()
        
        // Using our simplified implementation
        DayDetailScreenSimple(
            navController = navController,
            selectedDate = selectedDate
        )
    }
    
    // Day activity detail - to be implemented
    composable(
        route = "day/activities/{activityId}",
        arguments = listOf(
            navArgument("activityId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
        // Placeholder for activity detail screen
        // Will be implemented in a future update
    }
    
    // Day planner screen
    composable(
        route = "day/planner?date={date}",
        arguments = listOf(
            navArgument("date") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val dateString = backStackEntry.arguments?.getString("date")
        val selectedDate = dateString?.let {
            java.time.LocalDate.parse(it)
        } ?: java.time.LocalDate.now()
        
        DayPlannerScreenSimple(
            navController = navController,
            selectedDate = selectedDate
        )
    }
    
    // Enhanced Day planner screen with timeline view
    composable(
        route = "day/planner/enhanced?date={date}",
        arguments = listOf(
            navArgument("date") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val dateString = backStackEntry.arguments?.getString("date")
        val selectedDate = dateString?.let {
            java.time.LocalDate.parse(it)
        } ?: java.time.LocalDate.now()
        
        EnhancedDayPlannerScreen(
            navController = navController,
            selectedDate = selectedDate
        )
    }
    
    // Wellness screen
    composable("wellness") {
        WellnessScreen(navController = navController)
    }
}
