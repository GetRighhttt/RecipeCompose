# Kotlin Multiplatform migration assessment

Status: discovery complete; UI-strategy decision superseded by the Compose Multiplatform plan<br>
Date: 2026-08-15; decision updated 2026-08-22<br>
Scope: current Android app in `:app`, with Android and iOS as the likely future targets

> Decision update: the project will use Compose Multiplatform for shared Android/iOS UI, Koin for dependency injection, and Ktor with `kotlinx.serialization` for shared networking. Experimental Firebase authentication and analytics were removed because the product has no account-backed feature. The staged execution plan and current toolchain baseline are maintained in [Compose Multiplatform migration plan](COMPOSE_MULTIPLATFORM_MIGRATION_PLAN.md). This assessment remains the detailed inventory of Android coupling and migration risks.

## Executive recommendation

KMP is a reasonable fit for this app, but a full rewrite is not the best first move. The app already has a useful separation between domain models, repositories, state holders, and Compose screens. The separation is incomplete, though: several domain/data classes contain Android serialization or Room annotations, repositories are implemented directly with Retrofit/Gson, and the UI owns permissions, location, maps, navigation, resources, intents, and Android lifecycle objects.

Recommended direction:

1. Keep `:app` as the Android application.
2. Add a `:shared` KMP library targeting Android and iOS.
3. Move pure models, state, repository contracts, networking, search/business rules, and favorites persistence into `shared/commonMain` in stages.
4. Keep Android-only UI and integrations in `app` until an iOS app exists.
5. Decide later whether the Compose screens should move into Compose Multiplatform or whether iOS should use SwiftUI.

This gives the project a low-risk proof point: the shared module can be consumed by the existing Android app before an iOS UI is introduced. KMP source sets are designed for this split, and platform-specific implementations can be supplied with `expect`/`actual` or injected interfaces. See the [Kotlin Multiplatform project structure documentation](https://kotlinlang.org/docs/multiplatform/multiplatform-discover-project.html) and [Android's migration guide for existing projects](https://developer.android.com/kotlin/multiplatform/migrate).

## What exists today

The repository is currently a single Android application module:

```text
:app
└── src/main/java/com/example/recipe_app_compose
    ├── activities and app startup
    ├── core/components, navigation, utilities, permissions
    ├── di
    ├── features/categories/{data,domain,presentation}
    ├── features/location/{data,domain,presentation}
    └── ui/theme
```

There are no `commonMain`, `androidMain`, `iosMain`, `commonTest`, or iOS app source sets today. All Kotlin production code is under `app/src/main/java`.

The runtime behavior is:

```text
SplashScreenActivity
        |
        v
OnboardingActivity -- first run only / Preferences DataStore
        |
        v
MainActivity -- drawer, bottom bar, dialogs, Android navigation
        |
        +--> TheMealDB repositories --> Retrofit + Gson
        +--> Yelp repository ---------> Retrofit + Gson + API key
        +--> favorites ----------------> Room database
        +--> location/map -------------> Google Play Services + Google Maps Compose
        +--> images --------------------> Coil 2 / Android context
```

The current Android unit test task passes (`:app:testDebugUnitTest`) with focused coverage for startup routing, Yelp mapping/state/cancellation, location preferences, and recipe saved-state matching. Repository and persistence coverage should continue expanding before extraction.

## Migration options

| Option | What is shared | What remains platform-specific | Assessment for this app |
| --- | --- | --- | --- |
| KMP shared logic | Models, repositories, networking, database, state holders | Android Compose UI; SwiftUI on iOS | Recommended first phase. Lowest risk and good architectural value. |
| KMP + Compose Multiplatform | Most state, screens, theme, navigation, and logic | Maps, permissions, external actions, app entry points | Viable later. Requires replacing Android resources/context/navigation APIs. |
| KMP logic + native UIs | Shared logic only; Android Compose and iOS SwiftUI | All UI and platform UX | Best if native iOS UX is important or the team is stronger in SwiftUI. Requires a clean Swift-facing shared API. |
| Keep Android-only | No second target | Everything | Sensible if there is no iOS/product requirement. KMP adds build and library complexity without a second consumer. |

The current codebase does not require KMP to improve the Android app. KMP becomes compelling when the same recipe, search, favorites, location, and API behavior must serve iOS or another Kotlin target.

## File-by-file impact assessment

### 1. Gradle, modules, and project configuration

Files: `settings.gradle.kts`, `build.gradle.kts`, `app/build.gradle.kts`, `gradle/libs.versions.toml`, `gradle.properties`

Changes to consider:

- Add a `:shared` module and include it from `settings.gradle.kts`.
- Apply the Kotlin Multiplatform plugin to `:shared` and configure Android, `iosArm64`, and `iosSimulatorArm64` first. Add other iOS targets only when needed.
- Keep `com.android.application` on `:app`; the application module is still the Android entry point. The shared module should be a library, not an application.
- For a new KMP library module, evaluate the official `com.android.kotlin.multiplatform.library` plugin. Android documents it as the supported plugin for KMP library modules, while the existing Android application remains separate. The plugin has different Android DSL, resource, Java, and test behavior from a normal Android library. See the [Android-KMP library plugin documentation](https://developer.android.com/kotlin/multiplatform/plugin).
- Move dependencies into source-set-specific buckets. A dependency that is valid for `commonMain` must publish a KMP-compatible variant; Android-only dependencies belong in `androidMain` or remain in `:app`.
- Add `kotlinx-coroutines-core` to `commonMain`; do not expose `kotlinx-coroutines-android` from shared code.
- Add `kotlin.test` and coroutine test dependencies to `commonTest`. Keep Android instrumentation and Compose UI tests in Android source sets.
- Add iOS framework integration and an Xcode `iosApp` project. Decide whether to use direct framework integration, CocoaPods, or another supported integration path.
- Recheck Kotlin, AGP, KSP, Room, Compose, and iOS deployment-target compatibility as one matrix. Do not upgrade every library at the same time as the source extraction.
- The current build already reports migration-relevant warnings: `org.jetbrains.kotlin.android` is deprecated with AGP 9+, several `gradle.properties` flags are deprecated, and the legacy variant API is being used. Clean these warnings before or alongside KMP setup so KMP errors are not hidden by baseline Gradle warnings.
- `android.builtInKotlin=false` and `android.newDsl=false` are currently pinned in `gradle.properties`. Revisit them with the AGP/KMP migration guide rather than carrying them forward blindly.
- Decide how to generate configuration values for each platform. `BuildConfig` is Android-specific and cannot be the shared configuration mechanism.

Possible end state:

```text
settings.gradle.kts
├── include(":app")
├── include(":shared")
└── iosApp/                         # Xcode project, not a Gradle application module

shared/
├── build.gradle.kts
└── src/
    ├── commonMain/
    ├── commonTest/
    ├── androidMain/
    ├── androidHostTest/             # if enabled for the chosen Android-KMP plugin
    ├── iosMain/
    ├── iosTest/                     # if enabled/configured
    ├── iosArm64Main/
    └── iosSimulatorArm64Main/
```

### 2. Domain models and DTOs

Files under `features/categories/domain/model`, `features/location/domain/model`, and `core/util/Resource.kt`

Good candidates for `commonMain` after cleanup:

- `Resource`
- recipe/category/meal/ingredient value objects
- `LocationData`
- Yelp response/domain data
- `RecipeState`, `RandomMealState`, `IngredientMealState`, `DatabaseState`, and `YelpStates`
- repository interfaces
- pure search and formatting rules

Required changes:

- Remove `android.os.Parcelable` and `kotlinx.parcelize.*` from all shared models. This affects `Category`, `CategoryResponse`, `Ingredient`, `IngredientResponse`, `RandomMeal`, `RandomMealResponse`, `LocationData`, `YelpBusinesses`, `YelpCategories`, and `YelpSearchResult`.
- Remove `java.io.Serializable` from `YelpCoordinates` and `YelpLocations`; `java.io` is not common code.
- Replace navigation-time parceling with a platform-neutral route argument, normally a stable ID such as `category.idCategory.value`, and reload/select the object from state. The current `savedStateHandle` stores an entire `Category` as a Parcelable in `core/navigation/RecipeApp.kt`.
- Separate transport DTOs, domain models, and database entities. Today `Ingredient` and `RandomMeal` are both API-shaped models and UI/database-facing models, while `RandomMeal` also carries Room annotations. A cleaner split would be `RandomMealDto`, `RandomMeal`, and `FavoriteMealEntity` or a deliberate shared Room entity.
- Replace Gson `@SerializedName` with `kotlinx.serialization` `@SerialName` if the network layer moves to Ktor. Add `@Serializable` to DTOs rather than putting serialization annotations on the domain model unless the two shapes are intentionally identical.
- Review the inline value classes in `Category.kt`. Kotlin value classes are a reasonable common representation, but avoid Android parceling and ensure the generated iOS API is usable. For route and JSON boundaries, plain strings may be simpler.
- Consider replacing `UInt` in Yelp models and repository parameters with `Int`/`Long` plus validation. `UInt` is legal Kotlin but less natural at a Swift-facing boundary and is unnecessary for ordinary Yelp counts/limits.
- Move `YelpBusinesses.displayRating()` and `displayPhoneNumber()` out of the domain model. The methods are annotated `@Composable`, so the domain layer currently depends on Compose. Use a pure formatter in common code or format in each UI layer.
- Make nullable API fields and error semantics explicit. Decide whether missing Yelp address fields should be represented as nullable values, empty strings, or a normalized domain type.
- Normalize API error types. `Resource.Error` currently stores only a string; a shared layer will benefit from a stable error category such as network, unauthorized, rate-limited, server, decoding, and unknown.

### 3. TheMealDB and Yelp networking

Files: `features/categories/data/datasources/remote/api/ApiService.kt`, `RecipeRepositoryImpl.kt`, `RetrofitInstance.kt`, `SafeApiCaller.kt`, and the corresponding Yelp files

The repository contracts are good extraction seams, but the implementations are Android/JVM-shaped:

- Retrofit interfaces and `retrofit2.Response` should not be exposed from `commonMain`.
- Gson conversion and `GsonConverterFactory` are not the desired common serialization layer.
- `OkHttpClient`, `HttpLoggingInterceptor`, and `java.util.concurrent.TimeUnit` are currently configured in Android-oriented singleton objects.
- `Constants.BASE_URL`, `YELP_BASE_URL`, and `YELP_API_KEY` are obtained from Android `BuildConfig`.

Recommended shared replacement:

- Use a KMP HTTP client such as Ktor Client in `commonMain`, with `ContentNegotiation` and `kotlinx.serialization`.
- Provide an Android engine in `androidMain` and a Darwin engine in `iosMain`; Ktor documents this source-set pattern for Android/iOS clients. See [Ktor client engines](https://ktor.io/docs/client-engines.html) and [Ktor's KMP client setup](https://ktor.io/docs/full-stack-development-with-kotlin-multiplatform.html).
- Keep one shared request/repository implementation and inject the client, base URLs, auth header provider, and logger.
- Replace the two Retrofit singletons with one configurable shared `ApiClient` or two typed API clients that share a configured HTTP client.
- Configure JSON with unknown-key tolerance because third-party API payloads can add fields. Add explicit tests for all response shapes.
- Move retry, timeout, cancellation, and error mapping into a common policy. Preserve cancellation rather than converting it to a user-facing error.
- Preserve the current release policy that disables HTTP logging and the Yelp client&apos;s authorization-header redaction. Review whether debug body logging is appropriate before distributing debug builds.
- Decide how Yelp authentication works on iOS. A client-embedded Yelp key is extractable from any mobile binary; the safer design is a server-side proxy or a tightly restricted service credential.

An Android-only interim path is also valid: leave Retrofit in `app` and first move only models and repository interfaces to shared. That is useful if the team wants to validate the KMP module before replacing network code.

### 4. Favorites persistence and Room

Files: `RandomMeal.kt`, `RandomMealDAO.kt`, `RandomMealDatabase.kt`, `DatabaseRepositoryImpl.kt`, and `DependencyInjector.kt`

Room is no longer automatically an Android-only decision: the official Room KMP documentation says Room supports KMP from version 2.7.0, and this project currently declares 2.8.4. That makes shared favorites persistence plausible. See [Set up Room Database for KMP](https://developer.android.com/kotlin/multiplatform/room).

Changes still required:

- Move the entity and DAO into a shared data source only after confirming the exact Room/KSP versions for Kotlin 2.4.0 and the chosen KMP plugin.
- Add the KMP Room runtime and SQLite driver dependencies in the correct source sets. The KMP setup uses `androidx.sqlite:sqlite-bundled` and a platform-specific database builder/factory.
- Replace the Android-only `Room.databaseBuilder(context, ...)` call with a platform-specific database factory. Android uses `Context`; iOS needs a file path and native storage setup.
- Keep the database factory out of common code using an injected `DatabaseFactory` or `expect`/`actual`.
- Review the current destructive migration policy. `fallbackToDestructiveMigration()` can delete a user's favorites during schema changes; define real migrations before shipping cross-platform persistence.
- Preserve the repository&apos;s non-suspending `getMeals(): Flow<List<...>>` observation contract and suspend write operations when it moves to shared code.
- Decide whether the same database schema must be compatible across Android and iOS. If yes, add migration tests and test both drivers.
- Keep Room annotations in a data/entity package. If the same domain object is used by SwiftUI, do not make the public domain model depend on Room.

An alternative is SQLDelight or a small key-value store. Room is attractive here because the existing schema is tiny and the migration surface is already familiar, but SQLDelight can produce more explicit multiplatform SQL and Swift-friendly APIs.

### 5. ViewModels, state, and dependency injection

Files: `RecipeViewModel.kt`, `DatabaseViewModel.kt`, `LocationViewModel.kt`, `YelpViewModel.kt`, `DependencyInjector.kt`, and `RandomMealApp.kt`

The state flows are strong candidates for common code, but the current classes have Android lifecycle and global dependency coupling:

- `androidx.lifecycle.ViewModel`, `viewModelScope`, and `androidx.lifecycle.viewmodel.compose.viewModel()` are used directly.
- Constructor defaults read from `DependencyInjector`, which hides dependencies and requires the Android application to initialize first.
- `DependencyInjector` is an Android singleton that uses `Context`, Room, and lazy global repositories.
- `RandomMealApp` is an Android `Application` startup hook.
- Several composables instantiate ViewModels manually rather than obtaining them from a lifecycle owner. This can create duplicate state holders and scopes.

Recommended changes:

- Inject repositories, database stores, location services, connectivity monitors, and external actions explicitly. Remove production constructor defaults that reach into a global singleton.
- Move the shared state machines/use cases to common code. They can expose `StateFlow` and suspend commands without knowing whether the consumer is Compose or SwiftUI.
- Choose one lifecycle strategy:
  - use the KMP-capable AndroidX ViewModel artifact for shared ViewModels; current lifecycle versions are in the range where KMP ViewModel support is available, or
  - use platform-neutral state holders with an injected `CoroutineScope`, then let Android and iOS own lifecycle cancellation.
- If SwiftUI consumes the shared flows, plan how flows are bridged to Swift observation/Combine. If Compose Multiplatform is used, common Compose can collect them directly.
- Create an explicit `AppDependencies`/`AppContainer` factory per platform. Android can construct it from `Application`; iOS can construct it from the app delegate or Swift entry point.
- Keep navigation and screen-specific state in the UI layer. Shared state should not know about `NavHostController`, `Activity`, `Context`, or `SavedStateHandle` unless the chosen KMP ViewModel design specifically requires it.

Current behavior worth revisiting while extracting:

- The dish-search response model and related route/state names still use `Ingredient`, even though the endpoint searches complete meals by name. Rename the model and APIs when moving them so the shared contract describes the returned data accurately.
- The repository implementations never return `Resource.Loading`, so the loading branches in the ViewModels are currently unreachable. Either emit loading from a use case or model request state directly.
- `fetchCategories`, `fetchRandomMeal`, and `fetchIngredients` start separate fire-and-forget jobs from `RecipeViewModel.init`. During migration, make startup concurrency, cancellation, and failure isolation explicit and testable.

### 6. Location, connectivity, permissions, reverse geocoding, and maps

Files: `PermissionUtils.kt`, `PermissionsRequestLauncher.kt`, `LocationViewModel.kt`, `GoogleLocationSelectionScreen.kt`, `YelpScreen.kt`, and `AndroidManifest.xml`

This is the clearest platform boundary in the project. `PermissionUtils` currently combines five concerns:

1. Android runtime permission checks and launchers.
2. Google Play Services fused-location callbacks.
3. Android `Geocoder` reverse geocoding.
4. Android `ConnectivityManager` monitoring.
5. A Compose `rememberConnectivityState()` adapter.

Refactor toward common interfaces:

```text
commonMain
├── LocationProvider          -> Flow<LocationData>
├── PermissionState/Requester -> platform UX
├── ReverseGeocoder           -> suspend fun addressFor(LocationData)
├── ConnectivityMonitor       -> Flow<NetworkConnectionState>
└── MapLocation model         -> plain coordinates and display data

androidMain
├── FusedLocationProviderClient
├── ActivityResultContracts
├── ConnectivityManager
├── Geocoder
└── Google Maps Compose

iosMain / iosApp
├── CoreLocation permissions and updates
├── NWPathMonitor connectivity
├── CLGeocoder reverse geocoding
└── MapKit or Google Maps iOS SDK
```

Specific changes:

- Do not put `Context`, `PackageManager`, `Looper`, `LatLng`, `FusedLocationProviderClient`, or `ConnectivityManager` in common code.
- Keep permission request UI in each platform app. A shared state machine can report `Unknown`, `Denied`, `Restricted`, and `Granted`.
- Android's `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, and `INTERNET` are manifest declarations, not runtime permissions in the same sense as location. The current `RequestNetworkPermissions()` path should be reviewed rather than ported literally.
- Add iOS `Info.plist` location usage descriptions and configure the desired authorization level. Add any required network, URL-scheme, and map configuration in the iOS app target.
- The `GoogleLocationSelectionScreen` cannot be moved unchanged to common UI because it imports Google Maps Android Compose types. Keep it Android-specific, use MapKit or Google Maps iOS in native UI, or build a Compose Multiplatform map adapter with separate actual implementations.
- `LocationViewModel` should not call `android.util.Log`; use a common logger interface or a platform logger.
- Reverse geocoding is asynchronous on some platforms. Change the current synchronous `Geocoder.getFromLocation()` contract to a suspend function and model failures.
- Ensure location callbacks are removed when the screen/view model is disposed. The current location update registration does not show a matching removal path.

### 7. Authentication scope

Authentication and analytics are intentionally absent from the current application. Firebase was an Android learning experiment rather than support for an account-backed product capability, so its activities, screen, configuration files, Gradle plugin, and dependencies were removed before migration.

Do not add an auth abstraction to `commonMain` preemptively. If a future requirement such as cross-device favorite synchronization establishes a real account need, evaluate providers and platform policy at that time and add authentication as its own vertical slice.

### 8. Compose UI, navigation, resources, and platform actions

Files: `MainActivity.kt`, `SplashScreenActivity.kt`, `core/components/Widgets.kt`, `core/navigation/*`, all files under `presentation/view`, `ui/theme/*`, `app/src/main/res/*`

The layout and Material styling are the most reusable part of the UI, but the current screens are not common-ready.

Android-specific dependencies/usages to isolate:

- `ComponentActivity`, `setContent`, `enableEdgeToEdge`, and the Android activity lifecycle.
- `LocalContext`, `Intent`, `Toast`, `packageManager`, and `Activity.finish()`.
- `R.string`, `R.drawable`, Android XML themes, and Android launcher resources.
- `androidx.navigation` `NavHostController`, `SavedStateHandle`, and Parcelable route objects.
- `androidx.lifecycle.viewmodel.compose.viewModel()` and lifecycle-aware collection decisions.
- `androidx.activity.compose.rememberLauncherForActivityResult()`.
- Coil 2's Android-oriented `ImageLoader` construction.
- Google Maps Compose.

If sharing UI with Compose Multiplatform:

- Use a Compose Multiplatform-compatible plugin/dependency set rather than assuming the Android Compose BOM is enough.
- Replace `R` access and Android XML resources with a multiplatform resource strategy for strings, images, and other assets. Android launcher icons, manifest resources, backup rules, and XML themes remain in the Android app.
- Replace `LocalContext`-based image loader creation with a shared image-loading abstraction. Coil 3 supports Compose Multiplatform; its documentation recommends a Ktor network backend for multiplatform rendering. See [Coil's multiplatform setup](https://github.com/coil-kt/coil/blob/main/docs/getting_started.md).
- Replace `NavHostController` with a common destination model and a navigation implementation that supports the selected targets. Alternatively, keep navigation platform-specific and pass callbacks into shared screens.
- Use `collectAsState` or a common lifecycle-aware approach where `collectAsStateWithLifecycle` is not available in the selected common source set.
- Move `AppTheme`, typography, colors, and Material components only after confirming their Compose Multiplatform availability. Keep platform-specific system bars, splash behavior, and window configuration in the platform app.
- Move `ExternalLinkText` behind an `ExternalUriHandler` abstraction if it must be shared. `LocalUriHandler` may be available in a chosen common UI stack, but email/share actions still need platform adapters.
- Replace `painterResource` and Android drawable references in the splash screen with common resources or platform-specific splash screens.

If keeping native iOS UI:

- Leave the existing Android Compose screens in `:app`.
- Expose small, stable shared use-case/state APIs to SwiftUI rather than exporting Android Compose types.
- Keep a platform-specific screen for maps and permission flows.
- Decide whether the iOS app should reproduce the Android drawer/bottom-bar UX or use native iOS navigation and tab patterns.

Navigation-specific cleanup in `RecipeApp.kt`:

- Make `CategoryScreen` a common destination model with no `NavHostController` dependency.
- Pass a category ID or serialized route argument instead of a Parcelable `Category` object.
- Make screen state ownership explicit. The current app can create multiple `RecipeViewModel` and `DatabaseViewModel` instances in nested composables, which will matter even more when Android and iOS have different lifecycle models.

### 9. Images and media

Files: screen files using `rememberAsyncImagePainter`, `core/components/Widgets.kt`, `gradle/libs.versions.toml`

- Replace Coil 2 imports with Coil 3 KMP if sharing Compose UI, or keep Coil Android in `:app` and use a native iOS image loader.
- Continue using a single image loader. Unused Glide and its annotation processor were removed, leaving Coil as the Android implementation.
- Standardize image loading around URL strings and a shared placeholder/error policy.
- Move the bundled `dining_two.webp` and launcher assets into the appropriate common resource or platform asset locations. The obsolete `dinner.png` launcher was removed.
- The splash screen should be implemented as an Android splash/theme and an iOS launch screen rather than assuming one `SplashScreenActivity` can be shared.

### 10. Configuration, secrets, and platform files

Files: `app/build.gradle.kts`, `core/util/Constants.kt`, `app/src/main/AndroidManifest.xml`, and local `local.properties`

Changes and risks:

- `BuildConfig` values are generated only for the Android app. Create a shared `ApiConfig` interface and inject a platform/build-specific implementation.
- Do not copy the Android `local.properties` loading code into common code. It depends on `java.util.Properties`, Gradle project files, and Android BuildConfig generation.
- The Google Maps key is injected from `local.properties` through a manifest placeholder. Keep it restricted by Android package/SHA-1 and add a separately restricted key for the iOS bundle ID.
- The Yelp key is injected from `local.properties`, but any key shipped in an Android or iOS client can be extracted. Prefer a backend proxy; otherwise use the provider's restrictions and redact it from logs.
- Add iOS configuration equivalents: map key setup, API base URL configuration, bundle IDs, signing settings, URL schemes, and privacy usage descriptions.
- Review release configuration before adding an iOS target: minification, debug logging, symbolication, crash reporting, analytics consent, and per-platform environment values.

### 11. Tests and verification

Files: tests under `app/src/test/...` and `app/src/androidTest/...`

The generated placeholder tests have been removed. The project now has focused tests for startup routing, primary navigation, Yelp request mapping, Yelp state/cancellation behavior, phone formatting, and the offline fallback. Expand this baseline before extraction so Android behavior can be compared with shared behavior.

Recommended test layers:

- `shared/src/commonTest`: model normalization, `Resource` behavior, search matching, validation, error mapping, request/use-case state transitions, and repository tests with fake data sources.
- Ktor `MockEngine` tests: TheMealDB and Yelp success, HTTP errors, malformed payloads, cancellation, auth headers, and query parameters.
- Room KMP tests: insert, replace, observe, delete-one, delete-all, schema migration, and destructive-migration policy. Run against the Android and iOS-supported drivers where practical.
- Android tests: permission launcher, connectivity monitor, Google Maps/location adapter, Android navigation, and intent/share behavior.
- iOS tests: CoreLocation permission states, reverse geocoder, connectivity monitor, and map presentation adapter.
- Compose tests per UI target for loading, empty, error, search, favorite, and navigation flows.
- Add contract tests so Android and iOS implementations produce the same common error/state semantics.
- Add tests for lifecycle cancellation and duplicate requests; several current flows launch nested jobs and delay network calls.

Minimum migration gate for each extracted slice:

1. Existing Android unit/instrumented behavior remains green.
2. The shared module compiles for Android and iOS simulator.
3. The shared test suite runs without network access.
4. No common source imports Android, Java-only, Compose UI, Google Play Services, or Android resources.
5. The Android app consumes the shared implementation through interfaces, not global service locators.
6. A small iOS spike can call the shared repository/state API before the next slice is extracted.

## Suggested target architecture

```text
shared/commonMain
├── core
│   ├── Result/Error/Resource
│   ├── configuration (interfaces, no BuildConfig)
│   └── logging (interface)
├── features/recipes
│   ├── domain/model
│   ├── domain/repository
│   ├── domain/usecase
│   └── data
│       ├── remote (Ktor + kotlinx.serialization)
│       └── local (Room KMP or SQLDelight)
├── features/location
│   ├── model
│   ├── repository/contracts
│   └── usecase
└── presentation
    └── state holders or KMP ViewModels

shared/androidMain
├── Room database builder
├── Ktor Android engine
└── Android logger/configuration

shared/iosMain
├── Room/SQLite or SQLDelight iOS database factory
├── Ktor Darwin engine
└── iOS logger/configuration

app
├── Android activities/manifest/resources
├── Android Compose screens, or Compose Multiplatform UI entry point
└── permission/location/map adapters

iosApp
├── SwiftUI or Compose Multiplatform root
├── Info.plist and launch assets
└── iOS permission/location/map adapters
```

## Ordered migration plan

### Phase 0 — stabilize the Android baseline

- Rotate/restrict the committed Maps key and remove credentials from logs.
- Add repository, model, search, ViewModel/state, and favorites tests.
- Fix the stale-list and Yelp-filter issues before moving code.
- Extract pure search/formatting functions from composables and models.
- Keep the dependency graph intentional; Glide and the complete experimental Firebase stack have already been removed because the app did not use them.
- Clean deprecated Gradle/Kotlin flags and record the supported toolchain.

### Phase 1 — add the shared module without changing UI

- Create `:shared` with Android and iOS targets.
- Move `Resource`, plain models, state classes, and repository interfaces.
- Make `:app` depend on `:shared`.
- Keep Android implementations and UI working through adapters.
- Compile shared code for an iOS simulator as an empty/low-risk integration proof.

### Phase 2 — share recipe networking

- Introduce serializable DTOs and domain mapping in common code.
- Replace Retrofit/Gson with Ktor/kotlinx.serialization in common code, or temporarily keep an Android Retrofit adapter while the iOS client is prototyped.
- Add MockEngine and response/error contract tests.
- Inject API configuration and remove `Constants`' direct `BuildConfig` dependency.

### Phase 3 — share favorites storage

- Choose Room KMP or SQLDelight.
- Split entity from domain model.
- Add platform database factories and real schema migrations.
- Move the favorites repository and test it against both target drivers.

### Phase 4 — share state holders and onboarding persistence

- Move state holders/use cases to common code and expose a Swift-friendly API if using SwiftUI.
- Move the onboarding completion contract while keeping platform DataStore construction explicit.
- Preserve the direct onboarding-to-main startup policy on both platforms.

### Phase 5 — choose the UI strategy

- Native UI path: build iOS SwiftUI screens against the shared API and keep Android Compose unchanged.
- Shared UI path: migrate resources, navigation, image loading, lifecycle collection, and platform actions to Compose Multiplatform; leave maps and permission surfaces behind platform adapters.

### Phase 6 — platform services and release hardening

- Implement Android/iOS location, connectivity, reverse geocoding, maps, external URL/share, logging, and configuration adapters.
- Add iOS privacy strings, map setup, signing, and release configuration.
- Run Android and iOS functional parity checks for onboarding, recipe browsing, ingredient search, Yelp search, map selection, favorites, and directions.

## Final recommendation

Proceed with a shared-logic KMP spike, not a full UI migration. The highest-value shared slice is:

```text
plain models
  -> Ktor/kotlinx.serialization repositories
  -> recipe/Yelp use cases and search logic
  -> StateFlow state holders
  -> Android app first, iOS simulator second
```

Room KMP is now a credible option for favorites, but it should follow the networking/model extraction rather than lead the migration. Maps, permissions, reverse geocoding, external actions, and app startup should remain behind platform adapters. Compose Multiplatform should be a separate product/UX decision, because it changes resource, navigation, lifecycle, and platform-integration strategy beyond simply adding KMP.

## Reference documentation

- [Kotlin Multiplatform project structure](https://kotlinlang.org/docs/multiplatform/multiplatform-discover-project.html)
- [Add KMP to an existing Android project](https://developer.android.com/kotlin/multiplatform/migrate)
- [Hierarchical KMP source sets](https://kotlinlang.org/docs/multiplatform/multiplatform-hierarchy.html)
- [Android-KMP library plugin](https://developer.android.com/kotlin/multiplatform/plugin)
- [Room for KMP](https://developer.android.com/kotlin/multiplatform/room)
- [KMP ViewModel](https://developer.android.com/kotlin/multiplatform/viewmodel)
- [KMP DataStore](https://developer.android.com/kotlin/multiplatform/datastore)
- [Ktor multiplatform client engines](https://ktor.io/docs/client-engines.html)
- [Compose Multiplatform overview](https://kotlinlang.org/docs/multiplatform/compose-multiplatform.html)
- [Coil Compose Multiplatform setup](https://github.com/coil-kt/coil/blob/main/docs/getting_started.md)
