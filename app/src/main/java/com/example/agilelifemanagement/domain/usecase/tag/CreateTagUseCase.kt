package com.example.agilelifemanagement.domain.usecase.tag

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for creating a new tag.
 */
class CreateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: Tag): Result<Unit> {
        return try {
            tagRepository.insertTag(tag)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create tag")
        }
    }
}
