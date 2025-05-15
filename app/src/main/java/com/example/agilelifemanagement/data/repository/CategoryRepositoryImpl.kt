package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.CategoryLocalDataSource
import com.example.agilelifemanagement.data.mapper.CategoryMapper
import com.example.agilelifemanagement.data.remote.source.CategoryRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.ActivityCategory
import com.example.agilelifemanagement.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [CategoryRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
    private val categoryMapper: CategoryMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<ActivityCategory>> {
        // Trigger background sync of all categories
        syncCategoriesWithRemote()
        
        // Return local data immediately for responsive UI
        return categoryLocalDataSource.observeCategories()
            .map { categories -> categories.map { categoryMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    /**
     * Synchronizes local category data with remote data source in the background.
     */
    private fun syncCategoriesWithRemote() {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteCategories = categoryRemoteDataSource.getAllCategories()
                    val entityCategories = remoteCategories.map { categoryMapper.mapToEntity(it) }
                    categoryLocalDataSource.insertCategories(entityCategories)
                    Timber.d("Successfully synced ${remoteCategories.size} categories with remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing categories with remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }
    
    override suspend fun getCategoryById(categoryId: String): Result<ActivityCategory> = withContext(ioDispatcher) {
        try {
            // First try to get from local database
            var categoryEntity = categoryLocalDataSource.getCategoryById(categoryId)
            
            // If not found locally or we need the latest data, try remote
            if (categoryEntity == null) {
                try {
                    val remoteCategory = categoryRemoteDataSource.getCategoryById(categoryId)
                    if (remoteCategory != null) {
                        // Found on remote, save to local database
                        val entity = categoryMapper.mapToEntity(remoteCategory)
                        categoryLocalDataSource.insertCategory(entity)
                        categoryEntity = entity
                        Timber.d("Successfully fetched category from remote: $categoryId")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching category from remote: $categoryId")
                    // Continue with local data (which might be null)
                }
            }
            
            categoryEntity?.let {
                Result.success(categoryMapper.mapToDomain(it))
            } ?: Result.failure(NoSuchElementException("Category not found with ID: $categoryId"))
        } catch (e: Exception) {
            Timber.e(e, "Error getting category: $categoryId")
            Result.failure(e)
        }
    }

    override suspend fun createCategory(category: ActivityCategory): Result<ActivityCategory> = withContext(ioDispatcher) {
        try {
            // First save to local database for immediate update to UI
            val categoryEntity = categoryMapper.mapToEntity(category)
            val insertedId = categoryLocalDataSource.insertCategory(categoryEntity)
            val insertedCategory = category.copy(id = insertedId)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteCategory = categoryRemoteDataSource.createCategory(insertedCategory)
                    // Update local cache with remote response (might contain additional server-side data)
                    val updatedEntity = categoryMapper.mapToEntity(remoteCategory)
                    categoryLocalDataSource.updateCategory(updatedEntity)
                    Timber.d("Successfully synchronized category creation with remote: $insertedId")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync category creation with remote: $insertedId")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(insertedCategory)
        } catch (e: Exception) {
            Timber.e(e, "Error creating category: ${category.name}")
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: ActivityCategory): Result<ActivityCategory> = withContext(ioDispatcher) {
        try {
            // First update local database for immediate update to UI
            val categoryEntity = categoryMapper.mapToEntity(category)
            categoryLocalDataSource.updateCategory(categoryEntity)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteCategory = categoryRemoteDataSource.updateCategory(category)
                    // Update local cache with remote response (might contain additional server-side data)
                    val updatedEntity = categoryMapper.mapToEntity(remoteCategory)
                    categoryLocalDataSource.updateCategory(updatedEntity)
                    Timber.d("Successfully synchronized category update with remote: ${category.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync category update with remote: ${category.id}")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(category)
        } catch (e: Exception) {
            Timber.e(e, "Error updating category: ${category.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // First delete from local database for immediate update to UI
            categoryLocalDataSource.deleteCategory(categoryId)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteResult = categoryRemoteDataSource.deleteCategory(categoryId)
                    if (remoteResult) {
                        Timber.d("Successfully synchronized category deletion with remote: $categoryId")
                    } else {
                        Timber.w("Remote deletion returned false for category: $categoryId")
                        // This might indicate that the category doesn't exist remotely
                        // or couldn't be deleted due to constraints
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync category deletion with remote: $categoryId")
                    // Continue with local deletion - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting category: $categoryId")
            Result.failure(e)
        }
    }
}
