package com.example.agilelifemanagement.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.agilelifemanagement.data.local.dao.ActivityCategoryDao
import com.example.agilelifemanagement.data.local.entity.ActivityCategoryEntity
import com.example.agilelifemanagement.domain.model.ActivityCategory
import com.example.agilelifemanagement.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [CategoryRepository] that uses Room database as the data source.
 * 
 * This implementation follows Material 3 Expressive design principles by providing:
 * - Reactive data streams using Flow for real-time UI updates
 * - Support for customizing category colors within the Material 3 color system
 * - Support for Material 3 iconography
 * - Proper segregation of system and user-defined categories
 */
class CategoryRepositoryImpl @Inject constructor(
    private val activityCategoryDao: ActivityCategoryDao
) : CategoryRepository {

    /**
     * Maps an ActivityCategoryEntity to an ActivityCategory domain model.
     */
    private fun mapEntityToDomain(entity: ActivityCategoryEntity): ActivityCategory {
        return ActivityCategory(
            id = entity.id,
            name = entity.name,
            color = entity.colorHex,
            iconName = entity.iconName,
            isSystemCategory = entity.isSystemCategory
        )
    }

    /**
     * Maps an ActivityCategory domain model to an ActivityCategoryEntity.
     */
    private fun mapDomainToEntity(domainModel: ActivityCategory): ActivityCategoryEntity {
        return ActivityCategoryEntity(
            id = domainModel.id,
            name = domainModel.name,
            colorHex = domainModel.color,
            iconName = domainModel.iconName ?: "star",
            isSystemCategory = domainModel.isSystemCategory
        )
    }

    override fun getAllCategories(): Flow<List<ActivityCategory>> {
        return activityCategoryDao.getAllCategories().map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override suspend fun getCategoryById(categoryId: String): Result<ActivityCategory> {
        return try {
            val entity = activityCategoryDao.getCategoryById(categoryId) 
                ?: throw Exception("Category not found")
            Result.success(mapEntityToDomain(entity))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCategory(category: ActivityCategory): Result<ActivityCategory> {
        return try {
            val entity = mapDomainToEntity(category)
            activityCategoryDao.insertCategory(entity)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: ActivityCategory): Result<ActivityCategory> {
        return try {
            val entity = mapDomainToEntity(category)
            activityCategoryDao.updateCategory(entity)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Boolean> {
        return try {
            activityCategoryDao.deleteCategory(categoryId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Material 3 Expressive Design Support

    override suspend fun updateCategoryColor(categoryId: String, colorHex: String): Result<ActivityCategory> {
        return try {
            activityCategoryDao.updateCategoryColor(categoryId, colorHex)
            getCategoryById(categoryId) // Return the updated category
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategoryIcon(categoryId: String, iconName: String): Result<ActivityCategory> {
        return try {
            activityCategoryDao.updateCategoryIcon(categoryId, iconName)
            getCategoryById(categoryId) // Return the updated category
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchCategories(query: String): Flow<List<ActivityCategory>> {
        return activityCategoryDao.searchCategories("%$query%").map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override fun getSystemCategories(): Flow<List<ActivityCategory>> {
        return activityCategoryDao.getSystemCategories().map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override fun getUserCategories(): Flow<List<ActivityCategory>> {
        return activityCategoryDao.getUserCategories().map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }
}
