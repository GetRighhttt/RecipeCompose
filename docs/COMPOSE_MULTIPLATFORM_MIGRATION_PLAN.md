# Compose Multiplatform migration plan

Status: planning proposal; no migration work started<br>
Date: 2026-08-15<br>
Targets: Android and iOS<br>
Related discovery: [Kotlin Multiplatform migration assessment](KMP_MIGRATION_ASSESSMENT.md)

## Decision summary

Recipe Compose is a strong candidate for sharing both application logic and UI with Compose Multiplatform. The project is already written in Kotlin, the interface is already implemented with Jetpack Compose, and there is no existing SwiftUI application that must be preserved.

Compose Multiplatform does not replace Kotlin Multiplatform. The intended architecture uses Kotlin Multiplatform source sets for code sharing and Compose Multiplatform for the UI:

```text
Kotlin Multiplatform
├── shared models, repositories, state, and platform contracts
└── Compose Multiplatform
    └── shared Android and iOS UI
```

An iOS host is still required. It can remain a small Xcode project responsible for signing, application metadata, native configuration, assets, and launching the shared Compose root. The goal is not to build a second UI implementation in SwiftUI.

Recommended direction:

1. Keep `:app` as the working Android application and migration baseline.
2. Add a `:shared` Kotlin Multiplatform library with Compose Multiplatform support.
3. Add a small `iosApp` Xcode project that embeds the shared Compose UI.
4. Migrate one complete vertical slice at a time while keeping Android functional.
5. Share screens, navigation, ViewModels, repositories, networking, persistence, and resources.
6. Keep maps, Firebase initialization, connectivity, geocoding, sharing, and external navigation behind platform implementations.
7. Defer desktop and web targets until Android and iOS reach feature parity.

## Why this project fits

The migration begins from a favorable baseline:

- The production code is Kotlin rather than Java.
- The UI is Compose rather than Android Views or data binding.
- ViewModels already expose state through `StateFlow`.
- Domain models, repository contracts, UI states, and feature packages already exist.
- Recipe and restaurant features have clear API boundaries.
- Favorites are isolated behind a Room repository.
- The most platform-specific functionality is concentrated in identifiable areas.
- There is no existing iOS codebase or native design system to reconcile with a shared UI.

The primary migration work is removing Android dependencies from otherwise reusable code and replacing Android-only libraries with multiplatform libraries or platform adapters.

## Proposed project structure

```text
RecipeCompose
├── app
│   ├── AndroidManifest.xml
│   ├── MainActivity.kt
│   ├── Android application resources
│   ├── google-services.json
│   └── Android-only startup and configuration
│
├── shared
│   ├── src/commonMain
│   │   ├── app
│   │   │   ├── RecipeComposeApp.kt
│   │   │   ├── navigation
│   │   │   └── theme
│   │   ├── core
│   │   │   ├── configuration
│   │   │   ├── errors
│   │   │   ├── platform
│   │   │   └── resources
│   │   ├── features/recipes
│   │   │   ├── data
│   │   │   ├── domain
│   │   │   └── presentation
│   │   ├── features/restaurants
│   │   │   ├── data
│   │   │   ├── domain
│   │   │   └── presentation
│   │   ├── features/favorites
│   │   └── features/auth
│   │
│   ├── src/androidMain
│   │   ├── Google Maps Compose implementation
│   │   ├── Android Firebase implementation
│   │   ├── ConnectivityManager implementation
│   │   ├── Android Geocoder implementation
│   │   ├── Android external-action launchers
│   │   ├── Android Room database builder
│   │   └── Android configuration provider
│   │
│   ├── src/iosMain
│   │   ├── MapKit or Google Maps iOS implementation
│   │   ├── Firebase Apple implementation
│   │   ├── NWPathMonitor implementation
│   │   ├── CLGeocoder implementation
│   │   ├── iOS external-action launchers
│   │   ├── iOS Room database builder
│   │   └── iOS configuration provider
│   │
│   └── src/commonTest
│       ├── repository tests
│       ├── ViewModel tests
│       ├── search and state tests
│       └── network contract tests
│
└── iosApp
    ├── RecipeComposeApp.swift
    ├── ComposeView.swift
    ├── Info.plist
    ├── Assets.xcassets
    ├── GoogleService-Info.plist
    └── RecipeCompose.xcodeproj
```

The platform entry points should do little more than construct platform dependencies and display a shared root composable:

```kotlin
@Composable
fun RecipeComposeApp(dependencies: AppDependencies)
```

## Code-sharing boundaries

| Current concern | Target location | Required change |
| --- | --- | --- |
| Domain models and UI state | `commonMain` | Remove `Parcelable` and Android imports; use plain or serializable Kotlin models. |
| Repository contracts | `commonMain` | Keep interfaces platform-neutral. |
| `RecipeViewModel` | `commonMain` | Replace global dependency lookup with constructor injection. |
| `DatabaseViewModel` | `commonMain` | Inject the repository and use the multiplatform ViewModel artifact. |
| `YelpViewModel` | `commonMain` | Inject API configuration and repository dependencies. |
| Compose screens and widgets | Mostly `commonMain` | Replace `LocalContext`, Android resources, Toasts, and Intents. |
| Navigation | `commonMain` | Replace Parcelable objects in `SavedStateHandle` with serializable routes or stable IDs. |
| Strings, images, and fonts | Compose resources | Move reusable resources out of Android `R`; keep launcher and platform startup assets native. |
| TheMealDB and Yelp networking | `commonMain` | Replace Retrofit/Gson with Ktor and `kotlinx.serialization`. |
| Favorites persistence | Common schema and DAO | Configure Room KMP with Android and iOS database builders. |
| Dependency injection | Shared application graph | Replace `DependencyInjector` and its Android `Context` dependency. |
| Google Maps screen | Platform implementations | Keep shared destination state; render Google Maps on Android and a native map on iOS. |
| Reverse geocoding | Platform implementations | Use Android `Geocoder` and iOS `CLGeocoder`. |
| Connectivity | Platform implementations | Use `ConnectivityManager` and `NWPathMonitor`. |
| Firebase authentication | Shared contract, native implementations | Wrap the official Android and Apple SDKs behind `AuthRepository`. |
| Sharing and directions | Platform implementations | Emit shared UI events and let each platform open the appropriate application. |
| Secrets and URLs | Injected configuration | Keep `BuildConfig` Android-specific and supply iOS values through build configuration. |
| Application startup | Platform hosts | Keep Android activities and the iOS `@main` entry point outside `commonMain`. |

## Key technical decisions

### Shared module strategy

Keep the existing Android application module intact initially and introduce `:shared` as a library. This supports a gradual migration: Android can consume each extracted feature before the iOS application depends on it.

A one-module conversion of `:app` into a multiplatform application would produce a larger, harder-to-review change. It can be reconsidered after the shared migration is stable, but it is not required.

### UI and resources

Most composables can move to `commonMain` after replacing:

- `androidx.compose.ui.res.stringResource` with Compose Multiplatform resource accessors.
- Android drawable references with common image resources.
- Coil 2 Android image loading with Coil 3 multiplatform image loading.
- Toasts with a shared snackbar or one-shot message state.
- direct Intents with injected platform actions.
- `LocalContext` usage with shared APIs or platform implementations.

Android launcher icons, the Android splash theme, `AndroidManifest.xml`, iOS launch assets, and `Info.plist` remain platform-specific.

### Navigation

Use the multiplatform Navigation Compose implementation already modeled after AndroidX Navigation. Preserve the existing navigation concepts during the first migration and avoid adopting Navigation 3 at the same time.

The current practice of storing Parcelable domain objects in `SavedStateHandle` should be removed. Routes should carry a stable identifier or a small `@Serializable` route model. A destination can then load its content from a shared repository or receive it from shared state.

### State and dependency injection

The current ViewModels are suitable for sharing after their default references to `DependencyInjector` are removed. Dependencies should be explicit:

```kotlin
class RecipeViewModel(
    private val repository: RecipeRepository,
) : ViewModel()
```

For the current project size, manual constructor injection through an `AppDependencies` graph is preferable to adding a DI framework solely for the migration. A multiplatform DI framework can be introduced later if object-graph complexity justifies it.

### Networking

Replace Retrofit and Gson with:

- Ktor client in `commonMain`.
- OkHttp or Android Ktor engine on Android.
- Darwin engine on iOS.
- `kotlinx.serialization` DTOs.
- shared response-to-domain mapping.
- Ktor `MockEngine` tests that run without network access.

The Yelp authorization header must remain redacted from logging. Shipping a Yelp key in either mobile binary does not make it confidential; a backend proxy remains the stronger production design if the provider credential must be protected.

### Persistence

Room 2.8.4 can support the planned Android and iOS targets. Move the entity, DAO, database contract, and repository behavior into shared code while keeping database construction platform-specific.

Replace destructive migration as the long-term default with explicit schema migrations before treating iOS favorites as production data.

### Maps and directions

The shared layer should own:

- the restaurant coordinates;
- the active marker coordinates;
- whether the user moved the marker;
- marker labels and geocoding state;
- the request to launch directions.

The platform layer should own map rendering and external navigation:

```text
Shared map state
├── Android: Google Maps Compose
└── iOS: MapKit through UIKitView
```

MapKit is the recommended initial iOS implementation because it is native and avoids requiring the Android Maps Compose API to have a common equivalent. If identical Google Maps behavior and branding are product requirements, use the Google Maps SDK for iOS behind the same platform boundary.

Android can continue launching Google Maps directions. iOS can open Apple Maps or Google Maps when available. Recipe Compose does not require device-location permission merely to display a Yelp destination or hand it to a navigation application.

### Firebase

Define a shared contract for session state and account actions:

```kotlin
interface AuthRepository {
    val session: StateFlow<AuthSession>
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun createAccount(email: String, password: String): AuthResult
    suspend fun signOut()
    suspend fun deleteAccount(): AuthResult
}
```

Use Firebase Android in `androidMain` and the Firebase Apple SDK in the iOS application or `iosMain` adapter. Initialize each platform with its native configuration file. Account deletion must model recent-login and reauthentication failures instead of assuming deletion always succeeds.

Firestore, Analytics, and Performance should be audited before migration. Do not port dependencies that are not providing intentional product behavior.

## Ordered migration plan

### Phase 0 — protect the Android baseline

- Add meaningful tests for repository behavior, search, ViewModel state transitions, and favorites.
- Record the Android feature-parity checklist.
- Remove confirmed unused dependencies such as Glide and unused Firebase products.
- Address or document the current AGP and Gradle deprecation warnings.
- Keep the existing Android build green before introducing new targets.

Exit criteria:

- Android debug compilation and unit tests pass.
- The primary user flows have either automated coverage or a written manual test.
- No credentials are committed or printed in logs.

### Phase 1 — prove the toolchain and iOS host

- Add `:shared` with `commonMain`, `androidMain`, `iosMain`, and `commonTest`.
- Configure Android, `iosArm64`, and `iosSimulatorArm64` targets.
- Apply compatible Kotlin, Compose Multiplatform, KSP, Room, and AGP versions.
- Add an Xcode `iosApp` that displays a minimal shared composable.
- Make Android display the same shared proof-of-life composable without moving a feature.

Exit criteria:

- Android still launches normally.
- The iOS simulator launches the shared Compose UI.
- Shared tests run from Gradle.

### Phase 2 — migrate one recipe vertical slice

- Move shared result/error types and category models.
- Remove Parcelable requirements from the migrated models.
- Add Ktor and `kotlinx.serialization` for TheMealDB.
- Move `RecipeRepository`, its implementation, and `RecipeViewModel`.
- Move the category and detail screens.
- Introduce common resources and shared navigation for those destinations.

Exit criteria:

- Android and iOS load real category data.
- Both platforms navigate from category to detail.
- Repository and ViewModel tests run in `commonTest`.

This is the first meaningful go/no-go checkpoint. It validates networking, state, resources, navigation, UI, and iOS integration without migrating every feature.

### Phase 3 — complete recipes and favorites

- Move ingredient search and cancellation behavior.
- Move random meals and external source/video actions.
- Move common widgets, dialogs, loading states, and error states.
- Introduce Coil 3 multiplatform image loading.
- Configure Room KMP and migrate favorites.
- Implement Android and iOS database builders.

Exit criteria:

- Recipe browsing, ingredient search, random meals, and favorites have Android/iOS parity.
- Favorite data persists across relaunches on both platforms.
- Database behavior is covered by tests.

### Phase 4 — migrate restaurant discovery

- Move Yelp DTOs, domain models, repository, and ViewModel.
- Move restaurant search, debouncing, cancellation, and result UI.
- Inject Yelp API configuration rather than reading Android `BuildConfig` from shared code.
- Verify redacted logging on both platforms.

Exit criteria:

- Android and iOS can search for restaurants and select a result.
- Empty, loading, error, and cancellation states behave consistently.

### Phase 5 — implement maps and directions

- Extract common map state and events.
- Keep the Android Google Maps Compose implementation.
- Add the iOS MapKit implementation through UIKit interoperability.
- Add Android and iOS reverse-geocoding adapters.
- Add platform directions launchers.
- Verify marker selection and dragging semantics on both platforms.

Exit criteria:

- A Yelp restaurant opens at the correct coordinates.
- The user can reposition the active pin.
- Directions use the active pin rather than always using the original restaurant coordinates.
- Neither platform requests unnecessary location permission.

### Phase 6 — migrate authentication and platform services

- Move the login and account UI to shared Compose.
- Add the shared authentication contract and session state.
- Implement Firebase Android and Apple adapters.
- Replace activity-to-activity auth navigation with shared application state.
- Add connectivity, sharing, email, external URL, and message implementations.
- Keep splash and platform startup behavior native.

Exit criteria:

- Sign-up, sign-in, sign-out, account deletion, and reauthentication errors are handled on both platforms.
- Connectivity and external actions have platform-appropriate behavior.

### Phase 7 — parity, UX, and release hardening

- Review the drawer and bottom navigation for iOS usability.
- Verify safe areas, back gestures, keyboard behavior, dark mode, dynamic type, and accessibility.
- Add iOS privacy descriptions and production Firebase configuration.
- Restrict platform API keys by package or bundle identifier and signing identity where supported.
- Add CI jobs for Android, shared tests, and an iOS simulator build.
- Run the complete parity checklist before considering the migration complete.

## Testing strategy

### `commonTest`

- Model serialization and mapping.
- TheMealDB and Yelp request/response contracts.
- HTTP errors, malformed responses, and cancellation.
- Search debounce and stale-request prevention.
- ViewModel loading, success, empty, and error transitions.
- Favorites repository behavior.
- Authentication state transitions with fake repositories.

### Android verification

- Android application startup and Firebase initialization.
- Google Maps rendering and directions intents.
- Android connectivity and geocoding adapters.
- Manifest placeholder and configuration generation.
- Existing Compose UI behavior during each migration phase.

### iOS verification

- Framework linkage and simulator/device startup.
- MapKit marker interaction and geocoding.
- Firebase Apple initialization and authentication.
- URL schemes and directions fallback behavior.
- Keychain/session persistence, safe areas, back gestures, and accessibility.

## Decision checkpoints

Resolve these choices before their corresponding phase begins:

| Decision | Recommended initial choice | Revisit when |
| --- | --- | --- |
| iOS map provider | MapKit | Google Maps branding or feature parity becomes a requirement. |
| Dependency injection | Manual constructor injection | The shared object graph becomes difficult to maintain. |
| Navigation | Current multiplatform Navigation Compose | Android/iOS parity is complete and Navigation 3 offers a concrete benefit. |
| Firebase integration | Platform adapters over official SDKs | Adapter maintenance becomes more expensive than a vetted KMP wrapper. |
| Desktop/web targets | Exclude | Mobile parity is complete. |
| iOS top-level navigation | Shared Compose shell initially | User testing shows the Android-style shell feels inappropriate on iOS. |
| Yelp credential | Existing client configuration for development | The app is prepared for public production distribution. |

## Risk register

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Toolchain incompatibility across Kotlin, Compose, AGP, KSP, and Room | Build setup can block feature work. | Complete the proof-of-life phase before moving production code and pin a verified version matrix. |
| Firebase has separate official Android and Apple SDKs | Auth cannot move unchanged into `commonMain`. | Share the contract and state; implement and test native adapters. |
| Google Maps Compose is Android-specific | The current map composable cannot be copied into common code. | Share map state and use MapKit or Google Maps iOS behind a platform composable. |
| Android APIs are spread through UI files | Screens may appear portable while still depending on `LocalContext`, `R`, Toasts, or Intents. | Move screens individually and require zero `android.*` imports in `commonMain`. |
| Android-oriented navigation shell feels foreign on iOS | Functional parity may not produce good iOS UX. | Treat the root navigation shell as an explicit UX checkpoint. |
| Client API keys can be extracted from both mobile binaries | Moving configuration does not create secrecy. | Restrict keys and introduce a backend for credentials that must remain confidential. |
| Thin existing test coverage | Behavior can regress during extraction. | Add state, repository, and persistence tests before each vertical slice moves. |

## Definition of done

The migration is complete when:

1. Android and iOS launch the same shared Compose application root.
2. Recipe browsing, ingredient search, random meals, favorites, Yelp discovery, maps, marker selection, directions, and authentication work on both platforms.
3. Shared UI and business logic live in `commonMain` unless a documented platform reason prevents it.
4. Platform implementations are behind explicit interfaces or platform composables.
5. `commonMain` has no Android, Java-only, Firebase Android, Google Play Services, or Android resource imports.
6. Common tests run without external network access.
7. Android compilation, Android tests, shared tests, and the iOS simulator build run in CI.
8. Real credentials remain outside version control and authorization headers remain redacted.
9. Android behavior has not regressed from the pre-migration baseline.
10. The iOS interface has passed a platform UX, accessibility, and release-configuration review.

## Planning estimate

These are directional estimates for one developer familiar with the Android codebase:

| Milestone | Expected range |
| --- | --- |
| Shared module and iOS proof of life | 1–2 focused days |
| First real category/detail vertical slice | 2–4 focused days |
| Recipe and favorites parity | 3–6 focused days |
| Yelp, maps, and directions parity | 3–7 focused days |
| Firebase auth and remaining platform services | 3–7 focused days |
| iOS polish, CI, and release hardening | 3–7 focused days |

A functional prototype is much smaller than a release-quality migration. Maps, Firebase, iOS configuration, signing, accessibility, and cross-platform verification are expected to consume more time than moving most Compose layouts.

## Reference documentation

- [Compose Multiplatform FAQ and production status](https://kotlinlang.org/docs/multiplatform/faq.html)
- [Migrating a Jetpack Compose app to Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform/migrate-from-android.html)
- [Compose Multiplatform supported platforms](https://kotlinlang.org/docs/multiplatform/supported-platforms.html)
- [Compose Multiplatform resources](https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources.html)
- [Navigation in Compose Multiplatform](https://kotlinlang.org/docs/multiplatform/compose-navigation.html)
- [Multiplatform ViewModel](https://kotlinlang.org/docs/multiplatform/compose-viewmodel.html)
- [UIKit interoperability and MapKit](https://kotlinlang.org/docs/multiplatform/compose-uikit-integration.html)
- [Room for Kotlin Multiplatform](https://developer.android.com/kotlin/multiplatform/room)
- [Ktor client engines](https://ktor.io/docs/client-engines.html)
- [Firebase supported platforms and SDKs](https://firebase.google.com/docs/libraries)
