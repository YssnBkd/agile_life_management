package com.example.agilelifemanagement.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Task
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation destinations for the main routes in the app
 */
sealed class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val hasUnreadContent: Boolean = false
) {
    // Main bottom navigation destinations
    object Dashboard : NavDestination(
        route = "dashboard",
        title = "Dashboard",
        icon = Icons.Rounded.Dashboard
    )
    
    object Sprint : NavDestination(
        route = "sprint",
        title = "Sprints",
        icon = Icons.Rounded.DirectionsRun
    )
    
    object Day : NavDestination(
        route = "day",
        title = "My Day",
        icon = Icons.Rounded.CalendarMonth
    )
    
    object Task : NavDestination(
        route = "task",
        title = "Tasks",
        icon = Icons.Rounded.Task
    )
}

/**
 * Navigation routes for nested destinations within each main route
 */
object NavRoutes {
    // Dashboard routes
    const val DASHBOARD_HOME = "dashboard/home"
    
    // Sprint routes
    const val SPRINT_LIST = "sprint/list"
    const val SPRINT_DETAIL = "sprint/detail/{sprintId}"
    const val SPRINT_EDITOR = "sprint/editor?sprintId={sprintId}"
    const val SPRINT_REVIEW = "sprint/review/{sprintId}"
    
    // Day routes
    const val DAY_TIMELINE = "day/timeline?date={date}"
    const val DAY_MORNING_CHECKIN = "day/morning-checkin?date={date}"
    const val DAY_JOURNALING = "day/journaling?date={date}"
    const val DAY_EVENING_CHECKIN = "day/evening-checkin?date={date}"
    const val DAY_WEEK_VIEW = "day/week-view?date={date}"
    const val DAY_TEMPLATE = "day/template"
    const val DAY_ACTIVITY_EDITOR = "day/activity-editor?activityId={activityId}"
    
    // Task routes
    const val TASK_BACKLOG = "task/backlog"
    const val TASK_DETAIL = "task/detail/{taskId}"
    const val TASK_EDITOR = "task/editor?taskId={taskId}"
}

// Extension functions for route creation with parameters
fun NavRoutes.sprintDetail(sprintId: String) = "sprint/detail/$sprintId"
fun NavRoutes.sprintEditor(sprintId: String? = null) = 
    if (sprintId != null) "sprint/editor?sprintId=$sprintId" else "sprint/editor"
fun NavRoutes.sprintReview(sprintId: String) = "sprint/review/$sprintId"

fun NavRoutes.dayTimeline(date: String? = null) =
    if (date != null) "day/timeline?date=$date" else "day/timeline"
fun NavRoutes.dayMorningCheckin(date: String? = null) =
    if (date != null) "day/morning-checkin?date=$date" else "day/morning-checkin"
fun NavRoutes.dayJournaling(date: String? = null) =
    if (date != null) "day/journaling?date=$date" else "day/journaling"
fun NavRoutes.dayEveningCheckin(date: String? = null) =
    if (date != null) "day/evening-checkin?date=$date" else "day/evening-checkin"
fun NavRoutes.dayWeekView(date: String? = null) =
    if (date != null) "day/week-view?date=$date" else "day/week-view"
fun NavRoutes.dayTemplate() = "day/template"
fun NavRoutes.dayActivityEditor(activityId: String? = null) =
    if (activityId != null) "day/activity-editor?activityId=$activityId" else "day/activity-editor"

fun NavRoutes.taskDetail(taskId: String) = "task/detail/$taskId"
fun NavRoutes.taskEditor(taskId: String? = null) =
    if (taskId != null) "task/editor?taskId=$taskId" else "task/editor"
