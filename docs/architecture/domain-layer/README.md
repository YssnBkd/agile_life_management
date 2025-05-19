# Domain Layer

## Overview

The domain layer is an optional layer that sits between the UI and data layers. It's responsible for encapsulating complex business logic or simple business logic that is reused by multiple ViewModels. This layer should only be used when needed—for example, to handle complexity or favor reusability.

## Benefits of the Domain Layer

Adding a domain layer provides several advantages:

1. **Avoids code duplication**: Reuse logic across multiple ViewModels
2. **Improves readability**: Encapsulates complex operations into well-named use cases
3. **Enhances testability**: Isolates business logic for easier testing
4. **Prevents bloated ViewModels**: Splits responsibilities to maintain clean architecture
5. **Separates business rules from UI concerns**: Makes business rules more explicit

## Use Case Design

Use cases should:
- Be **focused** on a single responsibility
- Have a **clear, descriptive name** that describes their function
- Be **stateless** and not contain mutable data
- **Not depend on Android framework** classes
- Be **easily testable** without complex setup

### Naming Conventions

Follow a consistent naming pattern:
- **Verb + Noun + UseCase**: GetTasksUseCase, UpdateTaskStatusUseCase, ValidatePasswordUseCase
- Names should clearly communicate what the use case does

## Implementation Patterns

### Using the Invoke Operator

Implement the invoke() operator to make use cases callable as functions. This pattern allows callers to use the instance directly as a function.

### Continuous Data Stream Pattern

For use cases that provide continuous data updates:
- Return a Flow to deliver a stream of values
- Consider wrapping domain model objects in a Result class to handle different states (loading, success, error)
- Use appropriate coroutine dispatchers
- Implement error handling to handle exceptions appropriately

### One-shot Operation Pattern

For use cases that perform a single operation and return a result:
- Use suspend functions to handle asynchronous operations
- Return a Result wrapper to handle success and error cases
- Use appropriate coroutine dispatchers
- Implement proper error handling and logging

### Result Pattern

Define a sealed Result class to represent different states:
- Success: Contains the actual data
- Error: Contains exception information
- Loading: Indicates an operation in progress

This pattern provides type-safe error handling and enables uniform error management throughout the app.

### Dependencies

Use cases typically depend on:
- Repositories (from the data layer)
- Other use cases (for more complex scenarios)
- Utility classes

Use dependency injection to provide these dependencies. With Hilt:

- Create modules to provide use cases
- Inject repositories and other dependencies into use cases
- Consider the appropriate component scope for your use cases

## Calling Use Cases in Kotlin

From a ViewModel, use cases are called like functions:

### Using Use Cases in ViewModels

- Inject use cases into ViewModels as constructor parameters
- Call use cases as functions (using the invoke operator)
- Handle the results using coroutines and Flow
- Update UI state based on use case results

## Error Handling in Use Cases

When executing use cases, properly handle errors:
- Use try-catch blocks for exceptions
- Utilize Result type for expected errors
- Report errors back to the UI layer for user feedback
- Consider retry mechanisms for transient failures

## Lifecycle of Use Cases

Use cases should be:

## Threading and Coroutines

Use cases should not manage their own threading:
- Leave threading decisions to the caller (typically the ViewModel)
- Design use cases to be "main-safe" - safe to call from the main thread
- For one-shot operations, use suspend functions
- For continuous data streams, return Flow
- Avoid blocking operations in use case implementations

### Main-Safe Pattern

Create use cases that can be safely called from any thread, including the main thread:
- For filtering/mapping operations, leverage Flow operators
- Avoid CPU-intensive operations without explicit threading
- Let callers determine the appropriate dispatcher

## Common Use Case Patterns

### Reusable Simple Business Logic

Encapsulate business logic that's used in multiple places:

### Business Logic Encapsulation

Use cases should encapsulate discrete business rules and validation logic:
- Create use cases for validation logic that's used in multiple places
- Return rich result objects that include validation errors
- Make business rules explicit in the domain layer
- Ensure validation logic is consistent throughout the app

For example, a password validation use case could check for minimum length, case requirements, special characters, etc., and return both validation status and specific errors.

### Combining Multiple Repositories

Coordinate operations between multiple repositories:

### Coordinating Multiple Repositories

Use cases can coordinate operations between multiple repositories:
- Orchestrate complex workflows involving different data sources
- Ensure operations happen in the correct sequence
- Handle cross-cutting concerns like authentication
- Manage transaction boundaries across repositories

For example, a data synchronization use case might coordinate between user, task, and project repositories to ensure all data is synced in the correct order.

## Concurrent Operations

Use cases can efficiently manage concurrent operations:
- Use coroutines for structured concurrency
- Launch parallel operations when possible
- Await results in an appropriate order
- Aggregate results from multiple sources
- Handle errors consistently across all operations

## Other Domain Layer Consumers

The domain layer can be used by components other than ViewModels:

- **Services**: Background services running operations
- **Workers**: WorkManager tasks 
- **Broadcast Receivers**: Handling system events
- **Other domain layers**: Complex domain layers may have hierarchies

### Integration with Background Processing

Use cases can be consumed by background components:
- Workers can use use cases for background data operations
- Services can leverage use cases for long-running processes
- Broadcast receivers can execute use cases in response to system events

This demonstrates the versatility of the domain layer - the same business logic can be used from different parts of the application.

## Conclusion

The domain layer serves as a powerful tool for organizing business logic in a clean, testable way. By properly designing use cases with single responsibilities, appropriate threading, and error handling, you can create a more maintainable and flexible application architecture.

Key benefits include:
- Reusable business logic across different parts of the app
- Clear separation of concerns between UI, business rules, and data access
- Enhanced testability through focused, isolated components
- Better organization of complex application requirements

The domain layer should be designed thoughtfully, using it where it adds value rather than as a mandatory layer for every operation.

## Data Layer Access Restriction

For strict separation of concerns, you can restrict direct access to the data layer:

```kotlin
// In a DI module
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // Make repository implementations internal
    @Binds
    @Singleton
    internal abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository
}

// Make use cases the only public API
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideTasksUseCases(
        taskRepository: TaskRepository
    ): TaskUseCases {
        return TaskUseCases(
            getTasks = GetTasksUseCase(taskRepository),
            updateTask = UpdateTaskUseCase(taskRepository),
            createTask = CreateTaskUseCase(taskRepository),
            deleteTask = DeleteTaskUseCase(taskRepository)
        )
    }
}

// Group use cases in a data class
data class TaskUseCases(
    val getTasks: GetTasksUseCase,
    val updateTask: UpdateTaskUseCase,
    val createTask: CreateTaskUseCase,
    val deleteTask: DeleteTaskUseCase
)
```

## Testing Use Cases

Testing use cases is straightforward since they're focused and have clear dependencies:

```kotlin
class GetTasksUseCaseTest {
    // Mock dependencies
    private val taskRepository = mockk<TaskRepository>()
    
    // System under test
    private lateinit var getTasksUseCase: GetTasksUseCase
    
    @Before
    fun setup() {
        getTasksUseCase = GetTasksUseCase(taskRepository)
    }
    
    @Test
    fun `invoke returns tasks from repository`() = runTest {
        // Given
        val tasks = listOf(
            Task("1", "Task 1", status = TaskStatus.TODO),
            Task("2", "Task 2", status = TaskStatus.IN_PROGRESS)
        )
        every { taskRepository.getTasks() } returns flowOf(tasks)
        
        // When
        val result = getTasksUseCase().first()
        
        // Then
        verify { taskRepository.getTasks() }
        assertEquals(tasks, result)
    }
}
```

## Resources

- [Domain Layer documentation by Android Developers](https://developer.android.com/jetpack/guide/domain-layer)
- [Kotlin Coroutines documentation](https://kotlinlang.org/docs/coroutines-overview.html)
- [Clean Architecture principles](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
