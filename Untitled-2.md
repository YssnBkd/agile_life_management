auth/data/AuthRemoteDataSourceImpl.kt:114:1 Expecting a top level declaration
auth/domain/AuthRepositoryImpl.kt:55:9 Expecting member declaration
auth/domain/AuthRepositoryImpl.kt:55:16 Expecting member declaration
auth/domain/AuthRepositoryImpl.kt:55:21 Expecting member declaration
auth/domain/AuthRepositoryImpl.kt:55:22 Expecting member declaration
auth/domain/AuthRepositoryImpl.kt:55:34 Expecting member declaration
auth/domain/AuthRepositoryImpl.kt:55:36 Expecting member declaration
auth/domain/AuthRepositoryImpl.kt:56:13 Expecting an element
auth/domain/AuthRepositoryImpl.kt:56:31 Unexpected tokens (use ';' to separate expressions on the same line)
auth/domain/AuthRepositoryImpl.kt:66:13 Expecting an element
auth/domain/AuthRepositoryImpl.kt:66:29 Unexpected tokens (use ';' to separate expressions on the same line)
auth/domain/AuthRepositoryImpl.kt:67:13 Expecting an element
auth/domain/AuthRepositoryImpl.kt:67:31 Unexpected tokens (use ';' to separate expressions on the same line)
auth/domain/AuthRepositoryImpl.kt:100:1 Expecting a top level declaration











////
The errors are resolved !
When I run gradle build, I get the following error from 



Resolve this issue and think step-by-step. Always maintain a "best-practice mindset"
////
I re-run the gradle build, now I get the following errors from the same file:



Resolve this issue and think step-by-step. Always maintain a "best-practice mindset" and be consistent with previous decisions

///

You are an expert Android/Kotlin coding assistant with full read‑write access to my project’s codebase and build environment.

/data/repository/TagRepositoryImpl.kt:58:47 Return type of 'insertTag' is not a subtype of the return type of the overridden member 'suspend fun insertTag(tag: Tag): Result<String>' defined in 'com/example/agilelifemanagement/domain/repository/TagRepository'.
/data/repository/TagRepositoryImpl.kt:77:47 Return type of 'updateTag' is not a subtype of the return type of the overridden member 'suspend fun updateTag(tag: Tag): Result<Unit>' defined in 'com/example/agilelifemanagement/domain/repository/TagRepository'.
/data/repository/TagRepositoryImpl.kt:99:49 Return type of 'deleteTag' is not a subtype of the return type of the overridden member 'suspend fun deleteTag(id: String): Result<Unit>' defined in 'com/example/agilelifemanagement/domain/repository/TagRepository'.
/di/RepositoryModule.kt:38:16 Unresolved reference 'TaskRepositoryImpl'.
/domain/usecase/task/CreateTaskUseCase.kt:85:23 One type argument expected. Use class 'Success' if you don't intend to pass type arguments.
/domain/usecase/task/CreateTaskUseCase.kt:87:52 Unresolved reference 'data'.
/domain/usecase/task/CreateTaskUseCase.kt:90:16 Return type mismatch: expected 'com.example.agilelifemanagement.domain.model.Result<kotlin.String>', actual 'kotlin.Result<kotlin.String>'.

"
   - The problematic file lives at: `<PATH/TO/repository/MyRepository.kt>` in the repository layer.
   - The repository layer handles data mapping between domain models and remote/local data sources.

2. TASKS
   a. Analyze the build logs to identify the root cause(s) of the failure.
   b. Open and inspect `TaskRepositoryImpl.kt` and any related interfaces or data classes.
   c. Diagnose whether the failure stems from:
      - Kotlin syntax or type‑mismatch issues
      - Missing or misconfigured Gradle dependencies
      - Incompatible plugin versions
      - Annotation‑processor / kapt misconfiguration
      - Other repository‑layer coding mistakes
   d. For each root cause, propose one or more robust, reliable fixes that:
      - Adhere to Kotlin and Android best practices
      - Are forward‑compatible with the current AGP and Kotlin versions
      - Don’t introduce regressions elsewhere
   e. Generate concrete code diffs / patch files for each fix, using unified diff format.
   f. Update or add unit/integration tests in `app/src/test/java/...` (and/or `androidTest`) to verify the repository behavior and prevent future breakage.
   g. Verify locally that after applying your patch and running `./gradlew clean build`, the build succeeds and tests pass.

Begin by summarizing your understanding of the failure, then proceed through the steps. Make sure every suggestion is actionable and self‑contained so I can apply it immediately.
--
fix the next DI module (AppModule.kt), focusing on the SyncManagerImpl error and ensuring all app-wide singletons are properly provided.
--
You have updated all queries in your GoalSprintCrossRefDao to use camelCase field names (goalId, sprintId)
---



You are an expert Android/Kotlin coding assistant with full read‑write access to my project’s codebase and build environment.

Run a gradle clean build terminal command and assess my project status. If any error comes up, resolve it by thinking step-by-step

You are an expert Android engineer specializing in Kotlin development, modern architecture, and UI/UX implementation. I need you to:

Perform a thorough code review of my Android app project
Create a detailed implementation plan for a ClickUp-inspired UI/UX design

Part 1: Code Review Instructions
Analyze my entire codebase with particular attention to:
Architecture Analysis

Identify the architectural pattern(s) used (MVVM, Clean Architecture, etc.)
Evaluate separation of concerns and component responsibilities
Assess dependency injection implementation
Review navigation patterns and flow control

Code Quality Assessment

Highlight use of Kotlin idioms, extensions, and language features
Identify code smells, anti-patterns, and potential bugs
Evaluate error handling strategies and edge case management
Assess concurrency approaches and thread management
Review resource utilization (layouts, strings, dimensions, etc.)

Performance and Optimization

Identify potential performance bottlenecks
Evaluate memory management practices
Assess startup time optimization techniques
Review UI rendering performance considerations

Integration and Data Management

Evaluate API integration patterns
Review data persistence solutions (Room, SharedPreferences, etc.)
Assess state management approaches
Evaluate caching strategies

Security Review

Identify potential security vulnerabilities
Review credential and sensitive data handling
Assess input validation and sanitization practices

Testing Coverage

Evaluate unit, integration, and UI test implementation
Identify testing gaps and recommend improvements
Review test quality and maintainability

Future-Proofness Assessment

Evaluate scalability of the architecture
Assess maintainability and readability
Review dependency management approach
Analyze compatibility with latest Android versions and API changes
Assess alignment with Google's recommended architecture components

Part 2: UI/UX Implementation Plan
After reviewing the codebase, provide a detailed UI/UX implementation plan inspired. Focus on:
Visual Design System

Color palette, typography, and spacing system
Component styling (buttons, cards, inputs, etc.)
Animation and transition specifications
Iconography recommendations

Component Architecture

Reusable UI components structure
State management for UI components
Responsive layout implementation strategy
Accessibility considerations

Screen Implementations

Breakdown of key screens inspired by ClickUp's interface
Navigation patterns and information architecture
Data visualization components
Interaction patterns and feedback mechanisms

Technical Implementation

Recommended libraries and frameworks
Implementation approach (Jetpack Compose vs XML layouts)
Performance optimization strategies
Testing approach for UI components
---
My goal is to be able to open my app on Android Studio phone simulator and see create tasks as soon as possible.

You are an expert Android/Kotlin coding assistant with full read‑write access to my project’s codebase and build environment. Determine the next best course of action given the files given and recommend next steps at the end of each output
