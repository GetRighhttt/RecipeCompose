package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.AppHorizontalMediaCard
import com.example.recipe_app_compose.core.components.ConfirmationDialog
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.states.DatabaseUiState
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.cancel
import com.example.recipe_app_compose.shared.generated.resources.error
import com.example.recipe_app_compose.shared.generated.resources.no_saved_dishes
import com.example.recipe_app_compose.shared.generated.resources.no_saved_dishes_message
import com.example.recipe_app_compose.shared.generated.resources.remove
import com.example.recipe_app_compose.shared.generated.resources.remove_all_saved_dishes
import com.example.recipe_app_compose.shared.generated.resources.remove_all_saved_dishes_message
import com.example.recipe_app_compose.shared.generated.resources.try_again
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.jetbrains.compose.resources.stringResource

@Composable
fun SharedFavoritesScreen(
    uiState: DatabaseUiState,
    onMealSelected: (RandomMeal) -> Unit,
    onDeleteAll: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showError by remember { mutableStateOf(false) }
    var confirmDeleteAll by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        showError = uiState.error != null
    }

    Box(modifier.fillMaxSize()) {
        when {
            uiState.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            uiState.error != null && showError -> ConfirmationDialog(
                title = stringResource(Res.string.error),
                message = uiState.error,
                onConfirm = {
                    showError = false
                    onRetry()
                },
                onDismiss = { showError = false },
                confirmLabel = stringResource(Res.string.try_again),
            )
            uiState.list.isEmpty() -> EmptyFavorites()
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(AppSpacing.Large),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            ) {
                items(uiState.list, key = { it.idMeal ?: "local:${it.id}" }) { meal ->
                    AppHorizontalMediaCard(
                        painter = rememberAsyncImagePainter(meal.strMealThumb.orEmpty()),
                        imageDescription = meal.strMeal,
                        onClick = { onMealSelected(meal) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = meal.strMeal.orEmpty(),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                item(key = "delete_all") {
                    FilledTonalButton(
                        onClick = { confirmDeleteAll = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(Res.string.remove_all_saved_dishes))
                    }
                }
            }
        }
    }

    if (confirmDeleteAll) {
        ConfirmationDialog(
            title = stringResource(Res.string.remove_all_saved_dishes),
            message = stringResource(Res.string.remove_all_saved_dishes_message),
            onConfirm = {
                confirmDeleteAll = false
                onDeleteAll()
            },
            onDismiss = { confirmDeleteAll = false },
            confirmLabel = stringResource(Res.string.remove),
            dismissLabel = stringResource(Res.string.cancel),
        )
    }
}

@Composable
private fun EmptyFavorites() {
    Column(
        modifier = Modifier.fillMaxSize().padding(AppSpacing.ExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.no_saved_dishes),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.no_saved_dishes_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
