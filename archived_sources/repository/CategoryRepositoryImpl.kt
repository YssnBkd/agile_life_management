package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.ActivityCategoryLocalDataSource
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
 * Follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryLocalDataSource: ActivityCategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
    private val categoryMapper: CategoryMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<ActivityCategory>> {
        // Offline-first: Return local data immediately, then try to sync with remote
        syncCategoriesWithRemote()
        return categoryLocalDataSource.observeCategories()
            .map { categoryEntities -> categoryEntities.map { categoryMapper.mapToDomain(it) } }
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
                    // Handle error but don't propagate - offline-first approach continues with local data
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
            Result.failure(e)
        }
    }

    override suspend fun createCategory(category: ActivityCategory): Result<ActivityCategory> = withContext(ioDispatcher) {
        try {
            // First save to local database for immediate feedback
            val categoryEntity = categoryMapper.mapToEntity(category)
            val insertedId = categoryLocalDataSource.insertCategory(categoryEntity)
            val insertedCategory = category.copy(id = insertedId.toString())
            
            // Then try to save to remote in background
            launch {
                try {
                    categoryRemoteDataSource.createCategory(insertedCategory)
                    Timber.d("Successfully synced new category to remote: ${insertedCategory.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing new category to remote: ${insertedCategory.id}")
                    // Category will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(insertedCategory)
        } catch (e: Exception) {
            Timber.e(e, "Error creating category locally")
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: ActivityCategory): Result<ActivityCategory> = withContext(ioDispatcher) {
        try {
            // Update local database first for immediate feedback
            val categoryEntity = categoryMapper.mapToEntity(category)
            categoryLocalDataSource.updateCategory(categoryEntity)
            
            // Then try to update remote in background
            launch {
                try {
                    categoryRemoteDataSource.updateCategory(category)
                    Timber.d("Successfully synced updated category to remote: ${category.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing updated category to remote: ${category.id}")
                    // Category will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(category)
        } catch (e: Exception) {
            Timber.e(e, "Error updating category locally: ${category.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Delete from local database first
            categoryLocalDataSource.deleteCategory(categoryId)
            
            // Then try to delete from remote in background
            launch {
                try {
                    categoryRemoteDataSource.deleteCategory(categoryId)
                    Timber.d("Successfully deleted category from remote: $categoryId")
                } catch (e: Exception) {
                    Timber.e(e, "Error deleting category from remote: $categoryId")
                    // Could add to a deletion sync queue for retry mechanism
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting category locally: $categoryId")
            Result.failure(e)
        }
    }
}
