package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for tag operations.
 * Manages tags used for categorizing tasks.
 */
interface TagRepository {
    /**
     * Get all tags as an observable Flow.
     * @return A Flow emitting lists of all tags when changes occur
     */
    fun getAllTags(): Flow<List<Tag>>
    
    /**
     * Get a specific tag by ID.
     * @param tagId The tag identifier
     * @return A Result containing the tag if found, or an error if not
     */
    suspend fun getTagById(tagId: String): Result<Tag>
    
    /**
     * Create a new tag.
     * @param tag The tag to create
     * @return A Result containing the created tag with its assigned ID
     */
    suspend fun createTag(tag: Tag): Result<Tag>
    
    /**
     * Update an existing tag.
     * @param tag The updated tag
     * @return A Result containing the updated tag if successful
     */
    suspend fun updateTag(tag: Tag): Result<Tag>
    
    /**
     * Delete a tag.
     * @param tagId The ID of the tag to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteTag(tagId: String): Result<Boolean>
    
    /**
     * Get tags for a specific task.
     * @param taskId The task identifier
     * @return A Flow emitting lists of tags associated with the specified task
     */
    fun getTagsForTask(taskId: String): Flow<List<Tag>>
    
    /**
     * Gets an existing tag by name or creates a new one if it doesn't exist.
     * This is used when assigning tags to tasks.
     * 
     * @param tagName The name of the tag to find or create
     * @return The existing or newly created tag
     */
    suspend fun getOrCreateTag(tagName: String): Tag
}
