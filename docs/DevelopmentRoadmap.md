# AgileLifeManagement Development Roadmap

## Overview
This roadmap outlines the implementation plan for connecting the UI layer to local data repositories in the AgileLifeManagement app. The focus is on creating a testable offline-first application that works on Android Studio emulators.

## Timeline Overview

| Week | Phase | Focus Area | Status |
|------|-------|------------|--------|
| 1 | Phase 1 | Local Database Setup | Not Started |
| 2 | Phase 2 | Repository Layer Implementation | Not Started |
| 3 | Phase 3-4 | DI Setup and Use Cases (Part 1) | Not Started |
| 4 | Phase 4-5 | Use Cases (Part 2) and ViewModels (Part 1) | Not Started |
| 5 | Phase 5-6 | ViewModels (Part 2) and UI Integration (Part 1) | Not Started |
| 6 | Phase 6-7 | UI Integration (Part 2) and Testing | Not Started |
| 7 | Phase 7-8 | Testing and Polish | Not Started |
| 8 | Phase 8 | Final Optimizations and Deployment to Emulator | Not Started |

## Prerequisites

### Dependencies Required
```kotlin
// build.gradle (app)
dependencies {
    // Room
    implementation "androidx.room:room-runtime:2.5.2"
    implementation "androidx.room:room-ktx:2.5.2"
    kapt "androidx.room:room-compiler:2.5.2"
    
    // Hilt
    implementation "com.google.dagger:hilt-android:2.46.1"
    kapt "com.google.dagger:hilt-android-compiler:2.46.1"
    implementation 'androidx.hilt:hilt-navigation-compose:1.0.0'
    
    // Coroutines & Flow
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1"
    
    // ViewModel & LiveData
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1"
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"
}
```

### Project Structure Requirements
- Clean architecture layers must be properly separated
- Domain models should be immutable and free from framework dependencies
- UI components should follow Material 3 Expressive design principles

## Detailed Implementation Plan

### Phase 1: Local Database Setup (Week 1)

#### 1.1 Room Database Configuration
- Create a central AppDatabase class
  - Define abstract DAO accessor methods
  - Configure database version and export schema settings
  - Add necessary annotations
- Configure database version, migrations strategy
  - Set initial version to 1
  - Create a migration plan document for future updates
- Set up type converters for complex types
  - LocalDate converter
  - LocalTime converter
  - Color converter (for Material design colors)
  - ImageVector name converter (for storing icon references)
  - UUID converter

##### Current Status: Completed
- Created DateTimeConverters for LocalDate and LocalTime
- Created ColorConverters for Material 3 colors
- Added IconConverters for Material Icons
- Implemented UUIDConverter for unique IDs
- Created the central AppDatabase class with proper annotations
- Created a database migration strategy document for future schema updates

#### 1.2 Entity Classes
- Create/Update Entity classes for:
  - TimeBlockEntity
    - Map from TimeBlock domain model
    - Add primary key, relationship fields
    - Support category, color, and icon storage
  - TaskEntity
    - Map from Task domain model
    - Add priority, status, and deadline fields
    - Include relations to sprints and categories
  - DayActivityEntity
    - Map from DayActivity domain model
    - Store scheduled time and duration
    - Link to categories and tasks
  - ActivityCategoryEntity
    - Store activity categories with their properties
    - Include color and icon references
- Add proper Room annotations
  - @Entity, @PrimaryKey annotations
  - @ColumnInfo for custom column names
  - @Ignore for fields not to be persisted
- Implement relationship mappings
  - Use @Relation for one-to-many relationships
  - Create junction tables for many-to-many relationships
  - Define foreign keys with @ForeignKey

##### Current Status: Completed
- Found existing entity classes that provide good coverage of our domain models
- Enhanced TimeBlockEntity with proper Room annotations and relationships
- Improved ActivityCategoryEntity with Material 3 design support
- Enhanced DayActivityEntity documentation to highlight Material 3 design principles
- Added Material 3 design principle documentation to TaskEntity
- Verified all entity classes have proper relationship mappings including foreign keys
- All entity classes now follow Material 3 Expressive design principles

#### 1.3 Data Access Objects (DAOs)
- Implement DAOs for primary entities:
  - TimeBlockDao
    - Query time blocks by date range
    - Filter by category
    - Support for completion status updates
  - DayActivityDao
    - Retrieve activities for a specific day
    - Filter by category and completion status
    - Support for scheduling queries
  - TaskDao
    - Support for filtering by status and priority
    - Handle due date queries and deadline proximity
    - Manage sprint associations
  - ActivityCategoryDao
    - Basic CRUD operations
    - Support for category color and icon updates
- Implement common query patterns:
  - Use Flow return types for reactive UI updates
  - Include sorting parameters in queries
  - Support paging for large result sets
- Add specialized query methods:
  - Calendar view data retrieval
  - Dashboard summary queries
  - Analytics and reporting queries

##### Current Status: Completed
- Created TimeBlockDao with comprehensive query methods supporting Material 3 design
- Enhanced ActivityCategoryDao documentation to highlight Material 3 Expressive design support
- Enhanced TaskDao with additional analytics and dashboard-focused queries
- Added support for reactive data streams with Flow return types
- Implemented sorting parameters in queries for better visual organization
- Added specialized query methods for timeline and dashboard visualizations

### Phase 2: Repository Layer Implementation (Week 2)

#### 2.1 Repository Interfaces
- Define repository interfaces for primary domain models:
  - TimeBlockRepository
    - Define methods for timeline data access
    - Add support for date range filtering
    - Include completion status operations
  - DayActivityRepository
    - Support day and week-based operations
    - Define category filtering methods
    - Add schedule management operations
  - TaskRepository
    - Define status and priority filtering
    - Add deadline management methods
    - Include sprint and tag association operations
  - ActivityCategoryRepository
    - Define basic CRUD operations
    - Add methods for Material 3 color and icon management
- Ensure interfaces follow Material 3 reactive pattern:
  - Use Flow return types for continuous UI updates
  - Include suspend functions for one-time operations
  - Define error handling approach

##### Current Status: Completed
- Defined TimeBlockRepository interface with methods for timeline data access and filtering
- Created CategoryRepository interface with Material 3 color and icon management support
- Enhanced repository interfaces to use Flow return types for reactive UI updates
- Added Result return type for error handling
- Implemented repository interfaces that align with Material 3 Expressive design principles

#### 2.2 Repository Implementations
- Create offline repository implementations using Room DAOs
- Implement mappers between Entity and Domain models
- Add proper error handling

##### Current Status: Completed
- Implemented TimeBlockRepositoryImpl with Material 3 color and icon support
- Created CategoryRepositoryImpl with system and user category separation
- Added bidirectional mapping between entity and domain models
- Implemented proper error handling with Result type
- Used Flow for reactive data streams to support responsive UI updates

#### 2.3 Entity-Domain Mappers
- Create mapper classes for bidirectional conversion
- Handle data transformations between layers

##### Current Status: Completed
- Implemented mapper functions directly within Repository implementations
- Added support for Material 3 color system conversions (hex to Color and back)
- Created icon name to ImageVector conversion utilities
- Handled nullable fields appropriately in bidirectional mappings

### Phase 3: Dependency Injection Setup (Week 3)

#### 3.1 Dagger-Hilt Module Configuration
- Set up modules for database, DAOs, and repositories
- Configure appropriate scopes
- Provide dependencies for all components

#### 3.2 Application Class Setup
- Configure HiltAndroidApp
- Initialize necessary components

### Phase 4: UseCase Layer Implementation (Week 3-4)

#### 4.1 Define Use Cases
- Create single-responsibility use case classes
- Group related operations
- Implement business logic

#### 4.2 Error Handling
- Implement Result wrapper for use case responses
- Create consistent error handling strategy

### Phase 5: ViewModel Implementation (Week 4-5)

#### 5.1 Create ViewModels
- Implement ViewModels for each screen
- Inject required use cases
- Handle state management
- Implement UI events and actions

#### 5.2 UI State Management
- Define UI state classes for each screen
- Include loading, success, and error states
- Create event handlers for user interactions

### Phase 6: UI Integration (Week 5-6)

#### 6.1 Update Compose UI
- Connect screens to ViewModels
- Collect UI state in composables
- Handle user actions and events
- Implement error handling and loading states

#### 6.2 NavHost and Navigation Updates
- Update routes to use data-connected screens
- Add any additional navigation parameters
- Ensure proper data passing between screens

### Phase 7: Data Seeding and Testing (Week 6-7)

#### 7.1 Database Seeding
- Create database seeders for initial data
- Implement configurable seeding (dev vs production)
- Seed essential data for testing

#### 7.2 Emulator Testing
- Configure for emulator testing
- Add test-specific configurations
- Test all main user flows on emulator

### Phase 8: Application Polish and Optimization (Week 7-8)

#### 8.1 UI Refinements
- Polish UI components
- Add animations and transitions
- Handle edge cases (empty states, errors)
- Implement loading indicators

#### 8.2 Performance Optimization
- Optimize database queries
- Add indexes for frequently queried fields
- Improve large list loading

## Progress Tracking

This section will be updated as we complete each step of the implementation.

### Completed Steps
<!-- This section will be populated as we complete implementation steps -->
