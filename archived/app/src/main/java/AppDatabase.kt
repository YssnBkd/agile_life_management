//// Deprecated: Use AgileLifeDatabase instead. File intentionally left empty.
////
//// package com.example.agilelifemanagement.data.local
//
//import androidx.room.Database
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//import com.example.agilelifemanagement.data.local.converter.OffsetDateTimeConverter
//import com.example.agilelifemanagement.data.local.dao.*
//import com.example.agilelifemanagement.data.local.entity.*
//
///**
// * Main database for the Agile Life Management app.
// * This will serve as the single source of truth for local data.
// */
//@Database(
//    entities = [
//        UserEntity::class,
//        SprintEntity::class,
//        GoalEntity::class,
//        TaskEntity::class,
//        TagEntity::class,
//        NotificationEntity::class,
//        DailyCheckupEntity::class,
//        SprintReviewEntity::class,
//        ReviewEntryEntity::class,
//        CheckupEntryEntity::class,
//        TaskDescriptionEntity::class,
//        GoalDescriptionEntity::class,
//        SprintDescriptionEntity::class,
//        SyncStatusEntity::class,
//        // CrossRef and junction tables
//        TaskSprintCrossRefEntity::class,
//        TaskGoalCrossRefEntity::class,
//        TaskDependencyEntity::class,
//        TaskTagCrossRefEntity::class,
//        SprintTagCrossRefEntity::class,
//        GoalTagCrossRefEntity::class,
//        GoalSprintCrossRefEntity::class
//    ],
//    version = 1,
//    exportSchema = false
//)
//@TypeConverters(OffsetDateTimeConverter::class)
//
//    // Core DAOs
//    //abstract fun userDao(): UserDao // TODO: Implement UserDao
//    abstract fun sprintDao(): SprintDao
//    abstract fun goalDao(): GoalDao
//    abstract fun taskDao(): TaskDao
//    abstract fun tagDao(): TagDao
//    abstract fun notificationDao(): NotificationDao
//    abstract fun dailyCheckupDao(): DailyCheckupDao
//    abstract fun sprintReviewDao(): SprintReviewDao
//    abstract fun reviewEntryDao(): ReviewEntryDao
//    abstract fun checkupEntryDao(): CheckupEntryDao
//    // Description DAOs
//    abstract fun taskDescriptionDao(): TaskDescriptionDao
//    abstract fun goalDescriptionDao(): GoalDescriptionDao
//    abstract fun sprintDescriptionDao(): SprintDescriptionDao
//    abstract fun syncStatusDao(): SyncStatusDao
//    // CrossRef/junction DAOs
//    abstract fun taskSprintCrossRefDao(): TaskSprintCrossRefDao
//    abstract fun taskGoalCrossRefDao(): TaskGoalCrossRefDao
//    abstract fun taskDependencyDao(): TaskDependencyDao
//    abstract fun taskTagCrossRefDao(): TaskTagCrossRefDao
//    abstract fun sprintTagCrossRefDao(): SprintTagCrossRefDao
//    abstract fun goalTagCrossRefDao(): GoalTagCrossRefDao
//    abstract fun goalSprintCrossRefDao(): GoalSprintCrossRefDao
//
//    companion object {
//        const val DATABASE_NAME = "agile_life_management_db"
//    }
//}
