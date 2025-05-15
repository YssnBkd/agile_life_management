package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskPriority
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving filtered tasks based on various criteria.
 */
class GetFilteredTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Get filtered tasks based on specified parameters.
     *
     * @param query Optional search query for task title or description
     * @param status Optional task status filter
     * @param priority Optional task priority filter
     * @param dueDateFrom Optional due date range start
     * @param dueDateTo Optional due date range end
     * @param dueToday Only show tasks due today
     * @return Flow of filtered tasks
     */
    operator fun invoke(
        query: String? = null,
        status: TaskStatus? = null,
        priority: TaskPriority? = null,
        dueDateFrom: LocalDate? = null,
        dueDateTo: LocalDate? = null,
        dueToday: Boolean = false
    ): Flow<List<Task>> {
        return taskRepository.getAllTasks().map { tasks ->
            var filteredTasks = tasks
            
            // Filter by search query
            if (!query.isNullOrBlank()) {
                filteredTasks = filteredTasks.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    (it.description?.contains(query, ignoreCase = true) == true) 
                }
            }
            
            // Filter by status
            if (status != null) {
                filteredTasks = filteredTasks.filter { it.status == status }
            }
            
            // Filter by priority
            if (priority != null) {
                filteredTasks = filteredTasks.filter { it.priority == priority }
            }
            
            // Filter by due date range
            if (dueDateFrom != null && dueDateTo != null) {
                filteredTasks = filteredTasks.filter { task ->
                    task.dueDate?.let { it in dueDateFrom..dueDateTo } ?: false
                }
            } else if (dueDateFrom != null) {
                filteredTasks = filteredTasks.filter { task ->
                    task.dueDate?.let { it >= dueDateFrom } ?: false
                }
            } else if (dueDateTo != null) {
                filteredTasks = filteredTasks.filter { task ->
                    task.dueDate?.let { it <= dueDateTo } ?: false
                }
            }
            
            // Filter tasks due today
            if (dueToday) {
                val today = LocalDate.now()
                filteredTasks = filteredTasks.filter { task ->
                    task.dueDate == today
                }
            }
            
            filteredTasks
        }
    }
}
