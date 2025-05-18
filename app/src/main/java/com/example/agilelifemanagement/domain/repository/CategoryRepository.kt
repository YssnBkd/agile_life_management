package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.ActivityCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for activity category operations.
 * Manages the categories used to organize day activities.
 * 
 * This repository follows Material 3 Expressive design principles by providing:
 * - Reactive data streams for responsive UI updates
 * - Color management for visual theming consistency
 * - Icon management for visual representation
 * - Search capabilities for better UX in category selection
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
    
    // Material 3 Expressive Design Support
    
    /**
     * Update the color of a category.
     * Supports Material 3 dynamic color system for visual consistency.
     * 
     * @param categoryId The ID of the category to update
     * @param colorHex The new color in hex format (e.g., "#FF5733")
     * @return A Result containing the updated category if successful
     */
    suspend fun updateCategoryColor(categoryId: String, colorHex: String): Result<ActivityCategory>
    
    /**
     * Update the icon of a category.
     * Supports Material 3 iconography for visual representation.
     * 
     * @param categoryId The ID of the category to update
     * @param iconName The name of the icon to use
     * @return A Result containing the updated category if successful
     */
    suspend fun updateCategoryIcon(categoryId: String, iconName: String): Result<ActivityCategory>
    
    /**
     * Search for categories by name pattern.
     * Supports Material 3 search components for improved UX.
     * 
     * @param query The search query
     * @return A Flow emitting lists of matching categories
     */
    fun searchCategories(query: String): Flow<List<ActivityCategory>>
    
    /**
     * Get system-defined categories.
     * These are predefined categories with Material 3 design system colors.
     * 
     * @return A Flow emitting lists of system categories
     */
    fun getSystemCategories(): Flow<List<ActivityCategory>>
    
    /**
     * Get user-defined categories.
     * These are custom categories created by the user.
     * 
     * @return A Flow emitting lists of user categories
     */
    fun getUserCategories(): Flow<List<ActivityCategory>>
}
