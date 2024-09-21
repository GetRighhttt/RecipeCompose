package com.example.recipe_app_compose.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
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
        Text(
            text = category.strMeal,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = rememberAsyncImagePainter(category.strMealThumb),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.9f)
        )
        Spacer(modifier = Modifier.padding(top = 15.dp))
        Text(
            text = "Details",
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = 5.dp))
        Text(
            text = "Food Type : " + category.strCategory,
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
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text("Instructions: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.padding(top = 3.dp))
        VerticalScrollingWithFixedHeightTextDemo(
            category.strInstructions
        )
        Spacer(modifier = Modifier.padding(bottom = 15.dp))
        Text("Ingredients: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.padding(top = 3.dp))
        Box {
            LazyColumn(modifier = Modifier.fillMaxWidth().height(100.dp)) {
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
        style = TextStyle(fontWeight = FontWeight.Medium),
        modifier = Modifier
            .height(200.dp)
            .verticalScroll(rememberScrollState())
    )
}

@Composable
fun MessageCard(msg: String) {
    Row(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
        Column {
            Text(
                text = msg,
                style = TextStyle(fontWeight = FontWeight.Medium)
            )
        }
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