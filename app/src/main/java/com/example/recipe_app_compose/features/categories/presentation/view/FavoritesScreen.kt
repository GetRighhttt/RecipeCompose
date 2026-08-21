package com.example.recipe_app_compose.features.categories.presentation.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.AppMediaCard
import com.example.recipe_app_compose.core.components.DatabaseDialogWithImage
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: DatabaseViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            uiState.error != null && showErrorDialog -> AlertDialogExample(
                dialogTitle = stringResource(R.string.error),
                dialogText = stringResource(R.string.error_occurred, uiState.error ?: ""),
                onDismissRequest = { showErrorDialog = false },
                onConfirmation = {
                    showErrorDialog = false
                    viewModel.executeGetAllMeals()
                })

            else -> {
                MealDBScreen(
                    meals = uiState.list.orEmpty(),
                    onDeleteAll = viewModel::executeDeleteAll,
                    onDeleteMeal = viewModel::executeDeleteMeal
                )
            }
        }
    }

}


@Composable
fun MealDBScreen(
    meals: List<RandomMeal>,
    onDeleteAll: () -> Unit,
    onDeleteMeal: (RandomMeal) -> Unit
) {
    val context = LocalContext.current
    val allMealsDeletedMessage = stringResource(R.string.all_meals_deleted)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(AppSizes.MinimumGridCardWidth),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppSpacing.Large),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        items(
            items = meals,
            key = { it.idMeal ?: "local:${it.id}" },
        ) { meal ->
            MealDBItem(meal = meal, onDeleteMeal = onDeleteMeal)
        }

        item(
            key = "delete_all_meals",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppSpacing.Medium),
            ) {
                ElevatedButton(
                    onClick = {
                        onDeleteAll()
                        Toast.makeText(
                            context,
                            allMealsDeletedMessage,
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                    enabled = meals.isNotEmpty(),
                    elevation = ButtonDefaults.elevatedButtonElevation(),
                ) {
                    Text(
                        stringResource(R.string.delete_all_meals),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun MealDBItem(meal: RandomMeal, onDeleteMeal: (RandomMeal) -> Unit) {
    val context = LocalContext.current
    var alertState by remember { mutableStateOf(false) }
    val mealDeletedMessage = stringResource(R.string.meal_deleted)

    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier,
        enableDismissFromStartToEnd = false,
        onDismiss = { onDeleteMeal(meal) },
        backgroundContent = { DismissBackground(dismissState) },
        content = {
            AppMediaCard(
                painter = rememberAsyncImagePainter(meal.strMealThumb),
                imageDescription = meal.strMeal,
                onClick = { alertState = true },
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = meal.strMeal ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (alertState) {
                DatabaseDialogWithImage(
                    text = meal.strMeal ?: "",
                    source = listOf(meal.strSource ?: ""),
                    youtube = listOf(meal.strYoutube ?: ""),
                    painter = rememberAsyncImagePainter(meal.strMealThumb.orEmpty()),
                    imageDescription = stringResource(R.string.image),
                    onDismissRequest = { alertState = false },
                    onConfirmation = {
                        onDeleteMeal(meal)
                        Toast.makeText(
                            context,
                            mealDeletedMessage,
                            Toast.LENGTH_SHORT,
                        ).show()
                        alertState = false
                    },
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                )
            }
        })
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
