package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.DailyCheckupDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.PostgrestRequestBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for Daily Checkup operations with Supabase.
 */
@Singleton
class DailyCheckupApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "daily_checkups"
    
    /**
     * Get a daily checkup by ID from Supabase.
     */
    suspend fun getDailyCheckupById(checkupId: String): Result<DailyCheckupDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val checkup = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", checkupId)
                    }
                }
                .decodeSingle<DailyCheckupDto>()
            
            Result.Success(checkup)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting daily checkup by ID: ${e.message}", e)
            Result.Error("Failed to get daily checkup: ${e.message}", e)
        }
    }
    
    /**
     * Get all daily checkups for a user from Supabase.
     */
    suspend fun getDailyCheckupsByUserId(userId: String): Result<List<DailyCheckupDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val checkups = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                    order("date", Order.DESCENDING)
                }
                .decodeList<DailyCheckupDto>()
            
            Result.Success(checkups)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting daily checkups for user: ${e.message}", e)
            Result.Error("Failed to get daily checkups: ${e.message}", e)
        }
    }
    
    /**
     * Get all daily checkups for a sprint from Supabase.
     */
    suspend fun getDailyCheckupsBySprintId(sprintId: String): Result<List<DailyCheckupDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val checkups = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("sprint_id", sprintId)
                    }
                    order("date", Order.ASCENDING)
                }
                .decodeList<DailyCheckupDto>()
            
            Result.Success(checkups)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting daily checkups for sprint: ${e.message}", e)
            Result.Error("Failed to get daily checkups for sprint: ${e.message}", e)
        }
    }
    
    /**
     * Create a daily checkup in Supabase.
     */
    suspend fun createDailyCheckup(checkupDto: DailyCheckupDto): Result<DailyCheckupDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(checkupDto)
            
            Result.Success(checkupDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating daily checkup: ${e.message}", e)
            Result.Error("Failed to create daily checkup: ${e.message}", e)
        }
    }
    
    /**
     * Update a daily checkup in Supabase.
     */
    suspend fun updateDailyCheckup(checkupDto: DailyCheckupDto): Result<DailyCheckupDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .update({
                    set("notes", checkupDto.notes)
                    set("updated_at", System.currentTimeMillis())
                }) {
                    filter {
                        eq("id", checkupDto.id)
                    }
                }
            
            Result.Success(checkupDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating daily checkup: ${e.message}", e)
            Result.Error("Failed to update daily checkup: ${e.message}", e)
        }
    }
    
    /**
     * Delete a daily checkup from Supabase.
     */
    suspend fun deleteDailyCheckup(checkupId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", checkupId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting daily checkup: ${e.message}", e)
            Result.Error("Failed to delete daily checkup: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "DailyCheckupApiService"
    }
    
    /**
     * Upsert (insert or update) a daily checkup in Supabase.
     */
    suspend fun upsertDailyCheckup(checkupDto: DailyCheckupDto): Result<DailyCheckupDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            // Check if the checkup exists
            val exists = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", checkupDto.id)
                    }
                }
                .decodeList<DailyCheckupDto>()
                .isNotEmpty()
                
            if (exists) {
                // Update existing checkup
                updateDailyCheckup(checkupDto)
            } else {
                // Create new checkup
                createDailyCheckup(checkupDto)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting daily checkup: ${e.message}", e)
            Result.Error("Failed to upsert daily checkup: ${e.message}", e)
        }
    }
}
