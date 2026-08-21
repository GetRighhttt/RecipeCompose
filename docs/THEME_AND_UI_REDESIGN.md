# Theme and UI Redesign

## Purpose

This document records the first visual-system cleanup for Recipe Compose. The goal is not to redesign every screen at once. It establishes a consistent foundation that later screen-level changes can reuse without introducing one-off colors, typography, or shadows.

The application already had Material 3 theme support, but the previous implementation was largely generated code. It included light, dark, medium-contrast, and high-contrast palettes even though only the light and dark schemes were selected. Dynamic color was enabled by default, which also meant Android 12 and newer devices could replace the app palette with colors derived from the user's wallpaper.

## Design direction

Recipe Compose now uses a warm, food-inspired identity:

| Role | Visual intent | Typical usage |
| --- | --- | --- |
| Primary | Paprika and tomato red | Primary actions, active controls, prominent emphasis |
| Secondary | Sage and olive | Supporting actions, filters, and lower-priority accents |
| Tertiary | Saffron and amber | Occasional highlights that need distinction from primary actions |
| Background | Warm off-white or near-black | Full-screen foundations |
| Surface containers | Progressively stronger warm neutrals | Navigation, cards, dialogs, grouped content |
| Error | Material red | Destructive actions, validation, and swipe-to-delete feedback |

The colors are intentionally semantic. A composable should normally request `MaterialTheme.colorScheme.primary`, `surfaceContainer`, or another role instead of importing a raw color from `Color.kt`. That allows the same component to work in both light and dark mode.

## Changes made

### 1. Simplified color configuration

`Color.kt` now contains one light palette and one dark palette. The unused generated medium-contrast and high-contrast values were removed along with the unused `ColorFamily` wrapper.

This reduces the theme from hundreds of generated lines to the values the application actually consumes. If explicit contrast modes are implemented later, they should be added as a complete accessibility feature that reads the user's contrast preference and selects the appropriate scheme.

### 2. Stable branding with system light and dark mode

`AppTheme` still defaults to `isSystemInDarkTheme()`, so the application follows the device appearance setting. Dynamic wallpaper color is no longer enabled by default.

These are separate concepts:

- System light/dark mode decides whether Recipe Compose uses its light or dark brand palette.
- Dynamic color replaces brand colors with colors generated from the device wallpaper.

For a portfolio application, a stable palette makes screenshots, demonstrations, and the overall product identity more consistent. Dynamic color can be reintroduced later as an explicit user setting if desired.

### 3. Defined a typography hierarchy

The previous `Typography()` call used every Material default without expressing an application-specific hierarchy. The updated theme defines the text styles currently used most often:

- Headlines and large titles use semibold weight for clear screen hierarchy.
- Medium titles use medium weight for section and component titles.
- Body styles remain normal weight for comfortable reading.
- Labels use medium or semibold weight so buttons and compact UI remain legible.
- Explicit line heights improve multi-line readability and reduce inconsistent vertical rhythm.

The system font remains in use. This avoids adding font downloads, APK size, or licensing considerations while the broader visual direction is still being evaluated.

### 4. Reassigned large background areas

The login and splash screens previously used `tertiaryContainer` as a full-screen background. The bottom application bar used the same accent container. This made an accent role dominate unrelated areas of the app.

The updated usage is:

- Full-screen login and splash foundations use `background`.
- The bottom application bar uses `surfaceContainer`.
- Tertiary colors remain available for smaller, intentional highlights.

The login screen also supplies its color through `Scaffold.containerColor`. This is the component's intended API and is more reliable than drawing a background modifier behind a scaffold that paints its own surface.

### 5. Reduced exaggerated elevation

Several dialogs and buttons used elevations between 10 dp and 20 dp. Large shadows can make every element appear to float at the same importance level and tend to make a modern Material 3 interface feel visually heavy.

The dialogs now use a 3 dp elevation and `surfaceContainerHigh`. Elevated buttons use Material 3's standard elevated-button values. This creates hierarchy through both surface tone and restrained depth.

## How Material 3 color roles should be used

Use the following rules when updating remaining screens:

- `background`: the base color behind a whole screen.
- `surface`: content that belongs at the base surface level.
- `surfaceContainerLow`: subtle groups or low-emphasis cards.
- `surfaceContainer`: navigation bars and standard grouped regions.
- `surfaceContainerHigh`: dialogs or content that needs stronger separation.
- `primary`: the main action on a screen.
- `secondary`: supporting actions or selectable metadata.
- `tertiary`: rare accents that should not compete with primary actions.
- `error` and `errorContainer`: destructive or invalid states only.
- `on...` roles: content drawn on the matching color. For example, use `onPrimary` on `primary` and `onSurface` on `surface`.

Avoid choosing a role only because its current hex color looks convenient. The role describes behavior across themes; the palette provides its appearance.

## UX reasoning

### Color should communicate priority

If the same accent color fills a screen, navigation bar, card, and button, it stops communicating importance. Neutral surfaces provide visual rest, while the paprika primary color can identify the action the user is most likely to take next.

### Typography should communicate structure

Size alone does not create a readable hierarchy. Weight, line height, and spacing help users quickly distinguish a screen title, section title, body explanation, and action label. A small, consistent set of text styles also makes later responsive-layout work easier.

### Depth should be restrained

Material 3 can separate layers through tonal surfaces as well as shadows. Small elevation plus a distinct surface container is usually enough for a dialog. This keeps attention on recipe imagery and restaurant content instead of decoration around it.

### Food imagery should remain the strongest visual content

The supporting palette is warm but relatively quiet. Recipe photos and restaurant imagery can provide the broad range of saturated colors. The interface should frame that content rather than compete with it.

## Recommended next pass

The theme is a foundation, not a complete product redesign. The next pass should be completed screen by screen:

1. Standardize horizontal screen padding and vertical section spacing.
2. Define reusable card treatments for recipes, favorites, and restaurants.
3. Review image aspect ratios, cropping, placeholders, and loading states.
4. Make the primary action visually obvious on login, details, maps, and settings.
5. Replace rows that use spacer padding for alignment with responsive arrangements and constraints.
6. Review compact phones, large phones, tablets, landscape, font scaling, and both themes.
7. Add screenshot tests only after the visual system stabilizes enough for snapshots to provide value.

## Accessibility checks

Before calling the redesign complete, verify:

- Text and icon contrast in light and dark modes.
- Touch targets remain at least 48 dp even when icons are visually smaller.
- Content works at increased system font sizes without clipping.
- Meaning is not conveyed by color alone.
- Destructive actions have clear labels and confirmation where data loss is significant.
- TalkBack traversal follows the visual reading order.
- Motion does not block navigation and respects reduced-motion expectations where applicable.

## Files involved

- `app/src/main/java/com/example/recipe_app_compose/ui/theme/Color.kt`
- `app/src/main/java/com/example/recipe_app_compose/ui/theme/Theme.kt`
- `app/src/main/java/com/example/recipe_app_compose/ui/theme/Type.kt`
- `app/src/main/java/com/example/recipe_app_compose/LoginActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/SplashScreenActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/MainActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/core/components/Widgets.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/categories/presentation/view/FavoritesScreen.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/categories/presentation/view/SettingsScreen.kt`
