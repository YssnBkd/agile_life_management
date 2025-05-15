package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all tasks.
 * This use case follows the offline-first approach by immediately providing local data
 * while syncing with remote sources in the background.
 */
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Gets a Flow of all tasks, automatically updated when the repository data changes.
     * @return A Flow emitting lists of all tasks when changes occur
     */
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getAllTasks()
    }
}
