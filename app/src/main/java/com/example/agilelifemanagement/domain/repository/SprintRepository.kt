package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Sprint
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for Sprint operations.
 */
interface SprintRepository {
    fun getSprints(): Flow<List<Sprint>>
    fun getSprintById(id: String): Flow<Sprint?>
    fun getActiveSprintByDate(date: LocalDate): Flow<Sprint?>
    suspend fun insertSprint(sprint: Sprint): String
    suspend fun updateSprint(sprint: Sprint)
    suspend fun deleteSprint(id: String)
}
