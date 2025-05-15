package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.CategoryDto

/**
 * Service interface for category-related API operations.
 */
interface CategoryApiService {
    
    /**
     * Get all categories from the API.
     * @return List of category DTOs
     */
    suspend fun getAllCategories(): List<CategoryDto>
    
    /**
     * Get a specific category by ID from the API.
     * @param categoryId The category identifier
     * @return The category DTO if found, or null
     */
    suspend fun getCategoryById(categoryId: String): CategoryDto?
    
    /**
     * Create a new category in the API.
     * @param category The category DTO to create
     * @return The created category DTO with its assigned ID
     */
    suspend fun createCategory(category: CategoryDto): CategoryDto
    
    /**
     * Update an existing category in the API.
     * @param category The updated category DTO
     * @return The updated category DTO
     */
    suspend fun updateCategory(category: CategoryDto): CategoryDto
    
    /**
     * Delete a category from the API.
     * @param categoryId The ID of the category to delete
     * @return True if the category was successfully deleted
     */
    suspend fun deleteCategory(categoryId: String): Boolean
}
