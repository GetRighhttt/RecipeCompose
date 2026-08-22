# Retained location preference plan

Status: planned next feature  
Date: 2026-08-22

## Goal

Remember that a user chose device-based restaurant discovery so returning to Nearby can resolve their current area automatically. Do not persist latitude, longitude, or a location history.

Android already retains the foreground permission decision. Recipe Compose currently keeps the chosen search origin only in the route-scoped `YelpViewModel`, so it survives recomposition and ordinary tab restoration but is lost when that state holder or app process is destroyed.

## Recommended behavior

1. The first Nearby visit continues to show the current location/manual location choice.
2. After **Use my location** succeeds, persist a `CurrentLocation` preference in Preferences DataStore.
3. On a later Nearby visit:
   - if the stored preference is `CurrentLocation` and permission is still granted, resolve a recent or fresh coordinate automatically;
   - if permission was revoked, show the existing permission/manual fallback without automatically opening a system prompt;
   - if location resolution times out, show the existing unavailable/manual fallback.
4. After a successful manual search, optionally persist `ManualLocation(value)` so the same city or ZIP code can be restored.
5. Provide a visible **Choose another location** action that clears or replaces the preference.

## Suggested model

```kotlin
sealed interface LocationPreference {
    data object AskEveryTime : LocationPreference
    data object CurrentLocation : LocationPreference
    data class ManualLocation(val value: String) : LocationPreference
}

interface LocationPreferenceStore {
    val preference: Flow<LocationPreference>
    suspend fun set(preference: LocationPreference)
}
```

Keep the interface outside Android-specific code so it can move to the future shared module. The Android implementation can use Preferences DataStore; a Compose Multiplatform implementation can later use a platform-backed settings adapter.

## State and UX details

- Add an initial `RestoringPreference` state so the choice screen does not flash before DataStore emits.
- Persist `CurrentLocation` only after location permission and coordinate resolution succeed.
- Persist a manual value only after a valid Yelp request completes; trim it before storage.
- Never persist a coordinate from Google Play Services.
- Continue accepting approximate location and using balanced-power accuracy.
- Keep the existing five-minute location freshness policy, 10-second provider timeout, and 12-second ViewModel timeout unless measurement shows they need adjustment.

## Verification

- First visit still requires an explicit choice.
- Returning to Nearby in the same process reuses the active state.
- Returning after process death restores the saved preference.
- Revoking permission returns to the permission/manual fallback without crashing or prompting automatically.
- A failed location lookup does not loop or remain on a spinner.
- Choosing another location clears the previous behavior.
- No coordinate appears in DataStore, logs, Room, Firebase, saved instance state, or analytics.

