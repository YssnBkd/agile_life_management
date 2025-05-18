package com.example.agilelifemanagement.domain.usecase.analytics

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.GoalCategory
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.domain.repository.CategoryRepository
import com.example.agilelifemanagement.domain.repository.DayRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Use case for retrieving productivity analytics over a specified time period.
 */
class GetProductivityAnalyticsUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val dayRepository: DayRepository,
    private val categoryRepository: CategoryRepository
) {
    data class ProductivityData(
        val completedTasksCount: Int,
        val totalTasksCount: Int,
        val completionRate: Float,
        val categoryDistribution: Map<String, Float>,
        val dailyCompletionTrend: List<Pair<LocalDate, Float>>,
        val averageTasksPerDay: Float,
        val averageCompletionTimeMinutes: Float
    )

    /**
     * Get productivity analytics for the specified date range
     *
     * @param startDate Beginning of the analysis period
     * @param endDate End of the analysis period
     * @return Flow of productivity analytics data
     */
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): ProductivityData {
        // Get all tasks in the date range by collecting them day-by-day
        val tasks = mutableListOf<Task>()
        var currentDateForTasks = startDate
        while (!currentDateForTasks.isAfter(endDate)) {
            // Use getTasksByDate which is available in the repository
            val tasksForDate = taskRepository.getTasksByDate(currentDateForTasks).first()
            tasks.addAll(tasksForDate)
            currentDateForTasks = currentDateForTasks.plusDays(1)
        }
        
        // Get all activities in the date range
        val activitiesByDate = mutableMapOf<LocalDate, List<DayActivity>>()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            val activities = dayRepository.getActivitiesByDate(currentDate).first()
            activitiesByDate[currentDate] = activities
            currentDate = currentDate.plusDays(1)
        }
        
        // Get all categories for mapping
        val categories = categoryRepository.getAllCategories().first()
        val categoryMap = categories.associateBy { it.id }
        
        // Calculate basic metrics
        val completedTasks = tasks.filter { it.status == TaskStatus.COMPLETED }
        val completedTasksCount = completedTasks.size
        val totalTasksCount = tasks.size
        val completionRate = if (totalTasksCount > 0) {
            completedTasksCount.toFloat() / totalTasksCount
        } else {
            0f
        }
        
        // Calculate category distribution
        val categoryDistribution = tasks.groupBy { 
            // Using tags as proxy for categories since Task doesn't have categoryId
            it.tags.firstOrNull() ?: "Uncategorized" 
        }.mapValues { entry ->
            if (totalTasksCount > 0) {
                entry.value.size.toFloat() / totalTasksCount
            } else {
                0f
            }
        }
        
        // Calculate daily completion trend
        val dailyCompletionTrend = mutableListOf<Pair<LocalDate, Float>>()
        currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            val dayTasks = tasks.filter { it.dueDate == currentDate }
            val dayCompletionRate = if (dayTasks.isNotEmpty()) {
                dayTasks.count { it.status == TaskStatus.COMPLETED }.toFloat() / dayTasks.size
            } else {
                0f
            }
            dailyCompletionTrend.add(Pair(currentDate, dayCompletionRate))
            currentDate = currentDate.plusDays(1)
        }
        
        // Calculate average tasks per day
        val daysInRange = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1))
        val averageTasksPerDay = if (daysInRange > 0) {
            totalTasksCount.toFloat() / daysInRange
        } else {
            0f
        }
        
        // Calculate an estimate of average completion time (in days)
        // Since we don't have direct completion time data, we'll use the date difference between creation and due date as a proxy
        val averageCompletionTimeInDays = if (completedTasks.isNotEmpty()) {
            completedTasks
                .filter { it.dueDate != null } // createdDate is non-nullable so we don't need to check for it
                .map { ChronoUnit.DAYS.between(it.createdDate, it.dueDate).toFloat() }
                .takeIf { it.isNotEmpty() }?.average()?.toFloat() ?: 0f
        } else {
            0f
        }
        
        return ProductivityData(
            completedTasksCount = completedTasksCount,
            totalTasksCount = totalTasksCount,
            completionRate = completionRate,
            categoryDistribution = categoryDistribution,
            dailyCompletionTrend = dailyCompletionTrend,
            averageTasksPerDay = averageTasksPerDay,
            averageCompletionTimeMinutes = averageCompletionTimeInDays
        )
    }
}
