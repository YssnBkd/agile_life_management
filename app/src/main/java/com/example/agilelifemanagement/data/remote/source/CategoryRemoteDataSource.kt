package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.ActivityCategory

/**
 * Remote data source interface for activity categories.
 * Defines the contract for accessing and manipulating category data from remote sources.
 */
interface CategoryRemoteDataSource {
    
    /**
     * Get all categories from the remote source.
     * @return List of categories
     */
    suspend fun getAllCategories(): List<ActivityCategory>
    
    /**
     * Get a specific category by ID from the remote source.
     * @param categoryId The category identifier
     * @return The category if found, or null
     */
    suspend fun getCategoryById(categoryId: String): ActivityCategory?
    
    /**
     * Create a new category in the remote source.
     * @param category The category to create
     * @return The created category with its assigned ID
     */
    suspend fun createCategory(category: ActivityCategory): ActivityCategory
    
    /**
     * Update an existing category in the remote source.
     * @param category The updated category
     * @return The updated category
     */
    suspend fun updateCategory(category: ActivityCategory): ActivityCategory
    
    /**
     * Delete a category from the remote source.
     * @param categoryId The ID of the category to delete
     * @return True if the category was successfully deleted
     */
    suspend fun deleteCategory(categoryId: String): Boolean
}
