package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.domain.model.*
import com.example.agilelifemanagement.domain.repository.DayRepository
import com.example.agilelifemanagement.domain.repository.WellnessRepository
import com.example.agilelifemanagement.domain.repository.temporary.*
import com.example.agilelifemanagement.domain.usecase.day.activity.AddDayActivityUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.GetDayActivitiesUseCase
import com.example.agilelifemanagement.domain.usecase.day.template.*
import com.example.agilelifemanagement.domain.usecase.notification.*
import com.example.agilelifemanagement.domain.usecase.wellness.GetDailyCheckupUseCase
import com.example.agilelifemanagement.domain.usecase.wellness.SaveDailyCheckupUseCase
import com.example.agilelifemanagement.domain.usecase.wellness.GetWellnessAnalyticsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Singleton

/**
 * Temporary module to provide stub implementations of use cases
 * until the data layer is rebuilt.
 *
 * Created after the May 15, 2025 architectural change when
 * the data layer was archived for rebuilding.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    // Day template use cases
    @Provides
    @Singleton
    fun provideGetDayTemplatesUseCase(): GetDayTemplatesUseCase {
        val tempRepository = TempTemplateRepositoryImpl()
        return GetDayTemplatesUseCase(tempRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetTemplateByIdUseCase(): GetTemplateByIdUseCase {
        val tempRepository = TempTemplateRepositoryImpl()
        return GetTemplateByIdUseCase(tempRepository)
    }
    
    @Provides
    @Singleton
    fun provideCreateDayTemplateUseCase(): CreateDayTemplateUseCase {
        val tempRepository = TempTemplateRepositoryImpl()
        return CreateDayTemplateUseCase(tempRepository)
    }
    
    @Provides
    @Singleton
    fun provideUpdateDayTemplateUseCase(): UpdateDayTemplateUseCase {
        val tempRepository = TempTemplateRepositoryImpl()
        return UpdateDayTemplateUseCase(tempRepository)
    }
    
    @Provides
    @Singleton
    fun provideDeleteDayTemplateUseCase(): DeleteDayTemplateUseCase {
        val tempRepository = TempTemplateRepositoryImpl()
        return DeleteDayTemplateUseCase(tempRepository)
    }
    
    // Day activity use cases
    @Provides
    @Singleton
    fun provideGetDayActivitiesUseCase(dayRepository: DayRepository): GetDayActivitiesUseCase {
        return GetDayActivitiesUseCase(dayRepository)
    }
    
    @Provides
    @Singleton
    fun provideAddDayActivityUseCase(dayRepository: DayRepository): AddDayActivityUseCase {
        return AddDayActivityUseCase(dayRepository)
    }
    
    // Notification use cases
    @Provides
    @Singleton
    fun provideGetNotificationsUseCase(): GetNotificationsUseCase {
        val tempRepository = TempNotificationRepositoryImpl()
        return GetNotificationsUseCase(tempRepository)
    }
    
    @Provides
    @Singleton
    fun provideMarkNotificationAsReadUseCase(): MarkNotificationAsReadUseCase {
        val tempRepository = TempNotificationRepositoryImpl()
        return MarkNotificationAsReadUseCase(tempRepository)
    }
    
    // Wellness use cases
    @Provides
    @Singleton
    fun provideGetDailyCheckupUseCase(wellnessRepository: WellnessRepository): GetDailyCheckupUseCase {
        return GetDailyCheckupUseCase(wellnessRepository)
    }
    
    @Provides
    @Singleton
    fun provideSaveDailyCheckupUseCase(wellnessRepository: WellnessRepository): SaveDailyCheckupUseCase {
        return SaveDailyCheckupUseCase(wellnessRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetWellnessAnalyticsUseCase(wellnessRepository: WellnessRepository): GetWellnessAnalyticsUseCase {
        return GetWellnessAnalyticsUseCase(wellnessRepository)
    }
}
