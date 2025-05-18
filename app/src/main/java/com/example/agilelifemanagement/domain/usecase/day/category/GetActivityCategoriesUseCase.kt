package com.example.agilelifemanagement.domain.usecase.day.category

import com.example.agilelifemanagement.domain.model.GoalCategory
import com.example.agilelifemanagement.domain.repository.temporary.TempCategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all activity categories.
 * 
 * Note: This is a temporary implementation after the May 15, 2025 architectural change
 * where the data layer was archived for rebuilding.
 */
class GetActivityCategoriesUseCase @Inject constructor(
    private val categoryRepository: TempCategoryRepository
) {
    operator fun invoke(): Flow<List<GoalCategory>> {
        return categoryRepository.getAllCategories()
    }
}
