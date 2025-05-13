package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.dao.SprintDao
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SupabaseRealtimeManager
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.data.remote.api.SprintApiService
import com.example.agilelifemanagement.data.remote.dto.SprintDto
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import com.example.agilelifemanagement.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of SprintRepository that coordinates between local and remote data sources.
 * Follows the offline-first strategy with automatic synchronization.
 */
class SprintRepositoryImpl @Inject constructor(
    private val sprintDao: SprintDao,
    private val sprintApiService: SprintApiService,
    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor,
    private val supabaseManager: SupabaseManager,
    private val realtimeManager: SupabaseRealtimeManager
) : SprintRepository {

    override fun getSprints(): Flow<List<Sprint>> {
        return sprintDao.getAllSprints().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSprintById(id: String): Flow<Sprint?> {
        android.util.Log.d(TAG, "getSprintById called with id: $id")
        return sprintDao.getSprintById(id).map { entity ->
            if (entity == null) {
                android.util.Log.w(TAG, "No sprint found for id: $id")
            } else {
                android.util.Log.d(TAG, "Sprint found for id: $id: ${entity.id}")
            }
            entity?.toDomain()
        }
    }

    override fun getActiveSprintByDate(date: LocalDate): Flow<Sprint?> {
        val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return sprintDao.getActiveSprintByDate(timestamp).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun insertSprint(sprint: Sprint): Result<String> {
        android.util.Log.d(TAG, "insertSprint called with sprint: ${sprint.id}")
        return try {
            val userId = supabaseManager.getCurrentUserId().first() 
                ?: return Result.Error("User not authenticated")
            
            val id = sprint.id.ifEmpty { UUID.randomUUID().toString() }
            val entity = SprintEntity(
                id = id,
                name = sprint.name,
                summary = sprint.summary,
                description = sprint.description,
                startDate = sprint.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                endDate = sprint.endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                isActive = sprint.isActive,
                isCompleted = sprint.isCompleted,
                userId = userId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // Save to local database
            sprintDao.insertSprint(entity)
            android.util.Log.d(TAG, "Sprint inserted locally: $id")
            
            // Schedule for synchronization
            syncManager.scheduleSyncOperation(id, "sprint", PendingOperation.CREATE)
            
            // Try to sync immediately if online
            if (networkMonitor.isOnlineFlow.first()) {
                try {
                    val dto = SprintDto.fromEntity(entity)
                    val apiResult = sprintApiService.upsertSprint(dto)
                    when (apiResult) {
                        is Result.Success -> {
                            syncManager.markSynced(id, "sprint")
                            android.util.Log.d(TAG, "Sprint synced to server: $id")
                        }
                        is Result.Error -> {
                            android.util.Log.e(TAG, "Error syncing sprint: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error syncing sprint: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            Result.Success(id)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error inserting sprint: ${e.message}", e)
            Result.Error("Failed to create sprint: ${e.message}")
        }
    }

    override suspend fun updateSprint(sprint: Sprint): Result<Unit> {
        android.util.Log.d(TAG, "updateSprint called with sprint: ${sprint.id}")
        return try {
            val userId = supabaseManager.getCurrentUserId().first() 
                ?: return Result.Error("User not authenticated")
                
            // First, get the existing entity to preserve creation timestamp
            val existingEntity = sprintDao.getSprintById(sprint.id).first()
                ?: return Result.Error("Sprint with ID ${sprint.id} not found")
                
            val entity = SprintEntity(
                id = sprint.id,
                name = sprint.name,
                summary = sprint.summary.ifEmpty { null },
                description = if (sprint.description.isEmpty()) null else sprint.description,
                startDate = sprint.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                endDate = sprint.endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                isActive = sprint.isActive,
                isCompleted = sprint.isCompleted,
                userId = userId,
                createdAt = existingEntity.createdAt,
                updatedAt = System.currentTimeMillis()
            )
            
            // Update local database
            sprintDao.updateSprint(entity)
            android.util.Log.d(TAG, "Sprint updated locally: ${sprint.id}")
            
            // Schedule for synchronization
            syncManager.scheduleSyncOperation(sprint.id, "sprint", PendingOperation.UPDATE)
            
            // Try to sync immediately if online
            if (networkMonitor.isOnlineFlow.first()) {
                try {
                    val dto = SprintDto.fromEntity(entity)
                    val apiResult = sprintApiService.upsertSprint(dto)
                    when (apiResult) {
                        is Result.Success -> {
                            syncManager.markSynced(sprint.id, "sprint")
                            android.util.Log.d(TAG, "Sprint updated and synced to server: ${sprint.id}")
                        }
                        is Result.Error -> {
                            android.util.Log.e(TAG, "Error syncing sprint update: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for updated sprint: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error updating sprint: ${e.message}", e)
            Result.Error("Failed to update sprint: ${e.message}")
        }
    }

    override suspend fun deleteSprint(id: String): Result<Unit> {
        android.util.Log.d(TAG, "deleteSprint called with id: $id")
        return try {
            // Check if sprint exists
            val existingEntity = sprintDao.getSprintById(id).first()
                ?: return Result.Error("Sprint with ID $id not found")
                
            // Delete from local database
            sprintDao.deleteSprintById(id)
            android.util.Log.d(TAG, "Sprint deleted locally: $id")
            
            // Schedule for synchronization
            syncManager.scheduleSyncOperation(id, "sprint", PendingOperation.DELETE)
            
            // Try to sync immediately if online
            if (networkMonitor.isOnlineFlow.first()) {
                try {
                    val apiResult = sprintApiService.deleteSprint(id)
                    when (apiResult) {
                        is Result.Success -> {
                            syncManager.markSynced(id, "sprint")
                            android.util.Log.d(TAG, "Sprint deleted and synced from server: $id")
                        }
                        is Result.Error -> {
                            android.util.Log.e(TAG, "Error syncing sprint deletion: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for deleted sprint: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error deleting sprint: ${e.message}", e)
            Result.Error("Failed to delete sprint: ${e.message}")
        }
    }
    
    /**
     * Extension function to convert SprintEntity to Sprint domain model.
     */
    private fun SprintEntity.toDomain(): Sprint {
        return Sprint(
            id = id,
            name = name,
            summary = summary ?: "",
            description = description ?: emptyList(),
            startDate = Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDate(),
            endDate = Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault()).toLocalDate(),
            isActive = isActive,
            isCompleted = isCompleted
        )
    }
    
    companion object {
        private const val TAG = "SprintRepositoryImpl"
    }
}
