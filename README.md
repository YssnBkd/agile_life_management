# AgileLifeManagement

## Secure Supabase Configuration

**Never commit your Supabase credentials or other secrets to version control!**

### How to Configure Supabase Credentials

1. **Add credentials to `local.properties` (never committed):**

   ```properties
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_KEY=your-supabase-service-key
   ```

2. **How it works:**
   - The build script reads `SUPABASE_URL` and `SUPABASE_KEY` from `local.properties` and injects them as `BuildConfig` fields.
   - The app uses these fields as defaults for secure runtime credential management.

3. **.gitignore already protects you:**
   - Both `/local.properties` and `local.properties` are in `.gitignore` by default. Your secrets are safe from accidental commits.

### Best Practices
- Never log or print your Supabase key or other secrets.
- Always use environment variables or `local.properties` for secrets, never hardcode them.
- Rotate your keys regularly and restrict their privileges.

---

## .gitignore (excerpt)

```
/local.properties
local.properties
```

These lines ensure your secrets are never tracked by git.

---

## References
- [Android local.properties documentation](https://developer.android.com/studio/build#properties-files)
- [Supabase Security Best Practices](https://supabase.com/docs/guides/platform/security)
