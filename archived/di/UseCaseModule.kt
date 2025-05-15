package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.domain.repository.*
import com.example.agilelifemanagement.domain.usecase.dailycheckup.*
import com.example.agilelifemanagement.domain.usecase.day.GetDayScheduleUseCase
import com.example.agilelifemanagement.domain.usecase.day.GetWeekActivitiesUseCase
import com.example.agilelifemanagement.domain.usecase.day.UpdateDayScheduleUseCase
import com.example.agilelifemanagement.domain.usecase.day.activity.*
import com.example.agilelifemanagement.domain.usecase.day.template.*
import com.example.agilelifemanagement.domain.usecase.goal.*
import com.example.agilelifemanagement.domain.usecase.sprint.*
import com.example.agilelifemanagement.domain.usecase.sprintreview.*
import com.example.agilelifemanagement.domain.usecase.task.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Task Use Cases
    @Provides
    @Singleton
    fun provideGetTasksUseCase(taskRepository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetTaskByIdUseCase(taskRepository: TaskRepository): GetTaskByIdUseCase {
        return GetTaskByIdUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideCreateTaskUseCase(
        taskRepository: TaskRepository,
        sprintRepository: SprintRepository,
        goalRepository: GoalRepository
    ): CreateTaskUseCase {
        return CreateTaskUseCase(taskRepository, sprintRepository, goalRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateTaskUseCase(
        taskRepository: TaskRepository,
        sprintRepository: SprintRepository,
        goalRepository: GoalRepository
    ): UpdateTaskUseCase {
        return UpdateTaskUseCase(taskRepository, sprintRepository, goalRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteTaskUseCase(taskRepository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideAssignTaskToSprintUseCase(
        taskRepository: TaskRepository,
        sprintRepository: SprintRepository
    ): AssignTaskToSprintUseCase {
        return AssignTaskToSprintUseCase(taskRepository, sprintRepository)
    }

    @Provides
    @Singleton
    fun provideAssignTaskToGoalUseCase(
        taskRepository: TaskRepository,
        goalRepository: GoalRepository
    ): AssignTaskToGoalUseCase {
        return AssignTaskToGoalUseCase(taskRepository, goalRepository)
    }

    @Provides
    @Singleton
    fun provideManageTaskDependenciesUseCase(taskRepository: TaskRepository): ManageTaskDependenciesUseCase {
        return ManageTaskDependenciesUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideManageTaskTagsUseCase(
        taskRepository: TaskRepository,
        tagRepository: TagRepository
    ): ManageTaskTagsUseCase {
        return ManageTaskTagsUseCase(taskRepository, tagRepository)
    }

    // Goal Use Cases
    @Provides
    @Singleton
    fun provideGetGoalsUseCase(goalRepository: GoalRepository): GetGoalsUseCase {
        return GetGoalsUseCase(goalRepository)
    }

    @Provides
    @Singleton
    fun provideGetGoalByIdUseCase(goalRepository: GoalRepository): GetGoalByIdUseCase {
        return GetGoalByIdUseCase(goalRepository)
    }

    @Provides
    @Singleton
    fun provideCreateGoalUseCase(goalRepository: GoalRepository): CreateGoalUseCase {
        return CreateGoalUseCase(goalRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateGoalUseCase(goalRepository: GoalRepository): UpdateGoalUseCase {
        return UpdateGoalUseCase(goalRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteGoalUseCase(goalRepository: GoalRepository): DeleteGoalUseCase {
        return DeleteGoalUseCase(goalRepository)
    }

    @Provides
    @Singleton
    fun provideAssignGoalToSprintUseCase(
        goalRepository: GoalRepository,
        sprintRepository: SprintRepository
    ): AssignGoalToSprintUseCase {
        return AssignGoalToSprintUseCase(goalRepository, sprintRepository)
    }

    @Provides
    @Singleton
    fun provideGetTasksForGoalUseCase(
        taskRepository: TaskRepository,
        goalRepository: GoalRepository
    ): GetTasksForGoalUseCase {
        return GetTasksForGoalUseCase(taskRepository, goalRepository)
    }

    @Provides
    @Singleton
    fun provideCalculateGoalProgressUseCase(
        goalRepository: GoalRepository,
        taskRepository: TaskRepository
    ): CalculateGoalProgressUseCase {
        return CalculateGoalProgressUseCase(goalRepository, taskRepository)
    }

    // Sprint Use Cases
    @Provides
    @Singleton
    fun provideGetSprintsUseCase(sprintRepository: SprintRepository): GetSprintsUseCase {
        return GetSprintsUseCase(sprintRepository)
    }

    @Provides
    @Singleton
    fun provideGetSprintByIdUseCase(sprintRepository: SprintRepository): GetSprintByIdUseCase {
        return GetSprintByIdUseCase(sprintRepository)
    }

    @Provides
    @Singleton
    fun provideGetActiveSprintUseCase(sprintRepository: SprintRepository): GetActiveSprintUseCase {
        return GetActiveSprintUseCase(sprintRepository)
    }

    @Provides
    @Singleton
    fun provideCreateSprintUseCase(sprintRepository: SprintRepository): CreateSprintUseCase {
        return CreateSprintUseCase(sprintRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateSprintUseCase(sprintRepository: SprintRepository): UpdateSprintUseCase {
        return UpdateSprintUseCase(sprintRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteSprintUseCase(sprintRepository: SprintRepository): DeleteSprintUseCase {
        return DeleteSprintUseCase(sprintRepository)
    }

    // Daily Checkup Use Cases
    @Provides
    @Singleton
    fun provideGetDailyCheckupUseCase(dailyCheckupRepository: DailyCheckupRepository): GetDailyCheckupUseCase {
        return GetDailyCheckupUseCase(dailyCheckupRepository)
    }

    @Provides
    @Singleton
    fun provideCreateDailyCheckupUseCase(
        dailyCheckupRepository: DailyCheckupRepository,
        sprintRepository: SprintRepository
    ): CreateDailyCheckupUseCase {
        return CreateDailyCheckupUseCase(dailyCheckupRepository, sprintRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateDailyCheckupUseCase(
        dailyCheckupRepository: DailyCheckupRepository,
        sprintRepository: SprintRepository
    ): UpdateDailyCheckupUseCase {
        return UpdateDailyCheckupUseCase(dailyCheckupRepository, sprintRepository)
    }


    // Sprint Review Use Cases
    @Provides
    @Singleton
    fun provideGetSprintReviewUseCase(sprintReviewRepository: SprintReviewRepository): GetSprintReviewUseCase {
        return GetSprintReviewUseCase(sprintReviewRepository)
    }

    @Provides
    @Singleton
    fun provideCreateSprintReviewUseCase(
        sprintReviewRepository: SprintReviewRepository,
        sprintRepository: SprintRepository
    ): CreateSprintReviewUseCase {
        return CreateSprintReviewUseCase(sprintReviewRepository, sprintRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateSprintReviewUseCase(sprintReviewRepository: SprintReviewRepository): UpdateSprintReviewUseCase {
        return UpdateSprintReviewUseCase(sprintReviewRepository)
    }

    // Day/Activity Use Cases
    @Provides
    @Singleton
    fun provideGetDayActivitiesUseCase(dayRepository: DayRepository): GetDayActivitiesUseCase {
        return GetDayActivitiesUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideGetWeekActivitiesUseCase(dayRepository: DayRepository): GetWeekActivitiesUseCase {
        return GetWeekActivitiesUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideAddDayActivityUseCase(dayRepository: DayRepository): AddDayActivityUseCase {
        return AddDayActivityUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateDayActivityUseCase(dayRepository: DayRepository): UpdateDayActivityUseCase {
        return UpdateDayActivityUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteDayActivityUseCase(dayRepository: DayRepository): DeleteDayActivityUseCase {
        return DeleteDayActivityUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideToggleActivityCompletionUseCase(dayRepository: DayRepository): ToggleActivityCompletionUseCase {
        return ToggleActivityCompletionUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideGetDayScheduleUseCase(dayRepository: DayRepository): GetDayScheduleUseCase {
        return GetDayScheduleUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateDayScheduleUseCase(dayRepository: DayRepository): UpdateDayScheduleUseCase {
        return UpdateDayScheduleUseCase(dayRepository)
    }

    // Day Template Use Cases
    @Provides
    @Singleton
    fun provideGetDayTemplatesUseCase(dayRepository: DayRepository): GetDayTemplatesUseCase {
        return GetDayTemplatesUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideGetTemplateByIdUseCase(dayRepository: DayRepository): GetTemplateByIdUseCase {
        return GetTemplateByIdUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideCreateDayTemplateUseCase(dayRepository: DayRepository): CreateDayTemplateUseCase {
        return CreateDayTemplateUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateDayTemplateUseCase(dayRepository: DayRepository): UpdateDayTemplateUseCase {
        return UpdateDayTemplateUseCase(dayRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteDayTemplateUseCase(dayRepository: DayRepository): DeleteDayTemplateUseCase {
        return DeleteDayTemplateUseCase(dayRepository)
    }
}
