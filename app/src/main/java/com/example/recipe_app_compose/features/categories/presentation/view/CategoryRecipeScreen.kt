package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.DialogWithImage
import com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMeal
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel

@Composable
fun CategoryRecipeScreen(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()
    val viewState by viewModel.categoryMealState.collectAsStateWithLifecycle()
    var alertDialogState by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            viewState.loading -> CircularProgressIndicator(
                modifier
                    .align(Alignment.Center)
                    .aspectRatio(0.3f)
            )

            viewState.error != null -> AlertDialogExample(
                dialogTitle = stringResource(R.string.error),
                dialogText = stringResource(R.string.error_occurred, viewState.error ?: ""),
                onDismissRequest = { alertDialogState = false },
                onConfirmation = {
                    viewModel.fetchCategoryMeals()
                    alertDialogState = false
                },
            )

            else -> {
                // display list of categories
                CategoryMealScreen(categories = viewState.list ?: emptyList())
            }
        }
    }
}

@Composable
fun CategoryMealScreen(categories: List<CategoryMeal>) {
    LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            CategoryMealItem(category = category)
        }
    }
}

@Composable
fun CategoryMealItem(category: CategoryMeal) {
    var alertState by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                category.strMealThumb,
                imageLoader = ImageLoader.Builder(context).crossfade(1500).build()
            ),
            contentDescription = stringResource(R.string.image),
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = true, onClick = {
                    alertState = true
                })
        )
        if (alertState) {
            DialogWithImage(
                text = category.strMeal,
                painter = rememberAsyncImagePainter(
                    category.strMealThumb,
                    imageLoader = ImageLoader.Builder(context).crossfade(500).build()
                ),
                imageDescription = stringResource(R.string.image),
                onDismissRequest = { alertState = false },
                onConfirmation = { alertState = false },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        Text(
            text = category.strMeal,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(4.dp)
        )
    }
}