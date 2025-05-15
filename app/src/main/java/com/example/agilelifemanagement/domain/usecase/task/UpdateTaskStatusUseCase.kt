package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for updating a task's status.
 */
class UpdateTaskStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Updates the status of a task.
     * @param taskId The ID of the task to update
     * @param status The new status to set
     * @return Result containing the updated task if successful, or an error
     */
    suspend operator fun invoke(taskId: String, status: TaskStatus): Result<Task> {
        return taskRepository.updateTaskStatus(taskId, status)
    }
}
