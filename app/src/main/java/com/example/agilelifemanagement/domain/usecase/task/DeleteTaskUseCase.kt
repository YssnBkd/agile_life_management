package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for deleting a task.
 * 
 * This use case follows the offline-first approach, ensuring the task is deleted
 * from local storage immediately and then synchronizing with remote storage in the background.
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Deletes a task with the given ID.
     * 
     * @param taskId The unique identifier of the task to delete
     * @return Result indicating success or error with details
     */
    suspend operator fun invoke(taskId: String): Result<Unit> {
        // Map the Boolean result to Unit for clients that don't need the boolean value
        return taskRepository.deleteTask(taskId).map { Unit }
    }
}
