package com.example.recipe_app_compose.features.categories.presentation.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppControlShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing

@Composable
fun RandomMealPage(modifier: Modifier = Modifier) {
    val viewModel: RecipeViewModel = viewModel()
    val uiState by viewModel.randUiState.collectAsStateWithLifecycle()
    var showErrorDialog by remember { mutableStateOf(false) }
    var favoriteDialogState by remember { mutableStateOf(false) }
    var favoriteViewState by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        showErrorDialog = uiState.error != null
    }

    val databaseViewModel: DatabaseViewModel = viewModel()
    val context = LocalContext.current
    val currentMeal = uiState.item?.firstOrNull()
    val addedToFavoritesMessage = stringResource(R.string.added_to_favorites)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            uiState.loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            uiState.error != null && showErrorDialog -> AlertDialogExample(
                dialogTitle = stringResource(R.string.error),
                dialogText = stringResource(
                    R.string.error_occurred,
                    uiState.error ?: ""
                ),
                onDismissRequest = { showErrorDialog = false },
                onConfirmation = {
                    showErrorDialog = false
                    viewModel.fetchRandomMeal()
                }
            )

            else -> RandomCategoryScreen(
                categories = uiState.item.orEmpty(),
                isFavorite = favoriteViewState,
                onFavorite = {
                    favoriteDialogState = true
                    favoriteViewState = true
                },
                onRefresh = {
                    viewModel.fetchRandomMeal()
                    favoriteViewState = false
                },
            )
        }

        if (favoriteDialogState) {
            AlertDialogExample(
                dialogTitle = stringResource(R.string.favorites),
                dialogText = stringResource(R.string.would_you_like_to_add_this_to_your_favorites),
                onDismissRequest = {
                    favoriteDialogState = false
                    favoriteViewState = false
                },
                onConfirmation = {
                    favoriteDialogState = false
                    favoriteViewState = true
                    currentMeal?.let(databaseViewModel::executeInsertMeal)
                    Toast.makeText(
                        context,
                        buildString {
                            append("${currentMeal?.strMeal.orEmpty()} ")
                            append(addedToFavoritesMessage)
                        },
                        Toast.LENGTH_SHORT
                    ).show()
                },
            )
        }
    }
}

@Composable
fun RandomCategoryScreen(
    categories: List<RandomMeal>,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onRefresh: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = AppSizes.MaximumReadableWidth)
                .fillMaxWidth(),
            contentPadding = PaddingValues(AppSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Large),
        ) {
            items(
                items = categories,
                key = { it.idMeal ?: "local:${it.id}" },
            ) { category ->
                RandomMealItem(
                    category = category,
                    isFavorite = isFavorite,
                    onFavorite = onFavorite,
                    onRefresh = onRefresh,
                )
            }
        }
    }
}

@Composable
fun RandomMealItem(
    category: RandomMeal,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onRefresh: () -> Unit,
) {
    val listOfIngredients = listOfNotNull(
        category.strIngredient1,
        category.strIngredient2,
        category.strIngredient3,
        category.strIngredient4,
        category.strIngredient5,
        category.strIngredient6,
        category.strIngredient7,
        category.strIngredient8,
        category.strIngredient9,
    ).map(String::trim)
        .filter(String::isNotEmpty)
        .distinct()
    val sourceUrl = category.strSource.orEmpty().trim()
    val youtubeUrl = category.strYoutube.orEmpty().trim()
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Surface(
            shape = AppCardShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(AppSpacing.Large)) {
                Text(
                    text = category.strMeal.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppSpacing.Medium),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
                ) {
                    FilledTonalButton(
                        onClick = onFavorite,
                        enabled = !isFavorite,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.Small))
                        Text(
                            stringResource(
                                if (isFavorite) R.string.saved else R.string.save
                            )
                        )
                    }
                    OutlinedButton(
                        onClick = onRefresh,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.Small))
                        Text(stringResource(R.string.another_dish))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        Image(
            painter = rememberAsyncImagePainter(category.strMealThumb.orEmpty()),
            contentDescription = stringResource(R.string.image),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(AppCardShape)
        )

        Spacer(modifier = Modifier.height(AppSpacing.Large))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        ) {
            MealMetadataCard(
                label = stringResource(R.string.category),
                value = category.strCategory.orEmpty(),
                icon = Icons.Default.Restaurant,
                modifier = Modifier.weight(1f),
            )
            MealMetadataCard(
                label = stringResource(R.string.cuisine),
                value = category.strArea.orEmpty(),
                icon = Icons.Default.Public,
                modifier = Modifier.weight(1f),
            )
        }

        if (sourceUrl.isNotBlank() || youtubeUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
            Text(
                text = stringResource(R.string.resources),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                if (sourceUrl.isNotBlank()) {
                    FilledTonalButton(
                        onClick = { uriHandler.openUri(sourceUrl) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        Spacer(modifier = Modifier.width(AppSpacing.Small))
                        Text(stringResource(R.string.view_original_recipe))
                    }
                }
                if (youtubeUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = { uriHandler.openUri(youtubeUrl) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(AppSpacing.Small))
                        Text(stringResource(R.string.watch_video_instructions))
                    }
                }
            }
        }

        category.strInstructions?.takeIf(String::isNotBlank)?.let { instructions ->
            Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
            Text(
                text = stringResource(R.string.cooking_instructions),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            Surface(
                shape = AppCardShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = instructions,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(AppSpacing.Large),
                )
            }
        }

        if (listOfIngredients.isNotEmpty()) {
            Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
            Text(
                text = stringResource(R.string.ingredients_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
                listOfIngredients.forEachIndexed { index, ingredient ->
                    IngredientRow(
                        number = index + 1,
                        ingredient = ingredient,
                    )
                }
            }
        }
    }
}

@Composable
private fun MealMetadataCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = AppControlShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(AppSpacing.Small))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun IngredientRow(
    number: Int,
    ingredient: String,
) {
    Surface(
        shape = AppControlShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = AppControlShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(modifier = Modifier.width(AppSpacing.Medium))
            Text(
                text = ingredient,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
