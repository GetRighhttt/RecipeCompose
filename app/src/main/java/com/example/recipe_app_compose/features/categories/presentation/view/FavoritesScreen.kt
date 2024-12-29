package com.example.recipe_app_compose.features.categories.presentation.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
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
    val context = LocalContext.current
    val viewModel = DatabaseViewModel()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.height(400.dp)) {
            itemsIndexed(meals, key = { _, item -> item.hashCode() }
            ) { _, meal ->
                MealDBItem(meal = meal)
            }
        }
        Spacer(modifier = Modifier.padding(20.dp))
        ElevatedButton(
            onClick = {
                viewModel.executeDeleteAll.invoke()
                Toast.makeText(context, "All Meals Deleted", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.CenterHorizontally),
            elevation = ButtonDefaults.buttonElevation(15.dp)
        ) {
            Text("Delete All Meals", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun MealDBItem(meal: RandomMeal) {

    val viewModel = DatabaseViewModel()
    var alertState by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    viewModel.executeDeleteMeal(meal)
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    viewModel.executeDeleteMeal(meal)
                }

                SwipeToDismissBoxValue.Settled -> {
                    return@rememberSwipeToDismissBoxState false
                }
            }
            return@rememberSwipeToDismissBoxState true
        })

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier,
        backgroundContent = {
            DismissBackground(
                dismissState,
                meal = meal
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painter = rememberAsyncImagePainter(
                    meal.strMealThumb,
                    imageLoader = ImageLoader.Builder(context).crossfade(500).build()
                ),
                    contentDescription = "Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                        .clickable {
                            alertState = true
                        })
                if (alertState) {
                    DatabaseDialogWithImage(
                        text = meal.strMeal ?: "",
                        source = listOf(meal.strSource ?: ""),
                        youtube = listOf(meal.strYoutube ?: ""),
                        painter = rememberAsyncImagePainter(
                            meal.strMealThumb ?: "",
                            imageLoader = ImageLoader.Builder(context).crossfade(500).build()
                        ),
                        imageDescription = "Image",
                        onDismissRequest = {
                            viewModel.executeDeleteMeal.invoke(meal)
                            Toast.makeText(context, "Meal Deleted", Toast.LENGTH_SHORT).show()
                            alertState = false
                        },
                        onConfirmation = { alertState = false },
                        modifier = Modifier
                    )
                }
                Text(
                    text = meal.strMeal ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(4.dp)
                )
            }
        })
}

@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState, meal: RandomMeal) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color(0xFFFF1744)
        SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF1744)
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (dismissState.progress > 0.3F) {
            Icon(
                Icons.Sharp.Delete,
                contentDescription = "delete"
            )
            Text(
                text = if (dismissState.progress > 0.3F) "Delete ${meal.strMeal}?" else "",
                style = MaterialTheme.typography.labelSmall
            )
        } else {
            Box {}
        }
    }
}