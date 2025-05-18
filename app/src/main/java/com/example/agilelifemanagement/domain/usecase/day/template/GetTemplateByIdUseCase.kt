package com.example.agilelifemanagement.domain.usecase.day.template

import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.domain.repository.temporary.TempTemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a specific day template by ID.
 */
class GetTemplateByIdUseCase @Inject constructor(
    private val templateRepository: TempTemplateRepository
) {
    operator fun invoke(templateId: String): Flow<DayTemplate?> {
        return templateRepository.getTemplateById(templateId)
    }
}
