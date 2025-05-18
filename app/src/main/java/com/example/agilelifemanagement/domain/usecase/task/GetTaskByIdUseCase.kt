package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for retrieving a specific task by ID.
 */
class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    // Converting from the repository's suspend function returning Result<Task> 
    // to a Flow<Task?> for reactive UI consumption
    operator fun invoke(taskId: String): Flow<Task?> = flow {
        // Execute the repository call
        val result = taskRepository.getTaskById(taskId)
        
        // Emit the task if successful, otherwise emit null
        emit(result.getOrNull())
    }.catch { exception ->
        // Handle any exceptions during the flow and emit null
        Timber.e(exception, "Error getting task: $taskId")
        emit(null)
    }
}
