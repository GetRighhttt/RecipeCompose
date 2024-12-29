package com.example.recipe_app_compose.features.categories.presentation.view

import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.states.RecipeState
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel

@Composable
fun RecipeScreen(
    modifier: Modifier = Modifier,
    viewState: RecipeState,
    navigateToDetail: (Category) -> Unit
) {
    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 30.dp, bottom = 80.dp),
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                viewState.loading -> CircularProgressIndicator(modifier.align(Alignment.Center))
                viewState.error != null -> {
                    viewModel.fetchCategories()
                    Log.d("RECIPE_SCREEN", "Error in Recipe Screen")
                }
                else -> {
                    // display list of categories
                    CategoryScreen(categories = viewState.list ?: emptyList(), navigateToDetail)
                }
            }
        }
    }
}

@Composable
fun CategoryScreen(categories: List<Category>, navigateToDetail: (Category) -> Unit) {
    LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            CategoryItem(category = category) {
                navigateToDetail(category)
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, navigateToDetail: (Category) -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = rememberAsyncImagePainter(
            category.strCategoryThumb,
            imageLoader = ImageLoader.Builder(context).crossfade(500).build()
        ),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clickable {
                    navigateToDetail(category)
                })
        Text(
            text = category.strCategory,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(4.dp)
        )
    }
}