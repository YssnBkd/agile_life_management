package com.example.agilelifemanagement.domain.repository


import com.example.agilelifemanagement.domain.model.DailyCheckup
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for DailyCheckup operations.
 */
interface DailyCheckupRepository {
    fun getCheckups(): Flow<List<DailyCheckup>>
    fun getCheckupById(id: String): Flow<DailyCheckup?>
    fun getCheckupByDate(date: LocalDate): Flow<DailyCheckup?>
    fun getCheckupsBySprintId(sprintId: String): Flow<List<DailyCheckup>>
    suspend fun insertCheckup(checkup: DailyCheckup): String
    suspend fun updateCheckup(checkup: DailyCheckup)
    suspend fun deleteCheckup(id: String)

}
