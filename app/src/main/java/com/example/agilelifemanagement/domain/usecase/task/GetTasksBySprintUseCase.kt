package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving tasks associated with a specific sprint.
 */
class GetTasksBySprintUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(sprintId: String): Flow<List<Task>> {
        return taskRepository.getTasksBySprint(sprintId)
    }
}
