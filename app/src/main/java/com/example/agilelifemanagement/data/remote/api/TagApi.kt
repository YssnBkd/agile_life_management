package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.TagDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API client for tag-related operations using Ktor client.
 * Communicates with the Supabase backend for CRUD operations on tags.
 */
@Singleton
class TagApi @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "tags"
    
    /**
     * Fetches all tags from the API.
     * @return List of TagDto objects
     */
    suspend fun getAllTags(): List<TagDto> {
        return httpClient.get(baseUrl).body()
    }
    
    /**
     * Fetches a specific tag by ID from the API.
     * @param tagId The tag identifier
     * @return TagDto object
     */
    suspend fun getTagById(tagId: String): TagDto {
        return httpClient.get("$baseUrl/$tagId").body()
    }
    
    /**
     * Creates a new tag in the API.
     * @param tag The TagDto to create
     * @return The created TagDto with assigned ID
     */
    suspend fun createTag(tag: TagDto): TagDto {
        return httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(tag)
        }.body()
    }
    
    /**
     * Updates an existing tag in the API.
     * @param tagId The tag identifier
     * @param tag The updated TagDto
     * @return The updated TagDto
     */
    suspend fun updateTag(tagId: String, tag: TagDto): TagDto {
        return httpClient.put("$baseUrl/$tagId") {
            contentType(ContentType.Application.Json)
            setBody(tag)
        }.body()
    }
    
    /**
     * Deletes a tag from the API.
     * @param tagId The tag identifier to delete
     */
    suspend fun deleteTag(tagId: String) {
        httpClient.delete("$baseUrl/$tagId")
    }
}
