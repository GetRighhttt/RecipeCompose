package com.example.recipe_app_compose.features.location.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.recipe_app_compose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun YelpSearchTopAppBar(
    query: String,
    searchActive: Boolean,
    searchEnabled: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val showSearchField = searchActive && searchEnabled

    LaunchedEffect(showSearchField) {
        if (showSearchField) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun closeSearch() {
        keyboardController?.hide()
        focusManager.clearFocus()
        if (query.isNotEmpty()) {
            onQueryChange("")
        }
        onSearchActiveChange(false)
    }

    TopAppBar(
        title = {
            if (showSearchField) {
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(stringResource(R.string.search_nearby_restaurants)) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.clear_search),
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
            } else {
                Text(
                    text = stringResource(R.string.shops),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = if (showSearchField) ::closeSearch else onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(
                        if (showSearchField) R.string.close_search else R.string.back
                    ),
                )
            }
        },
        actions = {
            if (!showSearchField && searchEnabled) {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                    )
                }
            }
        },
    )
}
