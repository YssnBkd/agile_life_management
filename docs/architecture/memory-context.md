<PROJECT TECHNOLOGY STACK>
The AgileLifeManagement project is committed to using the following core technologies and libraries throughout its architecture and implementation:

- Architecture Components: ViewModel, StateFlow, Room
- Dependency Injection: Hilt
- Navigation: Jetpack Navigation Compose
- Networking: Ktor Client
- Local Database: Room
- Backend Services: Supabase (Auth, PostgreSQL, Storage)
- Asynchronous Programming: Kotlin Coroutines and Flow
- Date/Time: ThreeTenABP
- Image Loading: Coil
- Logging: Timber
- JSON Serialization: Gson
- CI/CD: GitHub Actions

All future architectural decisions, implementation plans, and code generation should align with this technology stack. When designing or refactoring any layer (UI, domain, data, DI), always consider these tools as the default choices unless explicitly stated otherwise.
</PROJECT TECHNOLOGY STACK>

---

<USE CASE IMPLEMENTATION SUMMARY>
AgileLifeManagement Use Case Implementation Summary (May 2025)

A comprehensive set of use cases has been implemented for the AgileLifeManagement app, fully aligned with Clean Architecture principles and the project's architectural guidelines. All use cases reside in the domain/usecase directory and are organized by feature. They encapsulate business logic, interact with domain repositories, and are injectable via DI. The implemented use cases include:

1. Day Management:
   - Activities: Get, Add, Update, Delete, Toggle completion
   - Day schedule: Get, Update
   - Week view: Get activities for week
   - Templates: Get, Create, Update, Delete
2. Task Management:
   - CRUD: Get, Create, Update, Delete
   - Specialized: Get by ID, Get by Sprint, Update Status, Filtered retrieval, Get by date
3. Sprint Management:
   - CRUD: Get, Create, Update, Delete
   - Sprint review: Create, Get
   - Task tracking within sprints
4. Goal Management:
   - CRUD: Get, Create, Update, Delete
5. Tag Management:
   - Get all, Create
6. Wellness Tracking:
   - Daily checkup: Get, Save
   - Wellness analytics
7. Dashboard & Analytics:
   - Dashboard summary
   - Productivity analytics

All use cases are single-responsibility, well-documented, and serve as the bridge between UI and data layers, ensuring maintainability and scalability for the app's business logic.
</USE CASE IMPLEMENTATION SUMMARY>

---

<UI LAYER BEST PRACTICES>
# Android UI Layer Implementation Best Practices

## UI Layer Responsibility
The UI layer (presentation layer) is responsible for displaying application data on the screen and handling user interactions. It consists of:
- UI elements that render data (Jetpack Compose functions or Views)
- State holders (ViewModel classes) that hold data, expose it to UI, and handle logic

## ViewModel Best Practices
- ViewModels should own UI state and transform domain data into UI-ready format
- Expose immutable state objects to the UI layer (use StateFlow/LiveData)
- Handle UI events and translate them to data layer operations
- Create coroutines using viewModelScope to properly respect lifecycle
- Avoid direct Android framework dependencies in ViewModels

## State Management
- Define UI state as immutable data classes
- Use sealed classes/interfaces for modeling UI events
- Implement Unidirectional Data Flow (UDF):
  - State flows down from ViewModel to UI
  - Events flow up from UI to ViewModel
  - ViewModel updates state based on events and data changes

## Jetpack Compose Implementation
- Pass state down to composables, not ViewModel instances
- Define clear parameters for composables with the minimum information needed
- Use rememberSaveable for state that needs to survive configuration changes
- Create composables that are solely responsible for UI rendering
- Separate UI logic from business logic

## Threading Considerations
- UI should be updated only on the main thread
- ViewModels should manage threading details for UI consumers
- Expose suspend functions that are safe to call from the main thread
- Use appropriate coroutine dispatchers (Main for UI updates)

## Integration with Navigation
- Use the Navigation Component for Jetpack Compose
- Keep navigation logic in the UI layer, not in ViewModels
- Pass minimal data between destinations using safe args
- Consider deep linking requirements in navigation design

## Error Handling in UI
- Include error states in your UI state models
- Provide user-friendly error messages
- Implement retry mechanisms for failed operations
- Consider offline scenarios and provide appropriate feedback
</UI LAYER BEST PRACTICES>

---

<DOMAIN LAYER IMPLEMENTATION GUIDE>
# Android Domain Layer Implementation Guide

## Domain Layer Purpose
The domain layer is an optional layer that sits between the UI and data layers. It should be used when:
- Complex business logic needs to be encapsulated
- Business logic needs to be reused across multiple ViewModels
- Clear separation between data manipulation and presentation is required

## Use Case Design Principles
- Each use case should have a single responsibility (do one thing well)
- Name use cases following the convention: VerbNounUseCase (e.g., `GetTasksUseCase`, `CompleteActivityUseCase`)
- Use cases should be stateless and handle only business logic
- Use cases should not contain Android framework dependencies
- Design use cases to be easily testable

## Implementation Patterns
- In Kotlin, implement the `invoke()` operator to make use cases callable as functions
- Use cases should depend on repositories, not directly on data sources
- Use cases can combine data from multiple repositories when needed
- Return domain models, not data layer models or UI models

## Thread Management
- Use cases should not manage their own threading (this is the caller's responsibility)
- Design use cases to be "main-safe" - safe to call from the main thread
- Expose suspend functions for one-shot operations 
- Expose Flow for continuous data streams
- Avoid blocking operations in use case implementations

## Dependency Structure
- Use cases should take repositories as constructor parameters
- More complex use cases can use other use cases as dependencies
- Use dependency injection (Hilt) to provide use case instances
- Define interfaces for use cases when needed for testing or flexibility

## Error Handling
- Use cases should propagate errors to the caller
- Consider using Result type or custom exception types to represent failures
- Handle expected business rule violations with specific error types
- Document error cases that callers need to handle

</USE CASE IMPLEMENTATION SUMMARY>

---

<DATA LAYER IMPLEMENTATION GUIDE>
# Android Data Layer Implementation Guide

## Data Layer Responsibility
The data layer contains the business logic that gives value to your app. It's responsible for:
- Data operations (create, read, update, delete) 
- Business rule implementation
- Managing data sources
- Caching strategies
- Error handling for data operations

## Repository Pattern Implementation
- Create separate repository classes for each data type (e.g., `TaskRepository`, `SprintRepository`)
- Repositories act as Single Source of Truth (SSOT) for their respective data
- Repositories abstract away the implementation details of data sources
- Repositories should expose only immutable data to other layers
- Define clear interfaces/contracts for repositories to enable testing and flexibility

### Repository Methods
- For one-shot operations: Use suspend functions
- For continuous data streams: Use Flow
- Handle errors appropriately, either with Result types or exceptions
- Centralize business logic operations within repositories

## Data Sources
- Each data source should work with only one source of data (network, database, file, etc.)
- Implement data sources as separate classes injected into repositories
- Keep data source implementations independent of each other
- Handle source-specific error cases at this level

## Room Database Implementation
- Define entities that match your domain models
- Create DAOs (Data Access Objects) with clear, focused methods
- Use suspend functions for one-shot queries and Flow for observing database changes
- Implement type converters for complex data types
- Consider database migrations strategy early 

## Network Data Layer
- Use Ktor Client for network requests
- Create response models separate from domain models
- Implement mappers to convert between network and domain models
- Handle network errors and status codes appropriately
- Consider implementing retry policies for unstable connections

## Caching Strategy
- Implement offline-first approach where appropriate
- Use database as the Single Source of Truth
- Sync periodically with remote data sources
- Consider time-based cache invalidation for certain data types
- Utilize Room's capabilities for complex querying and filtering

## Threading and Coroutines
- Make repository methods main-safe (safe to call from main thread)
- Use appropriate coroutine dispatchers (IO for database/network operations)
- Repository methods should handle their own threading concerns
- Consider using coroutine scopes that match the lifecycle of your data
- Implement proper cancellation handling for long-running operations

## Error Handling
- Define domain-specific exceptions or use Result type
- Provide meaningful error states for common failure scenarios
- Implement recovery mechanisms where possible
- Log errors for debugging purposes
- Consider retry strategies for transient failures
</DATA LAYER IMPLEMENTATION GUIDE>

---

<LAYERED ARCHITECTURE PRINCIPLES>
# Android Layered Architecture: Core Principles

## Architectural Principles

1. **Separation of Concerns**: 
   - Each component should have a single responsibility
   - UI-based classes (Activities, Fragments) should only contain UI logic and OS interactions
   - Business logic should be separated from UI code
   - Data operations should be isolated in a dedicated layer

2. **Drive UI from Data Models**:
   - UIs should be driven by data models, preferably persistent models
   - Models are independent from UI elements and component lifecycle
   - This ensures users don't lose data when OS destroys the app to free resources
   - Apps continue working with flaky network connections

3. **Single Source of Truth (SSOT)**:
   - Each data type should have one authoritative source that owns and can modify it
   - SSOT exposes data using immutable types
   - To modify data, the SSOT exposes functions or receives events
   - Benefits include centralization of changes, protection of data, and traceability
   - In offline-first apps, the database is typically the SSOT

4. **Unidirectional Data Flow (UDF)**:
   - State flows in only one direction, events flow in the opposite direction
   - Typically: data flows from sources to UI, user events flow from UI to data sources
   - This pattern ensures data consistency and improves debugging

5. **Dependency Injection**:
   - Dependencies should be injected rather than constructed within classes
   - Hilt is the recommended DI library for Android
   - DI enables better testing and separation of concerns

## Recommended Layer Structure

The Modern Android Architecture encourages:
- A reactive and layered architecture
- Unidirectional Data Flow in all layers
- UI layer with state holders for UI complexity management
- Coroutines and flows for asynchronous operations
- Strategic use of dependency injection

Each layer has distinct responsibilities and communication patterns, working together to create maintainable, testable applications.
</LAYERED ARCHITECTURE PRINCIPLES>

---

<ARCHITECTURE ANTI-PATTERNS AND MIGRATION>
# Android Architecture Anti-Patterns and Migration Strategies

## Common Architectural Anti-Patterns

### UI Layer Anti-Patterns
- **Bloated Activities/Fragments**: UI classes handling business logic
- **Direct Android API calls in ViewModels**: ViewModels with Android dependencies are hard to test
- **UI State Leaks**: Storing mutable state in Composables, causing unintended recompositions
- **Direct Data Access**: UI directly accessing repositories/data sources, bypassing domain layer
- **Mixing UI and Business Logic**: Complex state transformations in Composable functions

### Domain Layer Anti-Patterns
- **Anemic Use Cases**: Creating use cases that simply pass through to repositories without adding value
- **Too Many Dependencies**: Use cases with too many injected dependencies, violating single responsibility
- **Android Dependencies**: Use cases referencing Android framework classes
- **Ignoring Threading**: Blocking the main thread with synchronous operations

### Data Layer Anti-Patterns
- **God Repositories**: Repositories handling too many data types or responsibilities
- **Exposing Mutable Types**: Repositories returning mutable lists or objects
- **Ignoring Error Handling**: Not propagating or handling errors properly
- **Inconsistent Threading**: Not specifying or enforcing threading policies

### Dependency Injection Anti-Patterns
- **Service Locator Pattern**: Using static globals to access dependencies
- **Hard-coded Dependencies**: Constructing dependencies directly instead of injecting them
- **Missing Scopes**: Not defining proper component scopes, leading to memory leaks
- **Circular Dependencies**: Creating dependency cycles that are hard to understand and maintain

## Migration Strategies

### Incremental UI Layer Migration
1. **Start with ViewModels**:
   - Convert existing ViewModels to follow UDF pattern
   - Inject dependencies rather than constructing them
   - Define clear UI state models and events

2. **Extract UI Logic**:
   - Move business logic from UI components to ViewModels
   - Extract complex UI logic to separate state holders
   - Use `@Composable` functions for UI rendering only

3. **Adopt Jetpack Compose**:
   - Start with isolated components
   - Gradually replace XML layouts with Compose
   - Create a consistent design system with Compose

### Rebuilding the Data Layer
1. **Define Clear Interfaces**:
   - Start with repository interfaces that define contracts
   - Model domain entities that represent core business objects
   - Design clear data source abstractions

2. **Implement with Modern Patterns**:
   - Use Kotlin Coroutines and Flow for asynchronous operations
   - Implement Room for local persistence
   - Use Ktor Client for network requests
   - Ensure repositories follow Single Source of Truth principle

3. **Connect Gradually**:
   - Connect new repositories to existing ViewModels one at a time
   - Add comprehensive tests for new implementations
   - Phase out old data sources as new ones become available

### Introducing the Domain Layer
1. **Identify Complex Logic**:
   - Identify business logic spread across UI and data layers
   - Look for logic used by multiple ViewModels
   - Find validation, formatting, or computation logic

2. **Extract Use Cases**:
   - Create focused use cases with single responsibilities
   - Ensure use cases are free of framework dependencies
   - Inject repositories as dependencies

3. **Update Callers**:
   - Modify ViewModels to use new use cases
   - Remove duplicated logic from data sources
   - Update tests to reflect new architecture


### Phase 2: Implementation
The implementation is done by the class.

### Phase 3: Connect to ViewModel
The ViewModel uses the new architecture.
}
```

## Monitoring and Validating Migration
- Use architecture linting rules to enforce patterns
- Establish comprehensive test coverage for new components
- Track technical debt metrics during migration
- Consider feature flags to gradually roll out architectural changes
- Use dependency visualization tools to ensure proper structure
</ARCHITECTURE ANTI-PATTERNS AND MIGRATION>

---

<KOTLIN COROUTINES AND FLOW GUIDELINES>
# Kotlin Coroutines and Flow in Android Architecture

## Architectural Principles for Asynchronous Code

### Threading Best Practices
- **Layer Responsibility**: Each layer is responsible for its own concurrency policy
- **Main-Safety**: All API functions should be safe to call from the main thread
- **Dispatcher Selection**: Use the appropriate dispatcher for the operation type:
  - `Dispatchers.IO` for network, disk operations
  - `Dispatchers.Default` for CPU-intensive work
  - `Dispatchers.Main` for UI operations
- **Dispatcher Injection**: Inject dispatchers to improve testability

### Structured Concurrency
- Use proper coroutine scopes tied to component lifecycles:
  - `viewModelScope` for ViewModels
  - `lifecycleScope` for UI components
  - Custom scopes for repositories and data sources
- Always ensure proper cancellation when components are destroyed
- Use child coroutines instead of launching independent jobs

## Flow Operators for Common Tasks

### Transformation
- `map`: Transform each emitted value
- `transform`: More flexible transformation
- `flatMapLatest`: Transform and flatten, canceling previous collections when new value arrives

### Filtering
- `filter`: Emit only values that satisfy predicate
- `filterNotNull`: Skip null values
- `distinctUntilChanged`: Only emit if different from previous value

### Combining
- `combine`: Combine multiple flows, emitting when any flow emits
- `zip`: Combine values from multiple flows, emitting when all have provided a value
- `merge`: Merge multiple flows into one

### Error Handling
- `catch`: Handle errors and possibly emit fallback values
- `retry`: Retry a flow on error
- `retryWhen`: Retry with more complex conditions

## StateFlow for UI State
- Use `StateFlow` to represent UI state
- Initialize with a default state value
- Only update state through `.update { }` or `.value =` on the mutable version
- Expose only the immutable `StateFlow` to consumers
- Collect in a lifecycle-aware manner in UI components

## SharedFlow for Events
- Use `SharedFlow` for one-time events
- Configure replay and buffer sizes based on needs
- Consider using a wrapper like `Event<T>` to handle one-time consumption

## Testing Coroutines
- Use `TestCoroutineDispatcher` or `StandardTestDispatcher` for testing
- Replace dispatchers with test versions in tests
- Use `runTest` to execute coroutines in a controlled test environment
- Test both success and failure cases
- Verify proper cancellation behavior
</KOTLIN COROUTINES AND FLOW GUIDELINES>

---

<STUB IMPLEMENTATION WORKFLOW>
Generic workflow for converting stub files to full implementations in the AgileLifeManagement Android project:

1. Identify the target functionality by searching for code and actual logic, not just by file name, to avoid duplicates and ensure correct targeting.
2. Review existing interfaces, contracts, data models, use cases, and business logic relevant to the stub.
3. Check for the existence and sufficiency of local/remote data sources, DAOs, entities, and mappers.
4. Design the implementation:
   - Use an offline-first, single source of truth (SSOT) approach (Room for local, Ktor/Supabase for remote).
   - Expose observable data as Flow, use suspend functions for CRUD, and Result<T> for error handling.
   - Map between domain and entity models as needed.
5. Implement the file:
   - Wire up local and remote data sources.
   - Update local first, then sync to remote in the background.
   - Expose all required methods from the domain interface.
   - Handle errors and logging appropriately.
6. Run and debug the build after each implementation with `./gradlew :app:compileDebugKotlin`.
7. After debugging, create a memory with insights and lessons learned from the process.

This workflow should be referenced for each stub file implementation to ensure consistency and completeness.
</STUB IMPLEMENTATION WORKFLOW>

---

<HILT DEPENDENCY INJECTION GUIDE>
# Dependency Injection with Hilt in Android Architecture

## Core Hilt Concepts
- Hilt is the recommended dependency injection library for Android
- Built on top of Dagger, providing simpler API and Android-specific features
- Automatically generates components for Android framework classes
- Handles Android component lifecycles (Activities, Fragments, etc.)
- Provides compile-time verification of dependency graph


## Scoping Dependencies
Hilt provides various component scopes to match Android lifecycles:

- `@Singleton`: Application lifetime
- `@ActivityRetainedScoped`: Survives configuration changes
- `@ActivityScoped`: Activity lifetime
- `@FragmentScoped`: Fragment lifetime
- `@ViewScoped`: View lifetime
- `@ServiceScoped`: Service lifetime

## Best Practices
- Keep modules focused on a specific layer or feature
- Use constructor injection when possible
- Leverage interface-based abstraction for testability
- Define clear scope boundaries
- Use qualifiers to disambiguate similar types
- Avoid injecting Android context directly; use application context
- Keep dependency graph simple and avoid circular dependencies
</HILT DEPENDENCY INJECTION GUIDE>
