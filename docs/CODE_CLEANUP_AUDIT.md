# Code cleanup audit

Date: 2026-08-22  
Scope: correctness, naming, state ownership, build configuration, resources, and physical-device smoke testing

## Correctness changes

- Corrected authentication routing so **Sign in** signs in an existing user and **Create account** is an explicit separate action.
- Added accurate authentication success/failure copy and keyboard submit behavior.
- Made recipe and database UI collections non-null, eliminating defensive nullable-list handling throughout Compose screens.
- Switched root navigation state collection to lifecycle-aware collection.
- Prevented duplicate saved dishes with a transactional remote-meal-ID check in the Room DAO.
- Delayed saved/removed success messages and detail navigation until Room confirms the operation succeeded.
- Derived Featured Dish saved state from Room instead of a temporary local Boolean.
- Added database error propagation and retry observation to `DatabaseViewModel`.
- Cancelled stale Yelp requests when a newer location/search origin takes ownership, with a unit test covering the race.
- Kept the Saved-details remove action visibly destructive with a persistent Material error-container background.

## Naming and structure

- Renamed `YelpRepImpl` to `YelpRepositoryImpl` and `DatabaseRepoImpl` to `DatabaseRepositoryImpl`.
- Replaced execution-oriented database method names with `saveMeal`, `getMeals`, `deleteMeal`, and `deleteAllMeals`.
- Replaced ambiguous shared component names with `ConfirmationDialog`, `EmailField`, `PasswordInput`, and `ExternalLinkText`.
- Renamed private dish-search grid/item functions and variables that incorrectly described full meals as categories.
- Removed dead shared composables, unused geocoding DTOs, and generated placeholder tests.

The larger `Ingredient` model/route rename remains intentionally deferred. The endpoint returns full meal search results, so that terminology should be corrected as part of the shared-model extraction rather than mixed into this focused pass.

## Build and resource cleanup

- Removed unused LiveData, Glide, Firestore, Firebase Performance, and AndroidX splash-screen dependencies and unused plugin aliases.
- Removed the redundant explicit-backing-fields compiler flag under Kotlin 2.4.10.
- Upgraded the Gradle wrapper from 9.5.0 to 9.7.0 and refreshed the generated wrapper JAR and platform launch scripts.
- Replaced deprecated AGP properties and the deprecated Room destructive-migration overload.
- Restricted internal activities from external launch, removed unnecessary package queries and Wi-Fi permission, and kept only the launcher activity exported.
- Restored the generated adaptive launcher icon, removed obsolete duplicate launcher bitmaps, and moved the splash artwork to `drawable-nodpi`.
- Removed unused legacy colors and strings and aligned visible terminology around Saved dishes.
- Kept Maps and Yelp keys injected from `local.properties`; no real key was added to tracked defaults.

## Verification completed

- `:app:testDebugUnitTest` passed.
- `:app:lintDebug` passed with no code/resource warnings after the Gradle 9.7 upgrade.
- `:app:assembleDebug` passed.
- The debug APK installed over the existing app on a physical Samsung device while preserving app data.
- Physical smoke tests passed for cold startup, Explore, Search and debounced results, Search-to-Explore navigation, Saved list/details, scrollable details, persistent remove styling, manual Chicago restaurant search, map loading, draggable marker, zoom-control separation, Directions handoff to Google Maps, and Map-to-Nearby back navigation.
- Sign-out, account deletion, destructive saved-dish actions, and UI automation tests were intentionally not run.

## Follow-up risks and features

1. Implement the retained location preference described in [`LOCATION_PREFERENCE_PLAN.md`](LOCATION_PREFERENCE_PLAN.md). Persist the preference, not coordinates.
2. Replace Room destructive fallback with explicit migrations before changing a production database schema.
3. Move direct Firebase calls behind an auth/session contract and model loading, reauthentication, and field-level validation before a store release.
4. Replace Parcelable objects stored in navigation-entry state with stable IDs or serialized route arguments for stronger process-death restoration.
5. Rename the dish-search `Ingredient` DTO/state/route family when extracting shared models for Compose Multiplatform.
6. Add focused Room repository tests and continue expanding state-holder tests before migration.
