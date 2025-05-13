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
    suspend fun insertSprint(sprint: Sprint): com.example.agilelifemanagement.domain.model.Result<String>
    suspend fun updateSprint(sprint: Sprint): com.example.agilelifemanagement.domain.model.Result<Unit>
    suspend fun deleteSprint(id: String): com.example.agilelifemanagement.domain.model.Result<Unit>
}
