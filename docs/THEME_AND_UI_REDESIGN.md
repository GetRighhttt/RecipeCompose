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

`AppMediaCard` owns the common presentation for category, meal, favorite, ingredient, and restaurant cards:

- A consistent 16 dp container shape.
- A square, cropped image area.
- A tonal `surfaceContainerLow` background.
- A single click target covering the complete card.
- Consistent 12 dp content padding.

Keeping the full card clickable improves discoverability and provides a larger touch target than making only the image clickable. The content slot still lets each feature provide the information it needs: a category title, meal name, or restaurant rating and address.

### Adaptive grids

The browse, category-meal, ingredient, favorites, and restaurant grids now use `GridCells.Adaptive` with a minimum card width of 152 dp. Compose calculates how many columns can fit in the available width.

This replaces fixed two-column and three-column assumptions. A compact phone can reduce the column count when necessary, while a tablet or landscape layout can use the additional space without a separate screen implementation. The card width, grid spacing, and screen padding stay predictable.

### Clearer card content hierarchy

Card titles now use `titleMedium`, allow two lines, and truncate safely when required. Restaurant cards separate the business name, rating, and address into distinct typographic roles:

- The name is the primary card content.
- The rating uses the tertiary accent for a compact highlight.
- The address uses `onSurfaceVariant` to remain readable without competing with the name.

Decorative emoji was removed from rating text in favor of a stable star character and semantic color. Phone numbers were removed from the compact grid tile to reduce density; the tile's job is to support quick comparison and navigation.

### Natural detail-page scrolling

The category detail and random-meal content now let the outer page own vertical scrolling. The previous random-meal page placed instructions and ingredients inside separate fixed-height scrolling areas. Nested scrolling makes content feel trapped, hides information behind small viewports, and creates competing gestures.

With one vertical owner:

- Instructions expand to their natural height.
- All ingredients remain discoverable in the same reading flow.
- Font scaling no longer has to fit inside an arbitrary 80 dp or 100 dp region.
- Scroll position and gesture behavior are easier for users to understand.

The category detail page also uses a maximum readable width of 640 dp. On larger displays, this prevents long lines of description text from stretching across the entire screen.

### Responsive authentication

The login screen no longer uses 100 dp of fixed vertical padding. Its content now:

- Respects the scaffold's system-bar insets at the page level.
- Scrolls when the keyboard or a compact display reduces available height.
- Uses a maximum width of 480 dp on large displays.
- Uses shared spacing and a minimum 48 dp login action.
- Uses the standard control shape instead of a one-off 5 dp corner radius.

The important modifier distinction is that `padding(innerPadding)` belongs around the screen content, not on the logo. `Scaffold` reports space consumed by system bars and app chrome; applying it to only one child leaves the rest of the screen unaware of those insets.

### Settings structure and action hierarchy

The settings screen previously repeated nine booleans and nine nearly identical text-button blocks. It also positioned account buttons using 40 dp start/end padding inside a row. That could squeeze or overlap labels on compact screens and with larger fonts.

The updated implementation uses:

- A `SettingsPage` enum as the source of page title and dialog content.
- One nullable `activePage` state instead of nine independent dialog booleans.
- A tonal surface to group related settings rows.
- Full-width rows with at least a 48 dp touch target.
- Vertically stacked account actions that remain usable at any supported width.
- A primary filled sign-out action and an error-colored outlined delete action.

The visual distinction between sign out and delete communicates that account deletion is destructive without making both actions look equally prominent.

### Navigation and map review

The bottom bar now uses a restrained top-corner shape and a standard 64 dp content height instead of appearing as a 70 dp fully rounded floating pill across the screen.

The map itself remains full bleed because the geographic content is the screen. Its directions action was already implemented as a Material 3 extended floating action button, aligned to the bottom start to avoid Google's zoom controls on the right. No additional card container was added around the map because that would reduce useful map area without improving hierarchy.

### Informational and fallback screens

The information page now uses a centered, width-constrained tonal surface so its small amount of content reads as one intentional group on phones and tablets. Ingredient labels use consistent tonal rows rather than unstyled text separated by arbitrary padding.

The existing network and location fallback screens already followed the desired pattern: one clear title, supporting explanation, primary recovery action, and secondary alternative. They were reviewed but did not need structural changes in this pass.

### Featured dish and meal-dialog refinement

The original `See Our Best Dishes!` title was used for two different experiences and sounded more like promotional copy than application navigation. The destinations now have specific titles:

- `Featured Dish` identifies the single random-meal experience.
- `Explore Dishes` identifies the category meal gallery.

The Featured Dish page keeps both of its useful actions, but no longer places two bare icons around a compressed title. The meal name now leads a tonal header card. Below it, `Save` is a labeled tonal button and `Another` is a labeled outlined button. Labels reduce icon ambiguity, while the different button treatments establish priority. After a successful save, the action reads `Saved` and is disabled to prevent repeated inserts from the same visible meal.

The category meal preview previously included `Dismiss` and `Confirm`, even though both actions closed the dialog. It is now a purpose-specific `MealPreviewDialog` with the image, meal name, and one `Close` action.

The Favorites dialog is now a purpose-specific `FavoriteMealDialog`. Source and YouTube links were removed because that surface is for managing saved meals. It contains only the meal name, image, a neutral `Dismiss` action, and an error-colored `Delete Meal` action. This keeps the destructive choice clear without competing with unrelated links.

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

### Layouts based on user intent

Using a two-column grid everywhere made unrelated screens feel generic and forced information-rich cards into narrow spaces. Layouts are now selected according to what the user is doing:

- Browse Cuisines uses a single-column editorial feed with wide 16:9 images. Categories are broad entry points and benefit from visual presence.
- Explore Dishes retains an adaptive gallery because users are choosing primarily from meal imagery.
- Ingredient search retains an adaptive grid because compact scanning is useful for a potentially large result set.
- Favorites uses a compact horizontal list because it is a saved-item management screen, and the wider swipe surface makes deletion easier to understand.
- Restaurants uses a compact horizontal list so business name, rating, and address remain readable while comparing nearby options.

`AppHorizontalMediaCard` provides the common foundation for compact list items, while `AppMediaCard` supports a configurable image ratio for galleries and editorial cards. Sharing these primitives keeps shape, color, image cropping, and touch behavior consistent without forcing every feature into the same layout.

## Part-two verification checklist

The code compiles, but visual QA should still be performed on real Compose layouts. Check the following before treating part two as complete from a product perspective:

1. Compact phone in portrait with the keyboard open on login and search.
2. Standard phone in light and dark mode.
3. Landscape phone and a tablet-sized emulator to confirm adaptive column counts.
4. System font scales of 1.0, 1.3, and 1.5.
5. Very long recipe, meal, and restaurant names.
6. Missing or slow-loading remote images.
7. Favorites swipe behavior at each adaptive grid width.
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
- `app/src/main/java/com/example/recipe_app_compose/LoginActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/SplashScreenActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/MainActivity.kt`
- `app/src/main/java/com/example/recipe_app_compose/core/components/Widgets.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/categories/presentation/view/FavoritesScreen.kt`
- `app/src/main/java/com/example/recipe_app_compose/features/categories/presentation/view/SettingsScreen.kt`
