# Retained location preference implementation

Status: implemented
Date: 2026-08-22

## Goal

Remember that a user chose device-based restaurant discovery so returning to Nearby can resolve their current area automatically. Do not persist latitude, longitude, or a location history.

Android retains the foreground permission decision. Recipe Compose now stores the user's device-location intent in Preferences DataStore, allowing the choice to survive route recreation and process death without persisting coordinates.

## Implemented behavior

1. The first Nearby visit continues to show the current location/manual location choice.
2. After **Use my location** succeeds, persist a `CurrentLocation` preference in Preferences DataStore.
3. On a later Nearby visit:
   - if the stored preference is `CurrentLocation` and permission is still granted, resolve a recent or fresh coordinate automatically;
   - if permission was revoked, show the existing permission/manual fallback without automatically opening a system prompt;
   - if location resolution times out, show the existing unavailable/manual fallback.
4. A manual search changes the preference back to `AskEveryTime`; the entered city or ZIP code is not persisted.
5. A visible **Choose another location** action clears the device-location behavior.

## Model

```kotlin
enum class LocationPreference {
    AskEveryTime,
    CurrentLocation,
}

interface LocationPreferenceStore {
    val preference: Flow<LocationPreference>
    suspend fun setPreference(preference: LocationPreference)
}
```

The interface and model remain outside Android-specific code so they can move to a future shared module. `DataStoreLocationPreferenceStore` is the current Android implementation; Compose Multiplatform can later provide a platform-backed settings adapter.

## State and UX details

- An initial `RestoringPreference` state prevents the choice screen from flashing before DataStore emits.
- Persist `CurrentLocation` only after location permission and coordinate resolution succeed.
- Manual searches reset the preference without retaining the entered location.
- Never persist a coordinate from Google Play Services.
- Continue accepting approximate location and using balanced-power accuracy.
- Keep the existing five-minute location freshness policy, 10-second provider timeout, and 12-second ViewModel timeout unless measurement shows they need adjustment.

## Verification

- Unit coverage verifies the initial restoration state, `AskEveryTime`, restored current-location behavior, revoked permission, current-location persistence, manual override, cancellation, and timeout paths.
- Physical-device verification covers first choice, retained choice after a cold launch, approximate-only permission, permission revocation fallback, and **Choose another location**.
- No coordinate is written to DataStore, Room, Firebase, saved instance state, or analytics.
