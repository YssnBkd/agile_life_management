package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.DayActivityEntity
import com.example.agilelifemanagement.domain.model.DayActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between [DayActivity] domain model and [DayActivityEntity] data model.
 */
@Singleton
class DayActivityMapper @Inject constructor() {
    
    /**
     * Maps a data layer entity to a domain layer model.
     * @param entity The entity to map from
     * @return The mapped domain model
     */
    fun mapToDomain(entity: DayActivityEntity): DayActivity {
        return DayActivity(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            date = entity.date,
            startTime = entity.startTime,
            endTime = entity.endTime,
            isCompleted = entity.isCompleted,
            categoryId = entity.categoryId,
            priority = entity.priority
        )
    }
    
    /**
     * Maps a domain layer model to a data layer entity.
     * @param domainModel The domain model to map from
     * @return The mapped entity
     */
    fun mapToEntity(domainModel: DayActivity): DayActivityEntity {
        return DayActivityEntity(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            date = domainModel.date,
            startTime = domainModel.startTime,
            endTime = domainModel.endTime,
            isCompleted = domainModel.isCompleted,
            categoryId = domainModel.categoryId,
            priority = domainModel.priority
        )
    }
}
