# Room Database Implementation

## Overview

Room is Android's recommended persistence library that provides an abstraction layer over SQLite. Room makes it easier to work with databases in Android while providing compile-time verification of SQL queries and reducing boilerplate code.

This guide covers best practices for implementing Room in the context of a clean architecture, offline-first Android application.

## Room Architecture Components

Room consists of three main components:

1. **Entity**: Java/Kotlin classes annotated with `@Entity` that represent tables in the database
2. **DAO**: Data Access Objects annotated with `@Dao` that provide methods to interact with the database
3. **Database**: An abstract class annotated with `@Database` that serves as the main access point to the database

## Setting Up Room

### Dependencies

Add the required dependencies to your app-level `build.gradle` file:

```kotlin
dependencies {
    // Room components
    implementation "androidx.room:room-runtime:2.5.1"
    implementation "androidx.room:room-ktx:2.5.1"      // Kotlin Extensions and Coroutines support
    kapt "androidx.room:room-compiler:2.5.1"           // Annotation processor

    // Optional - Test helpers
    testImplementation "androidx.room:room-testing:2.5.1"
}
```

### Defining Entities

Entities represent tables in your database. Each entity should correspond to a domain model in your application:

```kotlin
// Define enum classes for task status
enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED
}

// Type converter for TaskStatus
class TaskStatusConverter {
    @TypeConverter
    fun toTaskStatus(value: Int): TaskStatus = TaskStatus.values()[value]
    
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): Int = status.ordinal
}

@Entity(
    tableName = "tasks",
    indices = [
        Index("status"),
        Index("due_date")
    ]
)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    
    val title: String,
    
    val description: String? = null,
    
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,
    
    val status: TaskStatus,
    
    @ColumnInfo(name = "sprint_id")
    val sprintId: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
```

### Defining Relationships

Room provides several ways to handle relationships between entities:

#### One-to-One Relationships

```kotlin
// Entity with a reference to another entity
@Entity(
    tableName = "task_details",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("task_id")]
)
data class TaskDetailEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id")
    val taskId: String,
    
    val notes: String? = null,
    
    val attachments: String? = null, // Stored as JSON string
    
    val metadata: String? = null // Stored as JSON string
)
```

#### One-to-Many Relationships

Create a data class with a relationship between entities:

```kotlin
data class TaskWithSubtasks(
    @Embedded val task: TaskEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "parent_task_id"
    )
    val subtasks: List<SubtaskEntity>
)

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("parent_task_id")]
)
data class SubtaskEntity(
    @PrimaryKey
    val id: String,
    
    val title: String,
    
    val isCompleted: Boolean,
    
    @ColumnInfo(name = "parent_task_id")
    val parentTaskId: String
)
```

#### Many-to-Many Relationships

Use a junction table to create many-to-many relationships:

```kotlin
// Task entity (already defined above)

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey
    val id: String,
    
    val name: String,
    
    val color: String
)

// Junction table
@Entity(
    tableName = "task_tag_cross_ref",
    primaryKeys = ["task_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("task_id"),
        Index("tag_id")
    ]
)
data class TaskTagCrossRef(
    @ColumnInfo(name = "task_id")
    val taskId: String,
    
    @ColumnInfo(name = "tag_id")
    val tagId: String
)

// Relationship class
data class TaskWithTags(
    @Embedded val task: TaskEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskTagCrossRef::class,
            parentColumn = "task_id",
            entityColumn = "tag_id"
        )
    )
    val tags: List<TagEntity>
)
```

### Creating DAOs

DAOs (Data Access Objects) define the methods to interact with the database:

```kotlin
@Dao
interface TaskDao {
    // Queries that return Flow for observing changes
    @Query("SELECT * FROM tasks ORDER BY updated_at DESC")
    fun observeTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE sprint_id = :sprintId ORDER BY updated_at DESC")
    fun observeTasksBySprintId(sprintId: String): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY updated_at DESC")
    fun observeTasksByStatus(status: Int): Flow<List<TaskEntity>>
    
    // One-shot queries using suspend functions
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    
    @Update
    suspend fun updateTask(task: TaskEntity): Int
    
    @Query("UPDATE tasks SET status = :status, updated_at = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: Int, updatedAt: Long): Int
    
    @Delete
    suspend fun deleteTask(task: TaskEntity): Int
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String): Int
    
    // Relationship queries
    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun observeTaskWithSubtasks(taskId: String): Flow<TaskWithSubtasks?>
    
    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun observeTaskWithTags(taskId: String): Flow<TaskWithTags?>
    
    // Transaction methods
    @Transaction
    suspend fun updateTaskAndSubtasks(task: TaskEntity, subtasks: List<SubtaskEntity>) {
        updateTask(task)
        // Delete existing subtasks
        deleteSubtasksForTask(task.id)
        // Insert new subtasks
        insertSubtasks(subtasks)
    }
}

@Dao
interface SubtaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtasks(subtasks: List<SubtaskEntity>)
    
    @Query("DELETE FROM subtasks WHERE parent_task_id = :taskId")
    suspend fun deleteSubtasksForTask(taskId: String)
}

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun observeTags(): Flow<List<TagEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)
    
    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: String): TagEntity?
    
    @Delete
    suspend fun deleteTag(tag: TagEntity): Int
}

@Dao
interface TaskTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskTagCrossRef(crossRef: TaskTagCrossRef)
    
    @Query("DELETE FROM task_tag_cross_ref WHERE task_id = :taskId")
    suspend fun deleteTaskTagsForTask(taskId: String)
    
    @Transaction
    suspend fun replaceTaskTags(taskId: String, tagIds: List<String>) {
        deleteTaskTagsForTask(taskId)
        tagIds.forEach { tagId ->
            insertTaskTagCrossRef(TaskTagCrossRef(taskId, tagId))
        }
    }
}
```

### Creating the Database

The database class serves as the main access point to the database:

```kotlin
@Database(
    entities = [
        TaskEntity::class,
        SubtaskEntity::class,
        TagEntity::class,
        TaskTagCrossRef::class,
        TaskDetailEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun tagDao(): TagDao
    abstract fun taskTagDao(): TaskTagDao
    
    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "agilelifemanagement.db"
            )
                .fallbackToDestructiveMigration() // For development only
                .build()
        }
    }
}
```

### Type Converters

Room can only store primitive types and Strings. For complex types, you need type converters:

```kotlin
class Converters {
    // Date conversions
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
    
    // JSON conversions for complex types
    @TypeConverter
    fun fromAttachmentList(value: List<Attachment>?): String? {
        return value?.let { Json.encodeToString(it) }
    }
    
    @TypeConverter
    fun toAttachmentList(value: String?): List<Attachment>? {
        return value?.let { Json.decodeFromString(it) }
    }
}
```

## Database Access in Clean Architecture

### Local Data Source Implementation

In a clean architecture, you typically have a local data source that encapsulates database access:

```kotlin
interface TaskLocalDataSource {
    fun observeTasks(): Flow<List<TaskEntity>>
    fun observeTasksBySprintId(sprintId: String): Flow<List<TaskEntity>>
    fun observeTasksByStatus(status: Int): Flow<List<TaskEntity>>
    suspend fun getTaskById(taskId: String): TaskEntity?
    suspend fun insertTask(task: TaskEntity): Long
    suspend fun insertTasks(tasks: List<TaskEntity>)
    suspend fun updateTask(task: TaskEntity): Int
    suspend fun updateTaskStatus(taskId: String, status: Int): TaskEntity?
    suspend fun deleteTask(taskId: String): Int
    fun observeTaskWithSubtasks(taskId: String): Flow<TaskWithSubtasks?>
    fun observeTaskWithTags(taskId: String): Flow<TaskWithTags?>
    suspend fun updateTaskAndSubtasks(task: TaskEntity, subtasks: List<SubtaskEntity>)
}

class TaskLocalDataSourceImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao
) : TaskLocalDataSource {
    override fun observeTasks(): Flow<List<TaskEntity>> = taskDao.observeTasks()
    
    override fun observeTasksBySprintId(sprintId: String): Flow<List<TaskEntity>> = 
        taskDao.observeTasksBySprintId(sprintId)
    
    override fun observeTasksByStatus(status: Int): Flow<List<TaskEntity>> = 
        taskDao.observeTasksByStatus(status)
    
    override suspend fun getTaskById(taskId: String): TaskEntity? = 
        taskDao.getTaskById(taskId)
    
    override suspend fun insertTask(task: TaskEntity): Long = 
        taskDao.insertTask(task)
    
    override suspend fun insertTasks(tasks: List<TaskEntity>) = 
        taskDao.insertTasks(tasks)
    
    override suspend fun updateTask(task: TaskEntity): Int = 
        taskDao.updateTask(task)
    
    override suspend fun updateTaskStatus(taskId: String, status: Int): TaskEntity? {
        val updatedAt = System.currentTimeMillis()
        val rowsAffected = taskDao.updateTaskStatus(taskId, status, updatedAt)
        
        return if (rowsAffected > 0) {
            taskDao.getTaskById(taskId)
        } else {
            null
        }
    }
    
    override suspend fun deleteTask(taskId: String): Int = 
        taskDao.deleteTaskById(taskId)
    
    override fun observeTaskWithSubtasks(taskId: String): Flow<TaskWithSubtasks?> = 
        taskDao.observeTaskWithSubtasks(taskId)
    
    override fun observeTaskWithTags(taskId: String): Flow<TaskWithTags?> = 
        taskDao.observeTaskWithTags(taskId)
    
    override suspend fun updateTaskAndSubtasks(task: TaskEntity, subtasks: List<SubtaskEntity>) = 
        taskDao.updateTaskAndSubtasks(task, subtasks)
}
```

### Dependency Injection with Hilt

Provide the database and DAOs using Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "agilelifemanagement.db"
        )
            .fallbackToDestructiveMigration() // For development only
            .build()
    }
    
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    fun provideSubtaskDao(database: AppDatabase): SubtaskDao {
        return database.subtaskDao()
    }
    
    @Provides
    fun provideTagDao(database: AppDatabase): TagDao {
        return database.tagDao()
    }
    
    @Provides
    fun provideTaskTagDao(database: AppDatabase): TaskTagDao {
        return database.taskTagDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Singleton
    @Provides
    fun provideTaskLocalDataSource(
        taskDao: TaskDao,
        subtaskDao: SubtaskDao
    ): TaskLocalDataSource {
        return TaskLocalDataSourceImpl(taskDao, subtaskDao)
    }
    
    @Singleton
    @Provides
    fun provideTagLocalDataSource(
        tagDao: TagDao,
        taskTagDao: TaskTagDao
    ): TagLocalDataSource {
        return TagLocalDataSourceImpl(tagDao, taskTagDao)
    }
}
```

## Database Migration

As your app evolves, you'll need to update your database schema. Room provides a migration mechanism to handle this:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add a new column
        database.execSQL("ALTER TABLE tasks ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create a new table
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `comments` " +
            "(`id` TEXT NOT NULL, " +
            "`task_id` TEXT NOT NULL, " +
            "`content` TEXT NOT NULL, " +
            "`created_at` INTEGER NOT NULL, " +
            "`author_id` TEXT NOT NULL, " +
            "PRIMARY KEY(`id`), " +
            "FOREIGN KEY(`task_id`) REFERENCES `tasks`(`id`) ON DELETE CASCADE)"
        )
        
        // Create an index
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_comments_task_id` ON `comments` (`task_id`)"
        )
    }
}

// Use migrations in the database builder
Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    "agilelifemanagement.db"
)
    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
    .build()
```

## Testing Room Database

### Unit Testing DAOs

Room provides testing utilities to create an in-memory database for testing:

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertTaskAndReadInList() = runTest {
        // Given
        val task = TaskEntity(
            id = "1",
            title = "Test Task",
            status = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        // When
        taskDao.insertTask(task)
        
        // Then
        val allTasks = taskDao.observeTasks().first()
        assertEquals(1, allTasks.size)
        assertEquals(task, allTasks[0])
    }
    
    @Test
    fun updateTaskStatus() = runTest {
        // Given
        val taskId = "1"
        val task = TaskEntity(
            id = taskId,
            title = "Test Task",
            status = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        taskDao.insertTask(task)
        
        // When
        val newStatus = 1
        val now = System.currentTimeMillis()
        val rowsUpdated = taskDao.updateTaskStatus(taskId, newStatus, now)
        
        // Then
        assertEquals(1, rowsUpdated)
        val updatedTask = taskDao.getTaskById(taskId)
        assertEquals(newStatus, updatedTask?.status)
        assertEquals(now, updatedTask?.updatedAt)
    }
    
    @Test
    fun observeTasksEmitsUpdates() = runTest {
        // Given
        val tasksFlow = taskDao.observeTasks()
        
        // Initial state: empty list
        assertEquals(0, tasksFlow.first().size)
        
        // When - Insert a task
        val task = TaskEntity(
            id = "1",
            title = "Test Task",
            status = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        taskDao.insertTask(task)
        
        // Then - Flow should emit the new task
        val tasksAfterInsert = tasksFlow.first()
        assertEquals(1, tasksAfterInsert.size)
        assertEquals(task, tasksAfterInsert[0])
        
        // When - Update the task
        val updatedTask = task.copy(status = 1, updatedAt = System.currentTimeMillis())
        taskDao.updateTask(updatedTask)
        
        // Then - Flow should emit the updated task
        val tasksAfterUpdate = tasksFlow.first()
        assertEquals(1, tasksAfterUpdate.size)
        assertEquals(updatedTask, tasksAfterUpdate[0])
    }
}
```

### Integration Testing with Local Data Source

Test the local data source implementation with the actual DAOs:

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskLocalDataSourceImplTest {
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var subtaskDao: SubtaskDao
    private lateinit var dataSource: TaskLocalDataSourceImpl
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
        subtaskDao = database.subtaskDao()
        dataSource = TaskLocalDataSourceImpl(taskDao, subtaskDao)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun updateTaskStatusUpdatesAndReturnsTask() = runTest {
        // Given
        val task = TaskEntity(
            id = "1",
            title = "Test Task",
            status = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        dataSource.insertTask(task)
        
        // When
        val updatedTask = dataSource.updateTaskStatus("1", 1)
        
        // Then
        assertNotNull(updatedTask)
        assertEquals(1, updatedTask?.status)
        
        // Verify in DB
        val taskFromDb = dataSource.getTaskById("1")
        assertEquals(1, taskFromDb?.status)
    }
    
    @Test
    fun observeTaskWithSubtasks() = runTest {
        // Given
        val task = TaskEntity(
            id = "1",
            title = "Parent Task",
            status = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        val subtasks = listOf(
            SubtaskEntity(
                id = "s1",
                title = "Subtask 1",
                isCompleted = false,
                parentTaskId = "1"
            ),
            SubtaskEntity(
                id = "s2",
                title = "Subtask 2",
                isCompleted = true,
                parentTaskId = "1"
            )
        )
        
        // Insert data
        dataSource.insertTask(task)
        subtaskDao.insertSubtasks(subtasks)
        
        // When
        val taskWithSubtasks = dataSource.observeTaskWithSubtasks("1").first()
        
        // Then
        assertNotNull(taskWithSubtasks)
        assertEquals("Parent Task", taskWithSubtasks?.task?.title)
        assertEquals(2, taskWithSubtasks?.subtasks?.size)
        assertTrue(taskWithSubtasks?.subtasks?.any { it.id == "s1" } == true)
        assertTrue(taskWithSubtasks?.subtasks?.any { it.id == "s2" } == true)
    }
}
```

## Performance Optimization

### Indexing

Create indexes on columns that are frequently queried to improve performance:

```kotlin
@Entity(
    tableName = "tasks",
    indices = [
        Index("status"),
        Index("sprint_id"),
        Index("updated_at")
    ]
)
data class TaskEntity(
    // Entity definition
)
```

### Paging

For large datasets, use the Paging library with Room:

```kotlin
@Dao
interface TaskDao {
    // Return a PagingSource for use with the Paging library
    @Query("SELECT * FROM tasks ORDER BY updated_at DESC")
    fun getTasksPagingSource(): PagingSource<Int, TaskEntity>
    
    // Clear tasks when doing refresh
    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}

// In repository
class TaskRepositoryImpl @Inject constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {
    
    override fun getTasksPaged(): Flow<PagingData<Task>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 100
            ),
            pagingSourceFactory = { taskDao.getTasksPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }
}
```

### Precompiled Queries

Room automatically verifies SQL queries at compile time, but you can also use Raw queries for complex cases:

```kotlin
@Dao
interface TaskDao {
    @RawQuery
    fun getTasksWithCustomQuery(query: SupportSQLiteQuery): List<TaskEntity>
    
    // Helper method to create dynamic queries
    fun getTasksWithFilters(
        status: Int? = null,
        sprintId: String? = null,
        searchQuery: String? = null
    ): List<TaskEntity> {
        val queryBuilder = StringBuilder("SELECT * FROM tasks WHERE 1=1")
        val args = mutableListOf<Any>()
        
        status?.let {
            queryBuilder.append(" AND status = ?")
            args.add(it)
        }
        
        sprintId?.let {
            queryBuilder.append(" AND sprint_id = ?")
            args.add(it)
        }
        
        searchQuery?.let {
            queryBuilder.append(" AND (title LIKE ? OR description LIKE ?)")
            val searchPattern = "%$it%"
            args.add(searchPattern)
            args.add(searchPattern)
        }
        
        queryBuilder.append(" ORDER BY updated_at DESC")
        
        val query = SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
        return getTasksWithCustomQuery(query)
    }
}
```

## Best Practices for Room

1. **Use Coroutines and Flow**: Leverage Kotlin Coroutines and Flow for asynchronous operations and reactive data streams
2. **Follow Single Responsibility**: Each DAO should focus on a specific entity or related set of entities
3. **Use Foreign Keys**: Enforce referential integrity with foreign keys
4. **Add Indices**: Create indices on columns used in WHERE clauses or JOINs
5. **Implement Type Converters**: Use type converters for custom types
6. **Handle Migrations**: Plan for database migrations from the beginning
7. **Test Room Code**: Create comprehensive tests for your database operations
8. **Keep Entities Focused**: Design entities to represent database tables, not domain models
9. **Use Transactions**: Wrap related operations in transactions
10. **Consider Full Text Search**: Use FTS for efficient text searching

## Resources

- [Room persistence library documentation](https://developer.android.com/training/data-storage/room)
- [Testing Room migrations](https://developer.android.com/training/data-storage/room/migrating-db-versions)
- [Room with Paging](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
- [Room with Kotlin Coroutines and Flow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
