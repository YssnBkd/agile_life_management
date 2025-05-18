package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.DayScheduleEntity
import com.example.agilelifemanagement.domain.model.DaySchedule
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between [DaySchedule] domain model and [DayScheduleEntity] data model.
 */
@Singleton
class DayScheduleMapper @Inject constructor(
    private val dayActivityMapper: DayActivityMapper
) {
    
    /**
     * Maps a data layer entity to a domain layer model.
     * Note: Activities are not included in the entity and need to be provided separately.
     * 
     * @param entity The entity to map from
     * @param activities Optional list of activities to include in the domain model
     * @return The mapped domain model
     */
    fun mapToDomain(entity: DayScheduleEntity, activities: List<com.example.agilelifemanagement.domain.model.DayActivity> = emptyList()): DaySchedule {
        return DaySchedule(
            id = entity.id,
            date = entity.date,
            activities = activities,
            startTime = entity.startTime,
            endTime = entity.endTime,
            isOptimized = entity.isOptimized,
            userId = entity.userId,
            notes = entity.notes,
            createdDate = entity.createdDate,
            modifiedDate = entity.modifiedDate
        )
    }
    
    /**
     * Maps a domain layer model to a data layer entity.
     * Note: Activities are not included in the entity and need to be stored separately.
     * 
     * @param domainModel The domain model to map from
     * @return The mapped entity
     */
    fun mapToEntity(domainModel: DaySchedule): DayScheduleEntity {
        return DayScheduleEntity(
            id = domainModel.id,
            date = domainModel.date,
            startTime = domainModel.startTime,
            endTime = domainModel.endTime,
            isOptimized = domainModel.isOptimized,
            userId = domainModel.userId,
            notes = domainModel.notes,
            createdDate = domainModel.createdDate,
            modifiedDate = domainModel.modifiedDate
        )
    }
}
