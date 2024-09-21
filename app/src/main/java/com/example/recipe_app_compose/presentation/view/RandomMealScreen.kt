package com.example.recipe_app_compose.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.domain.model.RandomMeal
import com.example.recipe_app_compose.presentation.AlertDialogExample
import com.example.recipe_app_compose.presentation.viewmodel.RecipeViewModel

@Composable
fun RandomMealScreen(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()
    val viewState by viewModel.randomMealState
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
                        viewModel.fetchRandomMeal()
                        alertDialogState = false
                    }
                )
            else -> {
                // display list of categories
                RandomMealScreen(categories = viewState.item!!)
            }
        }
    }
}

@Composable
fun RandomMealScreen(categories: RandomMeal) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            RandomMealItem(categories)
        }
    }
}

@Composable
fun RandomMealItem(category: RandomMeal) {
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
        )
        Text(
            text = category.strMeal,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.strMeal,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.strMeal,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.strMeal,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.strMeal,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.strMeal,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
    }
}