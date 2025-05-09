package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.dao.GoalDao
import com.example.agilelifemanagement.data.local.dao.GoalSprintCrossRefDao
import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.data.local.entity.GoalSprintCrossRefEntity
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.data.remote.api.GoalApiService
import com.example.agilelifemanagement.data.remote.api.GoalSprintCrossRefApiService
import com.example.agilelifemanagement.data.remote.dto.GoalDto
import com.example.agilelifemanagement.data.remote.dto.GoalSprintCrossRefDto
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.repository.GoalRepository
import com.example.agilelifemanagement.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of GoalRepository that coordinates between local and remote data sources.
 * Follows the offline-first strategy with automatic synchronization.
 */
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val goalSprintCrossRefDao: GoalSprintCrossRefDao,
    private val goalApiService: GoalApiService,
    private val goalSprintCrossRefApiService: GoalSprintCrossRefApiService,
    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor
) : GoalRepository {

    override fun getGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { goalEntities -> goalEntities.map { it.toDomain() } }
    }

    override fun getGoalById(id: String): Flow<Goal?> {
        return goalDao.getGoalById(id).map { entity -> entity?.toDomain() }
    }

    override fun getGoalsByCategory(category: Goal.Category): Flow<List<Goal>> {
        return goalDao.getGoalsByCategory(category.ordinal).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getGoalsByDeadline(deadline: LocalDate): Flow<List<Goal>> {
        val timestamp = deadline.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return goalDao.getGoalsByDeadline(timestamp).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getGoalsBySprintId(sprintId: String): Flow<List<Goal>> {
        return goalDao.getGoalsBySprintId(sprintId).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun insertGoal(goal: Goal): String {
        val id = goal.id.ifEmpty { UUID.randomUUID().toString() }
        val goalEntity = GoalEntity(
            id = id,
            title = goal.title,
            summary = goal.summary,
            description = goal.description,
            category = goal.category.ordinal,
            deadline = goal.deadline?.let { it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() },
            isCompleted = goal.isCompleted,
            userId = "", // TODO: Assign userId from auth/session if required
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        goalDao.insert(goalEntity)
        syncManager.scheduleSync(id, "goal", PendingOperation.CREATE)
        return id
    }

    override suspend fun updateGoal(goal: Goal) {
        val existingEntity = goalDao.getGoalById(goal.id).first()
        if (existingEntity != null) {
            val updatedEntity = GoalEntity(
                id = existingEntity.id,
                title = goal.title,
                summary = goal.summary,
                description = goal.description,
                category = goal.category.ordinal,
                deadline = goal.deadline?.let { it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() },
                isCompleted = goal.isCompleted,
                userId = existingEntity.userId,
                createdAt = existingEntity.createdAt,
                updatedAt = System.currentTimeMillis()
            )
            goalDao.updateGoal(updatedEntity)
            syncManager.scheduleSync(goal.id, "goal", PendingOperation.UPDATE)
        }
    }

    override suspend fun deleteGoal(id: String) {
        // Delete from local database
        goalDao.deleteById(id)

        // Schedule for synchronization
        syncManager.scheduleSync(id, "goal", PendingOperation.DELETE)
    }

    override suspend fun addGoalToSprint(goalId: String, sprintId: String) {
        val crossRefId = UUID.randomUUID().toString()
        val crossRef = GoalSprintCrossRefEntity(
            id = crossRefId,
            goalId = goalId,
            sprintId = sprintId,
            createdAt = OffsetDateTime.now()
        )
        goalSprintCrossRefDao.insert(crossRef)
        syncManager.scheduleSync(crossRefId, "goal_sprint_cross_ref", PendingOperation.CREATE)
        if (networkMonitor.isOnlineFlow.first()) {
            try {
                val dto = GoalSprintCrossRefDto.fromEntity(crossRef)
                goalSprintCrossRefApiService.createGoalSprintRelation(dto)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error syncing goal-sprint relation: ${e.message}", e)
            }
        }
    }

    override suspend fun removeGoalFromSprint(goalId: String, sprintId: String) {
        val crossRef = goalSprintCrossRefDao.getGoalSprintCrossRef(goalId, sprintId)
        if (crossRef != null) {
            goalSprintCrossRefDao.delete(goalId, sprintId)
            syncManager.scheduleSync(crossRef.id, "goal_sprint_cross_ref", PendingOperation.DELETE)
            if (networkMonitor.isOnlineFlow.first()) {
                try {
                    goalSprintCrossRefApiService.deleteGoalSprintRelation(crossRef.id)
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error syncing delete goal-sprint relation: ${e.message}", e)
                }
            }
        }
    }

    /**
     * Extension function to convert GoalEntity to Goal domain model.
     */
    private fun GoalEntity.toDomain(): Goal {
        return Goal(
            id = id,
            title = title,
            summary = summary ?: "",
            description = description ?: emptyList(),
            category = Goal.Category.values()[category ?: 0],
            deadline = deadline?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() },
            isCompleted = isCompleted
        )
    }

    companion object {
        private const val TAG = "GoalRepositoryImpl"
    }
}
