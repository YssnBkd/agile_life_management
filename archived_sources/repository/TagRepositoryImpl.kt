package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.TagLocalDataSource
import com.example.agilelifemanagement.data.mapper.TagMapper
import com.example.agilelifemanagement.data.remote.source.TagRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.domain.repository.TagRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TagRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagLocalDataSource: TagLocalDataSource,
    private val tagRemoteDataSource: TagRemoteDataSource,
    private val tagMapper: TagMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TagRepository {

    override fun getAllTags(): Flow<List<Tag>> {
        // Offline-first: Return local data immediately, then try to sync with remote
        syncTagsWithRemote()
        return tagLocalDataSource.observeTags()
            .map { tagEntities -> tagEntities.map { tagMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    /**
     * Synchronizes local tag data with remote data source in the background.
     */
    private fun syncTagsWithRemote() {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteTags = tagRemoteDataSource.getAllTags()
                    val entityTags = remoteTags.map { tagMapper.mapToEntity(it) }
                    tagLocalDataSource.insertTags(entityTags)
                    Timber.d("Successfully synced ${remoteTags.size} tags with remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing tags with remote")
                    // Handle error but don't propagate - offline-first approach continues with local data
                }
            }
        }
    }

    override suspend fun getTagById(tagId: String): Result<Tag> = withContext(ioDispatcher) {
        try {
            // First try to get from local database
            var tagEntity = tagLocalDataSource.getTagById(tagId)
            
            // If not found locally or we need the latest data, try remote
            if (tagEntity == null) {
                try {
                    val remoteTag = tagRemoteDataSource.getTagById(tagId)
                    if (remoteTag != null) {
                        // Found on remote, save to local database
                        val entity = tagMapper.mapToEntity(remoteTag)
                        tagLocalDataSource.insertTag(entity)
                        tagEntity = entity
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching tag from remote: $tagId")
                    // Continue with local data (which might be null)
                }
            }
            
            tagEntity?.let {
                Result.success(tagMapper.mapToDomain(it))
            } ?: Result.failure(NoSuchElementException("Tag not found with ID: $tagId"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTag(tag: Tag): Result<Tag> = withContext(ioDispatcher) {
        try {
            // First save to local database for immediate feedback
            val tagEntity = tagMapper.mapToEntity(tag)
            val insertedId = tagLocalDataSource.insertTag(tagEntity)
            val insertedTag = tag.copy(id = insertedId.toString())
            
            // Then try to save to remote in background
            launch {
                try {
                    tagRemoteDataSource.createTag(insertedTag)
                    Timber.d("Successfully synced new tag to remote: ${insertedTag.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing new tag to remote: ${insertedTag.id}")
                    // Tag will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(insertedTag)
        } catch (e: Exception) {
            Timber.e(e, "Error creating tag locally")
            Result.failure(e)
        }
    }

    override suspend fun updateTag(tag: Tag): Result<Tag> = withContext(ioDispatcher) {
        try {
            // Update local database first for immediate feedback
            val tagEntity = tagMapper.mapToEntity(tag)
            tagLocalDataSource.updateTag(tagEntity)
            
            // Then try to update remote in background
            launch {
                try {
                    tagRemoteDataSource.updateTag(tag)
                    Timber.d("Successfully synced updated tag to remote: ${tag.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing updated tag to remote: ${tag.id}")
                    // Tag will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(tag)
        } catch (e: Exception) {
            Timber.e(e, "Error updating tag locally: ${tag.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteTag(tagId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Delete from local database first
            tagLocalDataSource.deleteTag(tagId)
            
            // Then try to delete from remote in background
            launch {
                try {
                    tagRemoteDataSource.deleteTag(tagId)
                    Timber.d("Successfully deleted tag from remote: $tagId")
                } catch (e: Exception) {
                    Timber.e(e, "Error deleting tag from remote: $tagId")
                    // Could add to a deletion sync queue for retry mechanism
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting tag locally: $tagId")
            Result.failure(e)
        }
    }

    override fun getTagsForTask(taskId: String): Flow<List<Tag>> {
        return tagLocalDataSource.observeTagsForTask(taskId)
            .map { tagEntities -> tagEntities.map { tagMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
}
