package com.example.recipe_app_compose.features.categories.presentation.view

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.DatabaseDialogWithImage
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
) {
    // declare view model and state variable
    val viewModel: DatabaseViewModel = viewModel()
    val viewState by viewModel.currentState.collectAsStateWithLifecycle()
    var alertDialogState by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            viewState.loading -> CircularProgressIndicator(modifier.align(Alignment.Center))
            viewState.error != null -> AlertDialogExample(dialogTitle = "Error",
                dialogText = "Error occurred: ${viewState.error}",
                onDismissRequest = { alertDialogState = false },
                onConfirmation = {
                    viewModel.executeGetAllMeals()
                    alertDialogState = false
                })

            else -> {
                MealDBScreen(meals = viewState.list ?: emptyList())
            }
        }
    }

}


@Composable
fun MealDBScreen(meals: List<RandomMeal>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Favorites",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 10.dp, bottom = 30.dp)
                .fillMaxWidth()
        )
        LazyVerticalGrid(GridCells.Fixed(4), modifier = Modifier.fillMaxSize()) {
            items(meals) { meal ->
                MealDBItem(meal = meal)
            }
        }
    }
}

@Composable
fun MealDBItem(meal: RandomMeal) {

    val viewModel = DatabaseViewModel()
    var alertState by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = rememberAsyncImagePainter(meal.strMealThumb),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clickable {
                    alertState = true
                })
        if (alertState) {
            DatabaseDialogWithImage(
                text = meal.strMeal,
                source = listOf(meal.strYoutube),
                youtube = listOf(meal.strSource),
                painter = rememberAsyncImagePainter(meal.strMealThumb),
                imageDescription = "Image",
                onDismissRequest = {
                    viewModel.executeDeleteMeal.invoke(meal)
                    alertState = false
                },
                onConfirmation = { alertState = false },
                modifier = Modifier
            )
        }
        Text(
            text = meal.strMeal,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(4.dp)
        )
    }
}