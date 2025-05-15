package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.GoalLocalDataSource
import com.example.agilelifemanagement.data.mapper.GoalMapper
import com.example.agilelifemanagement.data.remote.source.GoalRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.GoalStatus
import com.example.agilelifemanagement.domain.repository.GoalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [GoalRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalLocalDataSource: GoalLocalDataSource,
    private val goalRemoteDataSource: GoalRemoteDataSource,
    private val goalMapper: GoalMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> {
        // Trigger background sync of all goals
        syncGoalsWithRemote()
        
        // Return local data immediately for responsive UI
        return goalLocalDataSource.observeGoals()
            .map { goalEntities -> goalEntities.map { goalMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    /**
     * Synchronizes local goal data with remote data source in the background.
     */
    private fun syncGoalsWithRemote() {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteGoals = goalRemoteDataSource.getAllGoals()
                    val entityGoals = remoteGoals.map { goalMapper.mapToEntity(it) }
                    goalLocalDataSource.insertGoals(entityGoals)
                    Timber.d("Successfully synced ${remoteGoals.size} goals with remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing goals with remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }

    override fun getGoalsByStatus(status: GoalStatus): Flow<List<Goal>> {
        // Try to sync goals with this status from remote
        syncGoalsByStatus(status)
        
        // Return local data immediately for responsive UI
        return goalLocalDataSource.observeGoalsByStatus(status)
            .map { goalEntities -> goalEntities.map { goalMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncGoalsByStatus(status: GoalStatus) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteGoals = goalRemoteDataSource.getGoalsByStatus(status)
                    val entityGoals = remoteGoals.map { goalMapper.mapToEntity(it) }
                    goalLocalDataSource.insertGoals(entityGoals)
                    Timber.d("Successfully synced ${remoteGoals.size} goals with status $status from remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing goals with status $status from remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }

    override fun getUpcomingGoals(date: LocalDate): Flow<List<Goal>> {
        // Try to sync upcoming goals from remote
        syncUpcomingGoals(date)
        
        // Return local data immediately for responsive UI
        return goalLocalDataSource.observeUpcomingGoals(date)
            .map { goalEntities -> goalEntities.map { goalMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncUpcomingGoals(date: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteGoals = goalRemoteDataSource.getUpcomingGoals(date)
                    val entityGoals = remoteGoals.map { goalMapper.mapToEntity(it) }
                    goalLocalDataSource.insertGoals(entityGoals)
                    Timber.d("Successfully synced ${remoteGoals.size} upcoming goals for date $date from remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing upcoming goals for date $date from remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }

    override suspend fun getGoalById(goalId: String): Result<Goal> = withContext(ioDispatcher) {
        try {
            // First try to get from local database
            var goalEntity = goalLocalDataSource.getGoalById(goalId)
            
            // If not found locally or we need the latest data, try remote
            if (goalEntity == null) {
                try {
                    val remoteGoal = goalRemoteDataSource.getGoalById(goalId)
                    if (remoteGoal != null) {
                        // Found on remote, save to local database
                        val entity = goalMapper.mapToEntity(remoteGoal)
                        goalLocalDataSource.insertGoal(entity)
                        goalEntity = entity
                        Timber.d("Successfully fetched goal from remote: $goalId")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching goal from remote: $goalId")
                    // Continue with local data (which might be null)
                }
            }
            
            goalEntity?.let {
                Result.success(goalMapper.mapToDomain(it))
            } ?: Result.failure(NoSuchElementException("Goal not found with ID: $goalId"))
        } catch (e: Exception) {
            Timber.e(e, "Error getting goal: $goalId")
            Result.failure(e)
        }
    }

    override suspend fun createGoal(goal: Goal): Result<Goal> = withContext(ioDispatcher) {
        try {
            // First save to local database for immediate update to UI
            val goalEntity = goalMapper.mapToEntity(goal)
            val insertedId = goalLocalDataSource.insertGoal(goalEntity)
            val insertedGoal = goal.copy(id = insertedId)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteGoal = goalRemoteDataSource.createGoal(insertedGoal)
                    // Update local cache with remote response (might contain additional server-side data)
                    val updatedEntity = goalMapper.mapToEntity(remoteGoal)
                    goalLocalDataSource.updateGoal(updatedEntity)
                    Timber.d("Successfully synchronized goal creation with remote: $insertedId")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync goal creation with remote: $insertedId")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(insertedGoal)
        } catch (e: Exception) {
            Timber.e(e, "Error creating goal: ${goal.title}")
            Result.failure(e)
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Goal> = withContext(ioDispatcher) {
        try {
            // First update local database for immediate update to UI
            val goalEntity = goalMapper.mapToEntity(goal)
            goalLocalDataSource.updateGoal(goalEntity)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteGoal = goalRemoteDataSource.updateGoal(goal)
                    // Update local cache with remote response (might contain additional server-side data)
                    val updatedEntity = goalMapper.mapToEntity(remoteGoal)
                    goalLocalDataSource.updateGoal(updatedEntity)
                    Timber.d("Successfully synchronized goal update with remote: ${goal.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync goal update with remote: ${goal.id}")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(goal)
        } catch (e: Exception) {
            Timber.e(e, "Error updating goal: ${goal.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteGoal(goalId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // First delete from local database for immediate update to UI
            goalLocalDataSource.deleteGoal(goalId)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteResult = goalRemoteDataSource.deleteGoal(goalId)
                    if (remoteResult) {
                        Timber.d("Successfully synchronized goal deletion with remote: $goalId")
                    } else {
                        Timber.w("Remote deletion returned false for goal: $goalId")
                        // This might indicate that the goal doesn't exist remotely
                        // or couldn't be deleted due to constraints
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync goal deletion with remote: $goalId")
                    // Continue with local deletion - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting goal: $goalId")
            Result.failure(e)
        }
    }

    override suspend fun updateGoalStatus(goalId: String, status: GoalStatus): Result<Goal> = withContext(ioDispatcher) {
        try {
            // First update local database for immediate update to UI
            val goalEntity = goalLocalDataSource.getGoalById(goalId)
                ?: return@withContext Result.failure(NoSuchElementException("Goal not found with ID: $goalId"))

            val updatedEntity = goalEntity.copy(status = status)
            goalLocalDataSource.updateGoal(updatedEntity)
            val updatedGoal = goalMapper.mapToDomain(updatedEntity)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteGoal = goalRemoteDataSource.updateGoalStatus(goalId, status)
                    // Update local cache with remote response (might contain additional server-side data)
                    val remoteMappedEntity = goalMapper.mapToEntity(remoteGoal)
                    goalLocalDataSource.updateGoal(remoteMappedEntity)
                    Timber.d("Successfully synchronized goal status update with remote: $goalId")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync goal status update with remote: $goalId")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(updatedGoal)
        } catch (e: Exception) {
            Timber.e(e, "Error updating goal status: $goalId to $status")
            Result.failure(e)
        }
    }
}
