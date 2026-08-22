# Compose Multiplatform migration plan

Status: approved direction; implementation has not started<br>
Last revised: 2026-08-22<br>
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
6. Keep maps, connectivity, geocoding, sharing, and external navigation behind platform implementations.
7. Defer desktop and web targets until Android and iOS reach feature parity.

## Revision context

This revision reflects the application after the 2026 Android cleanup and redesign rather than the older two-year-old baseline. Since the original plan was written, the project has:

- migrated and centralized its Gradle Kotlin DSL configuration;
- upgraded to Kotlin 2.4.10, AGP 9.3.1, Gradle 9.7.0, and Java 21 for the build runtime;
- adopted an action-first Explore screen and persistent top-level navigation;
- added versioned onboarding persistence with DataStore 1.2.1;
- centralized startup routing between onboarding and the main application;
- improved lifecycle-aware state collection, location permission handling, restaurant discovery, Google Maps, directions, offline UI, previews, and unit coverage;
- removed the experimental Firebase authentication and analytics stack because no product capability required an account;
- retained Android-specific Google Maps, location, activity startup, and external-intent integrations.

The direction is now decided: this project will pursue shared Compose UI for Android and iOS, not a SwiftUI frontend over shared logic. The earlier KMP assessment remains useful as a dependency and platform-boundary inventory, but its UI-strategy decision point has been resolved by this document.

## Verified local baseline

| Concern | Current value | Migration implication |
| --- | --- | --- |
| Kotlin | 2.4.10 | Keep the Compose compiler plugin on the same Kotlin version. |
| Android Gradle Plugin | 9.3.1 | Use `com.android.kotlin.multiplatform.library` for the Android target in `:shared`; do not combine the KMP target with `com.android.application`. |
| Gradle | 9.7.0 | Already suitable for the current Android build; pin the wrapper during the migration. |
| Build JVM | Java 21.0.11 | Keep Java 21 as the reproducible build baseline. |
| Compose Android | BOM 2026.06.01 | The Android app can retain the BOM during extraction; common UI must use Compose Multiplatform dependencies. |
| Compose Multiplatform candidate | 1.11.1 | Verify the complete version matrix in the proof-of-life phase before moving production screens. |
| Koin candidate | 4.2.2 | Use Koin DSL modules in shared code first; defer annotations/compiler-plugin adoption until the base KMP graph is stable. |
| Ktor candidate | 3.5.1 | Use shared client configuration with OkHttp on Android and Darwin on iOS. |
| Room | 2.8.4 | KMP-capable, but database construction and migration tests remain platform-specific. |
| DataStore | 1.2.1 | The onboarding version can move behind a common persistence contract after the first shared UI proof. |
| Xcode | 26.5 installed | The active developer directory currently points to `/Library/Developer/CommandLineTools`; select full Xcode before building the iOS host. |
| iOS deployment target | 14 or newer | Compose Multiplatform 1.11.1 supports iOS 14 and newer. |

Before the first iOS build, select full Xcode and complete any first-launch setup:

```bash
sudo xcode-select --switch /Applications/Xcode.app/Contents/Developer
sudo xcodebuild -runFirstLaunch
```

This is a developer-machine prerequisite, not a repository change.

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
│   ├── SplashScreenActivity.kt
│   ├── OnboardingActivity.kt
│   ├── Android application resources
│   └── Android-only startup and configuration
│
├── shared
│   ├── src/commonMain
│   │   ├── app
│   │   │   ├── RecipeComposeApp.kt
│   │   │   ├── navigation
│   │   │   └── theme
│   │   ├── composeResources
│   │   │   ├── drawable
│   │   │   └── values
│   │   ├── core
│   │   │   ├── configuration
│   │   │   ├── errors
│   │   │   ├── persistence
│   │   │   ├── platform
│   │   │   └── resources
│   │   ├── features/onboarding
│   │   ├── features/recipes
│   │   │   ├── data
│   │   │   ├── domain
│   │   │   └── presentation
│   │   ├── features/restaurants
│   │   │   ├── data
│   │   │   ├── domain
│   │   │   └── presentation
│   │   └── features/favorites
│   │
│   ├── src/androidMain
│   │   ├── Google Maps Compose implementation
│   │   ├── ConnectivityManager implementation
│   │   ├── Android Geocoder implementation
│   │   ├── Android external-action launchers
│   │   ├── Android Room database builder
│   │   └── Android configuration provider
│   │
│   ├── src/iosMain
│   │   ├── MapKit or Google Maps iOS implementation
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
    └── RecipeCompose.xcodeproj
```

The platform entry points should initialize Koin with common and platform modules, then display a shared root composable:

```kotlin
@Composable
fun RecipeComposeApp()
```

## Code-sharing boundaries

| Current concern | Target location | Required change |
| --- | --- | --- |
| `StartupDestination` decision | `commonMain` | Move the existing pure resolver first and keep platform entry points responsible for launching their host UI. |
| Onboarding page models and UI | `commonMain` | Replace Android `R` strings with Compose resources; keep Android activity startup outside shared code. |
| `OnboardingPreferences` | Common contract plus platform storage construction | Preserve the versioned integer behavior; configure DataStore files per platform rather than passing Android `Context` into common code. |
| Domain models and UI state | `commonMain` | Remove `Parcelable` and Android imports; use plain or serializable Kotlin models. |
| Repository contracts | `commonMain` | Keep interfaces platform-neutral. |
| `RecipeViewModel` | `commonMain` | Replace global dependency lookup with constructor injection. |
| `DatabaseViewModel` | `commonMain` | Inject the repository and use the multiplatform ViewModel artifact. |
| `YelpViewModel` | `commonMain` | Inject API configuration and repository dependencies. |
| Compose screens and widgets | Mostly `commonMain` | Replace `LocalContext`, Android resources, Toasts, and Intents. |
| Explore shell and primary navigation | `commonMain` after recipe state is shared | Preserve the four destinations—Explore, Search, Nearby, and Saved—while replacing string routes with typed serializable routes. |
| Navigation | `commonMain` | Replace Parcelable objects in `SavedStateHandle` with serializable routes or stable IDs. |
| Strings, images, and fonts | Compose resources | Move reusable resources out of Android `R`; keep launcher and platform startup assets native. |
| TheMealDB and Yelp networking | `commonMain` | Replace Retrofit/Gson with Ktor and `kotlinx.serialization`. |
| Favorites persistence | Common schema and DAO | Configure Room KMP with Android and iOS database builders. |
| Dependency injection | Koin modules split across common and platform source sets | Replace `DependencyInjector`, global lazy properties, and default ViewModel repository arguments with explicit Koin definitions. |
| Google Maps screen | Platform implementations | Keep shared destination state; render Google Maps on Android and a native map on iOS. |
| Reverse geocoding | Platform implementations | Use Android `Geocoder` and iOS `CLGeocoder`. |
| Connectivity | Platform implementations | Use `ConnectivityManager` and `NWPathMonitor`. |
| Sharing and directions | Platform implementations | Emit shared UI events and let each platform open the appropriate application. |
| Secrets and URLs | Injected configuration | Keep `BuildConfig` Android-specific and supply iOS values through build configuration. |
| Application startup | Platform hosts plus common startup policy | Keep Android activities and the iOS `@main` entry point outside `commonMain`; share only onboarding destination policy. |

## Key technical decisions

### Shared module strategy

Keep the existing Android application module intact initially and introduce `:shared` as a library. This supports a gradual migration: Android can consume each extracted feature before the iOS application depends on it.

With AGP 9+, this separation is not merely a preference. The supported Android target for a KMP module uses `com.android.kotlin.multiplatform.library`, and there is no direct KMP replacement for `com.android.application`. The Android entry point must therefore remain in its own application module while `:shared` is a KMP library. Use the current `android {}` block inside the KMP DSL rather than the deprecated `androidLibrary {}` spelling.

The initial shared plugin set should be:

```text
org.jetbrains.kotlin.multiplatform
org.jetbrains.compose
org.jetbrains.kotlin.plugin.compose
com.android.kotlin.multiplatform.library
```

The existing `:app` keeps `com.android.application`, Secrets, and Android packaging responsibilities. It depends on `:shared` and hosts the shared root when that root is ready.

### UI and resources

Most composables can move to `commonMain` after replacing:

- `androidx.compose.ui.res.stringResource` with Compose Multiplatform resource accessors.
- Android drawable references with common image resources.
- Coil 2 Android image loading with Coil 3 multiplatform image loading.
- Toasts with a shared snackbar or one-shot message state.
- direct Intents with injected platform actions.
- `LocalContext` usage with shared APIs or platform implementations.

Android launcher icons, the Android splash theme, `AndroidManifest.xml`, iOS launch assets, and `Info.plist` remain platform-specific.

The onboarding screen is the recommended first shared UI proof because it already has explicit inputs, contains no network or map dependency, uses local artwork, and exercises paging, resources, theming, accessibility, and safe-area layout. Its completion storage and Android activity remain platform-host responsibilities during that proof.

### Navigation

Use the multiplatform Navigation Compose implementation already modeled after AndroidX Navigation. Preserve the four current primary destinations and existing detail flow during the first migration, and avoid adopting Navigation 3 at the same time.

The current practice of storing Parcelable domain objects in `SavedStateHandle` should be removed. Routes should carry a stable identifier or a small `@Serializable` route model. A destination can then load its content from a shared repository or receive it from shared state.

The existing `navigateToPrimaryDestination` behavior should remain the navigation contract: switching between Explore, Search, Nearby, and Saved must pop to the shared start destination, save state, launch once, and restore the selected tab. This behavior needs a regression test before the shell moves.

### State and dependency injection

The current ViewModels are suitable for sharing after their default references to `DependencyInjector` are removed. Dependencies should be explicit:

```kotlin
class RecipeViewModel(
    private val repository: RecipeRepository,
) : ViewModel()
```

Koin is the selected dependency-injection framework. The graph is small, but adopting Koin during extraction removes the custom global service locator and gives Android and iOS one consistent construction model.

Use regular Koin DSL modules first:

```text
commonMain
├── coreModule
├── recipeModule
├── restaurantModule
└── viewModelModule

androidMain
└── androidPlatformModule
    ├── Android location provider
    ├── Room Android builder
    └── Android platform actions

iosMain
└── iosPlatformModule
    ├── iOS location provider
    ├── Room iOS builder
    └── iOS platform actions
```

The initial shared dependencies should use `koin-core`, `koin-compose`, `koin-compose-viewmodel`, and `koin-test`, with `koin-android` owned by the Android host. Do not add Koin annotations or the Koin compiler plugin in the first toolchain change. Runtime DSL modules are sufficient for this graph and avoid introducing another compiler integration while Kotlin 2.4 and the KMP module are being proven.

Every module should have a verification test, and ViewModels must lose their default `DependencyInjector` arguments before moving to `commonMain`.

### Networking

Replace Retrofit and Gson with:

- Ktor 3.5.1 client in `commonMain`.
- OkHttp Ktor engine on Android.
- Darwin engine on iOS.
- `kotlinx.serialization` DTOs.
- shared response-to-domain mapping.
- Ktor `MockEngine` tests that run without network access.

The current network surface is intentionally small: three TheMealDB requests and one Yelp business-search request. Build one shared `HttpClient` factory, install content negotiation and JSON once, and keep provider-specific request code in separate data sources. Do not reproduce the current two independent Retrofit/OkHttp singleton stacks in Ktor.

Koin should own the client and data-source lifetimes:

```text
single HttpClient
  ├── TheMealDbRemoteDataSource
  │   └── RecipeRepository
  └── YelpRemoteDataSource
      └── YelpRepository
```

Keep timeout values, debug logging, error mapping, and authorization redaction equivalent to Android before changing behavior. Close the shared `HttpClient` when the application graph is stopped.

The Yelp authorization header must remain redacted from logging. Shipping a Yelp key in either mobile binary does not make it confidential; a backend proxy remains the stronger production design if the provider credential must be protected.

### Persistence

Room 2.8.4 can support the planned Android and iOS targets. Move the entity, DAO, database contract, and repository behavior into shared code while keeping database construction platform-specific.

Replace destructive migration as the long-term default with explicit schema migrations before treating iOS favorites as production data.

DataStore 1.2.1 is now used for the onboarding version. Preserve the integer version rather than replacing it with a Boolean. Move the startup decision to `commonMain` early, but defer moving the DataStore instance until the shared module and platform file-production pattern are verified. Android and iOS should provide their own storage path while common code owns the key and completion semantics.

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

### Authentication scope

Authentication is intentionally outside the current product and migration scope. The former Firebase implementation existed only as an API experiment; recipes, local favorites, restaurant discovery, maps, and directions do not require an account. Do not introduce a shared auth contract or platform SDKs during migration unless a concrete account-backed feature—such as cross-device synchronization—first establishes the requirement.

## Ordered migration plan

### Phase 0 — freeze and document the Android baseline

- Finish and verify the Explore, primary-navigation, and onboarding changes before moving files.
- Resolve the compact Search-grid presentation before freezing that screen: use an adaptive image-first grid with approximately 120–136 dp minimum tiles, two-line labels, and automatic two-column fallback for constrained width or larger fonts.
- Keep `:app:compileDebugKotlin` and `:app:testDebugUnitTest` green.
- Record a manual parity checklist for onboarding, startup, Explore, category details, search, favorites, nearby shops, maps, directions, offline recovery, and activity recreation.
- Record the known non-blocking warnings separately: redundant explicit-backing-fields flag, deprecated Room destructive-migration overload, and deprecated AGP properties.
- Remove confirmed unused dependencies only when that cleanup is isolated from the KMP scaffolding change.
- Select full Xcode and complete its first-launch setup.

Exit criteria:

- The current Android behavior is reproducible from a clean build.
- Unit tests pass and the manual checklist exists.
- `xcodebuild -version` works without overriding `DEVELOPER_DIR`.
- No credentials are committed or printed in logs.

### Phase 1 — prove the module boundary and shared UI toolchain

- Add `:shared` with `commonMain`, `androidMain`, `iosMain`, and `commonTest`.
- Apply Kotlin Multiplatform, Compose Multiplatform, the Compose compiler plugin, and `com.android.kotlin.multiplatform.library`.
- Configure Android, `iosArm64`, and `iosSimulatorArm64` targets.
- Keep `:app` as the only Android application module and make it depend on `:shared`.
- Add a small Xcode `iosApp` using direct local framework integration.
- Move the theme primitives and onboarding page UI/resources as the first shared Compose proof.
- Keep `SplashScreenActivity`, `OnboardingActivity`, and Android DataStore construction in `:app` for this phase.

Exit criteria:

- Android launches the shared onboarding composable through the existing host.
- The iOS simulator launches the same themed onboarding composable.
- Shared tests run from Gradle.
- No recipe, Yelp, map, or database implementation has moved yet.

This is the first hard stop. If the version matrix, Android-KMP plugin, Xcode framework integration, or resource generation is unstable, fix the toolchain before extracting production features.

### Phase 2 — extract pure application contracts

- Move `StartupDestination`, result/error types, plain UI states, repository contracts, and platform contracts to `commonMain`.
- Split `Parcelable`, Room entities, Gson DTOs, and domain models instead of carrying platform annotations into common code.
- Introduce typed serializable route identifiers; stop passing complete domain objects through `SavedStateHandle`.
- Introduce Koin common/platform modules and replace `DependencyInjector` access with constructor-injected definitions.
- Make the Android application consume the shared contracts through adapters while behavior remains unchanged.
- Move startup-resolution tests and new model/state tests to `commonTest`.

Exit criteria:

- `commonMain` contains no `android.*`, Java-only, Play Services, or Android resource imports.
- Android still runs against the same Retrofit, Room, and map implementations.
- Shared contract tests pass for Android and iOS simulator targets.

### Phase 3 — migrate the first recipe and Explore vertical slice

- Add Ktor Client, platform engines, and `kotlinx.serialization` for TheMealDB.
- Move category and random-meal DTO mapping, `RecipeRepository`, and the relevant `RecipeViewModel` state.
- Move category details, the featured card, and the Explore content composables to common UI.
- Keep Explore quick actions callback-based so Android can continue opening its existing Search, Nearby, and Saved destinations while those screens remain in `:app`.
- Introduce Compose resources and Coil 3 multiplatform image loading for this slice.
- Add `MockEngine` repository tests and ViewModel loading/success/empty/error tests.

Exit criteria:

- Android and iOS load real categories and a featured dish.
- Both platforms render Explore and open category details.
- Android Search, Nearby, and Saved callbacks still reach the existing Android destinations.

This is the first product-level go/no-go checkpoint because it validates networking, mapping, state, resources, image loading, shared UI, and iOS integration together.

### Phase 4 — complete recipes, navigation, onboarding persistence, and favorites

- Move dish search, cancellation/debounce behavior, recipe details, and random-meal actions.
- Move shared widgets, dialogs, loading, empty, and error states.
- Move the Explore/Search/Saved navigation shell to common UI using the existing top-level navigation semantics.
- Move the onboarding version behind a common persistence contract; retain platform DataStore file construction.
- Configure Room KMP, split entities from domain models, and add Android/iOS database builders.
- Replace destructive migration as the default and add persistence tests.

Exit criteria:

- Recipe browsing, search, details, featured dishes, onboarding state, and favorites have Android/iOS parity.
- Favorite and onboarding data persist across relaunches on both platforms.
- Search → Explore navigation has a shared regression test.

### Phase 5 — migrate restaurant discovery, maps, and location services

- Move Yelp DTOs, domain models, repository, ViewModel, search, and result UI.
- Inject Yelp configuration instead of reading Android `BuildConfig` from shared code.
- Extract shared location/map state and permission-neutral events.
- Keep Google Maps Compose in `androidMain` and add MapKit through UIKit interoperability in `iosMain`.
- Add Android/iOS current-location, reverse-geocoding, directions, and connectivity adapters.
- Keep the location request user-driven on both platforms.

Exit criteria:

- Both platforms search by current or manual location and open a selected result.
- A restaurant opens at the correct coordinates and a moved pin controls directions.
- Loading, empty, error, permission-denied, and cancellation states have parity.
- Authorization headers remain redacted.

### Phase 6 — migrate remaining platform actions

- Move the onboarding startup policy to common state while keeping Android activities and the iOS entry point native.
- Add platform implementations for sharing, email, external URLs, messages, and application settings.

Exit criteria:

- Startup moves directly from native launch/onboarding state into the shared application without an intermediate screen.
- External actions behave appropriately on each platform.

### Phase 7 — parity, UX, CI, and release hardening

- Review large titles, bottom navigation, drawer behavior, dialogs, and gestures specifically on iOS.
- Verify safe areas, keyboard behavior, dark mode, dynamic type, accessibility, restoration, and reduced motion.
- Add iOS privacy descriptions, signing, map configuration, and key restrictions.
- Add CI jobs for Android, common tests, and an iOS simulator build.
- Refresh the repository README with the final architecture, migration summary, and a curated screenshot set for Onboarding, Explore, Search, Nearby, and Maps. Capture only settled, representative states and prefer matching Android/iOS views once parity exists.
- Run the complete parity checklist before declaring the migration complete.

## Practical stopping point for the first migration session

A successful first session should finish Phase 0 and Phase 1, then stop with both platform hosts rendering the shared onboarding UI. If time remains, begin Phase 2 by moving only pure contracts and tests. Do not start networking, Room, or maps until the shared module and iOS framework remain reproducibly green.

That stopping point is intentionally useful rather than cosmetic: it proves shared Compose rendering, resources, paging, theming, Android consumption, Xcode integration, and iOS safe-area behavior without risking the working Android feature set.

## Migration working rules

- Keep every phase buildable on Android; add the iOS verification as soon as the touched code reaches a shared source set.
- Move code before redesigning its behavior. Koin DSL and Ktor are required migration infrastructure; do not add Navigation 3, Koin annotations/compiler plugins, a database replacement, or an unrelated account system at the same time.
- Prefer one complete vertical slice over moving every model, every screen, or every repository by layer.
- Keep temporary Android adapters explicit and delete them when their shared replacement is proven.
- Do not duplicate a mutable source of truth between `:app` and `:shared`; Android should consume the shared owner once a state holder moves.
- Keep secrets supplied by each platform host. A common configuration interface may expose values to shared code, but shared resources must never contain credentials.
- End each extraction with Android compilation, common tests, and the iOS simulator build appropriate to that phase.

## Testing strategy

### `commonTest`

- Model serialization and mapping.
- TheMealDB and Yelp request/response contracts.
- HTTP errors, malformed responses, and cancellation.
- Search debounce and stale-request prevention.
- Startup routing for incomplete and completed onboarding versions.
- Onboarding completion-version semantics.
- Primary navigation behavior, including Search → Explore restoration.
- ViewModel loading, success, empty, and error transitions.
- Favorites repository behavior.

### Android verification

- Android application startup.
- Activity recreation during onboarding and top-level navigation.
- DataStore persistence across process restart.
- Google Maps rendering and directions intents.
- Android connectivity and geocoding adapters.
- Manifest placeholder and configuration generation.
- Existing Compose UI behavior during each migration phase.

### iOS verification

- Framework linkage and simulator/device startup.
- Onboarding paging, completion persistence, and safe-area behavior.
- MapKit marker interaction and geocoding.
- URL schemes and directions fallback behavior.
- Safe areas, back gestures, and accessibility.

## Decision checkpoints

Resolve these choices before their corresponding phase begins:

| Decision | Recommended initial choice | Revisit when |
| --- | --- | --- |
| Shared module shape | One `:shared` UI-and-logic library plus separate app entry points | Shared code grows enough to justify separate `sharedLogic` and `sharedUI` modules. |
| iOS map provider | MapKit | Google Maps branding or feature parity becomes a requirement. |
| Dependency injection | Koin 4.2.2 with regular DSL modules | Compile-time annotations become valuable after the runtime graph and Kotlin 2.4 toolchain are stable. |
| Navigation | Current multiplatform Navigation Compose | Android/iOS parity is complete and Navigation 3 offers a concrete benefit. |
| Desktop/web targets | Exclude | Mobile parity is complete. |
| iOS top-level navigation | Shared Compose shell initially | User testing shows the Android-style shell feels inappropriate on iOS. |
| Onboarding persistence | Common version semantics with platform DataStore construction | A different Apple-native persistence requirement appears. |
| Yelp credential | Existing client configuration for development | The app is prepared for public production distribution. |

## Risk register

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Xcode is installed but Command Line Tools are currently selected | Gradle may report misleading Kotlin/Native or framework failures before compilation starts. | Select `/Applications/Xcode.app/Contents/Developer`, run first-launch setup, and verify `xcodebuild -version` in Phase 0. |
| Toolchain incompatibility across Kotlin, Compose, AGP, KSP, and Room | Build setup can block feature work. | Complete the proof-of-life phase before moving production code and pin a verified version matrix. |
| Android BOM and Compose Multiplatform dependencies coexist during extraction | Dependency resolution can produce subtle Android-only or binary-compatibility failures. | Keep the BOM in `:app`, use Compose Multiplatform coordinates only in `:shared`, and verify dependency resolution before moving screens. |
| Koin graph errors move from the custom service locator into runtime module definitions | Missing or duplicated bindings could fail only when a screen is opened. | Keep modules small, constructor-inject every definition, and run Koin module verification tests in `commonTest`. |
| Google Maps Compose is Android-specific | The current map composable cannot be copied into common code. | Share map state and use MapKit or Google Maps iOS behind a platform composable. |
| Android APIs are spread through UI files | Screens may appear portable while still depending on `LocalContext`, `R`, Toasts, or Intents. | Move screens individually and require zero `android.*` imports in `commonMain`. |
| Android-oriented navigation shell feels foreign on iOS | Functional parity may not produce good iOS UX. | Treat the root navigation shell as an explicit UX checkpoint. |
| Client API keys can be extracted from both mobile binaries | Moving configuration does not create secrecy. | Restrict keys and introduce a backend for credentials that must remain confidential. |
| Thin existing test coverage | Behavior can regress during extraction. | Add state, repository, and persistence tests before each vertical slice moves. |

## Definition of done

The migration is complete when:

1. Android and iOS launch the same shared Compose application root after their native startup host resolves onboarding state.
2. Onboarding, Explore, recipe browsing, ingredient search, random meals, favorites, Yelp discovery, maps, marker selection, directions, and offline recovery work on both platforms.
3. Shared UI and business logic live in `commonMain` unless a documented platform reason prevents it.
4. Platform implementations are behind explicit interfaces or platform composables.
5. `commonMain` has no Android, Java-only, Google Play Services, or Android resource imports.
6. Common tests run without external network access.
7. Android compilation, Android tests, shared tests, and the iOS simulator build run in CI.
8. Real credentials remain outside version control and authorization headers remain redacted.
9. Android behavior has not regressed from the pre-migration baseline.
10. The iOS interface has passed a platform UX, accessibility, and release-configuration review.
11. The README accurately describes the shared architecture and presents current, polished screenshots rather than pre-migration UI.

## Planning estimate

These are directional estimates for one developer familiar with the Android codebase:

| Milestone | Expected range |
| --- | --- |
| Phase 0 baseline and Xcode readiness | 1–2 focused hours |
| Shared module, iOS host, and shared onboarding proof | 3–6 focused hours after Xcode setup |
| Pure contracts and first recipe/Explore vertical slice | 1–2 focused days |
| Remaining recipes, shared navigation, DataStore, and favorites | 2–4 focused days |
| Yelp, location, maps, and directions parity | 3–6 focused days |
| Remaining platform actions | 1–3 focused days |
| iOS polish, CI, and release hardening | 2–5 focused days |

A functional prototype is much smaller than a release-quality migration. Maps, iOS configuration, signing, accessibility, and cross-platform verification are expected to consume more time than moving most Compose layouts.

## Reference documentation

- [Compose Multiplatform FAQ and production status](https://kotlinlang.org/docs/multiplatform/faq.html)
- [Compose Multiplatform compatibility and versions](https://kotlinlang.org/docs/multiplatform/compose-compatibility-and-versioning.html)
- [Migrating a Jetpack Compose app to Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform/migrate-from-android.html)
- [Recommended Kotlin Multiplatform project structure](https://kotlinlang.org/docs/multiplatform/multiplatform-project-recommended-structure.html)
- [Android-KMP library plugin](https://developer.android.com/kotlin/multiplatform/plugin)
- [Compose Multiplatform supported platforms](https://kotlinlang.org/docs/multiplatform/supported-platforms.html)
- [Compose Multiplatform resources](https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources.html)
- [Navigation in Compose Multiplatform](https://kotlinlang.org/docs/multiplatform/compose-navigation.html)
- [Multiplatform ViewModel](https://kotlinlang.org/docs/multiplatform/compose-viewmodel.html)
- [UIKit interoperability and MapKit](https://kotlinlang.org/docs/multiplatform/compose-uikit-integration.html)
- [Room for Kotlin Multiplatform](https://developer.android.com/kotlin/multiplatform/room)
- [DataStore for Kotlin Multiplatform](https://developer.android.com/kotlin/multiplatform/datastore)
- [Ktor client engines](https://ktor.io/docs/client-engines.html)
- [Ktor releases](https://ktor.io/docs/releases.html)
- [Koin Kotlin Multiplatform setup](https://insert-koin.io/docs/reference/koin-core/kmp-setup/)
- [Koin for Compose Multiplatform](https://insert-koin.io/docs/reference/koin-compose/compose/)
- [Koin releases](https://insert-koin.io/docs/support/releases/)
