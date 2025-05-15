package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing the synchronization status of an entity.
 * Used for implementing offline-first functionality.
 */
@Entity(tableName = "sync_status")
data class SyncStatusEntity(
    @PrimaryKey
    val entityId: String,
    val entityType: String, // "user", "sprint", "goal", "task", etc.
    val syncStatus: SyncStatus,
    val lastSyncAttempt: Long,
    val pendingOperation: PendingOperation,
    val errorMessage: String? = null,
    val retryCount: Int = 0
)

/**
 * Enum representing the synchronization status of an entity.
 */
enum class SyncStatus {
    SYNCED,     // Entity is synchronized with the remote database
    PENDING,    // Entity has changes that need to be synchronized
    FAILED      // Synchronization failed
}

/**
 * Enum representing the pending operation for an entity.
 */
enum class PendingOperation {
    NONE,       // No operation pending
    CREATE,     // Entity needs to be created in the remote database
    UPDATE,     // Entity needs to be updated in the remote database
    DELETE      // Entity needs to be deleted from the remote database
}
