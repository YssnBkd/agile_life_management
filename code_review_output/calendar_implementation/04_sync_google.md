# Google Calendar Sync Implementation

## Overview
- Use Google Calendar REST API via Google Play Services Auth
- OAuth2 authentication, refresh tokens, background sync
- Map Agile Life events/tasks to Google events

## Steps
1. Request calendar permissions and authenticate user via Google Sign-In
2. Use Google Calendar API to fetch, add, update, delete events
3. Store Google event IDs in local entities for mapping
4. Implement bi-directional sync (local→Google, Google→local)
5. Handle conflicts using timestamps and sync status
6. Use WorkManager for background sync

## Libraries
- Google Play Services Auth
- Google Calendar API client
- WorkManager for background tasks

## Example: Adding an Event
```kotlin
val event = Event()
event.summary = "My Agile Life Task"
event.start = EventDateTime().setDateTime(startDateTime)
event.end = EventDateTime().setDateTime(endDateTime)
val createdEvent = service.events().insert("primary", event).execute()
```

## References
- https://developers.google.com/calendar/api/guides/create-events
- https://developers.google.com/identity/sign-in/android/start-integrating
