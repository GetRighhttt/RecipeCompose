package com.example.recipe_app_compose.features.categories.presentation.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.ConfirmationDialog
import com.example.recipe_app_compose.core.components.AppHorizontalMediaCard
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.model.details.toMealDetails
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    onMealSelected: (RandomMeal) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DatabaseViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED,
    )
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        showErrorDialog = uiState.error != null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when {
            uiState.loading -> CircularProgressIndicator(modifier.align(Alignment.Center))
            uiState.error != null && showErrorDialog -> ConfirmationDialog(
                title = stringResource(R.string.error),
                message = stringResource(R.string.error_occurred, uiState.error ?: ""),
                onDismiss = { showErrorDialog = false },
                onConfirm = {
                    showErrorDialog = false
                    viewModel.retryLoadingMeals()
                },
                confirmLabel = stringResource(R.string.try_again),
            )

            else -> {
                MealDBScreen(
                    meals = uiState.list,
                    onDeleteAll = { onSuccess -> viewModel.deleteAllMeals(onSuccess) },
                    onDeleteMeal = viewModel::deleteMeal,
                    onMealSelected = onMealSelected,
                )
            }
        }
    }

}


@Composable
fun MealDBScreen(
    meals: List<RandomMeal>,
    onDeleteAll: (onSuccess: () -> Unit) -> Unit,
    onDeleteMeal: (RandomMeal) -> Unit,
    onMealSelected: (RandomMeal) -> Unit,
) {
    val context = LocalContext.current
    val allMealsDeletedMessage = stringResource(R.string.all_saved_dishes_removed)

    if (meals.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
                modifier = Modifier.padding(AppSpacing.ExtraLarge),
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp),
                )
                Text(
                    text = stringResource(R.string.no_saved_dishes),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(R.string.no_saved_dishes_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        items(
            items = meals,
            key = { it.idMeal ?: "local:${it.id}" },
        ) { meal ->
            MealDBItem(
                meal = meal,
                onDeleteMeal = onDeleteMeal,
                onClick = { onMealSelected(meal) },
            )
        }

        item(key = "delete_all_meals") {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppSpacing.Medium),
            ) {
                ElevatedButton(
                    onClick = {
                        onDeleteAll {
                            Toast.makeText(
                                context,
                                allMealsDeletedMessage,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                    elevation = ButtonDefaults.elevatedButtonElevation(),
                ) {
                    Text(
                        stringResource(R.string.remove_all_saved_dishes),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun MealDBItem(
    meal: RandomMeal,
    onDeleteMeal: (RandomMeal) -> Unit,
    onClick: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier,
        enableDismissFromStartToEnd = false,
        onDismiss = { onDeleteMeal(meal) },
        backgroundContent = { DismissBackground(dismissState) },
        content = {
            AppHorizontalMediaCard(
                painter = rememberAsyncImagePainter(meal.strMealThumb),
                imageDescription = meal.strMeal,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = meal.strMeal ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        })
}

@Composable
fun SavedMealDetailScreen(
    meal: RandomMeal,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DatabaseViewModel = koinViewModel()
    val context = LocalContext.current
    val mealDeletedMessage = stringResource(R.string.dish_removed)
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    SavedMealDetailContent(
        meal = meal,
        onRemove = { showDeleteConfirmation = true },
        modifier = modifier,
    )

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.remove_saved_dish_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.remove_saved_dish_message,
                        meal.strMeal.orEmpty(),
                    )
                )
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.deleteMeal(meal) {
                            Toast.makeText(
                                context,
                                mealDeletedMessage,
                                Toast.LENGTH_SHORT,
                            ).show()
                            onDeleted()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(stringResource(R.string.remove))
                }
            },
        )
    }
}

@Composable
internal fun SavedMealDetailContent(
    meal: RandomMeal,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MealDetailsPage(
        meal = meal.toMealDetails(),
        modifier = modifier,
        headerAction = {
            IconButton(
                onClick = onRemove,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.remove_from_saved),
                )
            }
        },
    )
}

@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val showDeleteAction = dismissState.targetValue != SwipeToDismissBoxValue.Settled

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        if (showDeleteAction) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(44.dp)
                    .width(38.dp)
                    .clip(RoundedCornerShape(25))
                    .background(MaterialTheme.colorScheme.errorContainer),
            ) {
                Icon(
                    imageVector = Icons.Sharp.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}
