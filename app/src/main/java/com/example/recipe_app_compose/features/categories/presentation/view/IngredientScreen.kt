package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.DialogWithImage
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IngredientScreen(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()
    val viewState by viewModel.ingredientMealState.collectAsStateWithLifecycle()
    var alertDialogState by remember { mutableStateOf(true) }

    // search stuff
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.ingredientsList.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        val focusManager = LocalFocusManager.current
        Scaffold(
            modifier = Modifier.fillMaxSize()
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

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = viewModel.onSearchTextChange,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search,
                                keyboardType = KeyboardType.Email,
                                showKeyboardOnFocus = true
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Enter)
                                },
                                onSearch = {
                                    focusManager.clearFocus(true)
                                    focusManager.moveFocus(FocusDirection.Enter)
                                },
                                onDone = {
                                    focusManager.clearFocus(force = false)
                                    focusManager.moveFocus(FocusDirection.Enter)
                                }
                            ),
                            maxLines = 1,
                            placeholder = { Text("Search") },
                            enabled = true,
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .focusable(true)
                        )
                        if (isSearching) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else if (viewState.list.isNullOrEmpty() || searchResults?.isNotEmpty() == true) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "No results found",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.displayLarge,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else {
                            IngredientMealScreen(categories = viewState.list ?: emptyList())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientMealScreen(categories: List<Ingredient>) {
    LazyVerticalGrid(GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
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
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(4.dp)
        )
    }
}
