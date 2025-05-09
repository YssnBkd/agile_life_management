package com.example.agilelifemanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.local.entity.SyncStatus
import com.example.agilelifemanagement.data.local.entity.SyncStatusEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SyncStatusEntity.
 * Handles database operations for synchronization status.
 */
@Dao
interface SyncStatusDao {
    
    /**
     * Insert a new sync status entity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncStatus(syncStatus: SyncStatusEntity)
    
    /**
     * Update an existing sync status entity.
     */
    @Update
    suspend fun updateSyncStatus(syncStatus: SyncStatusEntity)
    
    /**
     * Get sync status for a specific entity.
     */
    @Query("SELECT * FROM sync_status WHERE entityId = :entityId")
    suspend fun getSyncStatusById(entityId: String): SyncStatusEntity?
    
    /**
     * Get sync status for a specific entity as a Flow.
     */
    @Query("SELECT * FROM sync_status WHERE entityId = :entityId")
    fun getSyncStatusByIdFlow(entityId: String): Flow<SyncStatusEntity?>
    
    /**
     * Get all pending sync operations.
     */
    @Query("SELECT * FROM sync_status WHERE syncStatus = :status")
    suspend fun getSyncStatusByStatus(status: SyncStatus): List<SyncStatusEntity>
    
    /**
     * Get all pending sync operations for a specific entity type.
     */
    @Query("SELECT * FROM sync_status WHERE syncStatus = :status AND entityType = :entityType")
    suspend fun getSyncStatusByStatusAndType(status: SyncStatus, entityType: String): List<SyncStatusEntity>
    
    /**
     * Get all pending sync operations for a specific operation type.
     */
    @Query("SELECT * FROM sync_status WHERE pendingOperation = :operation")
    suspend fun getSyncStatusByOperation(operation: PendingOperation): List<SyncStatusEntity>
    
    /**
     * Delete sync status for a specific entity.
     */
    @Query("DELETE FROM sync_status WHERE entityId = :entityId")
    suspend fun deleteSyncStatus(entityId: String)
    
    /**
     * Delete all sync statuses.
     */
    @Query("DELETE FROM sync_status")
    suspend fun deleteAllSyncStatuses()
    
    /**
     * Mark an entity as synced.
     */
    @Query("UPDATE sync_status SET syncStatus = :syncStatus, pendingOperation = :pendingOperation, lastSyncAttempt = :timestamp WHERE entityId = :entityId")
    suspend fun markAsSynced(entityId: String, syncStatus: SyncStatus = SyncStatus.SYNCED, pendingOperation: PendingOperation = PendingOperation.NONE, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Mark an entity as failed.
     */
    @Query("UPDATE sync_status SET syncStatus = :syncStatus, lastSyncAttempt = :timestamp, errorMessage = :errorMessage, retryCount = retryCount + 1 WHERE entityId = :entityId")
    suspend fun markAsFailed(entityId: String, syncStatus: SyncStatus = SyncStatus.FAILED, timestamp: Long = System.currentTimeMillis(), errorMessage: String?)
}
