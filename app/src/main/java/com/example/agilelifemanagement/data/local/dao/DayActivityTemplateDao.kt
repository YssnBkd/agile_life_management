package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.DayActivityTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for accessing and manipulating day activity template data in the database.
 */
@Dao
interface DayActivityTemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: DayActivityTemplateEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<DayActivityTemplateEntity>)
    
    @Update
    suspend fun updateTemplate(template: DayActivityTemplateEntity): Int
    
    @Query("DELETE FROM day_activity_templates WHERE id = :templateId")
    suspend fun deleteTemplate(templateId: String): Int
    
    @Query("SELECT * FROM day_activity_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: String): DayActivityTemplateEntity?
    
    @Query("SELECT * FROM day_activity_templates")
    fun getAllTemplates(): Flow<List<DayActivityTemplateEntity>>
    
    @Query("SELECT * FROM day_activity_templates WHERE categoryId = :categoryId")
    fun getTemplatesByCategory(categoryId: String): Flow<List<DayActivityTemplateEntity>>
    
    @Query("SELECT * FROM day_activity_templates WHERE title LIKE :query OR description LIKE :query")
    fun searchTemplates(query: String): Flow<List<DayActivityTemplateEntity>>
}
