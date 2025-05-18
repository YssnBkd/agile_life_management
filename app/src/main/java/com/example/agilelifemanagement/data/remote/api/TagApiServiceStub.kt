package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.TagDto
import com.example.agilelifemanagement.di.UnauthenticatedClient
import io.ktor.client.HttpClient
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of TagApiService for development and testing.
 * Provides placeholder responses instead of making actual network calls.
 */
@Singleton
class TagApiServiceStub @Inject constructor(
    @UnauthenticatedClient private val httpClient: HttpClient
) : TagApiService {
    private val baseUrl = "tags" // kept for consistency with real implementation
    
    /**
     * Fetches all tags from the API (stub).
     * @return Empty list of TagDto objects
     */
    override suspend fun getAllTags(): List<TagDto> {
        Timber.d("TagApiServiceStub: getAllTags called (stub)")
        return emptyList()
    }
    
    /**
     * Fetches a specific tag by ID from the API (stub).
     * @param tagId The tag identifier
     * @return Null (stub implementation)
     */
    override suspend fun getTagById(tagId: String): TagDto? {
        Timber.d("TagApiServiceStub: getTagById called (stub) for tag: $tagId")
        return null
    }
    
    /**
     * Creates a new tag in the API (stub).
     * @param tag The TagDto to create
     * @return The input TagDto (stub implementation)
     */
    override suspend fun createTag(tag: TagDto): TagDto {
        Timber.d("TagApiServiceStub: createTag called (stub) for tag: ${tag.name}")
        return tag
    }
    
    /**
     * Updates an existing tag in the API (stub).
     * @param tag The TagDto to update
     * @return The input TagDto (stub implementation)
     */
    override suspend fun updateTag(tag: TagDto): TagDto {
        Timber.d("TagApiServiceStub: updateTag called (stub) for tag: ${tag.id}")
        return tag
    }
    
    /**
     * Deletes a tag from the API (stub).
     * @param tagId The ID of the tag to delete
     * @return True (stub implementation always succeeds)
     */
    override suspend fun deleteTag(tagId: String): Boolean {
        Timber.d("TagApiServiceStub: deleteTag called (stub) for tag: $tagId")
        return true
    }
}
