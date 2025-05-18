package com.example.agilelifemanagement.domain.usecase.dashboard

import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.domain.repository.DayRepository
import com.example.agilelifemanagement.domain.repository.SprintRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for generating a dashboard summary containing key metrics.
 */
class GetDashboardSummaryUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val sprintRepository: SprintRepository,
    private val dayRepository: DayRepository
) {
    data class DashboardSummary(
        val totalTasks: Int = 0,
        val completedTasks: Int = 0,
        val tasksForToday: Int = 0,
        val activeSprint: String? = null,
        val activeSprintProgress: Float = 0f,
        val recentDayActivitiesCompletion: Float = 0f,
        val upcomingDeadlines: Int = 0
    )

    operator fun invoke(): Flow<DashboardSummary> {
        val today = LocalDate.now()
        val tasksFlow = taskRepository.getAllTasks()
        // Instead of getActiveSprint which doesn't exist, we'll filter all sprints to find active ones
        val sprintsFlow = sprintRepository.getAllSprints().map { sprints ->
            // Find sprint that contains today's date (between startDate and endDate)
            sprints.firstOrNull { sprint ->
                today.isEqual(sprint.startDate) || today.isEqual(sprint.endDate) ||
                (today.isAfter(sprint.startDate) && today.isBefore(sprint.endDate))
            }
        }
        val activitiesFlow = dayRepository.getActivitiesByDate(today)

        return combine(
            tasksFlow,
            sprintsFlow,
            activitiesFlow
        ) { tasks, activeSprint, todayActivities ->
            // Calculate task metrics
            val totalTasks = tasks.size
            val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
            val tasksForToday = tasks.count { task ->
                task.dueDate == today
            }
            
            // Calculate sprint progress
            val activeSprintId = activeSprint?.id
            val activeSprintProgress = if (activeSprintId != null) {
                val sprintTasks = tasks.filter { it.sprintId == activeSprintId }
                if (sprintTasks.isNotEmpty()) {
                    sprintTasks.count { it.status == TaskStatus.COMPLETED }.toFloat() / sprintTasks.size
                } else {
                    0f
                }
            } else {
                0f
            }
            
            // Calculate day activities completion
            val recentDayActivitiesCompletion = if (todayActivities.isNotEmpty()) {
                todayActivities.count { it.completed }.toFloat() / todayActivities.size
            } else {
                0f
            }
            
            // Calculate upcoming deadlines
            val nextWeek = today.plusDays(7)
            val upcomingDeadlines = tasks.count { task ->
                task.status != TaskStatus.COMPLETED && 
                task.dueDate != null && 
                task.dueDate in today..nextWeek
            }
            
            DashboardSummary(
                totalTasks = totalTasks,
                completedTasks = completedTasks,
                tasksForToday = tasksForToday,
                activeSprint = activeSprint?.name,
                activeSprintProgress = activeSprintProgress,
                recentDayActivitiesCompletion = recentDayActivitiesCompletion,
                upcomingDeadlines = upcomingDeadlines
            )
        }
    }
}
