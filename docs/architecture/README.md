# Android Architecture Guide

This document provides a comprehensive overview of the recommended architecture for Android applications, following the official Android Architecture Guidelines.

## Overview

Modern Android architecture is built on the following key components and principles:

- **Layered Architecture**: Clear separation between UI, domain, and data layers
- **Unidirectional Data Flow (UDF)**: State flows down, events flow up
- **Reactive Programming**: Using Kotlin Coroutines and Flow
- **Dependency Injection**: Using Hilt for Android
- **Single Source of Truth (SSOT)**: Clear ownership of data
- **Testability**: Architecture designed for comprehensive testing

## Core Architectural Principles

### Separation of Concerns

Each component in your app should have a single responsibility. UI-based classes should only contain UI logic, while business logic should be kept separate. This approach:

- Avoids bloated UI components
- Improves testability
- Handles lifecycle concerns more effectively
- Reduces bugs and makes maintenance easier

### Drive UI from Data Models

Your UI should be driven by persistent data models that:
- Survive configuration changes and process death
- Continue to work when network connectivity is poor
- Provide a consistent user experience

### Single Source of Truth (SSOT)

Each data type should have exactly one authoritative source:
- Only the SSOT can modify and mutate data
- Data is exposed as immutable types to consumers
- Changes are centralized for easier debugging
- In offline-first apps, the database typically serves as the SSOT

### Unidirectional Data Flow (UDF)

Data should flow in one direction while events flow in the opposite direction:
- State flows down from higher-scoped components to lower-scoped ones
- Events flow up from lower-scoped components to the SSOT
- This pattern guarantees data consistency and simplifies debugging

## Recommended Layer Structure

The recommended architecture consists of the following layers:

### [UI Layer](ui-layer/README.md)

Responsible for displaying application data on screen and handling user interactions:
- UI elements (Views or Jetpack Compose)
- State holders (ViewModels)
- UI models and states

### [Domain Layer](domain-layer/README.md)

An optional layer responsible for encapsulating complex business logic or reusable logic:
- Use cases (or interactors)
- Domain models
- Business rules

### [Data Layer](data-layer/README.md)

Contains business logic and data operations:
- Repositories (Single Source of Truth)
- Data sources (local and remote)
- Data models and entities

## Managing Dependencies

Dependencies between components are managed through:
- [Dependency Injection](dependency-injection/README.md) with Hilt
- Repository pattern
- Interface-based abstractions

## Additional Topics

- [Best Practices](best-practices/README.md)
- [Testing Strategies](testing/README.md)

## Reference Architecture

![Android Architecture Diagram](https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview.png)

This layered architecture follows an offline-first approach with the local database as the Single Source of Truth. The UI observes data changes and updates automatically through reactive streams.
