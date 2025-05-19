package com.example.agilelifemanagement.ui.model

import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import com.example.agilelifemanagement.ui.screens.dashboard.TaskInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * UI model representing a day's data including tasks, timeline and wellness metrics.
 */
data class DayData(
    val id: String,
    val date: LocalDate,
    val mood: Int = 3, // 1-5 scale
    val sleepHours: Float = 0f,
    val energyLevel: Int = 3, // 1-5 scale
    val focusLevel: Int = 3, // 1-5 scale
    val productivityLevel: Int = 3, // 1-5 scale
    val exerciseMinutes: Int = 0,
    val waterGlasses: Int = 0,
    val dailyNote: String = "",
    val timeBlocks: List<TimeBlock> = emptyList(),
    val tasks: List<TaskInfo> = emptyList()
) {
    // Formatted date for display (e.g. "MAY 15")
    val formattedDate: String
        get() = date.format(DateTimeFormatter.ofPattern("MMM d")).uppercase()
    
    // Day of week (e.g. "Monday")
    val dayOfWeek: String
        get() = date.format(DateTimeFormatter.ofPattern("EEEE"))
    
    // Long formatted date (e.g. "Monday, May 15, 2023")
    val longFormattedDate: String
        get() = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
}

/**
 * Sample data provider for previews and testing.
 */
object SampleDayData {
    fun getDay(id: String): DayData {
        return DayData(
            id = id,
            date = LocalDate.now(),
            mood = 4,
            sleepHours = 7.5f,
            energyLevel = 4,
            focusLevel = 3,
            productivityLevel = 4,
            exerciseMinutes = 45,
            waterGlasses = 6,
            dailyNote = "Made good progress on the presentation. Need to follow up with team about next sprint planning.",
            timeBlocks = TimeBlock.getSampleTimeBlocks(),
            tasks = listOf(
                TaskInfo(
                    id = "1",
                    title = "Complete project proposal",
                    "Draft and send the project proposal to the client",
                    priority = TaskPriority.HIGH,
                    dueDate = "Today, 17:00",
                    isCompleted = false,
                    estimatedMinutes = 120
                ),
                TaskInfo(
                    id = "2",
                    title = "Daily team standup",
                    "Discuss progress and blockers with the team",
                    priority = TaskPriority.MEDIUM,
                    dueDate = "Today, 09:00",
                    isCompleted = true,
                    estimatedMinutes = 30
                ),
                TaskInfo(
                    id = "3",
                    title = "Review pull request #42",
                    "Code review for the authentication feature",
                    priority = TaskPriority.MEDIUM,
                    dueDate = "Today, 16:00",
                    isCompleted = false,
                    estimatedMinutes = 45
                )
            )
        )
    }
}
