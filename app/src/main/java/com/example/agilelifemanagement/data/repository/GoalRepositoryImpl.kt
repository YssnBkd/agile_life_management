package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.repository.GoalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val localDataSource: com.example.agilelifemanagement.data.local.source.GoalLocalDataSource,
    private val remoteDataSource: com.example.agilelifemanagement.data.remote.source.GoalRemoteDataSource,
    private val goalMapper: com.example.agilelifemanagement.data.mapper.GoalMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> =
        localDataSource.observeGoals().map { entities ->
            entities.map { goalMapper.mapToDomain(it) }
        }

    override suspend fun getGoalById(goalId: String): Result<Goal> = withContext(ioDispatcher) {
        try {
            val entity = localDataSource.getGoalById(goalId)
            if (entity != null) {
                Result.success(goalMapper.mapToDomain(entity))
            } else {
                Result.failure(NoSuchElementException("Goal not found with ID: $goalId"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting goal by ID")
            Result.failure(e)
        }
    }

    override suspend fun createGoal(goal: Goal): Result<Goal> = withContext(ioDispatcher) {
        try {
            val entity = goalMapper.mapToEntity(goal)
            localDataSource.insertGoal(entity)
            // Fire-and-forget remote sync
            kotlinx.coroutines.coroutineScope {
                launch {
                    try {
                        remoteDataSource.createGoal(goal)
                    } catch (e: Exception) {
                        Timber.e(e, "Remote sync failed for createGoal")
                    }
                }
            }
            Result.success(goal.copy(id = entity.id))
        } catch (e: Exception) {
            Timber.e(e, "Error creating goal")
            Result.failure(e)
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Goal> = withContext(ioDispatcher) {
        try {
            val entity = goalMapper.mapToEntity(goal)
            localDataSource.updateGoal(entity)
            // Fire-and-forget remote sync
            kotlinx.coroutines.coroutineScope {
                launch {
                    try {
                        remoteDataSource.updateGoal(goal)
                    } catch (e: Exception) {
                        Timber.e(e, "Remote sync failed for updateGoal")
                    }
                }
            }
            Result.success(goal)
        } catch (e: Exception) {
            Timber.e(e, "Error updating goal")
            Result.failure(e)
        }
    }

    override suspend fun deleteGoal(goalId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            localDataSource.deleteGoal(goalId)
            // Fire-and-forget remote sync
            kotlinx.coroutines.coroutineScope {
                launch {
                    try {
                        remoteDataSource.deleteGoal(goalId)
                    } catch (e: Exception) {
                        Timber.e(e, "Remote sync failed for deleteGoal")
                    }
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting goal")
            Result.failure(e)
        }
    }

    // Optionally: Add a method to refresh local from remote
    suspend fun refreshGoalsFromRemote() = withContext(ioDispatcher) {
        try {
            val remoteGoals = remoteDataSource.getAllGoals()
            val entities = remoteGoals.map { goalMapper.mapToEntity(it) }
            localDataSource.insertGoals(entities)
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing goals from remote")
        }
    }
}
