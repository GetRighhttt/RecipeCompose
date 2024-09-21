package com.example.recipe_app_compose.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.domain.model.RandomMeal
import com.example.recipe_app_compose.presentation.AlertDialogExample
import com.example.recipe_app_compose.presentation.viewmodel.RecipeViewModel

@Composable
fun RandomMealPage(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()
    val randomViewState by viewModel.randomMealState
    var alertDialogState by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            randomViewState.loading -> CircularProgressIndicator(
                modifier
                    .align(Alignment.Center)
                    .aspectRatio(0.5f)
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
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        Text(
            text = category.strMeal,
            style = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = rememberAsyncImagePainter(category.strMealThumb),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.9f)
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Category: " + category.strCategory,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Area: " + category.strArea,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Source: " + category.strSource,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "YouTube Link: " + category.strYoutube,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "1st Ingredient: " + category.strIngredient1,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "YouTube Link: " + category.strYoutube,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "YouTube Link: " + category.strYoutube,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "YouTube Link: " + category.strYoutube,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "YouTube Link: " + category.strYoutube,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "YouTube Link: " + category.strYoutube,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
    }
}

/*
{
  "meals": [
    {
      "idMeal": "53032",
      "strMeal": "Tonkatsu pork",
      "strDrinkAlternate": null,
      "strCategory": "Pork",
      "strArea": "Japanese",
      "strInstructions": "STEP 1\r\nRemove the large piece of fat on the edge of each pork loin, then bash each of the loins between two pieces of baking parchment until around 1cm in thickness – you can do this using a meat tenderiser or a rolling pin. Once bashed, use your hands to reshape the meat to its original shape and thickness – this step will ensure the meat is as succulent as possible.\r\n\r\nSTEP 2\r\nPut the flour, eggs and panko breadcrumbs into three separate wide-rimmed bowls. Season the meat, then dip first in the flour, followed by the eggs, then the breadcrumbs.\r\n\r\nSTEP 3\r\nIn a large frying or sauté pan, add enough oil to come 2cm up the side of the pan. Heat the oil to 180C – if you don’t have a thermometer, drop a bit of panko into the oil and if it sinks a little then starts to fry, the oil is ready. Add two pork chops and cook for 1 min 30 secs on each side, then remove and leave to rest on a wire rack for 5 mins. Repeat with the remaining pork chops.\r\n\r\nSTEP 4\r\nWhile the pork is resting, make the sauce by whisking the ingredients together, adding a splash of water if it’s particularly thick. Slice the tonkatsu and serve drizzled with the sauce.",
      "strMealThumb": "https://www.themealdb.com/images/media/meals/lwsnkl1604181187.jpg",
      "strTags": null,
      "strYoutube": "https://www.youtube.com/watch?v=aASr5x0d3Ys",
      "strIngredient1": "Pork Chops",
      "strIngredient2": "Flour",
      "strIngredient3": "Eggs",
      "strIngredient4": "Breadcrumbs",
      "strIngredient5": "Vegetable Oil",
      "strIngredient6": "Tomato Ketchup",
      "strIngredient7": "Worcestershire Sauce",
      "strIngredient8": "Oyster Sauce",
      "strIngredient9": "Caster Sugar",
      "strSource": "https://www.bbcgoodfood.com/recipes/tonkatsu-pork",
    }
  ]
}
 */