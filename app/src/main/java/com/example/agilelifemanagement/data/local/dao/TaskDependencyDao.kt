package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TaskDependencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDependencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskDependency: TaskDependencyEntity)

    @Delete
    suspend fun delete(taskDependency: TaskDependencyEntity)

    @Query("SELECT * FROM task_dependencies WHERE taskId = :taskId")
    fun getDependenciesForTask(taskId: String): Flow<List<TaskDependencyEntity>>

    @Query("DELETE FROM task_dependencies WHERE taskId = :taskId")
    suspend fun deleteDependenciesForTask(taskId: String)
}
