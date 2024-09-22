package com.example.recipe_app_compose.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
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
import com.example.recipe_app_compose.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.presentation.AlertDialogExample
import com.example.recipe_app_compose.presentation.DialogWithImage
import com.example.recipe_app_compose.presentation.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientScreen(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()
    val viewState by viewModel.ingredientMealState.collectAsStateWithLifecycle()
    var alertDialogState by remember { mutableStateOf(true) }
    var searchDialogState by remember { mutableStateOf(false) }

    // search stuff
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.ingredientsList.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                SearchBar(
                    query = searchText,
                    onQueryChange = viewModel::onSearchTextChange,
                    onSearch = viewModel::onSearchTextChange,
                    onActiveChange = { viewModel.onToggleSearch() },
                    active = isSearching,
                    placeholder = { Text("Searching...") },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IngredientMealScreen(searchResults ?: viewState.list ?: emptyList())
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { innerPadding ->
            when {
                viewState.loading -> CircularProgressIndicator(
                    modifier
                        .align(Alignment.Center)
                        .aspectRatio(0.3f)
                        .padding(innerPadding)
                )

                viewState.error != null -> AlertDialogExample(
                    dialogTitle = "Error",
                    dialogText = "Error occurred: ${viewState.error}",
                    onDismissRequest = { alertDialogState = false },
                    onConfirmation = {
                        alertDialogState = false
                    },
                )

                else -> {}
            }
        }
    }
}

@Composable
fun IngredientMealScreen(categories: List<Ingredient>) {
    LazyVerticalGrid(GridCells.Fixed(1), modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            IngredientMealItem(category = category)
        }
    }
}


@Composable
fun IngredientMealItem(category: Ingredient) {
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
