package com.example.agilelifemanagement.domain.usecase.day.template

import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.domain.repository.temporary.TempTemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all day templates.
 * 
 * Note: Using temporary repository interface after May 15, 2025 architectural change
 * where the data layer was archived for rebuilding.
 */
class GetDayTemplatesUseCase @Inject constructor(
    private val templateRepository: TempTemplateRepository
) {
    operator fun invoke(): Flow<List<DayTemplate>> {
        return templateRepository.getAllTemplates()
    }
}
