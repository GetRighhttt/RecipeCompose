# Recipe Compose

Recipe Compose is an end-to-end Android application built with Jetpack Compose. It connects recipe discovery with restaurant search and real-world navigation: users can explore meals from TheMealDB, find restaurants through Yelp, inspect a destination on an interactive Google Map, and continue directly into driving directions.

The project demonstrates production-oriented Compose practices across state-driven UI, remote APIs, local persistence, authentication, mapping, navigation, and lifecycle-aware state management. Its primary workflow goes beyond content browsing by turning restaurant discovery into an actionable destination.

## Product capabilities

- Browse recipe categories and detailed meal information.
- Search for meals by ingredient.
- Discover a random meal and open its source or YouTube instructions.
- Save and manage favorite meals locally.
- Discover nearby Yelp restaurants from the device's current location.
- Search by restaurant name or cuisine, with a city or ZIP code fallback.
- View a selected restaurant on an interactive Google Map.
- Reposition the destination marker and launch driving directions to the active pin.
- Authenticate users with Firebase.
- Respond to loading, error, and network-connectivity states.

## Engineering highlights

- Declarative, state-driven screens built entirely with Jetpack Compose and Material 3.
- Unidirectional UI state exposed from ViewModels through `StateFlow`.
- Lifecycle-aware Flow collection that avoids observing inactive screens.
- Explicit UI events for retries, searches, refreshes, dialogs, favorites, and navigation.
- Debounced remote search with cancellation to prevent outdated requests from controlling the UI.
- Repository boundaries for Retrofit services and Room persistence.
- Navigation Compose routes with state passed between destinations.
- Reusable Compose components for dialogs, form fields, lists, and application chrome.
- Local-first favorites with Room and swipe-to-delete interactions.
- External navigation handoff that follows the currently selected map marker.
- Foreground-only location access with support for approximate and precise permission.
- Graceful location fallbacks for denied permission, unavailable coordinates, and manual search.
- Build-time credential injection, redacted authorization headers, and debug-only HTTP body logging.

## Restaurant navigation

The restaurant workflow turns the user's current area into an actionable destination:

```text
Foreground location (approximate or precise)
        ↓
Nearby Yelp restaurant discovery
        ↓
Restaurant selection
        ↓
Interactive map and destination marker
        ↓
Optional marker adjustment
        ↓
Google Maps driving directions
```

If foreground location is denied or unavailable, the Shops screen remains usable through a city or ZIP code search. The app does not request background location.

The directions action uses either the restaurant marker or the user-adjusted marker as its destination. Google Maps manages the route origin and its own navigation permissions after the handoff.

## Technology

| Area | Implementation |
| --- | --- |
| User interface | Jetpack Compose, Material 3 |
| State and lifecycle | ViewModel, StateFlow, lifecycle-aware collection |
| Navigation | Navigation Compose |
| Networking | Retrofit, OkHttp, Gson |
| Local persistence | Room |
| Recipe data | TheMealDB API |
| Restaurant discovery | Yelp Fusion API |
| Location and mapping | Fused Location Provider, Google Maps Compose, Google Maps directions handoff |
| Cloud services | Firebase Authentication, Firestore, Analytics, Performance |
| Build tooling | Kotlin DSL, version catalogs, KSP, Secrets Gradle Plugin |

## Project setup

### Prerequisites

- Android Studio and a compatible Android SDK.
- A Google Maps Platform API key with Maps SDK for Android enabled.
- A Yelp Fusion API key.
- A Firebase project and `google-services.json` for a separate Firebase environment.

### Installation

1. Clone the repository:

   ```bash
   git clone git@github.com:GetRighhttt/RecipeCompose.git
   cd RecipeCompose
   ```

2. Add the following values to the root `local.properties` file:

   ```properties
   MAPS_API_KEY=your_google_maps_key
   YELP_API_KEY=your_yelp_fusion_key
   YELP_BASE_URL=https://api.yelp.com/v3/
   BASE_URL=https://www.themealdb.com/api/json/v1/1/
   ```

   `local.properties` is excluded from version control. The checked-in `local.defaults.properties` supplies non-sensitive defaults for project synchronization and keyless CI builds.

3. Build the debug application:

   ```bash
   ./gradlew :app:assembleDebug
   ```

4. Run the `app` configuration on an Android device or emulator with Google APIs.

On first opening Shops, choose **Use my location** and grant approximate or precise foreground access to load nearby restaurants. The permission prompt is user initiated and can be declined without blocking the feature; enter a city or ZIP code instead.

## Credential handling

The Secrets Gradle Plugin exposes local configuration through generated `BuildConfig` values and Android manifest placeholders while keeping real credentials out of source control.

This protects the repository, not the compiled APK. For an appropriate deployment configuration:

- Restrict the Maps key to the application ID and signing-certificate fingerprint.
- Use separate development and production credentials.
- Keep real credentials out of `local.defaults.properties`.
- Route Yelp requests through a backend service if the credential must remain confidential.

## Verification

Run the primary local checks with:

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug
```

Additional engineering notes are tracked in [`docs/`](docs), including the [theme and UI redesign](docs/THEME_AND_UI_REDESIGN.md), [Gradle Kotlin DSL migration notes](docs/GRADLE_KOTLIN_DSL_MIGRATION.md), [KMP migration assessment](docs/KMP_MIGRATION_ASSESSMENT.md), and [Compose Multiplatform migration plan](docs/COMPOSE_MULTIPLATFORM_MIGRATION_PLAN.md).

## Demonstrations

### Interface and navigation

https://github.com/user-attachments/assets/471a4c36-3430-4303-ac4d-3d97941ac137

### Restaurant maps

https://github.com/user-attachments/assets/85ea40df-5807-4948-b3f7-42c5830a4a0a

### Local favorites

https://github.com/user-attachments/assets/5f3f7408-b13d-4225-8854-7ce993fea4a4

## Contributing

1. Fork the repository and create a focused branch.
2. Implement and test the change.
3. Run the verification commands above.
4. Open a pull request describing the user-facing behavior and implementation details.

## Contact

Questions and feedback are welcome at **stefanbusiness95@gmail.com**.
