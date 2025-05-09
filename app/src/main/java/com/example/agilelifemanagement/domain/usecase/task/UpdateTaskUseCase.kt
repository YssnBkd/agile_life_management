package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.GoalRepository
import com.example.agilelifemanagement.domain.repository.SprintRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for updating an existing task with validation.
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val sprintRepository: SprintRepository,
    private val goalRepository: GoalRepository
) {
    /**
     * Update an existing task with validation.
     *
     * @param id The ID of the task to update (required).
     * @param title The updated title of the task (required).
     * @param description The updated description of the task (optional).
     * @param dueDate The updated due date of the task (optional).
     * @param priority The updated priority of the task.
     * @param status The updated status of the task.
     * @param sprintId The updated sprint ID to associate with the task (optional).
     * @param goalId The updated goal ID to associate with the task (optional).
     * @param estimatedEffort The updated estimated effort for the task.
     * @param tags Updated list of tag IDs to associate with the task.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(
        id: String,
        title: String,
        description: List<String>,
        dueDate: LocalDate?,
        priority: Task.Priority,
        status: Task.Status,
        sprintId: String?,
        goalId: String?,
        estimatedEffort: Int,
        tags: List<String>
    ): Result<Unit> {
        // Validation
        if (title.isBlank()) {
            return Result.Error("Task title cannot be empty")
        }
        
        // Check if task exists
        val existingTask = taskRepository.getTaskById(id).first()
            ?: return Result.Error("Task not found")
        
        // Validate sprint if provided
        if (sprintId != null) {
            val sprint = sprintRepository.getSprintById(sprintId).first()
            if (sprint == null) {
                return Result.Error("Sprint not found")
            }
            
            // Validate due date against sprint dates
            if (dueDate != null && sprint.endDate < dueDate) {
                return Result.Error("Due date cannot be after sprint end date")
            }
        }
        
        // Validate goal if provided
        if (goalId != null) {
            val goal = goalRepository.getGoalById(goalId).first()
            if (goal == null) {
                return Result.Error("Goal not found")
            }
        }
        
        // Create updated task
        val updatedTask = Task(
            id = id,
            title = title,
            description = description,
            dueDate = dueDate,
            priority = priority,
            status = status,
            sprintId = sprintId,
            goalId = goalId,
            estimatedEffort = estimatedEffort,
            tags = tags
        )
        
        return try {
            // Update task
            taskRepository.updateTask(updatedTask)
            
            // Handle sprint association changes if needed
            if (existingTask.sprintId != sprintId) {
                existingTask.sprintId?.let { 
                    taskRepository.removeTaskFromSprint(id, it) 
                }
                sprintId?.let { 
                    taskRepository.addTaskToSprint(id, it) 
                }
            }
            
            // Handle goal association changes if needed
            if (existingTask.goalId != goalId) {
                existingTask.goalId?.let { 
                    taskRepository.removeTaskFromGoal(id, it) 
                }
                goalId?.let { 
                    taskRepository.addTaskToGoal(id, it) 
                }
            }
            
            // Handle tag changes
            // First, get current tags
            val currentTags = existingTask.tags
            
            // Remove tags that are no longer associated
            currentTags.filter { it !in tags }.forEach { tagId ->
                taskRepository.removeTagFromTask(id, tagId)
            }
            
            // Add new tags
            tags.filter { it !in currentTags }.forEach { tagId ->
                taskRepository.addTagToTask(id, tagId)
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update task: ${e.message}", e)
        }
    }
}
