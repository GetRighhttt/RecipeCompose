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

## Screen-level objectives

The theme established a foundation rather than a complete product redesign. The following objectives guided the screen-by-screen work documented in part two:

1. Standardize horizontal screen padding and vertical section spacing.
2. Define reusable card treatments for recipes, favorites, and restaurants.
3. Review image aspect ratios, cropping, placeholders, and loading states.
4. Make the primary action visually obvious on login, details, maps, and settings.
5. Replace rows that use spacer padding for alignment with responsive arrangements and constraints.
6. Review compact phones, large phones, tablets, landscape, font scaling, and both themes.
7. Capture representative device screenshots after the visual system stabilizes.

## Part two: screen-level visual system

The second pass applies the theme principles to the application's actual layouts. It focuses on reusable rules rather than making isolated cosmetic edits to each screen.

### Shared dimensions and shapes

`Dimens.kt` is now the source of truth for the small spacing scale, minimum touch target, responsive grid width, readable-content width, and standard card/control shapes.

This does not mean every distance in the application must become a token. Values belong in the shared system when they express a repeated design decision. Unique measurements tied to one component, such as a map control offset, can remain local.

The spacing scale is:

| Token | Value | Intended use |
| --- | --- | --- |
| Extra small | 4 dp | Tight relationships inside one content group |
| Small | 8 dp | Compact component spacing |
| Medium | 12 dp | Grid gaps and card content relationships |
| Large | 16 dp | Standard screen padding and section separation |
| Extra large | 24 dp | Major section breaks and roomy content padding |
| Extra extra large | 32 dp | Large page-level separation |

Using a small scale creates rhythm. Random values such as 5, 10, 15, 20, 30, 40, 50, and 100 dp previously made similar layouts feel unrelated and were difficult to adapt across screen sizes.

### Reusable media cards

`AppMediaCard` and `AppHorizontalMediaCard` own the common presentation for category, meal, saved-dish, and restaurant cards:

- A consistent 16 dp container shape.
- A square, cropped image area.
- A tonal `surfaceContainerLow` background.
- A single click target covering the complete card.
- Consistent 12 dp content padding.

Keeping the full card clickable improves discoverability and provides a larger touch target than making only the image clickable. The content slot still lets each feature provide the information it needs: a category title, meal name, or restaurant rating and address.

### Adaptive grids

Grid layouts remain only where rapid visual scanning is the primary task. Category meals use `GridCells.Adaptive` with the standard 152 dp minimum width. Dish search uses a denser 104 dp minimum to produce three image-first columns on a typical 360 dp phone; at a system font scale of 1.3 or greater it returns to the wider 152 dp minimum so labels retain room to breathe.

This replaces fixed two-column assumptions without forcing every destination into a grid. Compose calculates how many columns fit in the available width, while editorial content, saved dishes, and restaurants use layouts that better match their information density.

### Clearer card content hierarchy

Card titles now use `titleMedium`, allow two lines, and truncate safely when required. Restaurant cards separate the business name, rating, and address into distinct typographic roles:

- The name is the primary card content.
- The rating uses the tertiary accent for a compact highlight.
- The address uses `onSurfaceVariant` to remain readable without competing with the name.

Decorative emoji was removed from rating text in favor of a stable star character and semantic color. Phone numbers were removed from the compact restaurant row to reduce density; the row's job is to support quick comparison and navigation.

### Natural detail-page scrolling

The category detail and random-meal content now let the outer page own vertical scrolling. The previous random-meal page placed instructions and ingredients inside separate fixed-height scrolling areas. Nested scrolling makes content feel trapped, hides information behind small viewports, and creates competing gestures.

With one vertical owner:

- Instructions expand to their natural height.
- All ingredients remain discoverable in the same reading flow.
- Font scaling no longer has to fit inside an arbitrary 80 dp or 100 dp region.
- Scroll position and gesture behavior are easier for users to understand.

The category detail page also uses a maximum readable width of 640 dp. On larger displays, this prevents long lines of description text from stretching across the entire screen.

### Authentication removal and honest feature scope

The previous Settings destination presented nine placeholder rows even though the application did not provide configurable settings. It also repeated the Settings title inside the page below the navigation title. Those rows were useful while experimenting with dialogs and Compose state, but they implied functionality the product did not have.

The temporary Account replacement was also removed once authentication was recognized as an experiment rather than a product requirement. Recipe discovery, local favorites, nearby shops, maps, and directions do not need an identity, so forcing account creation added friction without unlocking behavior. The drawer now contains only Home and Info, and Firebase Authentication and Analytics are no longer dependencies.

### Navigation and map review

The bottom bar now uses a restrained top-corner shape and a standard 64 dp content height instead of appearing as a 70 dp fully rounded floating pill across the screen.

The map itself remains full bleed because the geographic content is the screen. Its directions action was already implemented as a Material 3 extended floating action button, aligned to the bottom start to avoid Google's zoom controls on the right. No additional card container was added around the map because that would reduce useful map area without improving hierarchy.

### Informational and fallback screens

The information page now uses a centered, width-constrained tonal surface so its small amount of content reads as one intentional group on phones and tablets. Ingredient labels use consistent tonal rows rather than unstyled text separated by arbitrary padding.

The existing network and location fallback screens already followed the desired pattern: one clear title, supporting explanation, primary recovery action, and secondary alternative. They were reviewed but did not need structural changes in this pass.

### Featured dish and saved-meal refinement

The original `See Our Best Dishes!` title sounded more like promotional copy than application navigation. The useful random-meal experience is now titled `Featured Dish`.

The Featured Dish page keeps both of its useful actions, but no longer places two bare icons around a compressed title. The meal name now leads a tonal header card. Below it, `Save` is a labeled tonal button and `Another` is a labeled outlined button. Labels reduce icon ambiguity, while the different button treatments establish priority. After a successful save, the action reads `Saved` and is disabled to prevent repeated inserts from the same visible meal.

Saved dishes no longer open a small management dialog. Selecting one now navigates to the complete recipe-details destination so saved content has the same reading experience as search and Featured Dish. An error-colored close action replaces the save heart in the title card. Its container stays transparent at rest, appears only while pressed, and opens a confirmation before deletion. Swipe-to-delete remains available on the list for users who prefer the faster management gesture.

The separate Explore Dishes modal was removed after product review. It fetched a random category, displayed another image grid, and offered Refresh without advancing the recipe-discovery or navigation workflow. Its bottom-bar entry point, modal UI, screen, ViewModel state, repository method, API endpoint, response models, and unused preview dialog were removed together.

### Modern featured-dish details

The Featured Dish page no longer uses a generic `Details` heading followed by labels, repeated dividers, and “Click here” links. That structure treated metadata, navigation, long-form instructions, and ingredients as if they had the same importance.

The page now follows an editorial recipe hierarchy:

1. Meal name and Save/Another actions.
2. A wide 4:3 hero image.
3. Compact Category and Cuisine metadata cards.
4. A Resources section with `View original recipe` and `Watch video instructions` actions, shown only when the corresponding URL exists.
5. Instructions in a readable tonal surface.
6. A numbered ingredient list using the API's natural text casing.

The content is constrained to 640 dp on wide displays so instructions remain comfortable to read. Resource actions use full-width labeled buttons instead of ambiguous inline links, and the old “Click here” copy was also removed from the ingredient-detail page.

### Shared recipe-detail presentation

Selecting a dish from search previously opened an older detail layout with a square image, generic dividers, raw metadata labels, and a separate ingredient-row treatment. The search flow now opens a dedicated `Recipe Details` page that uses the same editorial hierarchy as Featured Dish.

The common image, metadata, resources, instructions, and ingredient sections are owned by `MealDetailsContent`. `MealDetailsPage` also owns the width constraint, outer scrolling, and page padding shared by search and saved-dish routes. Featured Dish supplies its Save and Another Dish controls through an optional action slot. Search supplies a title-level save heart whose state is derived from Room, while a saved dish supplies the title-level remove action. This keeps each destination honest without allowing separate details implementations to drift visually.

API-provided category and cuisine values are optional. When either value is blank, the metadata card now displays `Unknown` rather than rendering an apparently broken empty value. Optional source and YouTube actions remain hidden when their URLs are absent.

### Preview-driven visual iteration

The project now includes a debug-only screen preview catalog under `app/src/debug`. Shared preview fixtures provide realistic categories, recipes, favorites, and restaurant results without shipping fake data in release builds.

The catalog renders paired light and dark previews for onboarding, cuisine browsing and details, dish search and recipe details, Featured Dish, Saved, saved-dish details, Shops, location choice and permission fallbacks, Info, offline handling, and map fallback/control states. Service-owning entry points remain responsible for ViewModels, permissions, Room, and network setup; previews call extracted stateless content instead. This separation makes layout editing fast without making production composables aware of preview mode.

Google Maps does not reliably render its live map canvas in Android Studio Preview. The catalog therefore previews the real Directions control on a neutral map placeholder and separately previews the invalid-location fallback. Marker movement, map loading, and Google-rendered controls still require a device or emulator.

### Layouts based on user intent

Using a two-column grid everywhere made unrelated screens feel generic and forced information-rich cards into narrow spaces. Layouts are now selected according to what the user is doing:

- Browse Cuisines uses a single-column editorial feed with wide 16:9 images. Categories are broad entry points and benefit from visual presence.
- Dish search uses a compact image-first adaptive grid because fast scanning is useful for a potentially large result set. Labels are secondary to imagery, and the minimum tile width expands under larger font scales.
- Saved uses a compact horizontal list because it is a local collection and management screen, and the wider swipe surface makes deletion easier to understand.
- Restaurants uses a compact horizontal list so business name, rating, and address remain readable while comparing nearby options.

`AppHorizontalMediaCard` provides the common foundation for compact list items, while `AppMediaCard` supports a configurable image ratio for galleries and editorial cards. Sharing these primitives keeps shape, color, image cropping, and touch behavior consistent without forcing every feature into the same layout.

## Part three: product-flow refinement

The final design pass focused on how the screens connect, not only how each screen looks in isolation.

### First-run onboarding

A three-page onboarding flow now introduces discovery, local saving, and nearby restaurant navigation before the main application. The artwork is built from Material color roles and vector icons, so it follows light and dark mode without maintaining separate image assets. Users can move through the pager, go back between pages, or skip immediately.

Completion is stored with Preferences DataStore. Its coroutine and Flow APIs keep persistence off the main thread, while the startup decision remains explicit: onboarding is shown only until completion, then startup opens the main application. A stateless content boundary keeps the screen previewable without introducing preview-only behavior into production code.

### Explore as an action hub

The former home experience depended heavily on generic content lists and a separate Explore Dishes experiment that only refreshed data. The redesigned Explore page gives the product a clear starting point:

- Search dishes, Nearby shops, and Saved dishes are visible as primary shortcuts.
- A featured meal adds useful discovery content rather than another duplicate grid.
- The same four destinations remain available in the persistent bottom bar.
- Bottom-navigation selection is route driven, so opening Search from the home shortcut still selects Search and leaves Explore available as a one-tap return path.

This consolidates existing capabilities instead of adding a screen without a distinct job.

### User-driven and bounded location resolution

Nearby now begins in an explicit `LocationChoiceRequired` state. It does not resolve location or request permission simply because the route was opened. The user chooses **Use my location** or enters a city or ZIP code, and Android permission is requested only after the location action.

When location is authorized, the provider accepts a coordinate cached within the last five minutes before requesting a fresh one. The ViewModel bounds the complete resolution attempt to 12 seconds. A missing or slow coordinate therefore becomes a visible retry/manual-search fallback instead of an indefinite loading state. Permission denial and coordinate unavailability remain separate states because only the former should direct a user to application settings.

Successful restaurant results and their search origin remain in the route-scoped ViewModel while that navigation entry is alive. This avoids replacing useful content during ordinary recomposition or a temporary Activity recreation.

### Portfolio screenshot set

The README now uses a current light-theme gallery covering onboarding, cuisine browsing, Explore, dish search, recipe details, Featured Dish, nearby restaurant results, the interactive map, and Saved management. The map capture verifies that the destination marker renders and that the Directions button remains on the left, clear of Google's zoom controls. Authentication captures are no longer presented because account access is not part of the product.

## Verification and remaining visual checks

The debug build, location ViewModel unit tests, and primary device journeys were exercised after the final pass. The remaining checks are useful release-hardening work rather than blockers for the redesign documentation:

1. Compact phone in portrait with the keyboard open on dish and manual-location search.
2. Standard phone in light and dark mode.
3. Landscape phone and a tablet-sized emulator to confirm adaptive column counts.
4. System font scales of 1.0, 1.3, and 1.5.
5. Very long recipe, meal, and restaurant names.
6. Missing or slow-loading remote images.
7. Saved-dish swipe behavior on compact and expanded list widths.
8. Map zoom controls and the directions button in portrait and landscape.

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
- `app/src/main/java/com/example/recipe_app_compose/ui/theme/Dimens.kt`
- `app/src/main/java/com/example/recipe_app_compose/SplashScreenActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/MainActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/OnboardingActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/core/navigation/CategoryNavigation.kt`
- `app/src/main/java/com/example/recipe_app_compose/core/navigation/RecipeApp.kt`
- `app/src/main/java/com/example/recipe_app_compose/core/onboarding/OnboardingPreferences.kt`
- `app/src/main/java/com/example/recipe_app_compose/core/components/Widgets.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/categories/presentation/view/FavoritesScreen.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/categories/presentation/view/IngredientScreen.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/categories/presentation/view/MealDetailsContent.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/location/data/location/AndroidCurrentLocationProvider.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/location/presentation/view/YelpScreen.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/location/presentation/viewmodel/YelpViewModel.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/onboarding/presentation/OnboardingScreen.kt`
- `app/src/debug/java/com/example/recipe_app_compose/preview/PreviewData.kt`
- `app/src/debug/java/com/example/recipe_app_compose/preview/ScreenPreviews.kt`
