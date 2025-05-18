package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.TagLocalDataSource
import com.example.agilelifemanagement.data.mapper.TagMapper
import com.example.agilelifemanagement.data.remote.source.TagRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.domain.repository.TagRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TagRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class TagRepositoryImpl @Inject constructor(
    private val localDataSource: TagLocalDataSource,
    private val remoteDataSource: TagRemoteDataSource,
    private val tagMapper: TagMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TagRepository {
    
    // Repository-scoped coroutine scope for background operations
    private val repositoryScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    override fun getAllTags(): Flow<List<Tag>> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncAllTags()
            } catch (e: Exception) {
                Timber.e(e, "Error syncing all tags in background")
            }
        }
        
        return localDataSource.observeTags()
            .map { entities -> entities.map { tagMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private suspend fun syncAllTags() {
        withContext(ioDispatcher) {
            try {
                val remoteTags = remoteDataSource.getAllTags()
                // Map and save to local database
                val entities = remoteTags.map { tagMapper.mapToEntity(it) }
                localDataSource.insertTags(entities)
                Timber.d("Successfully synced ${remoteTags.size} tags from remote")
            } catch (e: Exception) {
                Timber.e(e, "Error syncing tags from remote")
                // Handle error but don't propagate - offline-first approach continues with local data
            }
        }
    }

    override suspend fun getTagById(tagId: String): Result<Tag> = withContext(ioDispatcher) {
        try {
            // Try to get from local database first
            val localTag = localDataSource.getTagById(tagId)
            
            if (localTag != null) {
                Result.success(tagMapper.mapToDomain(localTag))
            } else {
                // If not in local database, try to get from remote
                try {
                    val remoteTag = remoteDataSource.getTagById(tagId)
                    if (remoteTag != null) {
                        // Save to local database for future access
                        localDataSource.insertTag(tagMapper.mapToEntity(remoteTag))
                        Result.success(remoteTag)
                    } else {
                        Result.failure(NoSuchElementException("Tag not found with ID: $tagId"))
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching tag from remote: $tagId")
                    Result.failure(e)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting tag by ID: $tagId")
            Result.failure(e)
        }
    }

    override suspend fun createTag(tag: Tag): Result<Tag> = withContext(ioDispatcher) {
        try {
            // Ensure the tag has an ID
            val tagWithId = if (tag.id.isBlank()) {
                tag.copy(id = UUID.randomUUID().toString())
            } else {
                tag
            }
            
            // Save to local database first for immediate UI feedback
            val tagEntity = tagMapper.mapToEntity(tagWithId)
            localDataSource.insertTag(tagEntity)
            
            // Then try to save to remote in background
            try {
                val remoteTag = remoteDataSource.createTag(tagWithId)
                // If remote has a different ID, update the local record
                if (remoteTag.id != tagWithId.id) {
                    val updatedEntity = tagMapper.mapToEntity(remoteTag)
                    localDataSource.insertTag(updatedEntity)
                    return@withContext Result.success(remoteTag)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving tag to remote: ${tagWithId.id}")
                // Tag will be synced later when connectivity is restored
            }
            
            Result.success(tagWithId)
        } catch (e: Exception) {
            Timber.e(e, "Error creating tag")
            Result.failure(e)
        }
    }

    override suspend fun updateTag(tag: Tag): Result<Tag> = withContext(ioDispatcher) {
        try {
            // Update local database first for immediate UI feedback
            val tagEntity = tagMapper.mapToEntity(tag)
            val updateResult = localDataSource.updateTag(tagEntity)
            
            if (updateResult <= 0) {
                return@withContext Result.failure(NoSuchElementException("Tag not found with ID: ${tag.id}"))
            }
            
            // Then try to update in remote in background
            try {
                remoteDataSource.updateTag(tag)
            } catch (e: Exception) {
                Timber.e(e, "Error updating tag in remote: ${tag.id}")
                // Tag update will be synced later when connectivity is restored
            }
            
            Result.success(tag)
        } catch (e: Exception) {
            Timber.e(e, "Error updating tag: ${tag.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteTag(tagId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Delete from local database first for immediate UI feedback
            val deleteResult = localDataSource.deleteTag(tagId)
            
            if (deleteResult <= 0) {
                return@withContext Result.failure(NoSuchElementException("Tag not found with ID: $tagId"))
            }
            
            // Then try to delete from remote in background
            try {
                remoteDataSource.deleteTag(tagId)
            } catch (e: Exception) {
                Timber.e(e, "Error deleting tag from remote: $tagId")
                // Deletion will be synced later when connectivity is restored
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting tag: $tagId")
            Result.failure(e)
        }
    }

    override fun getTagsForTask(taskId: String): Flow<List<Tag>> {
        return localDataSource.observeTagsForTask(taskId)
            .map { entities -> entities.map { tagMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    /**
     * Gets an existing tag by name or creates a new one if it doesn't exist.
     * This is used when assigning tags to tasks.
     * 
     * @param tagName The name of the tag to find or create
     * @return The existing or newly created tag
     */
    // Implementation of getOrCreateTag from TagRepository interface
    override suspend fun getOrCreateTag(tagName: String): Tag = withContext(ioDispatcher) {
        // Search for tag with the given name
        val existingTags = localDataSource.searchTags(tagName)
            .map { entities -> entities.map { tagMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
            .first() // This is safe in a suspend function
            
        // Check if we found an exact match
        val exactMatch = existingTags.find { tag -> tag.name.equals(tagName, ignoreCase = true) }
        
        if (exactMatch != null) {
            return@withContext exactMatch
        }
        
        // Create a new tag if no match found
        val newTag = Tag(
            id = UUID.randomUUID().toString(),
            name = tagName,
            color = generateRandomColor() // In a real app, this might use a predefined palette
        )
        
        createTag(newTag).getOrThrow()
    }
    
    private fun generateRandomColor(): String {
        val colors = listOf(
            "#4287f5", // Blue
            "#f54242", // Red
            "#42f587", // Green
            "#f5d742", // Yellow
            "#8942f5", // Purple
            "#f542f2", // Pink
            "#f58c42"  // Orange
        )
        return colors.random()
    }
}
