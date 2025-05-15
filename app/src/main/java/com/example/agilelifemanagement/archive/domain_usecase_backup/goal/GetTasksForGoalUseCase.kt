package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.GoalRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for retrieving all tasks associated with a specific goal.
 */
class GetTasksForGoalUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository
) {
    /**
     * Get all tasks associated with a specific goal.
     *
     * @param goalId The ID of the goal.
     * @return Flow emitting a list of tasks associated with the goal.
     */
    suspend operator fun invoke(goalId: String): Flow<List<Task>> {
        // Validate goal exists
        val goal = goalRepository.getGoalById(goalId).first()
            ?: throw IllegalArgumentException("Goal not found")
        
        return taskRepository.getTasksByGoalId(goalId)
    }
}
