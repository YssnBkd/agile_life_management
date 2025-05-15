# AgileLifeManagement Project Implementation Plan

## Current Project State Analysis

Based on the project examination, here's the current situation:

1. **Architecture Status**: 
   - The UI layer is mostly intact with ViewModels and Compose UI components
   - The domain layer has use cases defined but is missing implementation connections
   - The data layer has been archived (moved to the archive folder) and needs to be rebuilt
   
2. **Technology Stack**:
   - Jetpack Compose for UI
   - Hilt for dependency injection
   - Kotlin Coroutines and Flow for asynchronous programming
   - Room for local database (to be reimplemented)
   - Ktor Client for networking (to be reimplemented)
   - Supabase for backend services

3. **Missing Components**:
   - Repository implementations
   - Data sources (local and remote)
   - Database configuration
   - API service implementation

## Implementation Plan

### Phase 1: Set Up Core Infrastructure

#### Step 1: Define Domain Models

Create the core domain models that will be used throughout the application. These should be clean Kotlin data classes without any Android dependencies.

**Database Schema and Entity Relationships**:

Based on a comprehensive review of use cases, the following schema has been designed:

1. **Core Entities**:
   - **Task**: Tasks represent work items that can be associated with sprints
      - Properties: id, title, description, status (enum), priority (enum), dueDate, createdDate, tags, sprintId (nullable)
      - Relationships: Many-to-one with Sprint (optional), Many-to-many with Tag

   - **Sprint**: Time-boxed periods for completing sets of tasks
      - Properties: id, name, startDate, endDate, goals, status (enum)
      - Relationships: One-to-many with Task, One-to-one with SprintReview

   - **SprintReview**: Assessment of a completed sprint
      - Properties: id, sprintId, completionRate, lessonsLearned, date
      - Relationships: One-to-one with Sprint

   - **Goal**: Strategic objectives tracked over time
      - Properties: id, title, description, deadline, status (enum), priority (enum)
      - Relationships: None (though can be referenced in Sprint goals as text)

   - **DayActivity**: Scheduled activities for specific days
      - Properties: id, title, description, date, scheduledTime, duration, completed, categoryId
      - Relationships: Many-to-one with ActivityCategory

   - **ActivityCategory**: Categories for organizing activities
      - Properties: id, name, color
      - Relationships: One-to-many with DayActivity

   - **DayActivityTemplate**: Reusable templates for common activities
      - Properties: id, title, description, defaultDuration, categoryId
      - Relationships: Many-to-one with ActivityCategory

   - **DailyCheckup**: Daily wellness self-assessments
      - Properties: id, date, moodRating, sleepQuality, stressLevel, energyLevel, notes
      - Relationships: None

   - **Tag**: Labels for categorizing tasks
      - Properties: id, name, color
      - Relationships: Many-to-many with Task

2. **Join Tables**:
   - **TaskTagCrossRef**: Join table for task-tag many-to-many relationship
      - Properties: taskId, tagId
      - Primary key: Composite (taskId, tagId)

**Prompt for Windsurf**:
```
Create domain model classes for the AgileLifeManagement project. These should be pure Kotlin data classes that represent our core business objects. 

Start with the following entities based on our existing use cases:
- Task: with properties for id, title, description, status (enum), priority (enum), dueDate, createdDate, sprintId (nullable), tags (List<String>)
- Sprint: with properties for id, name, startDate, endDate, goals (List<String>), status (enum)
- SprintReview: with properties for id, sprintId, completionRate, lessonsLearned (List<String>), date
- Goal: with properties for id, title, description, deadline, status (enum), priority (enum)
- DayActivity: with properties for id, title, description, date, scheduledTime, duration, completed, categoryId
- ActivityCategory: with properties for id, name, color
- DayActivityTemplate: with properties for id, title, description, defaultDuration, categoryId
- DailyCheckup: with properties for id, date, moodRating, sleepQuality, stressLevel, energyLevel, notes
- Tag: with properties for id, name, color

Also create enums for:
- TaskStatus (TODO, IN_PROGRESS, BLOCKED, COMPLETED)
- TaskPriority (LOW, MEDIUM, HIGH, URGENT)
- SprintStatus (PLANNED, ACTIVE, COMPLETED)
- GoalStatus (NOT_STARTED, IN_PROGRESS, COMPLETED)
- GoalPriority (LOW, MEDIUM, HIGH)

Place these in the com.example.agilelifemanagement.domain.model package. Use appropriate data types, including LocalDate/LocalDateTime for dates and time.
```

#### Step 2: Define Repository Interfaces

Create repository interfaces that will define the contract for data operations.

**Prompt for Windsurf**:
```
Create repository interfaces for the AgileLifeManagement project. These interfaces will define the contract between the domain and data layers.

Based on our domain models, we need these repository interfaces:
1. TaskRepository - For CRUD operations on tasks
2. SprintRepository - For sprint management
3. GoalRepository - For goal tracking
4. DayActivityRepository - For daily activities
5. CategoryRepository - For activity categories
6. WellnessRepository - For daily checkups and wellness data

Each repository interface should declare methods with suspend functions for one-shot operations and Flow for observing data changes over time.

Place these in com.example.agilelifemanagement.domain.repository package. Ensure they align with the use cases we already have in the domain layer.
```

### Phase 2: Implement Local Data Sources

#### Step 3: Set Up Room Database

Create the Room database configuration, entities, and DAOs.

**Prompt for Windsurf**:
```
Implement a Room database for local persistence in the AgileLifeManagement app. Create:

1. Database configuration class (AppDatabase)
2. Database entities that map to our domain models:
   - TaskEntity
   - SprintEntity
   - GoalEntity
   - DayActivityEntity
   - ActivityCategoryEntity
   - DailyCheckupEntity

3. Data Access Objects (DAOs) with methods for each entity:
   - TaskDao
   - SprintDao
   - GoalDao
   - DayActivityDao
   - ActivityCategoryDao
   - DailyCheckupDao

Include appropriate type converters for complex types like dates, lists, and enums. Use Room's relationship annotations where needed (like @Relation).

Place these in com.example.agilelifemanagement.data.local package structure.
```

#### Step 4: Create Local Data Sources

Implement local data sources that will use Room DAOs to access data.

**Prompt for Windsurf**:
```
Create local data sources for the AgileLifeManagement app. These classes will use Room DAOs to perform database operations.

For each entity, create a data source class:
1. TaskLocalDataSource
2. SprintLocalDataSource
3. GoalLocalDataSource
4. DayActivityLocalDataSource
5. CategoryLocalDataSource
6. WellnessLocalDataSource

Each data source should:
- Be injected with its corresponding DAO
- Provide methods that map to the DAO operations
- Use appropriate threading via coroutines
- Handle basic error cases

Place these in com.example.agilelifemanagement.data.local.source package.
```

### Phase 3: Implement Remote Data Sources

#### Step 5: Set Up Ktor Client and API Services

Configure Ktor Client and create API service interfaces.

**Prompt for Windsurf**:
```
Set up the networking layer for the AgileLifeManagement app using Ktor Client to interact with Supabase backend services.

1. Create a NetworkModule that provides a configured HttpClient instance with:
   - JSON serialization/deserialization
   - Logging
   - Error handling
   - Authentication headers

2. Create API service interfaces for:
   - TaskApiService
   - SprintApiService
   - GoalApiService
   - DayActivityApiService
   - CategoryApiService
   - WellnessApiService

These should define suspend functions for REST operations against Supabase tables.

3. Implement concrete classes for these interfaces that use the HttpClient.

Place these in com.example.agilelifemanagement.data.remote package.
```

#### Step 6: Create Remote Data Sources

Implement remote data sources that will use the API services to access remote data.

**Prompt for Windsurf**:
```
Create remote data sources for the AgileLifeManagement app that will use our API services to interact with Supabase.

For each API service, create a remote data source class:
1. TaskRemoteDataSource
2. SprintRemoteDataSource
3. GoalRemoteDataSource
4. DayActivityRemoteDataSource
5. CategoryRemoteDataSource
6. WellnessRemoteDataSource

Each remote data source should:
- Be injected with its corresponding API service
- Provide methods that call the API service methods
- Transform between API DTOs and domain models
- Handle network errors appropriately
- Use coroutines for asynchronous operations

Place these in com.example.agilelifemanagement.data.remote.source package.
```

### Phase 4: Implement Repository Layer

#### Step 7: Create Repository Implementations

Implement the repository interfaces using both local and remote data sources.

**Prompt for Windsurf**:
```
Implement repository classes for the AgileLifeManagement app. These repositories will act as the Single Source of Truth by coordinating between local and remote data sources.

Create implementations for each repository interface:
1. TaskRepositoryImpl
2. SprintRepositoryImpl
3. GoalRepositoryImpl
4. DayActivityRepositoryImpl
5. CategoryRepositoryImpl
6. WellnessRepositoryImpl

Each repository implementation should:
- Be injected with both local and remote data sources
- Implement the repository interface methods
- Use an offline-first strategy (read from local, write to both)
- Handle synchronization between local and remote
- Use appropriate coroutine dispatchers for threading
- Provide proper error handling and recovery
- Use mappers to convert between entity and domain models

Place these in com.example.agilelifemanagement.data.repository package.
```

#### Step 8: Create Data Mappers

Create mapper classes to convert between different data models.

**Prompt for Windsurf**:
```
Create mapper classes for the AgileLifeManagement app to convert between different types of models (domain models, database entities, API DTOs).

For each domain model, create mappers:
1. TaskMapper
2. SprintMapper
3. GoalMapper
4. DayActivityMapper
5. CategoryMapper
6. WellnessMapper

Each mapper should include:
- Functions to map from entity to domain model
- Functions to map from domain model to entity
- Functions to map from API DTO to domain model
- Functions to map from domain model to API DTO

Consider using extension functions for clean, readable code.

Place these in com.example.agilelifemanagement.data.mapper package.
```

#### Step 9: Set Up Dependency Injection

Configure Hilt modules to provide instances of repositories and data sources.

**Prompt for Windsurf**:
```
Create Hilt modules for dependency injection in the AgileLifeManagement app. These modules will provide instances of our repositories and data sources.

Create these Hilt modules:
1. RepositoryModule - Binds repository interfaces to their implementations
2. LocalDataModule - Provides local data sources and Room database
3. RemoteDataModule - Provides remote data sources and API services
4. NetworkModule - Provides HttpClient and network utilities
5. DispatcherModule - Provides coroutine dispatchers

Each module should:
- Use appropriate scope annotations (@Singleton, etc.)
- Use @Provides or @Binds as appropriate
- Follow Hilt best practices for injection

Place these in com.example.agilelifemanagement.di package.
```

### Phase 5: Connect Domain and UI Layers

#### Step 10: Complete Use Case Integration

Ensure all use cases are properly connected to their repositories.

**Prompt for Windsurf**:
```
Review and update the existing use cases in the AgileLifeManagement app to ensure they're correctly connected to the newly implemented repositories.

For each use case in the domain.usecase package:
1. Verify it's injected with the correct repository interface
2. Check that it's using the repository methods correctly
3. Ensure it follows the invoke pattern for clean usage
4. Verify threading concerns are handled appropriately
5. Check error handling strategy is consistent

If any missing use cases are identified based on UI needs, create those as well. Make sure all use cases are properly annotated for Hilt injection.
```

#### Step 11: Update ViewModels

Update ViewModels to use the use cases for data operations.

**Prompt for Windsurf**:
```
Update the ViewModels in the AgileLifeManagement app to use our use cases for data operations. Make sure they follow the Unidirectional Data Flow pattern.

For each ViewModel:
1. Inject the required use cases
2. Define a UI state class that represents all possible UI states
3. Use StateFlow to expose UI state to the UI
4. Create event handler functions that call use cases
5. Update state based on use case results
6. Handle errors appropriately
7. Use viewModelScope for coroutine management

Focus on these ViewModels first:
- TaskViewModel (to create)
- SprintViewModel (to create)
- DayViewModel (to update)
- TemplateViewModel (to update)
- WeekViewModel (to update)

Place new ViewModels in the appropriate ui/screens package.
```

### Phase 6: Integration Testing and Refinement

#### Step 12: Create Integration Tests

Create integration tests for the repository implementations.

**Prompt for Windsurf**:
```
Create integration tests for the repository implementations in the AgileLifeManagement app.

For each repository:
1. Create a test class that uses Hilt's testing utilities
2. Use TestCoroutineDispatcher for controlling coroutines
3. Set up test versions of data sources (fakes or mocks)
4. Test offline-first strategy works correctly
5. Test error handling and recovery
6. Test synchronization between local and remote

Focus on testing the most critical repositories first:
- TaskRepositoryImpl
- DayActivityRepositoryImpl
- SprintRepositoryImpl

Place these in the androidTest or test directory as appropriate, mirroring the package structure of the repositories.
```

#### Step 13: Create UI Tests

Create UI tests for important screens.

**Prompt for Windsurf**:
```
Create UI tests for key screens in the AgileLifeManagement app using Compose test tools.

For these important screens:
1. Day view
2. Week view
3. Task list
4. Sprint detail

Create test cases that:
1. Set up test dependencies using Hilt
2. Inject fake repositories for deterministic testing
3. Test UI renders correctly for different states
4. Test user interactions update the UI as expected
5. Test error states are handled properly

Use createComposeRule() and Compose testing utilities to verify UI elements.

Place these in the androidTest directory.
```

## Implementation Timeline

- Phase 1 (Days 1-2): Set up core domain models and repository interfaces
- Phase 2 (Days 3-5): Implement local data sources and Room database
- Phase 3 (Days 6-8): Implement remote data sources and API services
- Phase 4 (Days 9-12): Implement repositories and data mappers
- Phase 5 (Days 13-15): Connect domain and UI layers
- Phase 6 (Days 16-18): Create tests and refine implementation

## Next Steps 

Begin with Phase 1, Step 1: Define Domain Models. This will establish the foundation for our data layer rebuilding effort.

Once the domain models are defined, proceed to Step 2: Define Repository Interfaces to establish the contracts that will guide our repository implementations.

Progress through the phases sequentially, testing each component as it's implemented to ensure it works as expected before moving on to the next step.
