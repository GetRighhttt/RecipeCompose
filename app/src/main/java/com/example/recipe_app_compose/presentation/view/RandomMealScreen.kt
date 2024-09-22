@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.recipe_app_compose.presentation.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.domain.model.RandomMeal
import com.example.recipe_app_compose.presentation.AlertDialogExample
import com.example.recipe_app_compose.presentation.viewmodel.RecipeViewModel

@Composable
fun RandomMealPage(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()
    val randomViewState by viewModel.randomMealState.collectAsStateWithLifecycle()
    var alertDialogState by remember { mutableStateOf(true) }
//    var navigateBackState by remember { mutableStateOf(false) }
    var favoriteDialogState by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        val context = LocalContext.current
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            randomViewState.item?.first()?.strMeal.toString(),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                favoriteDialogState = true
                            }) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite"
                            )
                        }
                        if (favoriteDialogState) {
                            AlertDialogExample(
                                dialogTitle = "Favorite",
                                dialogText = "Would you like to add this to your favorites?",
                                onDismissRequest = { favoriteDialogState = false },
                                onConfirmation = {
                                    favoriteDialogState = false
                                    Toast.makeText(
                                        context,
                                        "${randomViewState.item?.first()?.strMeal.toString()} " +
                                                "added to favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.fetchRandomMeal()
                            }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            when {
                randomViewState.loading -> CircularProgressIndicator(
                    modifier
                        .align(Alignment.Center)
                        .aspectRatio(0.5f)
                        .padding(innerPadding)
                )

                randomViewState.error != null ->
                    AlertDialogExample(
                        dialogTitle = "Error",
                        dialogText = "Error occurred: ${randomViewState.error}",
                        onDismissRequest = { alertDialogState = false },
                        onConfirmation = {
                            viewModel.fetchRandomMeal()
                        }
                    )

                else -> {
                    // display list of categories
                    RandomCategoryScreen(categories = randomViewState.item ?: emptyList())
                }
            }
        }
    }
}

@Composable
fun RandomCategoryScreen(categories: List<RandomMeal>) {
    LazyVerticalGrid(GridCells.Fixed(1), modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            RandomMealItem(category = category)
        }
    }
}

@Composable
fun RandomMealItem(category: RandomMeal) {

    // define list for Lazy Column
    val listOfIngredients = listOf(
        category.strIngredient1,
        category.strIngredient2,
        category.strIngredient3,
        category.strIngredient4,
        category.strIngredient5,
        category.strIngredient6,
        category.strIngredient7,
        category.strIngredient8,
        category.strIngredient9
    )

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        Image(
            painter = rememberAsyncImagePainter(category.strMealThumb),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.9f)
        )

        Spacer(modifier = Modifier.padding(top = 5.dp))
        Text(
            text = "Details",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            ),
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.padding(top = 20.dp))

        Text(
            text = "Type : " + category.strCategory,
            style = TextStyle(fontWeight = FontWeight.Medium),
        )

        Spacer(modifier = Modifier.padding(top = 5.dp))
        Text(
            text = "Originated : " + category.strArea,
            style = TextStyle(fontWeight = FontWeight.Medium),
        )

        Spacer(modifier = Modifier.padding(top = 5.dp))
        Text(
            text = "Source : " + category.strSource,
            style = TextStyle(fontWeight = FontWeight.Medium),
        )

        Spacer(modifier = Modifier.padding(top = 5.dp))
        Text(
            text = "YouTube : " + category.strYoutube,
            style = TextStyle(fontWeight = FontWeight.Medium),
        )
        Spacer(modifier = Modifier.padding(top = 5.dp))
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

        Text("Instructions: ", style = TextStyle(fontWeight = FontWeight.Bold))

        Spacer(modifier = Modifier.padding(top = 3.dp))
        VerticalScrollingWithFixedHeightTextDemo(
            category.strInstructions
        )

        Spacer(modifier = Modifier.padding(bottom = 5.dp))
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.padding(bottom = 5.dp))
        Text("Ingredients: ", style = TextStyle(fontWeight = FontWeight.Bold))

        Spacer(modifier = Modifier.padding(top = 8.dp, bottom = 2.dp))
        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                items(listOfIngredients) { msg ->
                    MessageCard(msg)
                }
            }
        }
    }
}

@Composable
fun VerticalScrollingWithFixedHeightTextDemo(randomText: String) {
    Text(
        text = randomText,
        style = TextStyle(fontWeight = FontWeight.Normal),
        modifier = Modifier
            .height(100.dp)
            .verticalScroll(rememberScrollState())
            .padding(top = 5.dp)
    )
}

@Composable
fun MessageCard(msg: String) {
    Row(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)) {
        Column {
            Text(
                text = msg,
                style = TextStyle(fontWeight = FontWeight.Normal)
            )
        }
    }
}