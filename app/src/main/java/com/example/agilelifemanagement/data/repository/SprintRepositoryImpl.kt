package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.dao.SprintDao
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.data.remote.SupabaseManager
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
    private val supabaseManager: SupabaseManager
) : SprintRepository {

    override fun getSprints(): Flow<List<Sprint>> {
        return sprintDao.getAllSprints().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSprintById(id: String): Flow<Sprint?> {
        return sprintDao.getSprintById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getActiveSprintByDate(date: LocalDate): Flow<Sprint?> {
        val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return sprintDao.getActiveSprintByDate(timestamp).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun insertSprint(sprint: Sprint): String {
        val userId = supabaseManager.getCurrentUserId().first() ?: throw IllegalStateException("User not authenticated")
        
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
        
        // Schedule for synchronization
        syncManager.scheduleSync(id, "sprint", PendingOperation.CREATE)
        
        // Try to sync immediately if online
        if (networkMonitor.isOnlineFlow.first()) {
            try {
                val dto = SprintDto.fromEntity(entity)
                val result = sprintApiService.upsertSprint(dto)
                
                if (result is Result.Error) {
                    // Log error but don't throw - we'll sync later
                    android.util.Log.e(TAG, "Error syncing sprint: ${result.message}")
                }
            } catch (e: Exception) {
                // Log error but don't throw - we'll sync later
                android.util.Log.e(TAG, "Error syncing sprint: ${e.message}", e)
            }
        }
        
        return id
    }

    override suspend fun updateSprint(sprint: Sprint) {
        val userId = supabaseManager.getCurrentUserId().first() ?: throw IllegalStateException("User not authenticated")
        
        // First, get the existing entity to preserve creation timestamp
        val existingEntity = sprintDao.getSprintById(sprint.id).first()
            ?: throw IllegalArgumentException("Sprint with ID ${sprint.id} not found")
        
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
        
        // Schedule for synchronization
        syncManager.scheduleSync(sprint.id, "sprint", PendingOperation.UPDATE)
    }

    override suspend fun deleteSprint(id: String) {
        // Delete from local database
        sprintDao.deleteSprintById(id)
        
        // Schedule for synchronization
        syncManager.scheduleSync(id, "sprint", PendingOperation.DELETE)
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
