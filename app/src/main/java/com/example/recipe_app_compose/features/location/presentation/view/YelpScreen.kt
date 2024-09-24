package com.example.recipe_app_compose.features.location.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.DialogWithImage
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpBusinesses
import com.example.recipe_app_compose.features.location.presentation.viewmodel.YelpViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun YelpScreen(modifier: Modifier = Modifier) {

    // declare view model and state variable
    val viewModel: YelpViewModel = viewModel()
    val viewState by viewModel.yelpState.collectAsStateWithLifecycle()
    var alertDialogState by remember { mutableStateOf(true) }

    // search stuff
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.businessList.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        val focusManager = LocalFocusManager.current
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            when {
                viewState.loading -> CircularProgressIndicator(
                    modifier
                        .align(Alignment.Center)
                        .padding(innerPadding)
                )

                viewState.error != null -> AlertDialogExample(
                    dialogTitle = "Error",
                    dialogText = "Error occurred: ${viewState.error}",
                    onDismissRequest = { alertDialogState = false },
                    onConfirmation = {
                        alertDialogState = false
                        viewModel.getBusinesses(searchText)
                    },
                )

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // set up searchbar with outline text field
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
                            placeholder = { Text("Search Nearby Restaurants") },
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
                                    text = "No results found",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 30.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else {
                            YelpListScreen(categories = viewState.list ?: emptyList())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YelpListScreen(categories: List<YelpBusinesses>) {
    LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            YelpItem(category = category)
        }
    }
}

@Composable
fun YelpItem(category: YelpBusinesses) {
    var alertState by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(category.imageUrl),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clickable(enabled = true, onClick = {
                    alertState = true
                })
        )
        // navigate to new activity for maps here
        if (alertState) {
            DialogWithImage(
                text = category.name,
                painter = rememberAsyncImagePainter(category.imageUrl),
                imageDescription = "Image",
                onDismissRequest = { alertState = false },
                onConfirmation = { alertState = false },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Text(
            text = category.name,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.location.address1,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.location.city,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.location.state,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = category.phone,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(4.dp)
        )

    }
}
