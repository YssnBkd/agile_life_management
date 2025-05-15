package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.ActivityCategory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [CategoryRemoteDataSource] that serves as a stub for future remote integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual remote API calls in future iterations.
 */
@Singleton
class CategoryRemoteDataSourceImpl @Inject constructor() : CategoryRemoteDataSource {
    
    override suspend fun getAllCategories(): List<ActivityCategory> {
        Timber.d("Remote: getAllCategories called (stub)")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getCategoryById(categoryId: String): ActivityCategory? {
        Timber.d("Remote: getCategoryById called (stub) for category: $categoryId")
        return null // Stub implementation
    }
    
    override suspend fun createCategory(category: ActivityCategory): ActivityCategory {
        Timber.d("Remote: createCategory called (stub) for category: ${category.name}")
        return category // Stub implementation, just returns the input category
    }
    
    override suspend fun updateCategory(category: ActivityCategory): ActivityCategory {
        Timber.d("Remote: updateCategory called (stub) for category: ${category.id}")
        return category // Stub implementation, just returns the input category
    }
    
    override suspend fun deleteCategory(categoryId: String): Boolean {
        Timber.d("Remote: deleteCategory called (stub) for category: $categoryId")
        return true // Stub implementation, pretend it always succeeds
    }
}
