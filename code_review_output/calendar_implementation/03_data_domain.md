# Calendar Data & Domain Model

## Entities
- `CalendarEventEntity` (Room): id, title, description, start/end, type, status, recurrence, externalId, lastModified
- `CalendarSyncStatusEntity`: eventId, source, syncState, lastSync, conflictState

## Repository Interfaces
```kotlin
interface CalendarRepository {
    suspend fun getEvents(start: LocalDate, end: LocalDate): List<CalendarEvent>
    suspend fun addEvent(event: CalendarEvent): Result<Unit>
    suspend fun updateEvent(event: CalendarEvent): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
    suspend fun syncWithExternalCalendars(): Result<Unit>
}
```

## Use Cases
- `GetCalendarEventsUseCase`
- `AddCalendarEventUseCase`
- `SyncCalendarUseCase`
- `ResolveConflictUseCase`

---

**See sync and integration files for external calendar details.**
