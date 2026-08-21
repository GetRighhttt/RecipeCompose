# Gradle Kotlin DSL migration notes

Status: Android build migrated and verified<br>
Date: 2026-08-21<br>
Related planning: [Kotlin Multiplatform migration assessment](KMP_MIGRATION_ASSESSMENT.md), [Compose Multiplatform migration plan](COMPOSE_MULTIPLATFORM_MIGRATION_PLAN.md)

## Summary

The project now uses Gradle Kotlin DSL for the root project, settings, and Android app module:

- `settings.gradle.kts`
- `build.gradle.kts`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`

The repository was already using `.gradle.kts` files when this pass started, so this was not a full Groovy-to-Kotlin file conversion. The cleanup focused on making the existing Kotlin DSL setup more consistent, more centralized, and easier to evolve toward a future Kotlin Multiplatform or Compose Multiplatform structure.

## What changed

- Moved remaining hardcoded plugin versions into the Gradle version catalog.
- Added catalog aliases for Room, KSP, Google Services, and Firebase Performance plugins.
- Replaced raw plugin declarations in `build.gradle.kts` and `app/build.gradle.kts` with `alias(libs.plugins...)` declarations.
- Removed stale comments that described older Gradle setup patterns rather than the current build.
- Preserved the existing Android application module layout and dependency graph.

## Why this matters

Centralizing plugin versions in `gradle/libs.versions.toml` gives the project a single place to reason about build tooling compatibility. That becomes especially useful before adding a `:shared` module, because KMP and Compose Multiplatform require Kotlin, AGP, KSP, Room, Compose Compiler, and AndroidX libraries to line up cleanly.

This also keeps the Android app build readable:

- project-level build files declare which plugins are available;
- module-level build files apply only the plugins the module needs;
- plugin versions live beside dependency versions in the catalog.

That structure is closer to how this project would be maintained in a larger production codebase.

## Current plugin ownership

| Plugin | Version source | Applied in |
| --- | --- | --- |
| Android application | `libs.versions.toml` | `:app` |
| Compose compiler | `libs.versions.toml` | `:app` |
| Kotlin Parcelize | `libs.versions.toml` | `:app` |
| KSP | `libs.versions.toml` | `:app` |
| Google Services | `libs.versions.toml` | `:app` |
| Secrets Gradle Plugin | `libs.versions.toml` | `:app` |
| Room Gradle Plugin | `libs.versions.toml` | available at root, not currently applied in `:app` |
| Firebase Performance Plugin | `libs.versions.toml` | available at root, not currently applied in `:app` |
| Foojay toolchain resolver | inline in `settings.gradle.kts` | settings plugin block |

The Foojay resolver version remains inline because settings plugins cannot use the project version catalog in the same straightforward way as project plugins.

## Verification

The Kotlin compilation check passes:

```bash
./gradlew :app:compileDebugKotlin
```

The build still reports existing Android Gradle Plugin warnings for deprecated `gradle.properties` flags and a Room warning for `fallbackToDestructiveMigration()`. Those warnings are not caused by the Kotlin DSL cleanup, but they should be addressed before a larger KMP or Compose Multiplatform migration so build output stays trustworthy.

## Follow-up cleanup

- Remove or replace deprecated Android Gradle Plugin flags in `gradle.properties`.
- Replace the deprecated Room destructive migration call with the newer overload or explicit migrations.
- Remove the redundant `-Xexplicit-backing-fields` compiler flag once the project no longer needs to document that compiler behavior manually.
- Decide whether unused root plugin aliases, such as Firebase Performance or Room, should stay because they represent planned build support or be removed until they are actively applied.
- Remove unused dependencies such as Glide if source search continues to confirm they are not used.
