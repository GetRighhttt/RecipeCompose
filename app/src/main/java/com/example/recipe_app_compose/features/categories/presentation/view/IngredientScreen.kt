package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
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

            else -> IngredientSearchContent(
                searchText = searchText,
                isSearching = isSearching,
                searchResults = searchResults,
                onSearchTextChange = viewModel::onSearchTextChange,
                onIngredientSelected = onIngredientSelected,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun IngredientSearchContent(
    searchText: String,
    isSearching: Boolean,
    searchResults: List<Ingredient>,
    onSearchTextChange: (String) -> Unit,
    onIngredientSelected: (Ingredient) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = AppSizes.MaximumReadableWidth)
                .fillMaxWidth(),
            contentPadding = PaddingValues(AppSpacing.Large),
        ) {
            item {
                MealDetailsContent(meal = ingredient.toMealDetailsUiModel())
            }
        }
    }
}
