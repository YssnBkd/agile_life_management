package com.example.agilelifemanagement.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        SprintEntity::class,
        GoalEntity::class,
        TaskEntity::class,
        TagEntity::class,
        NotificationEntity::class,
        DailyCheckupEntity::class,
        SprintReviewEntity::class,


        TaskSprintCrossRefEntity::class,
        TaskGoalCrossRefEntity::class,
        GoalSprintCrossRefEntity::class,
        TaskDependencyEntity::class,
        TaskTagCrossRefEntity::class,
        GoalTagCrossRefEntity::class,
        SprintTagCrossRefEntity::class,
        SyncStatusEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AgileLifeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sprintDao(): SprintDao
    abstract fun goalDao(): GoalDao
    abstract fun taskDao(): TaskDao
    abstract fun tagDao(): TagDao
    abstract fun notificationDao(): NotificationDao
    abstract fun dailyCheckupDao(): DailyCheckupDao
    abstract fun sprintReviewDao(): SprintReviewDao

    abstract fun taskSprintCrossRefDao(): TaskSprintCrossRefDao
    abstract fun taskGoalCrossRefDao(): TaskGoalCrossRefDao
    abstract fun goalSprintCrossRefDao(): GoalSprintCrossRefDao
    abstract fun taskDependencyDao(): TaskDependencyDao
    abstract fun taskTagCrossRefDao(): TaskTagCrossRefDao
    abstract fun goalTagCrossRefDao(): GoalTagCrossRefDao
    abstract fun sprintTagCrossRefDao(): SprintTagCrossRefDao
    abstract fun syncStatusDao(): SyncStatusDao
}
