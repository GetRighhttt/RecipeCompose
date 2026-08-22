# Code cleanup audit

Date: 2026-08-22  
Scope: correctness, naming, state ownership, build configuration, resources, and physical-device smoke testing

## Correctness changes

- Removed the experimental Firebase authentication gate after confirming that no product feature required an account; completed onboarding now opens the application directly.
- Made recipe and database UI collections non-null, eliminating defensive nullable-list handling throughout Compose screens.
- Switched root navigation state collection to lifecycle-aware collection.
- Prevented duplicate saved dishes with a transactional remote-meal-ID check in the Room DAO.
- Delayed saved/removed success messages and detail navigation until Room confirms the operation succeeded.
- Derived Featured Dish saved state from Room instead of a temporary local Boolean.
- Kept unsaved recipe-detail actions visually unselected and guarded saved-state matching against blank remote meal IDs.
- Added database error propagation and retry observation to `DatabaseViewModel`.
- Cancelled stale Yelp requests when a newer location/search origin takes ownership, with a unit test covering the race.
- Kept the Saved-details remove action visibly destructive with a persistent Material error-container background.

## Naming and structure

- Renamed `YelpRepImpl` to `YelpRepositoryImpl` and `DatabaseRepoImpl` to `DatabaseRepositoryImpl`.
- Replaced execution-oriented database method names with `saveMeal`, `getMeals`, `deleteMeal`, and `deleteAllMeals`.
- Replaced ambiguous shared component names with `ConfirmationDialog` and `ExternalLinkText`; later removed the authentication-only form components with the Firebase experiment.
- Renamed private dish-search grid/item functions and variables that incorrectly described full meals as categories.
- Removed dead shared composables, unused geocoding DTOs, and generated placeholder tests.

The larger `Ingredient` model/route rename remains intentionally deferred. The endpoint returns full meal search results, so that terminology should be corrected as part of the shared-model extraction rather than mixed into this focused pass.

## Build and resource cleanup

- Removed unused LiveData, Glide, AndroidX splash-screen, and all experimental Firebase dependencies and plugin aliases.
- Removed `LoginActivity`, the Account route/screen, `google-services.json`, authentication strings, and authentication-only previews and screenshot assets.
- Removed the redundant explicit-backing-fields compiler flag under Kotlin 2.4.10.
- Upgraded the Gradle wrapper from 9.5.0 to 9.7.0 and refreshed the generated wrapper JAR and platform launch scripts.
- Replaced deprecated AGP properties and the deprecated Room destructive-migration overload.
- Restricted internal activities from external launch, removed unnecessary package queries and Wi-Fi permission, and kept only the launcher activity exported.
- Restored the original food artwork inside a modern adaptive launcher icon after the generated template icon failed to render reliably.
- Removed unused legacy colors and strings and aligned visible terminology around Saved dishes.
- Kept Maps and Yelp keys injected from `local.properties`; no real key was added to tracked defaults.

## Verification completed

- `:app:testDebugUnitTest` passed.
- `:app:lintDebug` passed; the report contains only version-availability notices for Gradle 9.7.1 and Maps Compose 8.5.0.
- `:app:assembleDebug` passed.
- The debug APK installed over the existing app on a physical Samsung device while preserving app data.
- Authentication-removal smoke tests passed for returning-user startup, first-run onboarding Skip-to-Explore navigation, and the Home/Info-only drawer. The original onboarding DataStore preference was restored after the isolated first-run test.
- Physical smoke tests passed for cold startup, adaptive launcher-icon rendering, Explore, Search and debounced results, Search-to-Explore navigation, Saved list/details, scrollable details, saved/unsaved action styling, persistent remove styling, manual Chicago restaurant search, retained current-location intent, approximate-only permission, revoked-permission fallback, location-choice reset across a cold launch, map loading, draggable marker, zoom-control separation, Directions handoff to Google Maps, and Map-to-Nearby back navigation.
- Destructive saved-dish actions and UI automation tests were intentionally not run.

## Follow-up risks and features

1. Replace Room destructive fallback with explicit migrations before changing a production database schema.
2. Replace Parcelable objects stored in navigation-entry state with stable IDs or serialized route arguments for stronger process-death restoration.
3. Rename the dish-search `Ingredient` DTO/state/route family when extracting shared models for Compose Multiplatform.
4. Add focused Room repository tests and continue expanding state-holder tests before migration.
