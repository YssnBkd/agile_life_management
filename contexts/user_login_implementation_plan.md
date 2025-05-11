# User Login Implementation Plan for Agile Life Management App

## Overview
This document details the plan for implementing user login (authentication only, no sign up) using Supabase Auth, fully integrated with the app’s architecture, code quality, and future-proofing strategies. The plan aligns with your custom `Result` class usage for error handling, as established in other repositories.

---

## 1. Requirements & Principles
- **Authentication Provider**: Supabase Auth (email/password only)
- **Architecture**: MVVM, repository pattern, modularized codebase
- **Error Handling**: Use custom `Result` class for all repository and data-source operations
- **Security**: Secure token/session storage (Encrypted DataStore), input validation, no sensitive info in logs
- **UI/UX**: Material 3, Inter font, dark/light theme, smooth transitions, accessibility
- **Offline-first**: Graceful handling of login state and errors when offline
- **Testing**: Unit/UI tests for all authentication flows
- **Future-proofing**: Decoupled authentication logic, easy provider swap/extension

---

## 2. High-Level Architecture

### a. Feature Module
- Create an `auth` feature module (if not present)
- Define clear API boundaries for the module

### b. Core Components
- **AuthRepository**: Interface for login/logout, user state, error mapping (returns custom `Result`)
- **AuthRemoteDataSource**: Handles Supabase API calls
- **AuthLocalDataSource**: Securely manages session/token in Encrypted DataStore
- **AuthViewModel**: Exposes login state and actions to UI
- **AuthState**: Sealed class for UI state (Loading, Success, Error, LoggedOut)

---

## 3. Implementation Steps

### 1. Data Layer

#### a. AuthRemoteDataSource
- Implement login (email/password) using Supabase Auth API
- Handle all Supabase exceptions, map to domain errors
- Return results using the custom `Result` class

#### b. AuthLocalDataSource
- Use Encrypted DataStore for storing session/token securely
- Methods for saving, retrieving, and clearing session
- All operations return `Result`

#### c. AuthRepositoryImpl
- Implements `AuthRepository`
- Coordinates between remote and local data sources
- Exposes: `login(email, password)`, `logout()`, `getCurrentUser()`, `isLoggedIn()`
- All methods return custom `Result`

### 2. Domain Layer
- Define `AuthRepository` interface
- Define models: `User`, `AuthResult`, `AuthError`
- Use custom `Result` for all public API methods

### 3. Presentation Layer

#### a. LoginScreen
- Compose-based UI (Material 3, Inter font, dark/light theme)
- Fields: Email, Password, Login button
- Show loading indicator, error messages, and success navigation
- No sign up/registration UI

#### b. AuthViewModel
- Exposes state: `authState: StateFlow<AuthState>`
- Handles login intent, input validation, error mapping
- Uses repository, all calls return custom `Result`

#### c. Navigation
- Update `AppNavHost`:
  - If not authenticated, show LoginScreen
  - After login, navigate to TaskScreen
  - On logout, navigate back to LoginScreen
- Ensure navigation is state-driven and safe

### 4. Global Error Handling
- Implement global error handler for authentication failures (network, invalid credentials, etc.)
- Use Snackbar or dialog for user-friendly messages
- Log errors for analytics/debugging

### 5. Security
- Use Encrypted DataStore for all sensitive data
- Never log credentials or tokens
- Validate all inputs (email format, password non-empty)
- Clear session on logout

### 6. Offline Handling
- Detect network state
- If offline, show appropriate message on login attempt
- Allow app usage in offline mode if session is valid

### 7. Testing
- Unit tests for AuthRepository, ViewModel, and data sources
- UI tests for LoginScreen (success, failure, offline)
- Mock Supabase responses for testing

### 8. CI/CD Integration

---

## 9. Implementation Status (as of 2025-05-10)

### Completed
- Auth feature module structure created
- Domain interfaces and models defined
- Data source/repository interfaces scaffolded
- Hilt DI module set up and imports fixed
- AuthViewModel implemented with state management
- LoginScreen composable implemented (Material 3, error/loading states)
- Navigation logic: login required for unauthenticated users, main content for authenticated
- Logout flow: settings screen, session clear, secure navigation to login (with bug-free popUpTo)
- Error handling and Result class integration

### Outstanding
- [ ] Implement actual Supabase API calls and Encrypted DataStore logic in data sources
- [ ] Persist authentication/session state across app restarts (Encrypted DataStore)
- [ ] Add tests for all flows (unit/UI)
- [ ] Polish UI/UX for accessibility and edge cases
- [ ] Document error codes and recovery flows

---

**Summary of recent progress:**
- Completed ViewModel, LoginScreen, navigation integration, and logout flow
- Fixed navigation and DI issues
- Error handling and state management are robust
- Next: persist session, connect real Supabase API, and expand test coverage

See codebase for latest details.
- Add authentication tests to CI pipeline
- Ensure static analysis covers new code
- Automate test runs on PRs

---

## 4. Integration Points
- **SyncManager**: On logout, clear all local user data and reset sync state
- **User Context**: After login, fetch user profile and cache locally
- **App Modularization**: Expose only necessary interfaces from auth module to others

---

## 5. Future-Proofing
- Keep AuthRepository interface open for new auth providers (OAuth, SSO, etc.)
- Modularize so auth logic can be swapped or extended
- ### 5.1. Create `AuthModule.kt` in `auth/di/` for providing dependencies using Hilt

- Use Hilt for all dependency injection in this project.
- Annotate provider functions with `@Provides` and use appropriate scopes (`@Singleton`, `@ViewModelScoped`, etc.).
- Do not introduce Koin or other DI frameworks to avoid fragmentation.
- All new modules and features must use Hilt for dependency injection.
- Use Hilt scopes as appropriate for your classes.

---

## 6. UI/UX Details
- Use Material 3 components and established color/typography system
- Animate transitions between login and main app
- Accessibility: content descriptions, error focus, keyboard navigation

---

## 7. Example File/Component List
- `auth/data/AuthRemoteDataSource.kt`
- `auth/data/AuthLocalDataSource.kt`
- `auth/domain/AuthRepository.kt`
- `auth/domain/model/User.kt`
- `auth/domain/model/AuthResult.kt`
- `auth/domain/model/AuthError.kt`
- `auth/ui/LoginScreen.kt`
- `auth/ui/AuthViewModel.kt`
- `auth/di/AuthModule.kt`
- Update `AppNavHost.kt` for navigation logic

---

## 8. Rollout Plan
1. Set up auth module and data sources
2. Implement repository and ViewModel
3. Build and test LoginScreen
4. Integrate with navigation and main app
5. Add tests and CI/CD steps
6. QA and review for security, error handling, and UX

---

## 9. Notes for Beginners
- The custom `Result` class is used everywhere to ensure consistent error handling and easy integration with the rest of your app.
- All repository and data source operations should return `Result<SuccessType, ErrorType>`.
- This approach improves maintainability, testability, and user experience.
