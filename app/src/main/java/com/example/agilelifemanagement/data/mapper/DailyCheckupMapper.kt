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
            moodRating = entity.moodRating,
            energyLevel = entity.energyLevel,
            sleepQuality = entity.sleepQuality,
            stressLevel = entity.stressLevel,
            notes = entity.notes
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
            moodRating = domainModel.moodRating,
            energyLevel = domainModel.energyLevel,
            sleepQuality = domainModel.sleepQuality,
            stressLevel = domainModel.stressLevel,
            notes = domainModel.notes
        )
    }
}
