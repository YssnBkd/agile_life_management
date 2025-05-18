package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.TagDto

/**
 * Service interface for tag-related API operations.
 * Defines the contract for communicating with the remote API for tag data.
 */
interface TagApiService {
    
    /**
     * Fetch all tags from the API.
     * @return List of tag DTOs
     */
    suspend fun getAllTags(): List<TagDto>
    
    /**
     * Fetch a specific tag by ID from the API.
     * @param tagId The tag identifier
     * @return The tag DTO if found, or null
     */
    suspend fun getTagById(tagId: String): TagDto?
    
    /**
     * Create a new tag in the API.
     * @param tag The tag DTO to create
     * @return The created tag DTO with assigned ID
     */
    suspend fun createTag(tag: TagDto): TagDto
    
    /**
     * Update an existing tag in the API.
     * @param tag The tag DTO to update
     * @return The updated tag DTO
     */
    suspend fun updateTag(tag: TagDto): TagDto
    
    /**
     * Delete a tag from the API.
     * @param tagId The ID of the tag to delete
     * @return True if the tag was successfully deleted
     */
    suspend fun deleteTag(tagId: String): Boolean
}
