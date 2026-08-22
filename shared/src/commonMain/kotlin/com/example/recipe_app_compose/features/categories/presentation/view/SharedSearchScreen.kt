package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.presentation.RecipeStore
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.action_close
import com.example.recipe_app_compose.shared.generated.resources.clear_search
import com.example.recipe_app_compose.shared.generated.resources.nav_search
import com.example.recipe_app_compose.shared.generated.resources.no_results_found
import com.example.recipe_app_compose.shared.generated.resources.search_dishes_by_name
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Shared compact, image-first dish search used by both platform hosts. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SharedSearchScreen(
    store: RecipeStore,
    query: String,
    results: List<Ingredient>,
    isLoading: Boolean,
    onDishSelected: (Ingredient) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = AppSizes.MaximumReadableWidth)
                .fillMaxSize()
                .padding(horizontal = AppSpacing.Large),
        ) {
            CompactSearchField(
                value = query,
                onValueChange = store::onSearchTextChange,
                onSearch = focusManager::clearFocus,
                modifier = Modifier.padding(bottom = AppSpacing.Medium),
            )

            when {
                isLoading -> Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                results.isEmpty() -> Box(Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(Res.string.no_results_found),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> DishSearchGrid(
                    meals = results,
                    onDishSelected = onDishSelected,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CompactSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = MaterialTheme.colorScheme.onSurface

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = contentColor),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Text,
            showKeyboardOnFocus = true,
        ),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        modifier = modifier
            .fillMaxWidth()
            .height(AppSizes.MinimumTouchTarget)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(start = AppSpacing.Large),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.nav_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = AppSpacing.Medium),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.search_dishes_by_name),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    innerTextField()
                }
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            painter = painterResource(Res.drawable.action_close),
                            contentDescription = stringResource(Res.string.clear_search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun DishSearchGrid(
    meals: List<Ingredient>,
    onDishSelected: (Ingredient) -> Unit,
) {
    val minimumTileWidth = if (LocalDensity.current.fontScale >= 1.3f) {
        AppSizes.MinimumGridCardWidth
    } else {
        AppSizes.MinimumCompactGridCardWidth
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = minimumTileWidth),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = AppSpacing.Small),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Large),
    ) {
        items(meals, key = { it.idMeal ?: it.strMeal.orEmpty() }) { meal ->
            DishSearchItem(
                meal = meal,
                onClick = { onDishSelected(meal) },
            )
        }
    }
}

@Composable
private fun DishSearchItem(
    meal: Ingredient,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = AppCardShape,
        color = MaterialTheme.colorScheme.background,
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(meal.strMealThumb.orEmpty()),
                contentDescription = meal.strMeal,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(AppCardShape),
            )
            Text(
                text = meal.strMeal.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(
                    start = AppSpacing.ExtraSmall,
                    top = AppSpacing.Small,
                    end = AppSpacing.ExtraSmall,
                ),
            )
        }
    }
}
