package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.AppMediaCard
import com.example.recipe_app_compose.core.components.MealPreviewDialog
import com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMeal
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing

@Composable
fun CategoryRecipeScreen(modifier: Modifier = Modifier) {

    val viewModel: RecipeViewModel = viewModel()
    val uiState by viewModel.mealUiState.collectAsStateWithLifecycle()
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        showErrorDialog = uiState.error != null
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.loading -> CircularProgressIndicator(
                modifier
                    .align(Alignment.Center)
                    .aspectRatio(0.3f)
            )

            uiState.error != null && showErrorDialog -> AlertDialogExample(
                dialogTitle = stringResource(R.string.error),
                dialogText = stringResource(R.string.error_occurred, uiState.error ?: ""),
                onDismissRequest = { showErrorDialog = false },
                onConfirmation = {
                    showErrorDialog = false
                    viewModel.fetchCategoryMeals()
                },
            )

            else -> {
                // display list of categories
                CategoryMealScreen(categories = uiState.list ?: emptyList())
            }
        }
    }
}

@Composable
fun CategoryMealScreen(categories: List<CategoryMeal>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(AppSizes.MinimumGridCardWidth),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppSpacing.Large),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        items(categories, key = CategoryMeal::idMeal) { category ->
            CategoryMealItem(category = category)
        }
    }
}

@Composable
fun CategoryMealItem(category: CategoryMeal) {
    var alertState by remember { mutableStateOf(false) }
    AppMediaCard(
        painter = rememberAsyncImagePainter(category.strMealThumb),
        imageDescription = category.strMeal,
        onClick = { alertState = true },
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = category.strMeal,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    if (alertState) {
        MealPreviewDialog(
            text = category.strMeal,
            painter = rememberAsyncImagePainter(category.strMealThumb),
            imageDescription = stringResource(R.string.image),
            onDismissRequest = { alertState = false },
        )
    }
}
