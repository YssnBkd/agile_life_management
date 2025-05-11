# Testing & CI for Calendar Feature

## Unit Testing
- Mock Google/Apple APIs for repository and use case tests
- Test all sync flows, including conflict and offline scenarios

## Integration Testing
- Test end-to-end sync between local, Google, and Apple calendars
- Test permission flows and error handling

## UI Testing
- Compose UI tests for calendar views, dialogs, and sync status indicators

## CI/CD Integration
- Add all tests to CI pipeline (e.g., GitHub Actions)
- Run static analysis (Detekt, ktlint) and dependency checks
- Audit for permissions and security leaks

## Example: Mocking Google Calendar API (Kotlin)
```kotlin
val mockService = mock(GoogleCalendarService::class.java)
whenever(mockService.getEvents(...)).thenReturn(listOf(...))
```

## References
- https://developer.android.com/training/testing
- https://github.com/mockk/mockk
