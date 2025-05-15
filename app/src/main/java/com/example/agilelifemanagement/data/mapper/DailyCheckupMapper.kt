package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import com.example.agilelifemanagement.domain.model.DailyCheckup
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between [DailyCheckup] domain model and [DailyCheckupEntity] data model.
 */
@Singleton
class DailyCheckupMapper @Inject constructor() {
    
    /**
     * Maps a data layer entity to a domain layer model.
     * @param entity The entity to map from
     * @return The mapped domain model
     */
    fun mapToDomain(entity: DailyCheckupEntity): DailyCheckup {
        return DailyCheckup(
            id = entity.id,
            date = entity.date,
            mood = entity.mood,
            energyLevel = entity.energyLevel,
            sleepQuality = entity.sleepQuality,
            sleepHours = entity.sleepHours,
            stressLevel = entity.stressLevel,
            productivityRating = entity.productivityRating,
            notes = entity.notes,
            focusRating = entity.focusRating,
            physicalActivityMinutes = entity.physicalActivityMinutes
        )
    }
    
    /**
     * Maps a domain layer model to a data layer entity.
     * @param domainModel The domain model to map from
     * @return The mapped entity
     */
    fun mapToEntity(domainModel: DailyCheckup): DailyCheckupEntity {
        return DailyCheckupEntity(
            id = domainModel.id,
            date = domainModel.date,
            mood = domainModel.mood,
            energyLevel = domainModel.energyLevel,
            sleepQuality = domainModel.sleepQuality,
            sleepHours = domainModel.sleepHours,
            stressLevel = domainModel.stressLevel,
            productivityRating = domainModel.productivityRating,
            notes = domainModel.notes,
            focusRating = domainModel.focusRating,
            physicalActivityMinutes = domainModel.physicalActivityMinutes
        )
    }
}
