package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.ConfirmationDialog
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.domain.model.details.containsSavedMeal
import com.example.recipe_app_compose.features.categories.domain.model.details.toMealDetails
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IngredientScreen(
    modifier: Modifier = Modifier,
    onIngredientSelected: (Ingredient) -> Unit,
) {
    val viewModel: RecipeViewModel = koinViewModel()
    val uiState by viewModel.ingUiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.ingredientsList.collectAsStateWithLifecycle()
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        showErrorDialog = uiState.error != null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when {
            uiState.loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            uiState.error != null && showErrorDialog -> ConfirmationDialog(
                title = stringResource(R.string.error),
                message = stringResource(R.string.error_occurred, uiState.error ?: ""),
                onDismiss = { showErrorDialog = false },
                onConfirm = {
                    showErrorDialog = false
                    viewModel.fetchIngredients(
                        searchText.ifBlank { RecipeViewModel.SEARCH_DEFAULT }
                    )
                },
                confirmLabel = stringResource(R.string.try_again),
            )

            else -> IngredientSearchContent(
                searchText = searchText,
                isSearching = isSearching,
                searchResults = searchResults,
                onSearchTextChange = viewModel::onSearchTextChange,
                onIngredientSelected = onIngredientSelected,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun IngredientSearchContent(
    searchText: String,
    isSearching: Boolean,
    searchResults: List<Ingredient>,
    onSearchTextChange: (String) -> Unit,
    onIngredientSelected: (Ingredient) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxSize()) {
        CompactSearchField(
            value = searchText,
            onValueChange = onSearchTextChange,
            onSearch = { focusManager.clearFocus() },
            modifier = Modifier.padding(bottom = AppSpacing.Medium),
        )

        when {
            isSearching -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            searchResults.isEmpty() -> Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.no_results_found),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> DishSearchGrid(
                meals = searchResults,
                onIngredientSelected = onIngredientSelected,
            )
        }
    }
}

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
                    imageVector = Icons.Default.Search,
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
                            text = stringResource(R.string.search_dishes_by_name),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    innerTextField()
                }
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear_search),
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
    onIngredientSelected: (Ingredient) -> Unit,
) {
    val fontScale = LocalDensity.current.fontScale
    val minimumTileWidth = if (fontScale >= 1.3f) {
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
                onClick = { onIngredientSelected(meal) },
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

@Composable
fun IngredientDetailScreen(
    ingredient: Ingredient,
    modifier: Modifier = Modifier,
) {
    val databaseViewModel: DatabaseViewModel = koinViewModel()
    val databaseUiState by databaseViewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite = databaseUiState.list.containsSavedMeal(ingredient.idMeal)
    val context = LocalContext.current
    val dishSavedMessage = stringResource(
        R.string.dish_saved_message,
        ingredient.strMeal ?: stringResource(R.string.unknown),
    )
    val dishAlreadySavedMessage = stringResource(
        R.string.dish_already_saved_message,
        ingredient.strMeal ?: stringResource(R.string.unknown),
    )

    IngredientDetailContent(
        ingredient = ingredient,
        isFavorite = isFavorite,
        onFavorite = {
            if (isFavorite) {
                android.widget.Toast.makeText(
                    context,
                    dishAlreadySavedMessage,
                    android.widget.Toast.LENGTH_SHORT,
                ).show()
            } else {
                databaseViewModel.saveMeal(ingredient.toRandomMeal()) {
                    android.widget.Toast.makeText(
                        context,
                        dishSavedMessage,
                        android.widget.Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
internal fun IngredientDetailContent(
    ingredient: Ingredient,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MealDetailsPage(
        meal = ingredient.toMealDetails(),
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        headerAction = {
            IconButton(
                onClick = onFavorite,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isFavorite) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Transparent
                    },
                    contentColor = if (isFavorite) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                ),
            ) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = stringResource(
                        if (isFavorite) R.string.saved else R.string.save
                    ),
                )
            }
        },
    )
}

private fun Ingredient.toRandomMeal() = RandomMeal(
    id = 0,
    idMeal = idMeal,
    strMeal = strMeal,
    strCategory = strCategory,
    strArea = strArea,
    strInstructions = strInstructions,
    strMealThumb = strMealThumb,
    strYoutube = strYoutube,
    strIngredient1 = strIngredient1,
    strIngredient2 = strIngredient2,
    strIngredient3 = strIngredient3,
    strIngredient4 = strIngredient4,
    strIngredient5 = strIngredient5,
    strIngredient6 = strIngredient6,
    strIngredient7 = strIngredient7,
    strIngredient8 = strIngredient8,
    strIngredient9 = strIngredient9,
    strSource = strSource,
)
