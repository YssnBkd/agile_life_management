# Database Schema Simplification: Adding Description Arrays to Replace Description Tables

This guide outlines the steps required to migrate the Agile Life Management app from using separate description tables for goals, tasks, and sprints to using a single `description` array (JSON) column in each of those tables.

---

## 1. Overview of the Change

- **Add a `description` column** (as JSON or array) to `goals`, `tasks`, and `sprints` tables
- **Remove the `goal_descriptions`, `task_descriptions`, and `sprint_descriptions` tables**
- **Store bullet points as arrays instead of separate table records**

---

## 2. Step-by-Step Migration Plan

### 2.1 Preparation

- **Back up all databases** (local Room DB and Supabase/Postgres)
- **Freeze deployments** during migration
- **Notify users** if production is impacted

### 2.2 Supabase/Postgres Migration

#### a. Schema Changes

1. **Add new column**
    ```sql
    ALTER TABLE agile_life.goals ADD COLUMN description JSONB DEFAULT '[]';
    ALTER TABLE agile_life.tasks ADD COLUMN description JSONB DEFAULT '[]';
    ALTER TABLE agile_life.sprints ADD COLUMN description JSONB DEFAULT '[]';
    ```
2. **Migrate data**
    - Aggregate bullet points from the description tables, ordered by `order`, and store as a JSON array in the new column.
    - Example for tasks:
      ```sql
      UPDATE agile_life.tasks t
      SET description = sub.descriptions
      FROM (
        SELECT task_id, jsonb_agg(bullet_point ORDER BY "order") AS descriptions
        FROM agile_life.task_descriptions
        GROUP BY task_id
      ) sub
      WHERE t.id = sub.task_id;
      ```
    - Repeat for goals and sprints.
3. **Validate migration** (optional)
4. **Drop old tables**
    ```sql
    DROP TABLE agile_life.task_descriptions;
    DROP TABLE agile_life.goal_descriptions;
    DROP TABLE agile_life.sprint_descriptions;
    ```

### 2.3 Room (Local) Database Migration

#### a. Schema Changes

1. **Increment Room DB version**
2. **Add new `description` column** to `TaskEntity`, `GoalEntity`, and `SprintEntity` (type: `List<String>`, stored as JSON/text)
3. **Remove description entities and DAOs** from the schema

#### b. Migration Logic

1. **Add migration code** in your Room `Migration` object:
    - Add the new column (e.g., `ALTER TABLE tasks ADD COLUMN description TEXT DEFAULT '[]'`)
    - For each task/goal/sprint, aggregate bullet points from the old description tables (if they exist), ordered by `order`, and update the new column with a JSON array.
    - Example (pseudocode):
      ```kotlin
      // For each task:
      val cursor = db.query("SELECT id FROM tasks")
      while (cursor.moveToNext()) {
          val taskId = cursor.getString(0)
          val descCursor = db.query("SELECT bulletPoint FROM task_descriptions WHERE taskId=? ORDER BY [order]", arrayOf(taskId))
          val bulletPoints = mutableListOf<String>()
          while (descCursor.moveToNext()) {
              bulletPoints.add(descCursor.getString(0))
          }
          val json = JSONArray(bulletPoints).toString()
          db.execSQL("UPDATE tasks SET description=? WHERE id=?", arrayOf(json, taskId))
      }
      ```
    - Repeat for goals and sprints.
2. **Drop old tables**:
    ```sql
    DROP TABLE IF EXISTS task_descriptions;
    DROP TABLE IF EXISTS goal_descriptions;
    DROP TABLE IF EXISTS sprint_descriptions;
    ```

---

## 3. Codebase Changes

### 3.1 Entity Classes

- Update `TaskEntity`, `GoalEntity`, and `SprintEntity` to include a new `description: List<String>` field
- Remove `TaskDescriptionEntity`, `GoalDescriptionEntity`, and `SprintDescriptionEntity`

### 3.2 Type Converters

- Add converters for `List<String>` <-> JSON

### 3.3 Database Class

- Remove description entities and DAOs from the `@Database` annotation and class

### 3.4 DTOs

- Update `TaskDto`, `GoalDto`, and `SprintDto` to include a `description: List<String>` field
- Remove `TaskDescriptionDto`, `GoalDescriptionDto`, and `SprintDescriptionDto`

### 3.5 API Services

- Remove `TaskDescriptionApiService`, `GoalDescriptionApiService`, and `SprintDescriptionApiService`
- Update `TaskApiService`, `GoalApiService`, and `SprintApiService` to handle the new field

### 3.6 Repository Implementations

- Update repository implementations to handle descriptions as arrays
- Remove description-related methods or update them to work with arrays

### 3.7 Domain Layer

- Update domain models and repository interfaces to use `List<String>` for descriptions
- Update use cases and ViewModels as needed

### 3.8 UI Layer

- Update UI components to display and edit bullet point arrays

---

## 4. Testing

- Unit test the migration logic (both Room and Supabase)
- Test UI for bullet point editing and display
- Test sync between local and remote after migration

---

## 5. Deployment

- Deploy Supabase migration first
- Release app update with Room migration and new code
- Monitor logs and user feedback

---

## 6. Rollback Plan

- Keep backups of all data and code
- If issues arise, restore from backup and revert code changes

---

## 7. Additional Notes

- Consider an incremental approach where both systems coexist during transition, if needed
- Add feature flags if you want to enable/disable the new description system during rollout

---

**End of Guide**
