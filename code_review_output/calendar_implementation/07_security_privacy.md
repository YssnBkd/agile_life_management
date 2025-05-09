# Security & Privacy for Calendar Integration

## Token & Credential Storage
- Store OAuth2 tokens (Google) and sensitive data in encrypted DataStore
- Never log tokens or sensitive calendar data
- Allow users to disconnect calendar integrations at any time

## Permissions & Consent
- Always request explicit user consent for calendar access
- Show clear rationale and allow opt-in/opt-out
- Respect all platform privacy requirements (Google/Apple)

## Data Handling
- Only sync user-authorized calendars
- Never expose calendar data to third parties
- Audit all logs for sensitive data

## Security Testing
- Add unit/integration tests for token handling, permissions, and data flows
- Use static analysis to check for leaks or insecure storage

## References
- https://developer.android.com/training/articles/keystore
- https://developer.apple.com/documentation/security
