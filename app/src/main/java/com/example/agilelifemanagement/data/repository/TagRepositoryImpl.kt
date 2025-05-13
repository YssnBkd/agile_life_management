package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.dao.TagDao
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.mappers.toTag
import com.example.agilelifemanagement.data.mappers.toTagEntity
import com.example.agilelifemanagement.data.remote.api.TagApiService
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import com.example.agilelifemanagement.util.NetworkMonitor

private const val TAG = "TagRepositoryImpl"

/**
 * Implementation of [TagRepository] that follows the offline-first strategy.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val tagApiService: TagApiService,
    private val syncManager: SyncManager,
    private val supabaseManager: SupabaseManager,
    private val networkMonitor: NetworkMonitor
) : TagRepository {

    override fun getTags(): Flow<List<Tag>> {
        return supabaseManager.getCurrentUserId().flatMapLatest { userId: String? ->
            if (userId == null) emptyFlow() else tagDao.getAllTags(userId).map { list -> list.map { it.toTag() } }
        }
    }

    override fun getTagById(id: String): Flow<Tag?> {
        // Room's getTagById is suspend, not Flow, so wrap in flow {}
        return kotlinx.coroutines.flow.flow {
            emit(tagDao.getTagById(id)?.toTag())
        }
    }

    override fun getTagsByTaskId(taskId: String): Flow<List<Tag>> =
        tagDao.getTagsByTaskId(taskId).map { list -> list.map { it.toTag() } }

    override fun getTagsByGoalId(goalId: String): Flow<List<Tag>> = emptyFlow()
    override fun getTagsBySprintId(sprintId: String): Flow<List<Tag>> = emptyFlow()

    override suspend fun insertTag(tag: Tag): String {
        val userId = supabaseManager.getCurrentUserId().first() ?: error("User ID must not be null")
        val id = tag.id ?: UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()
        val entity = tag.toTagEntity(userId, createdAt).copy(id = id)
        tagDao.insert(entity)
        syncManager.scheduleSyncOperation(id, "tag", PendingOperation.CREATE)
        return id
    }

    override suspend fun updateTag(tag: Tag) {
        val userId = supabaseManager.getCurrentUserId().first() ?: error("User ID must not be null")
        // Fetch existing entity to preserve createdAt
        val existing = tagDao.getTagById(tag.id ?: "") ?: error("Tag not found")
        val entity = tag.toTagEntity(userId, existing.createdAt)
        tagDao.update(entity)
        syncManager.scheduleSyncOperation(entity.id, "tag", PendingOperation.UPDATE)
    }

    override suspend fun deleteTag(id: String) {
        val entity = tagDao.getTagById(id)
        if (entity != null) {
            tagDao.delete(entity)
            syncManager.scheduleSyncOperation(id, "tag", PendingOperation.DELETE)
        }
    }
}
