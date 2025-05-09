package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.NotificationDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for Notification operations with Supabase.
 * Implements security best practices according to the app's security implementation guidelines.
 */
@Singleton
class NotificationApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.notifications"
    
    /**
     * Get a notification by ID from Supabase.
     */
    suspend fun getNotificationById(notificationId: String): Result<NotificationDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val notification = client.postgrest[tableName]
                .select {
                    filter {
                        eq("id", notificationId)
                    }
                }
                .decodeSingleOrNull<NotificationDto>()
            
            if (notification != null) {
                Result.Success(notification)
            } else {
                Result.Error("Notification not found", null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting notification by ID: ${e.message}", e)
            Result.Error("Failed to get notification: ${e.message}", e)
        }
    }
    
    /**
     * Get all notifications for a user from Supabase.
     */
    suspend fun getNotificationsByUserId(userId: String): Result<List<NotificationDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val notifications = client.postgrest[tableName]
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order("scheduled_time", Order.DESCENDING)
                }
                .decodeList<NotificationDto>()
            
            Result.Success(notifications)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting notifications for user: ${e.message}", e)
            Result.Error("Failed to get notifications: ${e.message}", e)
        }
    }
    
    /**
     * Get unread notifications for a user from Supabase.
     */
    suspend fun getUnreadNotifications(userId: String): Result<List<NotificationDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val notifications = client.postgrest[tableName]
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("is_read", false)
                    }
                    order("scheduled_time", Order.DESCENDING)
                }
                .decodeList<NotificationDto>()
            
            Result.Success(notifications)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting unread notifications: ${e.message}", e)
            Result.Error("Failed to get unread notifications: ${e.message}", e)
        }
    }
    
    /**
     * Get notifications related to a specific entity from Supabase.
     */
    suspend fun getNotificationsByRelatedEntity(
        userId: String,
        relatedEntityId: String,
        relatedEntityType: String
    ): Result<List<NotificationDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val notifications = client.postgrest[tableName]
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("related_entity_id", relatedEntityId)
                        eq("related_entity_type", relatedEntityType)
                    }
                    order("scheduled_time", Order.DESCENDING)
                }
                .decodeList<NotificationDto>()
            
            Result.Success(notifications)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting notifications for entity: ${e.message}", e)
            Result.Error("Failed to get notifications for entity: ${e.message}", e)
        }
    }
    
    /**
     * Create or update a notification in Supabase.
     */
    suspend fun upsertNotification(notificationDto: NotificationDto): Result<NotificationDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            // Check if notification exists
            val exists = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", notificationDto.id)
                    }
                }
                .decodeList<NotificationDto>()
                .isNotEmpty()
            if (exists) {
                client.postgrest[tableName]
                    .update(notificationDto) {
                        filter {
                            eq("id", notificationDto.id)
                        }
                    }
            } else {
                client.postgrest[tableName]
                    .insert(notificationDto)
            }
            
            Result.Success(notificationDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting notification: ${e.message}", e)
            Result.Error("Failed to save notification: ${e.message}", e)
        }
    }
    
    /**
     * Mark a notification as read in Supabase.
     */
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .update(mapOf("is_read" to true)) {
                    filter {
                        eq("id", notificationId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notification as read: ${e.message}", e)
            Result.Error("Failed to mark notification as read: ${e.message}", e)
        }
    }
    
    /**
     * Mark all notifications as read for a user in Supabase.
     */
    suspend fun markAllNotificationsAsRead(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .update(mapOf("is_read" to true)) {
                    filter {
                        eq("user_id", userId)
                        eq("is_read", false)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking all notifications as read: ${e.message}", e)
            Result.Error("Failed to mark all notifications as read: ${e.message}", e)
        }
    }
    
    /**
     * Delete a notification from Supabase.
     */
    suspend fun deleteNotification(notificationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", notificationId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting notification: ${e.message}", e)
            Result.Error("Failed to delete notification: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "NotificationApiService"
    }
}
