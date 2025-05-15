package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.ActivityCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for activity category operations.
 * Manages the categories used to organize day activities.
 */
interface CategoryRepository {
    /**
     * Get all activity categories as an observable Flow.
     * @return A Flow emitting lists of all categories when changes occur
     */
    fun getAllCategories(): Flow<List<ActivityCategory>>
    
    /**
     * Get a specific category by ID.
     * @param categoryId The category identifier
     * @return A Result containing the category if found, or an error if not
     */
    suspend fun getCategoryById(categoryId: String): Result<ActivityCategory>
    
    /**
     * Create a new activity category.
     * @param category The category to create
     * @return A Result containing the created category with its assigned ID
     */
    suspend fun createCategory(category: ActivityCategory): Result<ActivityCategory>
    
    /**
     * Update an existing activity category.
     * @param category The updated category
     * @return A Result containing the updated category if successful
     */
    suspend fun updateCategory(category: ActivityCategory): Result<ActivityCategory>
    
    /**
     * Delete an activity category.
     * @param categoryId The ID of the category to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteCategory(categoryId: String): Result<Boolean>
}
