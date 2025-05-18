# AgileLifeManagement Database Schema

This document outlines the database schema designed to support the UI requirements identified in the updated UI implementation plan. The schema follows Room database principles and is structured to ensure efficient data access patterns for the repositories.

## Table Definitions

### 1. Task Management Tables

#### `tasks` Table
```sql
CREATE TABLE tasks (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,  -- TODO, IN_PROGRESS, DONE, etc.
    priority TEXT NOT NULL,  -- LOW, MEDIUM, HIGH, URGENT
    due_date INTEGER,  -- Timestamp
    created_at INTEGER NOT NULL,  -- Timestamp
    modified_at INTEGER NOT NULL,  -- Timestamp
    category_id TEXT,
    sprint_id TEXT,
    parent_id TEXT,  -- For hierarchical tasks
    estimated_time INTEGER,  -- Minutes
    actual_time INTEGER,  -- Minutes
    assigned_to TEXT,
    is_archived INTEGER NOT NULL DEFAULT 0,  -- Boolean
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (sprint_id) REFERENCES sprints(id) ON DELETE SET NULL,
    FOREIGN KEY (parent_id) REFERENCES tasks(id) ON DELETE CASCADE
)
```

#### `task_labels` Junction Table
```sql
CREATE TABLE task_labels (
    task_id TEXT NOT NULL,
    label_id TEXT NOT NULL,
    PRIMARY KEY (task_id, label_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (label_id) REFERENCES labels(id) ON DELETE CASCADE
)
```

#### `labels` Table
```sql
CREATE TABLE labels (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    color TEXT NOT NULL,
    created_at INTEGER NOT NULL  -- Timestamp
)
```

#### `task_dependencies` Junction Table
```sql
CREATE TABLE task_dependencies (
    dependent_task_id TEXT NOT NULL,
    dependency_task_id TEXT NOT NULL,
    PRIMARY KEY (dependent_task_id, dependency_task_id),
    FOREIGN KEY (dependent_task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (dependency_task_id) REFERENCES tasks(id) ON DELETE CASCADE
)
```

#### `task_day_assignments` Junction Table
```sql
CREATE TABLE task_day_assignments (
    task_id TEXT NOT NULL,
    day_date TEXT NOT NULL,  -- YYYY-MM-DD format
    PRIMARY KEY (task_id, day_date),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
)
```

### 2. Sprint Management Tables

#### `sprints` Table
```sql
CREATE TABLE sprints (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    goal TEXT,
    start_date INTEGER NOT NULL,  -- Timestamp
    end_date INTEGER NOT NULL,  -- Timestamp
    created_at INTEGER NOT NULL,  -- Timestamp
    modified_at INTEGER NOT NULL,  -- Timestamp
    status TEXT NOT NULL,  -- PLANNED, ACTIVE, COMPLETED, CANCELLED
    review_notes TEXT,
    is_archived INTEGER NOT NULL DEFAULT 0  -- Boolean
)
```

#### `sprint_metrics` Table
```sql
CREATE TABLE sprint_metrics (
    id TEXT PRIMARY KEY,
    sprint_id TEXT NOT NULL,
    metric_date INTEGER NOT NULL,  -- Timestamp
    completed_points INTEGER NOT NULL DEFAULT 0,
    total_points INTEGER NOT NULL DEFAULT 0,
    blocker_count INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (sprint_id) REFERENCES sprints(id) ON DELETE CASCADE
)
```

### 3. Goal Management Tables

#### `goals` Table
```sql
CREATE TABLE goals (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    target_date INTEGER,  -- Timestamp
    created_at INTEGER NOT NULL,  -- Timestamp
    modified_at INTEGER NOT NULL,  -- Timestamp
    status TEXT NOT NULL,  -- NOT_STARTED, IN_PROGRESS, COMPLETED, ABANDONED
    progress INTEGER NOT NULL DEFAULT 0,  -- Percentage 0-100
    category_id TEXT,
    parent_id TEXT,  -- For hierarchical goals
    is_archived INTEGER NOT NULL DEFAULT 0,  -- Boolean
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (parent_id) REFERENCES goals(id) ON DELETE CASCADE
)
```

#### `goal_tasks` Junction Table
```sql
CREATE TABLE goal_tasks (
    goal_id TEXT NOT NULL,
    task_id TEXT NOT NULL,
    PRIMARY KEY (goal_id, task_id),
    FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
)
```

#### `goal_milestones` Table
```sql
CREATE TABLE goal_milestones (
    id TEXT PRIMARY KEY,
    goal_id TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    target_date INTEGER,  -- Timestamp
    is_completed INTEGER NOT NULL DEFAULT 0,  -- Boolean
    created_at INTEGER NOT NULL,  -- Timestamp
    FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE
)
```

### 4. Day Planning Tables

#### `day_schedules` Table
```sql
CREATE TABLE day_schedules (
    date TEXT PRIMARY KEY,  -- YYYY-MM-DD format
    notes TEXT,
    morning_mood TEXT,  -- VERY_BAD, BAD, NEUTRAL, GOOD, VERY_GOOD
    morning_energy INTEGER,  -- 1-10
    evening_mood TEXT,  -- VERY_BAD, BAD, NEUTRAL, GOOD, VERY_GOOD
    evening_energy INTEGER,  -- 1-10
    evening_reflection TEXT,
    created_at INTEGER NOT NULL,  -- Timestamp
    modified_at INTEGER NOT NULL  -- Timestamp
)
```

#### `day_activities` Table
```sql
CREATE TABLE day_activities (
    id TEXT PRIMARY KEY,
    day_date TEXT NOT NULL,  -- YYYY-MM-DD format
    title TEXT NOT NULL,
    description TEXT,
    start_time INTEGER NOT NULL,  -- Minutes from midnight
    end_time INTEGER NOT NULL,  -- Minutes from midnight
    category_id TEXT,
    is_completed INTEGER NOT NULL DEFAULT 0,  -- Boolean
    created_at INTEGER NOT NULL,  -- Timestamp
    modified_at INTEGER NOT NULL,  -- Timestamp
    FOREIGN KEY (day_date) REFERENCES day_schedules(date) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
)
```

#### `day_templates` Table
```sql
CREATE TABLE day_templates (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    created_at INTEGER NOT NULL,  -- Timestamp
    modified_at INTEGER NOT NULL  -- Timestamp
)
```

#### `template_activities` Table
```sql
CREATE TABLE template_activities (
    id TEXT PRIMARY KEY,
    template_id TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    start_time INTEGER NOT NULL,  -- Minutes from midnight
    end_time INTEGER NOT NULL,  -- Minutes from midnight
    category_id TEXT,
    FOREIGN KEY (template_id) REFERENCES day_templates(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
)
```

### 5. Wellness Tracking Tables

#### `daily_checkups` Table
```sql
CREATE TABLE daily_checkups (
    date TEXT PRIMARY KEY,  -- YYYY-MM-DD format
    mood TEXT,  -- VERY_BAD, BAD, NEUTRAL, GOOD, VERY_GOOD
    energy INTEGER,  -- 1-10
    sleep_hours REAL,  -- Hours, can be decimal
    sleep_quality TEXT,  -- POOR, FAIR, GOOD, EXCELLENT
    stress_level INTEGER,  -- 1-10
    notes TEXT,
    created_at INTEGER NOT NULL,  -- Timestamp
    modified_at INTEGER NOT NULL  -- Timestamp
)
```

#### `wellness_metrics` Table
```sql
CREATE TABLE wellness_metrics (
    id TEXT PRIMARY KEY,
    date TEXT NOT NULL,  -- YYYY-MM-DD format
    metric_type TEXT NOT NULL,  -- EXERCISE, MEDITATION, WATER, NUTRITION
    value REAL NOT NULL,
    unit TEXT,
    notes TEXT,
    created_at INTEGER NOT NULL,  -- Timestamp
    FOREIGN KEY (date) REFERENCES daily_checkups(date) ON DELETE CASCADE
)
```

### 6. Shared Tables

#### `categories` Table
```sql
CREATE TABLE categories (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    color TEXT NOT NULL,
    icon TEXT,
    parent_id TEXT,
    created_at INTEGER NOT NULL,  -- Timestamp
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
)
```

#### `sync_info` Table
```sql
CREATE TABLE sync_info (
    entity_type TEXT NOT NULL,  -- TASK, SPRINT, GOAL, etc.
    entity_id TEXT NOT NULL,
    last_synced_at INTEGER,  -- Timestamp
    sync_status TEXT NOT NULL,  -- PENDING, SYNCED, FAILED
    error_message TEXT,
    PRIMARY KEY (entity_type, entity_id)
)
```

## Entity Relationships

### Task Relationships
- Tasks can belong to a Sprint (Many-to-One)
- Tasks can have Labels (Many-to-Many)
- Tasks can belong to Categories (Many-to-One)
- Tasks can have Dependencies (Many-to-Many with self-reference)
- Tasks can be assigned to specific Days (Many-to-Many)
- Tasks can be hierarchical (parent-child relationships)
- Tasks can be associated with Goals (Many-to-Many)

### Sprint Relationships
- Sprints contain Tasks (One-to-Many)
- Sprints have daily Metrics (One-to-Many)

### Goal Relationships
- Goals are associated with Tasks (Many-to-Many)
- Goals have Milestones (One-to-Many)
- Goals can belong to Categories (Many-to-One)
- Goals can be hierarchical (parent-child relationships)

### Day Planning Relationships
- Day Schedules have Activities (One-to-Many)
- Day Templates have Activities (One-to-Many)
- Days can have assigned Tasks (Many-to-Many)

### Wellness Tracking Relationships
- Daily Checkups have Metrics (One-to-Many)
- Daily Checkups correspond to Day Schedules (One-to-One)

## Type Converters

For Room database implementation, the following type converters will be needed:

1. **DateConverter**: Convert between LocalDate/LocalDateTime and Long (timestamp)
2. **EnumConverters**: Convert between enum values and their string representations
3. **ListConverters**: Convert between lists (e.g., tags) and string representations
4. **UUIDConverter**: Convert between UUID and String

## Indices

For performance optimization, the following indices should be created:

```sql
-- Task indices
CREATE INDEX idx_tasks_sprint_id ON tasks(sprint_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_category_id ON tasks(category_id);

-- Sprint indices
CREATE INDEX idx_sprints_status ON sprints(status);
CREATE INDEX idx_sprints_date_range ON sprints(start_date, end_date);

-- Day activities indices
CREATE INDEX idx_day_activities_day_date ON day_activities(day_date);
CREATE INDEX idx_day_activities_time_range ON day_activities(start_time, end_time);

-- Goal indices
CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_goals_target_date ON goals(target_date);
CREATE INDEX idx_goals_category_id ON goals(category_id);
```

## Migration Strategy

When implementing this schema with Room, define proper migration paths to handle schema changes over time. The initial migration should set up all tables as defined above.

For subsequent migrations, follow these principles:
1. Never delete columns or tables without preserving data
2. Always provide a migration path for existing users
3. Use versioned migrations with proper testing

## Data Access Objects (DAOs)

For each major entity type, create a corresponding DAO interface following the Clean Architecture principles:

1. **TaskDao**
2. **SprintDao**
3. **GoalDao**
4. **DayScheduleDao**
5. **DayActivityDao**
6. **WellnessDao**
7. **CategoryDao**

Each DAO should provide methods for CRUD operations, as well as specialized queries needed by the UI.

## Repository Implementation

Each repository implementation will use one or more DAOs to provide data to the domain layer. Repositories should handle:

1. Data mapping between database entities and domain models
2. In-memory caching where appropriate
3. Synchronization between local database and remote API
4. Error handling and data validation
5. Transaction management for complex operations

Example repository pattern:

```kotlin
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskLabelDao: TaskLabelDao,
    private val remoteDataSource: TaskRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.observeAllTasks()
            .map { taskEntities ->
                taskEntities.map { it.toDomainModel() }
            }
            .flowOn(ioDispatcher)
    }
    
    // Other repository methods...
}
```

This database schema provides a solid foundation for implementing the repository layer that will support all the UI requirements outlined in the implementation plan.
