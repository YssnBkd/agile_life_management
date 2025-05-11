# Calendar Feature Architecture

## Layered Structure
- **UI Layer:** Compose-based calendar views, event dialogs, sync controls
- **Domain Layer:** Use cases for event CRUD, sync, conflict resolution
- **Data Layer:**
  - Local: Room entities for events, sync state
  - Remote: Google Calendar API, Apple EventKit, Supabase
- **SyncManager:** Extended to handle calendar sync and conflict resolution

## Modularity
- All calendar logic in a dedicated module or package (e.g., `feature-calendar` or `ui/screens/calendar`)
- External integrations (Google/Apple) abstracted behind interfaces

## Class Diagram Overview
- `CalendarEventEntity`, `CalendarSyncStatusEntity` (Room)
- `CalendarRepository` (interface)
- `LocalCalendarRepositoryImpl`, `GoogleCalendarRepositoryImpl`, `AppleCalendarRepositoryImpl`
- Use cases: `GetCalendarEventsUseCase`, `AddCalendarEventUseCase`, `SyncCalendarUseCase`, `ResolveConflictUseCase`

---

**See other files for detailed implementation, sync, and security plans.**
