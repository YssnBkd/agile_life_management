# Recommendations for Future Screen Implementations

> **Purpose:**
> This document not only summarizes best practices for implementing new screens, but also provides a checklist to prevent common build errors—such as unresolved references, mismatched state, or improper ViewModel/UI connections—like those encountered during the Task screen refactor.

Follow these guidelines for consistent, robust, and maintainable UI across all features, and to ensure smooth, error-free builds.

---

## 1. **ViewModel Design & State Management**
- Use `@HiltViewModel` and inject dependencies via constructor (`@Inject`).
- Expose UI state as a `StateFlow` or `LiveData` using a sealed class (e.g., `Loading`, `Success`, `Error`).
- Handle all data operations (CRUD) through repository/use case layers—never directly in the UI.
- Use coroutines and `viewModelScope` for async operations.

## 2. **UI Layer (Composable Screens)**
- Observe ViewModel state (`collectAsState()` for `StateFlow`).
- Show appropriate UI for each state:
  - **Loading:** Show a progress indicator.
  - **Error:** Show a clear, user-friendly error message.
  - **Success:** Render data (lists, details, etc.).
- Avoid direct access to domain models; always go through ViewModel state.
- Use stateless, reusable components as much as possible.

## 3. **Dialogs, Forms, and Interactions**
- Use dialogs for creation/editing; support validation and error feedback.
- Provide user feedback (snackbar, toast, etc.) on success/error.
- Use Compose best practices for forms (state hoisting, validation, accessibility).

## 4. **Navigation**
- Use a centralized navigation graph (NavHost) and pass navigation lambdas to screens.
- Support deep linking and back stack management.

## 5. **Accessibility & Responsiveness**
- Ensure all interactive elements have `contentDescription` and meet minimum touch targets.
- Test with large font sizes and different device sizes.
- Use adaptive layouts and semantic modifiers for accessibility.

## 6. **Performance & Error Handling**
- Minimize recomposition by using keys in lists and splitting large composables.
- Use sealed classes or `Result` for robust error handling and recovery.
- Log errors for debugging and analytics.

## 7. **Testing**
- Add Compose UI tests for all user flows and edge cases.
- Add ViewModel unit tests for state and error handling.

---

## Example: Screen Composable Structure
```kotlin
@Composable
fun ExampleScreen(viewModel: ExampleViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    when (uiState) {
        is ExampleViewModel.UiState.Loading -> { /* Show loading */ }
        is ExampleViewModel.UiState.Error -> { /* Show error */ }
        is ExampleViewModel.UiState.Success -> { /* Show data */ }
    }
}
```

---

## **Build Error Prevention Checklist**
Use this checklist before and during implementation of any new screen:

- [ ] **ViewModel:** Annotated with `@HiltViewModel` and uses `@Inject constructor` for dependencies.
- [ ] **State Exposure:** Exposes UI state as `StateFlow` or `LiveData` using a sealed class (not raw lists or primitives).
- [ ] **Repository Integration:** All data comes from repositories/use cases, not in-memory lists.
- [ ] **UI Observation:** UI observes ViewModel state (`collectAsState()` or equivalent), never accesses domain models directly.
- [ ] **Field Access:** All property accesses (e.g., `task.title`) are inside a valid state branch (e.g., `Success`).
- [ ] **Dialog/Form State:** Dialogs/forms use ViewModel methods for actions, not local mutations.
- [ ] **Error Handling:** All async operations are wrapped in try/catch and update error state.
- [ ] **No Unresolved References:** Remove all direct references to deprecated or removed properties after refactor (e.g., `viewModel.tasks`).
- [ ] **Testing:** Add basic Compose UI and ViewModel unit tests to catch regressions.
- [ ] **Accessibility:** All interactive elements have content descriptions and proper semantics.

---

## Summary Table
| Area                   | Recommendation                                             |
|------------------------|-----------------------------------------------------------|
| ViewModel              | Hilt, sealed state, repository integration                |
| UI State               | StateFlow, sealed classes, observe in Composables         |
| Error Handling         | Robust, user-friendly, recoverable                        |
| Accessibility          | Always test and annotate                                  |
| Navigation             | NavHost, lambdas, deep linking                            |
| Testing                | Compose UI tests, ViewModel unit tests                    |

---

**Follow these recommendations and the checklist for all future screen implementations to ensure consistency, maintainability, and a great user experience—while avoiding common build errors.**
