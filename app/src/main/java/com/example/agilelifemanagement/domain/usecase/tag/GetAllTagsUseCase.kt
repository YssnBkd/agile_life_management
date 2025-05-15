package com.example.agilelifemanagement.domain.usecase.tag

import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all tags.
 */
class GetAllTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    operator fun invoke(): Flow<List<Tag>> {
        return tagRepository.getAllTags()
    }
}
