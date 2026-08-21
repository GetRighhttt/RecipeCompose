package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.AppMediaCard
import com.example.recipe_app_compose.core.components.HyperlinkText
import com.example.recipe_app_compose.core.components.MessageCard
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IngredientScreen(
    modifier: Modifier = Modifier,
    onIngredientSelected: (Ingredient) -> Unit,
) {
    val viewModel: RecipeViewModel = viewModel()
    val uiState by viewModel.ingUiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.ingredientsList.collectAsStateWithLifecycle()
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        showErrorDialog = uiState.error != null
    }

    val focusManager = LocalFocusManager.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when {
            uiState.loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            uiState.error != null && showErrorDialog -> AlertDialogExample(
                dialogTitle = stringResource(R.string.error),
                dialogText = stringResource(R.string.error_occurred, uiState.error ?: ""),
                onDismissRequest = { showErrorDialog = false },
                onConfirmation = {
                    showErrorDialog = false
                    viewModel.fetchIngredients(
                        searchText.ifBlank { RecipeViewModel.SEARCH_DEFAULT }
                    )
                },
            )

            else -> Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = viewModel::onSearchTextChange,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text,
                        showKeyboardOnFocus = true,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() }
                    ),
                    singleLine = true,
                    placeholder = {
                        Text(stringResource(R.string.search_for_specific_meals))
                    },
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .focusable(),
                )

                when {
                    isSearching -> Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    searchResults.isEmpty() -> Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = stringResource(R.string.no_results_found),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    else -> IngredientMealScreen(
                        categories = searchResults,
                        onIngredientSelected = onIngredientSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun IngredientMealScreen(
    categories: List<Ingredient>,
    onIngredientSelected: (Ingredient) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = AppSizes.MinimumGridCardWidth),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = AppSpacing.Small),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        items(categories, key = { it.idMeal ?: it.strMeal.orEmpty() }) { category ->
            IngredientMealItem(
                category = category,
                onClick = { onIngredientSelected(category) },
            )
        }
    }
}

@Composable
private fun IngredientMealItem(
    category: Ingredient,
    onClick: () -> Unit,
) {
    AppMediaCard(
        painter = rememberAsyncImagePainter(category.strMealThumb.orEmpty()),
        imageDescription = category.strMeal,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = category.strMeal.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun IngredientDetailScreen(
    ingredient: Ingredient,
    modifier: Modifier = Modifier,
) {
    val ingredients = ingredient.ingredientNames()
    val websiteLabel = stringResource(R.string.click_here_for_website)
    val youtubeLabel = stringResource(R.string.click_here_for_youtube)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Image(
                painter = rememberAsyncImagePainter(ingredient.strMealThumb.orEmpty()),
                contentDescription = ingredient.strMeal,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
            )
        }
        item {
            Text(
                text = ingredient.strMeal.orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item { HorizontalDivider() }
        item {
            Text(
                text = stringResource(R.string.type) + " ${ingredient.strCategory.orEmpty()}",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        item {
            Text(
                text = stringResource(R.string.originated) + " ${ingredient.strArea.orEmpty()}",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        ingredient.strSource?.takeIf(String::isNotBlank)?.let { source ->
            item {
                Text(stringResource(R.string.source))
                HyperlinkText(
                    text = websiteLabel,
                    linkText = listOf(websiteLabel),
                    hyperlinks = listOf(source),
                )
            }
        }
        ingredient.strYoutube?.takeIf(String::isNotBlank)?.let { youtube ->
            item {
                Text(stringResource(R.string.youtube))
                HyperlinkText(
                    text = youtubeLabel,
                    linkText = listOf(youtubeLabel),
                    hyperlinks = listOf(youtube),
                )
            }
        }
        item { HorizontalDivider() }
        item {
            Text(
                text = stringResource(R.string.instructions),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        item {
            Text(
                text = ingredient.strInstructions.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        item { HorizontalDivider() }
        item {
            Text(
                text = stringResource(R.string.ingredients),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        items(items = ingredients, key = { it }) { name ->
            MessageCard(name.uppercase())
        }
        item { Spacer(modifier = Modifier.padding(bottom = 8.dp)) }
    }
}

private fun Ingredient.ingredientNames(): List<String> = listOfNotNull(
    strIngredient1,
    strIngredient2,
    strIngredient3,
    strIngredient4,
    strIngredient5,
    strIngredient6,
    strIngredient7,
    strIngredient8,
    strIngredient9,
).map(String::trim)
    .filter(String::isNotEmpty)
    .distinct()
