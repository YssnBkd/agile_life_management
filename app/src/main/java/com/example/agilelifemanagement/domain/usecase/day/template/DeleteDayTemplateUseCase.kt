package com.example.agilelifemanagement.domain.usecase.day.template

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.temporary.TempTemplateRepository
import javax.inject.Inject

/**
 * Use case for deleting a day template.
 */
class DeleteDayTemplateUseCase @Inject constructor(
    private val templateRepository: TempTemplateRepository
) {
    suspend operator fun invoke(templateId: String): Result<Unit> {
        return try {
            templateRepository.deleteTemplate(templateId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete template")
        }
    }
}
