package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.CategoryDto
import io.ktor.client.HttpClient
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [CategoryApiService] that serves as a stub for future API integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual API calls in future iterations.
 */
@Singleton
class CategoryApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : CategoryApiService {
    
    override suspend fun getAllCategories(): List<CategoryDto> {
        Timber.d("API: getAllCategories called (stub)")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getCategoryById(categoryId: String): CategoryDto? {
        Timber.d("API: getCategoryById called (stub) for category: $categoryId")
        return null // Stub implementation
    }
    
    override suspend fun createCategory(category: CategoryDto): CategoryDto {
        Timber.d("API: createCategory called (stub) for category: ${category.name}")
        return category.copy(id = "generated-id-${System.currentTimeMillis()}") // Stub implementation
    }
    
    override suspend fun updateCategory(category: CategoryDto): CategoryDto {
        Timber.d("API: updateCategory called (stub) for category: ${category.id}")
        return category // Stub implementation
    }
    
    override suspend fun deleteCategory(categoryId: String): Boolean {
        Timber.d("API: deleteCategory called (stub) for category: $categoryId")
        return true // Stub implementation
    }
}
