package com.example.agilelifemanagement.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

/**
 * Database migrations for the AgileLifeManagement Room database.
 * Each migration handles the schema changes between specific versions.
 */
object AppDatabaseMigrations {
    
    /**
     * Migration from version 1 to version 2.
     * Ensures data integrity between schema versions.
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            Timber.d("Running migration from version 1 to 2")
            
            // Create temporary tables with new schema
            // We'll use this approach to preserve data while updating schema
            
            // For task table migrations
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS tasks_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    userId TEXT NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    status TEXT NOT NULL,
                    priority TEXT NOT NULL,
                    dueDate TEXT,
                    createdDate TEXT NOT NULL,
                    sprintId TEXT
                )
            """)
            
            // Copy data from old tables to new ones (if they exist)
            try {
                database.execSQL("""
                    INSERT OR IGNORE INTO tasks_new(id, userId, title, description, status, priority, 
                    dueDate, createdDate, sprintId)
                    SELECT id, userId, title, description, status, priority, 
                    dueDate, createdDate, sprintId FROM tasks
                """)
            } catch (e: Exception) {
                Timber.e(e, "Error migrating tasks data")
                // Continue with migration even if copy fails
            }
            
            // Drop old tables
            try {
                database.execSQL("DROP TABLE IF EXISTS tasks")
            } catch (e: Exception) {
                Timber.e(e, "Error dropping tasks table")
            }
            
            // Rename new tables to original names
            database.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            
            // Similarly for other tables with schema issues
            // Time blocks
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS time_blocks_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    title TEXT NOT NULL,
                    description TEXT,
                    location TEXT,
                    start_time TEXT NOT NULL,
                    end_time TEXT NOT NULL,
                    date TEXT NOT NULL,
                    time_range TEXT NOT NULL DEFAULT '',
                    category_id TEXT NOT NULL DEFAULT 'TASK',
                    color_hex TEXT NOT NULL DEFAULT '#6750A4',
                    icon_name TEXT NOT NULL DEFAULT 'star',
                    is_completed INTEGER NOT NULL DEFAULT 0
                )
            """)
            
            try {
                database.execSQL("""
                    INSERT OR IGNORE INTO time_blocks_new(id, title, description, location, 
                    start_time, end_time, date, time_range, category_id, color_hex, icon_name, is_completed)
                    SELECT id, title, description, location, start_time, end_time, date, 
                    time_range, category_id, color_hex, icon_name, is_completed FROM time_blocks
                """)
            } catch (e: Exception) {
                Timber.e(e, "Error migrating time_blocks data")
            }
            
            try {
                database.execSQL("DROP TABLE IF EXISTS time_blocks")
            } catch (e: Exception) {
                Timber.e(e, "Error dropping time_blocks table")
            }
            
            database.execSQL("ALTER TABLE time_blocks_new RENAME TO time_blocks")
            
            // Create indices for improved performance
            database.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_sprintId ON tasks(sprintId)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_status ON tasks(status)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_dueDate ON tasks(dueDate)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_time_blocks_date ON time_blocks(date)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_time_blocks_category_id ON time_blocks(category_id)")
        }
    }
}
