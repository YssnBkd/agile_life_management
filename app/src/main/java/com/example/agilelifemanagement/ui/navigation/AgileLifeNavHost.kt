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
import com.example.agilelifemanagement.ui.screens.dashboard.DashboardScreenWithViewModel
import com.example.agilelifemanagement.ui.screens.day.DayTimelineScreen
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
        DashboardScreenWithViewModel(
            onTaskClick = { taskId -> navController.navigate("tasks/$taskId") },
            onSprintClick = { sprintId -> navController.navigate("sprints/$sprintId") },
            onGoalClick = { goalId -> navController.navigate("goals/$goalId") },
            onDayActivityClick = { activityId -> navController.navigate("day/activities/$activityId") },
            onWellnessClick = { navController.navigate("wellness") },
            onAllTasksClick = { navController.navigate(BottomNavDestination.Tasks.route) },
            onAllSprintsClick = { navController.navigate(BottomNavDestination.Sprints.route) },
            onAllGoalsClick = { navController.navigate("goals") }
        )
    }
    
    composable(BottomNavDestination.Tasks.route) {
        TaskBacklogScreen(navController = navController)
    }
    
    composable(BottomNavDestination.Sprints.route) {
        SprintListScreen(navController = navController)
    }
    
    composable(BottomNavDestination.Day.route) {
        DayTimelineScreen(navController = navController)
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
        TaskDetailScreen(
            taskId = taskId,
            navController = navController
        )
    }
    
    composable("tasks/create") {
        TaskEditorScreen(
            taskId = null,
            navController = navController
        )
    }
    
    composable(
        route = "tasks/edit/{taskId}",
        arguments = listOf(navArgument("taskId") { type = NavType.StringType })
    ) { backStackEntry ->
        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
        TaskEditorScreen(
            taskId = taskId,
            navController = navController
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
            sprintId = sprintId,
            navController = navController
        )
    }
    
    composable("sprints/create") {
        SprintEditorScreen(
            sprintId = null,
            navController = navController
        )
    }
    
    composable(
        route = "sprints/edit/{sprintId}",
        arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
    ) { backStackEntry ->
        val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
        SprintEditorScreen(
            sprintId = sprintId,
            navController = navController
        )
    }
}

/**
 * Day and wellness related navigation destinations
 */
private fun NavGraphBuilder.dayNavigation(navController: NavController) {
    composable("wellness") {
        WellnessScreen(navController = navController)
    }
}
