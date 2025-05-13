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
 * Use case for creating a new task with validation.
 */
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val sprintRepository: SprintRepository,
    private val goalRepository: GoalRepository
) {
    /**
     * Create a new task with validation.
     *
     * @param title The title of the task (required).
     * @param description The description of the task (optional).
     * @param dueDate The due date of the task (optional).
     * @param priority The priority of the task (default: MEDIUM).
     * @param status The status of the task (default: TODO).
     * @param sprintId The ID of the sprint to associate with the task (optional).
     * @param goalId The ID of the goal to associate with the task (optional).
     * @param estimatedEffort The estimated effort for the task (default: 0).
     * @param tags List of tag IDs to associate with the task (optional).
     * @return Result containing the ID of the created task or an error.
     */
    suspend operator fun invoke(
        title: String,
        description: List<String> = emptyList(),
        dueDate: LocalDate? = null,
        priority: Task.Priority = Task.Priority.MEDIUM,
        status: Task.Status = Task.Status.TODO,
        sprintId: String? = null,
        goalId: String? = null,
        estimatedEffort: Int = 0,
        tags: List<String> = emptyList()
    ): Result<String> {
        // Validation
        if (title.isBlank()) {
            return Result.Error("Task title cannot be empty")
        }
        
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
        
        // Create task
        val task = Task(
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
        
        // Since taskRepository.insertTask now returns a Result type, we need to handle it properly
        val insertResult = taskRepository.insertTask(task)
        
        return when (insertResult) {
            is Result.Success -> {
                val taskId = insertResult.data
                
                // Add tags if provided
                tags.forEach { tagId ->
                    taskRepository.addTagToTask(taskId, tagId)
                }
                
                Result.Success(taskId)
            }
            is Result.Error -> {
                Result.Error("Failed to create task: ${insertResult.message}")
            }
            else -> {
                // Handle loading state if present
                Result.Error("Unexpected state while creating task")
            }
        }
    }
}
