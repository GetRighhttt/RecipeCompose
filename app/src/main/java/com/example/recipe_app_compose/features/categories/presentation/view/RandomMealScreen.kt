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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.ConfirmationDialog
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.model.details.containsSavedMeal
import com.example.recipe_app_compose.features.categories.domain.model.details.toMealDetails
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RandomMealPage(modifier: Modifier = Modifier) {
    val viewModel: RecipeViewModel = koinViewModel()
    val uiState by viewModel.randUiState.collectAsStateWithLifecycle()
    var showErrorDialog by remember { mutableStateOf(false) }
    var favoriteDialogState by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        showErrorDialog = uiState.error != null
    }

    val databaseViewModel: DatabaseViewModel = koinViewModel()
    val databaseUiState by databaseViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentMeal = uiState.item.firstOrNull()
    val isFavorite = databaseUiState.list.containsSavedMeal(currentMeal?.idMeal)
    val dishSavedMessage = stringResource(
        R.string.dish_saved_message,
        currentMeal?.strMeal ?: stringResource(R.string.unknown),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            uiState.loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            uiState.error != null && showErrorDialog -> ConfirmationDialog(
                title = stringResource(R.string.error),
                message = stringResource(
                    R.string.error_occurred,
                    uiState.error ?: ""
                ),
                onDismiss = { showErrorDialog = false },
                onConfirm = {
                    showErrorDialog = false
                    viewModel.fetchRandomMeal()
                },
                confirmLabel = stringResource(R.string.try_again),
            )

            else -> RandomCategoryScreen(
                categories = uiState.item,
                isFavorite = isFavorite,
                onFavorite = { favoriteDialogState = true },
                onRefresh = viewModel::fetchRandomMeal,
            )
        }

        if (favoriteDialogState) {
            ConfirmationDialog(
                title = stringResource(R.string.saved),
                message = stringResource(R.string.confirm_save_dish),
                onDismiss = {
                    favoriteDialogState = false
                },
                onConfirm = {
                    favoriteDialogState = false
                    currentMeal?.let { meal ->
                        databaseViewModel.saveMeal(meal) {
                            Toast.makeText(
                                context,
                                dishSavedMessage,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                },
                confirmLabel = stringResource(R.string.save),
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
        meal = category.toMealDetails(),
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
                            Icons.Outlined.FavoriteBorder
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
