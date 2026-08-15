package com.example.recipe_app_compose.features.categories.presentation.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.HyperlinkText
import com.example.recipe_app_compose.core.components.MessageCard
import com.example.recipe_app_compose.core.components.VerticalScrollingWithFixedHeightTextDemo
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel

@Composable
fun RandomMealPage(modifier: Modifier = Modifier) {
    val viewModel: RecipeViewModel = viewModel()
    val randomViewState by viewModel.randomMealState.collectAsStateWithLifecycle()
    var showErrorDialog by remember { mutableStateOf(false) }
    var favoriteDialogState by remember { mutableStateOf(false) }
    var favoriteViewState by remember { mutableStateOf(false) }

    LaunchedEffect(randomViewState.error) {
        showErrorDialog = randomViewState.error != null
    }

    val databaseViewModel: DatabaseViewModel = viewModel()
    val context = LocalContext.current
    val currentMeal = randomViewState.item?.firstOrNull()
    val addedToFavoritesMessage = stringResource(R.string.added_to_favorites)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    enabled = currentMeal != null,
                    onClick = {
                        favoriteDialogState = true
                        favoriteViewState = true
                    },
                ) {
                    Icon(
                        imageVector = if (favoriteViewState) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = stringResource(R.string.favorites),
                    )
                }
                Text(
                    text = currentMeal?.strMeal.orEmpty(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                )
                IconButton(
                    onClick = {
                        viewModel.fetchRandomMeal()
                        favoriteViewState = false
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    randomViewState.loading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )

                    randomViewState.error != null && showErrorDialog -> AlertDialogExample(
                        dialogTitle = stringResource(R.string.error),
                        dialogText = stringResource(
                            R.string.error_occurred,
                            randomViewState.error ?: ""
                        ),
                        onDismissRequest = { showErrorDialog = false },
                        onConfirmation = {
                            showErrorDialog = false
                            viewModel.fetchRandomMeal()
                        }
                    )

                    else -> RandomCategoryScreen(
                        categories = randomViewState.item.orEmpty(),
                    )
                }
            }
        }

        if (favoriteDialogState) {
            AlertDialogExample(
                dialogTitle = stringResource(R.string.favorites),
                dialogText = stringResource(R.string.would_you_like_to_add_this_to_your_favorites),
                onDismissRequest = {
                    favoriteDialogState = false
                    favoriteViewState = false
                },
                onConfirmation = {
                    favoriteDialogState = false
                    favoriteViewState = true
                    currentMeal?.let(databaseViewModel::executeInsertMeal)
                    Toast.makeText(
                        context,
                        buildString {
                            append("${currentMeal?.strMeal.orEmpty()} ")
                            append(addedToFavoritesMessage)
                        },
                        Toast.LENGTH_SHORT
                    ).show()
                },
            )
        }
    }
}

@Composable
fun RandomCategoryScreen(categories: List<RandomMeal>) {
    LazyVerticalGrid(GridCells.Fixed(1), modifier = Modifier.fillMaxSize()) {
        items(
            items = categories,
            key = { it.idMeal ?: "local:${it.id}" },
        ) { category ->
            RandomMealItem(category = category)
        }
    }
}

@Composable
fun RandomMealItem(category: RandomMeal) {

    // define list for Lazy Column
    val listOfIngredients = listOfNotNull(
        category.strIngredient1,
        category.strIngredient2,
        category.strIngredient3,
        category.strIngredient4,
        category.strIngredient5,
        category.strIngredient6,
        category.strIngredient7,
        category.strIngredient8,
        category.strIngredient9,
    ).map(String::trim)
        .filter(String::isNotEmpty)
        .distinct()

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        Image(
            painter = rememberAsyncImagePainter(category.strMealThumb.orEmpty()),
            contentDescription = stringResource(R.string.image),
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.9f)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.padding(top = 5.dp))
        Text(
            text = stringResource(R.string.details),
            style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.padding(top = 20.dp))

        Text(
            text = buildString {
                append(stringResource(R.string.type))
                append(" ${category.strCategory ?: ""} ")
            },
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.padding(top = 5.dp))
        Text(
            text = buildString {
                append(stringResource(R.string.originated))
                append(" ${category.strArea ?: ""} ")
            },
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.padding(top = 5.dp))
        Row {
            Text(stringResource(R.string.source), style = MaterialTheme.typography.bodyMedium)
            HyperlinkText(
                text = "Source",
                linkText = listOf(stringResource(R.string.click_here_for_website)),
                hyperlinks = listOf(category.strSource ?: "")
            )
        }

        Spacer(modifier = Modifier.padding(top = 5.dp))
        Row {
            Text(stringResource(R.string.youtube), style = MaterialTheme.typography.bodyMedium)
            HyperlinkText(
                text = "Youtube",
                linkText = listOf(stringResource(R.string.click_here_for_youtube)),
                hyperlinks = listOf(category.strYoutube ?: "")
            )
        }

        Spacer(modifier = Modifier.padding(top = 5.dp))
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

        Text(stringResource(R.string.instructions), style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.padding(top = 3.dp))
        VerticalScrollingWithFixedHeightTextDemo(category.strInstructions ?: "")

        Spacer(modifier = Modifier.padding(bottom = 5.dp))
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.padding(bottom = 5.dp))
        Text(stringResource(R.string.ingredients), style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.padding(top = 8.dp, bottom = 2.dp))
        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                items(items = listOfIngredients, key = { it }) { ingredient ->
                    MessageCard(ingredient.uppercase())
                }
            }
        }
    }
}
