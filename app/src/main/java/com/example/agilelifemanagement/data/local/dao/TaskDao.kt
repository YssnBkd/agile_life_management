package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getAllTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    // Additional queries as needed (by priority, status, due date, etc.)
    @Query("SELECT * FROM tasks WHERE userId = :userId AND priority = :priority")
    fun getTasksByPriority(userId: String, priority: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND status = :status")
    fun getTasksByStatus(userId: String, status: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND dueDate = :dueDate")
    fun getTasksByDueDate(userId: String, dueDate: Long): Flow<List<TaskEntity>>

    @Query("DELETE FROM tasks WHERE id = :id AND userId = :userId")
    suspend fun deleteTaskById(userId: String, id: String)

    @Query("SELECT * FROM tasks WHERE id IN (:ids)")
    fun getTasksByIds(ids: List<String>): Flow<List<TaskEntity>>
}
