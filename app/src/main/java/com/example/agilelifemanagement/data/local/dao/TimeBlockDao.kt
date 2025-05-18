package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TimeBlockEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for accessing and manipulating time block data in the database.
 * 
 * This DAO supports Material 3 Expressive design principles by providing:
 * - Reactive data streams using Flow for responsive UI updates
 * - Time-based queries for timeline visualizations
 * - Category-based filtering for visual grouping
 * - Status updates for completion tracking
 */
@Dao
interface TimeBlockDao {
    /**
     * Insert a single time block.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeBlock(timeBlock: TimeBlockEntity)
    
    /**
     * Insert multiple time blocks.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeBlocks(timeBlocks: List<TimeBlockEntity>)
    
    /**
     * Update an existing time block.
     * 
     * @return The number of rows updated
     */
    @Update
    suspend fun updateTimeBlock(timeBlock: TimeBlockEntity): Int
    
    /**
     * Update the completion status of a time block.
     * 
     * @return The number of rows updated
     */
    @Query("UPDATE time_blocks SET is_completed = :completed WHERE id = :timeBlockId")
    suspend fun updateTimeBlockCompletion(timeBlockId: String, completed: Boolean): Int
    
    /**
     * Delete a time block by its ID.
     * 
     * @return The number of rows deleted
     */
    @Query("DELETE FROM time_blocks WHERE id = :timeBlockId")
    suspend fun deleteTimeBlock(timeBlockId: String): Int
    
    /**
     * Get a single time block by its ID.
     */
    @Query("SELECT * FROM time_blocks WHERE id = :timeBlockId")
    suspend fun getTimeBlockById(timeBlockId: String): TimeBlockEntity?
    
    /**
     * Get all time blocks for a specific date.
     * 
     * @param date The date in 'yyyy-MM-dd' format
     * @return A Flow of time blocks for the specified date
     */
    @Query("SELECT * FROM time_blocks WHERE date = :date ORDER BY start_time")
    fun getTimeBlocksByDate(date: String): Flow<List<TimeBlockEntity>>
    
    /**
     * Get all time blocks for a specific category.
     * 
     * @param categoryId The category identifier
     * @return A Flow of time blocks for the specified category
     */
    @Query("SELECT * FROM time_blocks WHERE category_id = :categoryId ORDER BY date, start_time")
    fun getTimeBlocksByCategory(categoryId: String): Flow<List<TimeBlockEntity>>
    
    /**
     * Get all time blocks for a specific date based on their completion status.
     * 
     * @param completed Whether the time blocks are completed
     * @param date The date in 'yyyy-MM-dd' format
     * @return A Flow of time blocks matching the criteria
     */
    @Query("SELECT * FROM time_blocks WHERE is_completed = :completed AND date = :date ORDER BY start_time")
    fun getTimeBlocksByCompletionAndDate(completed: Boolean, date: String): Flow<List<TimeBlockEntity>>
    
    /**
     * Get all time blocks within a date range.
     * 
     * @param startDate The start date in 'yyyy-MM-dd' format (inclusive)
     * @param endDate The end date in 'yyyy-MM-dd' format (inclusive)
     * @return A Flow of time blocks within the date range
     */
    @Query("SELECT * FROM time_blocks WHERE date BETWEEN :startDate AND :endDate ORDER BY date, start_time")
    fun getTimeBlocksForDateRange(startDate: String, endDate: String): Flow<List<TimeBlockEntity>>
    
    /**
     * Get all time blocks ordered by date and start time.
     * This ordering supports Material 3 timeline visualizations.
     */
    @Query("SELECT * FROM time_blocks ORDER BY date, start_time")
    fun getAllTimeBlocks(): Flow<List<TimeBlockEntity>>
    
    /**
     * Result class for category count query.
     * This is necessary for Room to properly map the SQL results.
     */
    data class CategoryCount(val category_id: String, val count: Int)
    
    /**
     * Get count of time blocks by category.
     * Useful for dashboard summaries and analytics.
     */
    @Query("SELECT category_id, COUNT(*) as count FROM time_blocks GROUP BY category_id")
    fun getTimeBlockCountByCategory(): Flow<List<CategoryCount>>
}
