package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.TimeBlock
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for TimeBlock operations.
 * Defines the contract for accessing and manipulating time block data.
 * 
 * This repository follows Material 3 Expressive design principles by providing:
 * - Reactive data streams for responsive timeline visualizations
 * - Date and time-based filtering for calendar integrations
 * - Category filtering for visual consistency with the design system
 * - Completion status handling for visual state representation
 */
interface TimeBlockRepository {
    /**
     * Get all time blocks as an observable Flow.
     * 
     * @return A Flow emitting lists of all time blocks when changes occur
     */
    fun getAllTimeBlocks(): Flow<List<TimeBlock>>
    
    /**
     * Get time blocks for a specific date.
     * Used in day planner screens and daily timeline visualizations.
     * 
     * @param date The date to filter by
     * @return A Flow emitting lists of time blocks for the specified date
     */
    fun getTimeBlocksByDate(date: LocalDate): Flow<List<TimeBlock>>
    
    /**
     * Get time blocks for a date range.
     * Used in week and month views with Material 3 calendar components.
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A Flow emitting lists of time blocks within the date range
     */
    fun getTimeBlocksForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TimeBlock>>
    
    /**
     * Get a specific time block by ID.
     * 
     * @param timeBlockId The time block identifier
     * @return A Result containing the time block if found, or an error if not
     */
    suspend fun getTimeBlockById(timeBlockId: String): Result<TimeBlock>
    
    /**
     * Get time blocks by category.
     * Used to visually group time blocks by category in Material 3 lists.
     * 
     * @param categoryId The category identifier
     * @return A Flow emitting lists of time blocks with the specified category
     */
    fun getTimeBlocksByCategory(categoryId: String): Flow<List<TimeBlock>>
    
    /**
     * Add a new time block.
     * 
     * @param timeBlock The time block to add
     * @return A Result containing the added time block with its assigned ID
     */
    suspend fun addTimeBlock(timeBlock: TimeBlock): Result<TimeBlock>
    
    /**
     * Update an existing time block.
     * 
     * @param timeBlock The updated time block
     * @return A Result containing the updated time block if successful
     */
    suspend fun updateTimeBlock(timeBlock: TimeBlock): Result<TimeBlock>
    
    /**
     * Update the completion status of a time block.
     * Used for visual state changes in Material 3 components.
     * 
     * @param timeBlockId The ID of the time block to update
     * @param completed The new completion status
     * @return A Result containing the updated time block if successful
     */
    suspend fun updateTimeBlockCompletion(timeBlockId: String, completed: Boolean): Result<TimeBlock>
    
    /**
     * Delete a time block.
     * 
     * @param timeBlockId The ID of the time block to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteTimeBlock(timeBlockId: String): Result<Boolean>
    
    /**
     * Get time blocks grouped by category.
     * Used for Material 3 dashboard visualizations and analytics.
     * 
     * @return A Flow emitting a map of category IDs to lists of time blocks
     */
    fun getTimeBlocksByCategories(): Flow<Map<String, List<TimeBlock>>>
    
    /**
     * Get count of time blocks by category.
     * Used for Material 3 dashboard visualizations and analytics.
     * 
     * @return A Flow emitting a map of category IDs to counts
     */
    fun getTimeBlockCountByCategory(): Flow<Map<String, Int>>
}
