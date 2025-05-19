# Architecture Best Practices

## Overview

This document covers architectural best practices for Android application development, focusing on creating maintainable, testable, and scalable applications. These practices align with Google's recommended app architecture and industry standards.

## General Architecture Principles

### Separation of Concerns

Each component in your application should have a single responsibility:

- **UI components** (Activities, Fragments) should only handle UI and OS interactions
- **ViewModels** should prepare and manage data for the UI
- **Use cases** should encapsulate business logic
- **Repositories** should abstract data operations
- **Data sources** should interface with specific data providers (network, database)

### Drive UI from Data Models

Always design your UI to be driven by observable data models:

- Data models should be independent from UI elements
- They should survive configuration changes and process death
- Use persistent models when possible to handle offline scenarios
- UI should reflect the current state of the data

### Single Source of Truth (SSOT)

Each type of data should have exactly one authoritative source:

- Only the SSOT can modify that data type
- Data should be exposed using immutable types
- Changes to data should be centralized
- In offline-first apps, the local database typically serves as the SSOT

### Unidirectional Data Flow (UDF)

Implement a unidirectional flow of data and events:

- Data flows down from data sources → repositories → (use cases) → ViewModels → UI
- Events flow up from UI → ViewModels → (use cases) → repositories → data sources
- This pattern ensures data consistency and simplifies debugging

## UI Layer Best Practices

### ViewModels

- Keep ViewModels free of Android framework dependencies
- Use `SavedStateHandle` to preserve state during process death
- Expose UI state as immutable objects using `StateFlow` or `LiveData`
- Handle UI events through well-defined functions
- Use `viewModelScope` for coroutines to respect the ViewModel lifecycle

### UI State Management

- Define UI state as immutable data classes
- Include all data needed for the UI to render
- Handle loading, success, and error states
- Use sealed classes for modeling complex UI events

### Jetpack Compose

- Follow Unidirectional Data Flow with Compose
- Use `by remember` and `by rememberSaveable` for state
- Pass only the necessary data to composables
- Define "smart" (connected to ViewModels) and "dumb" composables (pure UI)
- Use state hoisting to lift state to appropriate levels

### Navigation

- Use the Navigation Component for navigation between screens
- Pass only necessary data as arguments between destinations
- Handle deep links in the navigation graph
- Keep navigation logic in the UI layer, not in ViewModels

## Domain Layer Best Practices

### Use Case Design

- Create focused use cases with single responsibilities
- Name use cases using verb-noun convention (`GetTasksUseCase`, `UpdateTaskStatusUseCase`)
- Make use cases stateless
- Avoid Android dependencies in the domain layer
- Design use cases to be easily testable

### Using the Domain Layer Effectively

- Only add a domain layer when you need it
- Group related use cases in containers for easier consumption
- Reuse use cases across ViewModels
- Don't leak data layer abstractions through the domain layer


## Data Layer Best Practices

### Repository Pattern

- Create repositories for each major data type
- Repositories should abstract the details of how data is stored or retrieved
- Use repositories as the Single Source of Truth for their respective data
- Expose data through observable streams (Flow, LiveData)
- Handle business logic within repositories

### Data Sources

- Separate local and remote data sources
- Each data source should work with only one source of data
- Keep data sources focused on raw data operations
- Handle source-specific error cases at this level

### Offline-First Architecture

- Use the local database as the Single Source of Truth
- Update local data first, then sync with remote in the background
- Implement proper conflict resolution strategies
- Handle synchronization failures gracefully

### Error Handling

- Use Result type for one-shot operations to clearly communicate success/failure
- Define domain-specific exceptions
- Handle expected errors at the appropriate layer
- Log unexpected errors for debugging
- Provide meaningful error messages to users


## Threading and Concurrency

### Coroutines and Flow

- Use coroutines for asynchronous operations
- Use Flow for observable data streams
- Make repository methods main-safe
- Use appropriate dispatchers for different types of work
- Ensure proper cancellation when components are destroyed

### Structured Concurrency

Follow structured concurrency principles:
- Use appropriate coroutine scopes
  - `viewModelScope` for ViewModels
  - `lifecycleScope` for UI components
  - Custom scopes for repositories
- Use child coroutines instead of launching independent jobs
- Handle cancellation properly

## Dependency Injection

### Hilt Best Practices

- Use constructor injection when possible
- Define clear modules for each layer
- Use appropriate component scopes
- Use qualifiers to disambiguate similar types
- Keep the dependency graph simple and acyclic

## Performance Considerations

### Database Performance

- Use Room's paging support for large datasets
- Design efficient queries with proper indices
- Use transactions for related operations
- Consider using in-memory caching for frequently accessed data

### Memory Management

- Avoid memory leaks by respecting lifecycle boundaries
- Use weak references when appropriate
- Dispose of resources in lifecycle-aware components
- Be cautious with global state

## Common Anti-Patterns to Avoid

### UI Layer Anti-Patterns

- ❌ **Bloated Activities/Fragments**: UI classes handling business logic
- ❌ **Direct Android API calls in ViewModels**: Makes testing difficult
- ❌ **UI State Leaks**: Storing mutable state in composables
- ❌ **Direct Data Access**: UI directly accessing repositories, bypassing domain layer

### Domain Layer Anti-Patterns

- ❌ **Anemic Use Cases**: Pass-through use cases that don't add value
- ❌ **Too Many Dependencies**: Use cases with excessive injected dependencies
- ❌ **Android Dependencies**: Use cases referencing Android framework classes

### Data Layer Anti-Patterns

- ❌ **God Repositories**: Repositories handling too many data types
- ❌ **Exposing Mutable Types**: Returning mutable collections
- ❌ **Ignoring Error Handling**: Not propagating errors appropriately
- ❌ **Inconsistent Threading**: Not specifying threading policies

### Dependency Injection Anti-Patterns

- ❌ **Service Locator Pattern**: Using static globals to access dependencies
- ❌ **Hard-coded Dependencies**: Constructing dependencies directly
- ❌ **Missing Scopes**: Not defining proper component scopes
- ❌ **Circular Dependencies**: Creating dependency cycles

## Migration Strategies

### Incremental Architecture Migration

For existing apps that need architectural improvements:

1. **Start with clear interfaces**: Define the contracts between layers
2. **Implement the repository layer first**: Establish your SSOT
3. **Introduce ViewModels**: Move logic out of UI components
4. **Add Use Cases when needed**: Extract complex or reused business logic
5. **Refactor one feature at a time**: Don't try to rebuild everything at once

## Resources

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Guide to App Architecture](https://developer.android.com/jetpack/guide)
- [Architecture Components samples](https://github.com/android/architecture-components-samples)
- [Now in Android app](https://github.com/android/nowinandroid) - Reference architecture
