package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.dao.DailyCheckupDao
import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import com.example.agilelifemanagement.data.local.entity.PendingOperation // Added for sync operations
import com.example.agilelifemanagement.data.mappers.toDailyCheckup
import com.example.agilelifemanagement.data.remote.api.DailyCheckupApiService
import com.example.agilelifemanagement.data.remote.dto.DailyCheckupDto // Added for DTO conversion
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.DailyCheckupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import com.example.agilelifemanagement.util.NetworkMonitor

/**
 * Implementation of [DailyCheckupRepository] that follows the offline-first strategy.
 */
class DailyCheckupRepositoryImpl @Inject constructor(
    private val dailyCheckupDao: DailyCheckupDao,
    private val dailyCheckupApiService: DailyCheckupApiService,

    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor
) : DailyCheckupRepository {

    override fun getCheckups(): Flow<List<DailyCheckup>> {
        return dailyCheckupDao.getAllDailyCheckups().map { entities ->
            entities.map { it.toDailyCheckup() }
        }
    }

    override fun getCheckupById(id: String): Flow<DailyCheckup?> {
        return dailyCheckupDao.getDailyCheckupById(id).map { entity ->
            entity?.toDailyCheckup()
        }
    }

    override fun getCheckupByDate(date: LocalDate): Flow<DailyCheckup?> {
        val epochDay = date.toEpochDay() * 86400 // Convert to seconds
        return dailyCheckupDao.getDailyCheckupByDate(epochDay).map { entity ->
            entity?.toDailyCheckup()
        }
    }

    override fun getCheckupsBySprintId(sprintId: String): Flow<List<DailyCheckup>> {
        return dailyCheckupDao.getDailyCheckupsBySprintId(sprintId).map { entities ->
            entities.map { it.toDailyCheckup() }
        }
    }

    override suspend fun insertCheckup(checkup: DailyCheckup): String {
        val id = checkup.id.ifEmpty { UUID.randomUUID().toString() }
        val currentTimeSeconds = System.currentTimeMillis() / 1000
        
        val userId = syncManager.getCurrentUserId() ?: throw IllegalStateException("User not authenticated")
        val dailyCheckupEntity = DailyCheckupEntity(
            id = id,
            date = checkup.date.toEpochDay() * 86400,
            sprintId = checkup.sprintId,
            userId = userId,
            notes = checkup.notes.ifEmpty { null },
            createdAt = currentTimeSeconds,
            updatedAt = currentTimeSeconds
        )
        
        dailyCheckupDao.insert(dailyCheckupEntity)
        syncManager.scheduleSync(dailyCheckupEntity.id, "daily_checkup", PendingOperation.CREATE)
        
        return id
    }

    override suspend fun updateCheckup(checkup: DailyCheckup) {
        val existingEntity = dailyCheckupDao.getDailyCheckupByIdSync(checkup.id)
        if (existingEntity != null) {
            val updatedEntity = existingEntity.copy(
                date = checkup.date.toEpochDay() * 86400,
                sprintId = checkup.sprintId,
                notes = checkup.notes.ifEmpty { null },
                updatedAt = System.currentTimeMillis() / 1000
            )
            dailyCheckupDao.update(updatedEntity)
            syncManager.scheduleSync(checkup.id, "daily_checkup", PendingOperation.UPDATE)
        }
    }

    override suspend fun deleteCheckup(id: String) {
        dailyCheckupDao.deleteById(id)
        try {
            dailyCheckupApiService.deleteDailyCheckup(id)
        } catch (e: Exception) {
            // Log error but don't throw
        }
        syncManager.scheduleSync(id, "daily_checkup", PendingOperation.DELETE)
    }









    // Private helper methods
    // Synchronize a DailyCheckup entity with Supabase, with error handling
    private suspend fun syncDailyCheckup(dailyCheckupEntity: DailyCheckupEntity): Result<Unit> {
        return try {
            val dto = DailyCheckupDto.fromEntity(dailyCheckupEntity)
            dailyCheckupApiService.upsertDailyCheckup(dto)
            syncManager.markSynced(dailyCheckupEntity.id, "daily_checkup")
            Result.Success(Unit)
        } catch (e: Exception) {
            // Log error for debugging and analytics
            Result.Error("Failed to sync daily checkup: ${e.message}", e)
        }
    }


}
