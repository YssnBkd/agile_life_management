# Apple Calendar Sync Implementation (iOS)

## Overview
- Use EventKit framework for calendar access and sync
- Request calendar permissions and user consent
- Map Agile Life events/tasks to EKEvent

## Steps
1. Request `NSCalendarsUsageDescription` permission
2. Use EventKit to fetch, add, update, delete events
3. Store EKEvent identifiers in local entities for mapping
4. Implement bi-directional sync (local↔️Apple)
5. Handle conflicts and sync status
6. Use background tasks for sync

## Example: Adding an Event (Swift)
```swift
let event = EKEvent(eventStore: eventStore)
event.title = "Agile Life Task"
event.startDate = ...
event.endDate = ...
event.calendar = eventStore.defaultCalendarForNewEvents
try eventStore.save(event, span: .thisEvent)
```

## References
- https://developer.apple.com/documentation/eventkit/ekeventstore
- https://developer.apple.com/documentation/eventkit
