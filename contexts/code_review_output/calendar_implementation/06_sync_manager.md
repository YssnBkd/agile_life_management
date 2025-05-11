# Sync Manager, Conflict Resolution & Background Processing (2025 Best Practices)

## 1. SyncManager Extension & Background Processing
- **Extend SyncManager** to handle calendar events, leveraging your existing offline-first, local-first, and retry logic.
- **Track Sync State:** Use `CalendarSyncStatusEntity` with flags like `PENDING`, `IN_PROGRESS`, `CONFLICT`, `SYNCED`.
- **Timestamps:** Every entity should have a `lastModified` timestamp for accurate conflict detection.
- **WorkManager for Background Sync:**
  - Schedule sync jobs with WorkManager, using network constraints and exponential backoff.
  - Trigger sync on app launch, connectivity changes, significant local changes, and periodically.
  - Use foreground service with notification for user-initiated sync.

### Example: Scheduling Sync with WorkManager
```kotlin
val syncWorkRequest = OneTimeWorkRequestBuilder<CalendarSyncWorker>()
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .build()
WorkManager.getInstance(context).enqueue(syncWorkRequest)
```

## 2. Conflict Resolution (2025 Best Practices)
- **Automatic Detection:** Compare `lastModified` timestamps between local and remote.
- **Sync Flags:** Use flags to track sync status and detect conflicts.
- **Three-Way Merge:** For complex cases, compare local, remote, and last synced state; prompt user if both changed.
- **Automatic vs. Manual:**
  - Use last-write-wins for simple data.
  - For critical/conflicting data, mark as `CONFLICT` and show a UI for user resolution.
- **Audit Trail:** Log all changes and resolutions for debugging and rollback.

### Example: Conflict Detection Logic
```kotlin
if (localEvent.lastModified > remoteEvent.lastModified) {
    // Local is newer, push to remote
    syncRemote(localEvent)
} else if (remoteEvent.lastModified > localEvent.lastModified) {
    // Remote is newer, update local
    updateLocal(remoteEvent)
} else if (bothChangedSinceLastSync) {
    // Conflict detected
    markAsConflict(localEvent)
    // Prompt user for resolution
} else {
    // No conflict
}
```

## 3. Offline Support
- **Queue Changes:** Store unsynced changes locally with `isSynced = false`.
- **Background Worker:** Periodically check and sync unsynced changes when online.
- **Retry & Backoff:** Use exponential backoff for failed syncs.

## 4. Additional Recommendations
- **Batching:** Sync multiple changes together for efficiency.
- **Error Handling:** Catch exceptions (e.g., `HttpException`, 403, 404) and update sync status accordingly.
- **User Experience:** Minimize UI interruptions; prompt for conflict resolution only when necessary.
- **Security:** Encrypt all sensitive data, never log tokens or sensitive calendar info.

## 5. References & Further Reading
- [Android Data Sync Approaches: Offline-First, Remote-First & Hybrid Done Right (2025)](https://medium.com/@shivayogih25/android-data-sync-approaches-offline-first-remote-first-hybrid-done-right-c4d065920164)
- [Sample App: DataSyncApproaches (GitHub)](https://github.com/DigitalYogi1992/DataSyncApproaches.git)

---

**This approach aligns with the latest best practices and your existing offline-first, secure, and modular architecture.**
