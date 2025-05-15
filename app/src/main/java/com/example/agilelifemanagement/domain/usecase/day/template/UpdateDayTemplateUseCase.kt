package com.example.agilelifemanagement.domain.usecase.day.template

import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.TemplateRepository
import javax.inject.Inject

/**
 * Use case for updating an existing day template.
 */
class UpdateDayTemplateUseCase @Inject constructor(
    private val templateRepository: TemplateRepository
) {
    suspend operator fun invoke(template: DayTemplate): Result<Unit> {
        return try {
            templateRepository.updateTemplate(template)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update template")
        }
    }
}
