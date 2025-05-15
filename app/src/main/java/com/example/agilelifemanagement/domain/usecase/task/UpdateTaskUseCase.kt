package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for updating an existing task.
 * 
 * This use case follows the offline-first approach, updating the task in local storage
 * immediately and then synchronizing with remote storage in the background.
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Updates an existing task with new information.
     * 
     * @param task The task with updated information
     * @return Result containing the updated task or error details
     */
    suspend operator fun invoke(task: Task): Result<Task> {
        return taskRepository.updateTask(task)
    }
}
