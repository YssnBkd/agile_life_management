package com.example.agilelifemanagement.ui.navigation

/**
 * Navigation routes for the Agile Life Management app.
 */
object NavRoutes {
    // Main screens
    const val CALENDAR = "calendar"
    const val SPRINTS = "sprints"
    const val GOALS = "goals"
    const val TASKS = "tasks"
    const val DAILY_CHECKUP = "daily_checkup"
    const val SPRINT_REVIEW = "sprint_review"
    const val SETTINGS = "settings"
    
    // Detail screens
    const val SPRINT_DETAIL = "sprint_detail"
    const val GOAL_DETAIL = "goal_detail"
    const val TASK_DETAIL = "task_detail"
    
    // Arguments
    const val SPRINT_ID_ARG = "sprintId"
    const val GOAL_ID_ARG = "goalId"
    const val TASK_ID_ARG = "taskId"
    
    // Routes with arguments
    fun sprintDetail(sprintId: String = "{$SPRINT_ID_ARG}") = "$SPRINT_DETAIL/$sprintId"
    fun goalDetail(goalId: String = "{$GOAL_ID_ARG}") = "$GOAL_DETAIL/$goalId"
    fun taskDetail(taskId: String = "{$TASK_ID_ARG}") = "$TASK_DETAIL/$taskId"
}
