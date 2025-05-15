package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.ActivityCategoryDao
import com.example.agilelifemanagement.data.local.entity.ActivityCategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Local data source for activity categories.
 * Uses Room DAO to perform database operations.
 */
class ActivityCategoryLocalDataSource @Inject constructor(
    private val categoryDao: ActivityCategoryDao
) {
    /**
     * Get all categories as an observable flow.
     */
    fun observeCategories(): Flow<List<ActivityCategoryEntity>> = 
        categoryDao.getAllCategories()
    
    /**
     * Search categories by name.
     */
    fun searchCategories(query: String): Flow<List<ActivityCategoryEntity>> = 
        categoryDao.searchCategories("%$query%")
    
    /**
     * Get a specific category by ID.
     */
    suspend fun getCategoryById(categoryId: String): ActivityCategoryEntity? = 
        categoryDao.getCategoryById(categoryId)
    
    /**
     * Insert a category.
     */
    suspend fun insertCategory(category: ActivityCategoryEntity) {
        categoryDao.insertCategory(category)
    }
    
    /**
     * Insert multiple categories.
     */
    suspend fun insertCategories(categories: List<ActivityCategoryEntity>) {
        categoryDao.insertCategories(categories)
    }
    
    /**
     * Update a category.
     */
    suspend fun updateCategory(category: ActivityCategoryEntity): Int =
        categoryDao.updateCategory(category)
    
    /**
     * Delete a category.
     */
    suspend fun deleteCategory(categoryId: String): Int =
        categoryDao.deleteCategory(categoryId)
}
