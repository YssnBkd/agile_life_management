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
    suspend operator fun invoke(tag: Tag): kotlin.Result<Unit> {
        // Use the createTag method which is available in the repository
        return tagRepository.createTag(tag).map { Unit }
    }
}
