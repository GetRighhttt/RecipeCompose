package com.example.recipe_app_compose.features.categories.presentation.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
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
    MealDetailsContent(
        meal = category.toMealDetailsUiModel(),
        modifier = Modifier.fillMaxWidth(),
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(AppSpacing.Small))
                    Text(stringResource(R.string.another_dish))
                }
            }
        },
    )
}
