package com.example.agilelifemanagement.data.repository

import android.util.Log
import com.example.agilelifemanagement.data.local.dao.GoalDao
import com.example.agilelifemanagement.data.local.dao.GoalSprintCrossRefDao
import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.data.local.entity.GoalSprintCrossRefEntity
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.remote.AuthState
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SupabaseRealtimeManager
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.data.remote.api.GoalApiService
import com.example.agilelifemanagement.data.remote.api.GoalSprintCrossRefApiService
import com.example.agilelifemanagement.data.remote.dto.GoalDto
import com.example.agilelifemanagement.data.remote.dto.GoalSprintCrossRefDto
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.Result
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
    private val networkMonitor: NetworkMonitor,
    private val supabaseManager: SupabaseManager,
    private val realtimeManager: SupabaseRealtimeManager
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

    override suspend fun insertGoal(goal: Goal): Result<String> {
        return try {
            // Check authentication status
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return Result.Error("User not authenticated")
            }
            
            val id = goal.id.ifEmpty { UUID.randomUUID().toString() }
            val userId = authState.userId
            
            val currentTimeMillis = System.currentTimeMillis()
            val goalEntity = GoalEntity(
                id = id,
                title = goal.title,
                summary = goal.summary,
                description = goal.description,
                category = goal.category.ordinal,
                deadline = goal.deadline?.let { it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() },
                isCompleted = goal.isCompleted,
                userId = userId,
                createdAt = currentTimeMillis,
                updatedAt = currentTimeMillis
            )
            
            // Insert locally
            goalDao.insert(goalEntity)
            
            // Schedule for sync with new method that takes operation type
            syncManager.scheduleSyncOperation(
                entityId = id,
                entityType = "goal",
                operation = PendingOperation.CREATE
            )
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                // Immediate sync attempt when online
                try {
                    val dto = GoalDto.fromEntity(goalEntity)
                    val apiResult = goalApiService.upsertGoal(dto)
                    
                    when (apiResult) {
                        is Result.Success -> {
                            syncManager.markSynced(id, "goal")
                            Log.d(TAG, "Goal created and synced immediately: $id")
                        }
                        is Result.Error -> {
                            Log.e(TAG, "Failed immediate sync for new goal: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in immediate sync for new goal: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            Result.Success(id)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting goal: ${e.message}", e)
            return Result.Error("Failed to create goal: ${e.message}")
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit> {
        return try {
            // Check authentication status
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return Result.Error("User not authenticated")
            }
            
            // Get entity synchronously from the Flow
            val existingEntity = goalDao.getGoalById(goal.id).first()
                ?: return Result.Error("Goal not found: ${goal.id}")
            
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
            
            // Update locally
            goalDao.updateGoal(updatedEntity)
            
            // Schedule for sync
            syncManager.scheduleSyncOperation(
                entityId = goal.id,
                entityType = "goal",
                operation = PendingOperation.UPDATE
            )
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                // Immediate sync attempt when online
                try {
                    val dto = GoalDto.fromEntity(updatedEntity)
                    val apiResult = goalApiService.upsertGoal(dto)
                    
                    when (apiResult) {
                        is Result.Success -> {
                            syncManager.markSynced(goal.id, "goal")
                            Log.d(TAG, "Goal updated and synced immediately: ${goal.id}")
                        }
                        is Result.Error -> {
                            Log.e(TAG, "Failed immediate sync for updated goal: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in immediate sync for updated goal: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating goal: ${e.message}", e)
            return Result.Error("Failed to update goal: ${e.message}")
        }
    }

    override suspend fun deleteGoal(id: String): Result<Unit> {
        return try {
            // Check authentication status
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return Result.Error("User not authenticated")
            }
            
            // Get entity synchronously from the Flow
            val existingEntity = goalDao.getGoalById(id).first()
                ?: return Result.Error("Goal not found: $id")
            
            // Delete locally
            goalDao.deleteById(id)
            
            // Delete goal-sprint cross references by querying and deleting individually
            val crossRefs = goalSprintCrossRefDao.getSprintsForGoal(id)
            for (crossRef in crossRefs) {
                goalSprintCrossRefDao.delete(crossRef.goalId, crossRef.sprintId)
            }
            
            // Schedule for sync
            syncManager.scheduleSyncOperation(
                entityId = id,
                entityType = "goal",
                operation = PendingOperation.DELETE
            )
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                // Immediate sync attempt when online
                try {
                    val apiResult = goalApiService.deleteGoal(id)
                    
                    when (apiResult) {
                        is Result.Success -> {
                            syncManager.markSynced(id, "goal")
                            Log.d(TAG, "Goal deleted and synced immediately: $id")
                        }
                        is Result.Error -> {
                            Log.e(TAG, "Failed immediate sync for deleted goal: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in immediate sync for deleted goal: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting goal: ${e.message}", e)
            return Result.Error("Failed to delete goal: ${e.message}")
        }
    }

    override suspend fun addGoalToSprint(goalId: String, sprintId: String): Result<Unit> {
        return try {
            // Check authentication status
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return Result.Error("User not authenticated")
            }
            
            // Check if goal and sprint exist and belong to user
            val goalExists = goalDao.getGoalById(goalId).first() != null
            if (!goalExists) {
                return Result.Error("Goal not found: $goalId")
            }
            
            // Check if relation already exists
            val existingRelation = goalSprintCrossRefDao.getGoalSprintCrossRef(goalId, sprintId)
            if (existingRelation != null) {
                return Result.Success(Unit) // Already exists, consider it a success
            }
            
            // Create a new cross reference
            val crossRefId = UUID.randomUUID().toString()
            val currentTime = OffsetDateTime.now()
            
            val crossRef = GoalSprintCrossRefEntity(
                id = crossRefId,
                goalId = goalId,
                sprintId = sprintId,
                createdAt = currentTime
            )
            
            // Insert locally
            goalSprintCrossRefDao.insert(crossRef)
            
            // Schedule for sync
            syncManager.scheduleSyncOperation(
                entityId = crossRefId,
                entityType = "goal_sprint_cross_ref",
                operation = PendingOperation.CREATE
            )
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                // Immediate sync attempt when online
                try {
                    val dto = GoalSprintCrossRefDto.fromEntity(crossRef)
                    val apiResult = goalSprintCrossRefApiService.createGoalSprintRelation(dto)
                    
                    when (apiResult) {
                        is Result.Success -> {
                            syncManager.markSynced(crossRefId, "goal_sprint_cross_ref")
                            Log.d(TAG, "Goal-Sprint relation created and synced immediately: $crossRefId")
                        }
                        is Result.Error -> {
                            Log.e(TAG, "Failed immediate sync for new goal-sprint relation: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in immediate sync for new goal-sprint relation: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding goal to sprint: ${e.message}", e)
            return Result.Error("Failed to add goal to sprint: ${e.message}")
        }
    }

    override suspend fun removeGoalFromSprint(goalId: String, sprintId: String): Result<Unit> {
        return try {
            // Check authentication status
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return Result.Error("User not authenticated")
            }
            
            val crossRef = goalSprintCrossRefDao.getGoalSprintCrossRef(goalId, sprintId)
                ?: return Result.Error("Goal-Sprint relation not found")
            
            // Delete locally
            goalSprintCrossRefDao.delete(goalId, sprintId)
            
            // Schedule for sync
            syncManager.scheduleSyncOperation(
                entityId = crossRef.id,
                entityType = "goal_sprint_cross_ref",
                operation = PendingOperation.DELETE
            )
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                // Immediate sync attempt when online
                try {
                    val apiResult = goalSprintCrossRefApiService.deleteGoalSprintRelation(crossRef.id)
                    
                    when (apiResult) {
                        is Result.Success -> {
                            syncManager.markSynced(crossRef.id, "goal_sprint_cross_ref")
                            Log.d(TAG, "Goal-Sprint relation deleted and synced immediately: ${crossRef.id}")
                        }
                        is Result.Error -> {
                            Log.e(TAG, "Failed immediate sync for deleted goal-sprint relation: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in immediate sync for deleted goal-sprint relation: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            } else {
                // Will sync later when online
                Log.d(TAG, "Goal-Sprint relation will be synced later: ${crossRef.id}")
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing goal from sprint: ${e.message}", e)
            return Result.Error("Failed to remove goal from sprint: ${e.message}")
        }
    }

    /**
     * Extension function to convert GoalEntity to Goal domain model.
     */
    private fun GoalEntity.toDomain(): Goal {
        return Goal(
            id = this.id,
            title = this.title,
            summary = this.summary ?: "",
            description = this.description ?: emptyList(),
            category = Goal.Category.values()[this.category ?: 0],
            deadline = this.deadline?.let { LocalDate.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) },
            isCompleted = this.isCompleted
        )
    }
    
    companion object {
        private const val TAG = "GoalRepositoryImpl"
    }
}
