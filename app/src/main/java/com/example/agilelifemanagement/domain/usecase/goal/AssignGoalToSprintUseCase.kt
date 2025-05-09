package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.GoalRepository
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for assigning a goal to a sprint with validation.
 */
class AssignGoalToSprintUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val sprintRepository: SprintRepository
) {
    /**
     * Assign a goal to a sprint with validation.
     *
     * @param goalId The ID of the goal to assign.
     * @param sprintId The ID of the sprint to assign the goal to.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(goalId: String, sprintId: String): Result<Unit> {
        return try {
            // Validate goal
            val goal = goalRepository.getGoalById(goalId).first()
                ?: return Result.Error("Goal not found")
            
            // Validate sprint
            val sprint = sprintRepository.getSprintById(sprintId).first()
                ?: return Result.Error("Sprint not found")
            
            // Validate deadline against sprint dates
            if (goal.deadline != null && sprint.endDate < goal.deadline) {
                return Result.Error("Goal deadline (${goal.deadline}) cannot be after sprint end date (${sprint.endDate})")
            }
            
            // Assign goal to sprint
            goalRepository.addGoalToSprint(goalId, sprintId)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to assign goal to sprint: ${e.message}", e)
        }
    }
}
