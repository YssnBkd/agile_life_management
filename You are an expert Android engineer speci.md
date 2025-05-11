You are an expert Android engineer specializing in Kotlin development, modern architecture, and UI/UX implementation. I need you to:

1. Perform a thorough code review of my Android app project
2. Create a detailed implementation plan for a UI/UX design

## Part 1: Code Review Instructions

Analyze my entire codebase with particular attention to:

### Architecture Analysis
- Identify the architectural pattern(s) used (MVVM, Clean Architecture, etc.)
- Evaluate separation of concerns and component responsibilities
- Assess dependency injection implementation
- Review navigation patterns and flow control

### Code Quality Assessment
- Highlight use of Kotlin idioms, extensions, and language features
- Identify code smells, anti-patterns, and potential bugs
- Evaluate error handling strategies and edge case management
- Assess concurrency approaches and thread management
- Review resource utilization (layouts, strings, dimensions, etc.)

### Performance and Optimization
- Identify potential performance bottlenecks
- Evaluate memory management practices
- Assess startup time optimization techniques
- Review UI rendering performance considerations

### Integration and Data Management
- Evaluate API integration patterns
- Review data persistence solutions (Room, SharedPreferences, etc.)
- Assess state management approaches
- Evaluate caching strategies

### Security Review
- Identify potential security vulnerabilities
- Review credential and sensitive data handling
- Assess input validation and sanitization practices

### Testing Coverage
- Evaluate unit, integration, and UI test implementation
- Identify testing gaps and recommend improvements
- Review test quality and maintainability

### Future-Proofness Assessment
- Evaluate scalability of the architecture
- Assess maintainability and readability
- Review dependency management approach
- Analyze compatibility with latest Android versions and API changes
- Assess alignment with Google's recommended architecture components

## Part 2: UI/UX Implementation Plan

After reviewing the codebase, provide a detailed UI/UX implementation plan. Focus on:

### Visual Design System
- Color palette, typography, and spacing system
- Component styling (buttons, cards, inputs, etc.)
- Animation and transition specifications
- Iconography recommendations

### Component Architecture
- Reusable UI components structure
- State management for UI components
- Responsive layout implementation strategy
- Accessibility considerations

### Screen Implementations
- Breakdown of key screens infered from our use cases
- Navigation patterns and information architecture
- Data visualization components
- Interaction patterns and feedback mechanisms

### Technical Implementation
- Recommended libraries and frameworks
- Implementation approach (Jetpack Compose vs XML layouts)
- Performance optimization strategies
- Testing approach for UI components

Provide specific examples and code snippets where appropriate to illustrate recommended patterns and approaches. Be thorough but practical in your analysis.

For each major recommendation, explain both the rationale and implementation strategy.

Create a folder where you'll write your answers in structured set of markdown files.
Also write a set of markdown documents that will be used as useful context for our future prompts.



Scaffold the directory structure and navigation.
Implement atomic components with previews.
Build the Tasks screen and its supporting components as the first feature (core user flow).
Connect UI to ViewModels and repositories with proper state/error handling.
Add accessibility and performance optimizations from the start.
Set up UI tests for the Tasks flow.
Would you like me to start by scaffolding the directory structure and navigation, or begin with atomic components?