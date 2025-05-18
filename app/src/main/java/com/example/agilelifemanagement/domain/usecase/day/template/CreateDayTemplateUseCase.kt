package com.example.agilelifemanagement.domain.usecase.day.template

import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.temporary.TempTemplateRepository
import javax.inject.Inject

/**
 * Use case for creating a new day template.
 */
class CreateDayTemplateUseCase @Inject constructor(
    private val templateRepository: TempTemplateRepository
) {
    suspend operator fun invoke(template: DayTemplate): Result<Unit> {
        return try {
            val result = templateRepository.createTemplate(template)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create template")
        }
    }
}
