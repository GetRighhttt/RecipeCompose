package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.MessageCard
import com.example.recipe_app_compose.core.components.VerticalScrollingWithFixedHeightTextDemo
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IngredientScreen(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: RecipeViewModel = viewModel()
    val viewState by viewModel.ingredientMealState.collectAsStateWithLifecycle()
    var alertDialogState by remember { mutableStateOf(true) }

    // search stuff
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.ingredientsList.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        val focusManager = LocalFocusManager.current
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            when {
                viewState.loading -> CircularProgressIndicator(
                    modifier
                        .align(Alignment.Center)
                        .aspectRatio(0.3f)
                        .padding(innerPadding)
                )

                viewState.error != null -> AlertDialogExample(
                    dialogTitle = stringResource(R.string.error),
                    dialogText = stringResource(R.string.error_occurred, viewState.error ?: ""),
                    onDismissRequest = { alertDialogState = false },
                    onConfirmation = {
                        alertDialogState = false
                        viewModel.fetchIngredients("chicken")
                    },
                )

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = viewModel.onSearchTextChange,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search,
                                keyboardType = KeyboardType.Email,
                                showKeyboardOnFocus = true
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Enter)
                                },
                                onSearch = {
                                    focusManager.clearFocus(true)
                                    focusManager.moveFocus(FocusDirection.Enter)
                                },
                                onDone = {
                                    focusManager.clearFocus(force = false)
                                    focusManager.moveFocus(FocusDirection.Enter)
                                }
                            ),
                            maxLines = 1,
                            placeholder = { Text(stringResource(R.string.search_for_specific_meals)) },
                            enabled = true,
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .focusable(true)
                        )
                        if (isSearching) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else if (viewState.list.isNullOrEmpty() || searchResults?.isNotEmpty() == true) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = stringResource(R.string.no_results_found),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.displayLarge,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else {
                            IngredientMealScreen(categories = viewState.list ?: emptyList())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientMealScreen(categories: List<Ingredient>) {
    LazyVerticalGrid(GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            IngredientMealItem(category = category)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientMealItem(category: Ingredient) {
    val context = LocalContext.current
    var alertState by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                category.strMealThumb ?: "",
                imageLoader = ImageLoader.Builder(context).crossfade(500).build()
            ),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = true, onClick = {
                    alertState = true
                })
        )
        if (alertState) {
            // define list for Lazy Column
            Dialog(
                onDismissRequest = { alertState = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                ),
            ) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    category.strMeal ?: "",
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        alertState = false
                                    }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back)
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->

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
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                category.strMealThumb ?: "",
                                imageLoader = ImageLoader.Builder(LocalContext.current)
                                    .crossfade(400).build()
                            ),
                            contentDescription = stringResource(R.string.image),
                            modifier = Modifier
                                .aspectRatio(0.9F)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(
                            text = stringResource(R.string.details),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
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
                        Text(
                            text = buildString {
                                append(stringResource(R.string.source))
                                append(" ${category.strSource ?: ""} ")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(
                            text = buildString {
                                append(stringResource(R.string.youtube))
                                append(" ${category.strYoutube ?: ""} ")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )
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
                                items(listOfIngredients) { msg ->
                                    MessageCard(msg?.uppercase() ?: "")
                                }
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = category.strMeal ?: "",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(4.dp)
        )
    }
}
