package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.ActivityCategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for accessing and manipulating activity category data in the database.
 * 
 * This DAO supports Material 3 Expressive design principles by providing:
 * - Reactive data streams using Flow for responsive UI updates
 * - Category-based operations for visual grouping and organization
 * - Support for color and icon management to ensure visual consistency
 */
@Dao
interface ActivityCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ActivityCategoryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<ActivityCategoryEntity>)
    
    @Update
    suspend fun updateCategory(category: ActivityCategoryEntity): Int
    
    @Query("DELETE FROM activity_categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: String): Int
    
    @Query("SELECT * FROM activity_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): ActivityCategoryEntity?
    
    @Query("SELECT * FROM activity_categories")
    fun getAllCategories(): Flow<List<ActivityCategoryEntity>>
    
    @Query("SELECT * FROM activity_categories WHERE name LIKE :query")
    fun searchCategories(query: String): Flow<List<ActivityCategoryEntity>>
    
    /**
     * Update just the color of a category.
     * Supports Material 3 color system for visual theming.
     */
    @Query("UPDATE activity_categories SET color_hex = :colorHex WHERE id = :categoryId")
    suspend fun updateCategoryColor(categoryId: String, colorHex: String): Int
    
    /**
     * Update just the icon of a category.
     * Supports Material 3 iconography for visual representation.
     */
    @Query("UPDATE activity_categories SET icon_name = :iconName WHERE id = :categoryId")
    suspend fun updateCategoryIcon(categoryId: String, iconName: String): Int
    
    /**
     * Get only system-defined categories.
     * These are predefined categories with Material 3 design system colors.
     */
    @Query("SELECT * FROM activity_categories WHERE is_system_category = 1")
    fun getSystemCategories(): Flow<List<ActivityCategoryEntity>>
    
    /**
     * Get only user-defined categories.
     * These are custom categories created by the user.
     */
    @Query("SELECT * FROM activity_categories WHERE is_system_category = 0")
    fun getUserCategories(): Flow<List<ActivityCategoryEntity>>
}
