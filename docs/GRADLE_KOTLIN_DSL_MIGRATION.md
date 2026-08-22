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
- Added catalog aliases for the plugins applied by the Android build, including KSP, Google Services, and the Secrets Gradle Plugin.
- Replaced raw plugin declarations in `build.gradle.kts` and `app/build.gradle.kts` with `alias(libs.plugins...)` declarations.
- Upgraded the Gradle wrapper from 9.5.0 to 9.7.0 and regenerated the wrapper JAR and launch scripts with Gradle's wrapper task.
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
| Foojay toolchain resolver | inline in `settings.gradle.kts` | settings plugin block |

The Foojay resolver version remains inline because settings plugins cannot use the project version catalog in the same straightforward way as project plugins.

## Verification

The complete Gradle 9.7 verification suite passes:

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

The follow-up cleanup removed deprecated Android Gradle Plugin flags, the redundant explicit-backing-fields compiler flag, and the deprecated Room migration overload. Unused plugin aliases and dependencies were also removed so the catalog reflects the build that is actually applied.

The project now runs Gradle 9.7.0 with Android Gradle Plugin 9.3.1, Kotlin 2.4.10, and the configured Java 21 build runtime. No explicit-backing-fields compiler argument remains; Kotlin 2.4 language support handles the feature directly.

The resulting debug APK was also installed and cold-launched on the physical Samsung test device. The Explore screen rendered successfully, and the isolated crash buffer remained empty.

## Follow-up cleanup

- Replace destructive Room fallback with explicit migrations before changing a production schema.
- Add shared-module plugin aliases only when the Compose Multiplatform scaffold is introduced.
- Keep the Kotlin, Compose compiler, KSP, Room, AGP, and Gradle versions aligned as the toolchain evolves.
