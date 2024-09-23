package com.example.recipe_app_compose.presentation.view

import androidx.compose.foundation.Image
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
import com.example.recipe_app_compose.domain.model.category.Category
import com.example.recipe_app_compose.presentation.AlertDialogExample
import com.example.recipe_app_compose.presentation.viewmodel.RecipeViewModel

@Composable
fun RecipeScreen(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()
    val viewState by viewModel.categoriesState.collectAsStateWithLifecycle()
    var alertDialogState by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            viewState.loading -> CircularProgressIndicator(modifier.align(Alignment.Center))
            viewState.error != null ->
                AlertDialogExample(
                dialogTitle = "Error",
                dialogText = "Error occurred: ${viewState.error}",
                onDismissRequest = { alertDialogState = false },
                onConfirmation = {
                    viewModel.fetchCategories
                    alertDialogState = false
                }
            )

            else -> {
                // display list of categories
                CategoryScreen(categories = viewState.list ?: emptyList())
            }
        }
    }
}

@Composable
fun CategoryScreen(categories: List<Category>) {
    LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            CategoryItem(category = category)
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(category.strCategoryThumb),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )
        Text(
            text = category.strCategory,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
    }
}