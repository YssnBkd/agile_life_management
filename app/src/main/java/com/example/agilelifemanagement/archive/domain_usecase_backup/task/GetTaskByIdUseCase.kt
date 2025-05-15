package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a specific task by ID.
 */
class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Get a task by its ID.
     *
     * @param id The unique identifier of the task.
     * @return Flow emitting the task if found, or null if not found.
     */
    operator fun invoke(id: String): Flow<Task?> {
        return taskRepository.getTaskById(id)
    }
}
