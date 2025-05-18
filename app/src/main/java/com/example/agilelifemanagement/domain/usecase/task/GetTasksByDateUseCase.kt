package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving tasks by their due date.
 */
class GetTasksByDateUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Task>> {
        // Use the getTasksByDate method which is available in the repository
        return taskRepository.getTasksByDate(date)
    }
}
