package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.data.local.entity.TaskTagCrossRef
import com.example.agilelifemanagement.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Room DAO for accessing and manipulating task data in the database.
 */
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    
    @Update
    suspend fun updateTask(task: TaskEntity): Int
    
    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Int
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String): Int
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?
    
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE sprintId = :sprintId")
    fun getTasksBySprintId(sprintId: String): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE dueDate = :date OR (dueDate IS NULL AND createdDate = :date)")
    fun getTasksByDate(date: LocalDate): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate")
    fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskEntity>>
    
    // Task-Tag relationship operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskTagCrossRef(crossRef: TaskTagCrossRef)
    
    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId")
    suspend fun deleteTaskTagCrossRefs(taskId: String)
    
    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun deleteTaskTagCrossRef(taskId: String, tagId: String)
    
    @Transaction
    @Query("SELECT * FROM tasks WHERE id IN (SELECT taskId FROM task_tag_cross_ref WHERE tagId IN (:tagIds))")
    fun getTasksByTags(tagIds: List<String>): Flow<List<TaskEntity>>
    
    @Transaction
    suspend fun insertTaskWithTags(task: TaskEntity, tagIds: List<String>) {
        insertTask(task)
        tagIds.forEach { tagId ->
            insertTaskTagCrossRef(TaskTagCrossRef(task.id, tagId))
        }
    }
    
    @Transaction
    suspend fun updateTaskWithTags(task: TaskEntity, tagIds: List<String>) {
        updateTask(task)
        deleteTaskTagCrossRefs(task.id)
        tagIds.forEach { tagId ->
            insertTaskTagCrossRef(TaskTagCrossRef(task.id, tagId))
        }
    }
}
