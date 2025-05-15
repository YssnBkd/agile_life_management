package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.ActivityCategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for accessing and manipulating activity category data in the database.
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
}
