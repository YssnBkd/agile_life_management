package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.data.remote.api.TagApiService
import com.example.agilelifemanagement.data.remote.model.TagDto
import com.example.agilelifemanagement.data.remote.model.toDto
import com.example.agilelifemanagement.data.remote.model.toDomain
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Tag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for tags using Ktor client to communicate with the backend API.
 * This class handles all network operations related to tags.
 */
@Singleton
class TagRemoteDataSource @Inject constructor(
    private val tagApiService: TagApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Fetches all tags from the remote API.
     * @return List of tag domain models
     */
    suspend fun getAllTags(): List<Tag> = withContext(ioDispatcher) {
        try {
            tagApiService.getAllTags().map { it.toDomain() }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching tags from remote API")
            emptyList()
        }
    }

    /**
     * Fetches a specific tag by ID from the remote API.
     * @param tagId The tag identifier
     * @return The tag domain model, or null if not found
     */
    suspend fun getTagById(tagId: String): Tag? = withContext(ioDispatcher) {
        try {
            tagApiService.getTagById(tagId)?.toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching tag from remote API: $tagId")
            null
        }
    }

    /**
     * Creates a new tag in the remote API.
     * @param tag The tag domain model to create
     * @return The created tag domain model with assigned ID
     */
    suspend fun createTag(tag: Tag): Tag = withContext(ioDispatcher) {
        try {
            tagApiService.createTag(tag.toDto()).toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Error creating tag in remote API")
            throw e
        }
    }

    /**
     * Updates an existing tag in the remote API.
     * @param tag The tag domain model with updated properties
     * @return The updated tag domain model
     */
    suspend fun updateTag(tag: Tag): Tag = withContext(ioDispatcher) {
        try {
            tagApiService.updateTag(tag.toDto()).toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Error updating tag in remote API: ${tag.id}")
            throw e
        }
    }

    /**
     * Deletes a tag from the remote API.
     * @param tagId The tag identifier to delete
     * @return true if deletion was successful
     */
    suspend fun deleteTag(tagId: String): Boolean = withContext(ioDispatcher) {
        try {
            tagApiService.deleteTag(tagId)
            true
        } catch (e: Exception) {
            Timber.e(e, "Error deleting tag from remote API: $tagId")
            throw e
        }
    }
}
