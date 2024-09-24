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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.DialogWithImage
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
                dialogTitle = "Error",
                dialogText = "Error occurred: ${viewState.error}",
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
fun CategoryMealScreen(categories: List<com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMeal>) {
    LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            CategoryMealItem(category = category)
        }
    }
}

@Composable
fun CategoryMealItem(category: com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMeal) {
    var alertState by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(category.strMealThumb),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clickable(enabled = true, onClick = {
                    alertState = true
                })
        )
        if (alertState) {
            DialogWithImage(
                text = category.strMeal,
                painter = rememberAsyncImagePainter(category.strMealThumb),
                imageDescription = "Image",
                onDismissRequest = { alertState = false },
                onConfirmation = { alertState = false },
                modifier = Modifier
            )
        }
        Text(
            text = category.strMeal,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
    }
}