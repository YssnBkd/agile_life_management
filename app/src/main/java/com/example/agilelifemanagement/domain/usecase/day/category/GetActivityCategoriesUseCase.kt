package com.example.agilelifemanagement.domain.usecase.day.category

import com.example.agilelifemanagement.domain.model.GoalCategory
import com.example.agilelifemanagement.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all activity categories.
 */
class GetActivityCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<GoalCategory>> {
        return categoryRepository.getAllCategories()
    }
}
