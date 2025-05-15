package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for creating a new task.
 * This use case follows the offline-first approach by immediately saving to local storage
 * and syncing with remote sources in the background.
 */
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Creates a new task in the system.
     * @param task The task to create
     * @return Result containing the created task with its generated ID if successful, or an error
     */
    suspend operator fun invoke(task: Task): Result<Task> {
        return taskRepository.createTask(task)
    }
}
