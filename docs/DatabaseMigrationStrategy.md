# Database Migration Strategy

## Overview
This document outlines the migration strategy for the AgileLifeManagement Room database. Following proper database migration practices ensures that user data is preserved when the app is updated with schema changes.

## Current Schema Version
- **Version 1**: Initial database schema

## Migration Guidelines

### Adding a New Migration
When changing the database schema:

1. **Increment the version number** in `AppDatabase.kt`
2. **Create a Migration class** that extends `Migration` from Room
3. **Implement the SQL ALTER statements** needed to transform the old schema to the new one
4. **Add the migration to the database builder** in the Dagger-Hilt module
5. **Document the changes** in this file

### Example Migration Implementation

```kotlin
// Migration from version 1 to 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Example: Adding a new column to an existing table
        database.execSQL("ALTER TABLE time_blocks ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
    }
}

// In Dagger-Hilt module
@Provides
@Singleton
fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    )
    .addMigrations(MIGRATION_1_2)
    .build()
}
```

## Testing Migrations
All migrations should be tested to ensure they work correctly:

1. Create a test database with the old schema
2. Apply the migration
3. Verify the new schema is correct
4. Verify existing data is preserved correctly

## Migration History

### Version 1 → 2 (Planned)
- Initial schema to future schema changes
- *Planned changes will be documented here when implemented*

### Version 2 → 3 (Future)
- *Future migrations will be documented here*

## Schema Export
Room automatically exports the database schema when `exportSchema = true`. Schema JSON files are stored in:
`app/schemas/com.example.agilelifemanagement.data.local.db.AppDatabase/`

These schema files should be committed to version control to maintain a history of database changes.
