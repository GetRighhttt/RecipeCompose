package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.AppMediaCard
import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.states.UiState
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing

@Composable
fun RecipeScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    navigateToDetail: (Category) -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        when {
            uiState.loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            uiState.error != null -> AlertDialogExample(
                dialogTitle = stringResource(R.string.error),
                dialogText = stringResource(R.string.error_occurred, uiState.error),
                onDismissRequest = onRetry,
                onConfirmation = onRetry
            )

            else -> CategoryScreen(
                categories = uiState.list.orEmpty(),
                navigateToDetail = navigateToDetail,
            )
        }
    }
}

@Composable
fun CategoryScreen(categories: List<Category>, navigateToDetail: (Category) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = AppSizes.MaximumReadableWidth)
                .fillMaxWidth(),
            contentPadding = PaddingValues(AppSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Large),
        ) {
            items(categories, key = { it.idCategory.value }) { category ->
                CategoryItem(category = category) {
                    navigateToDetail(category)
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, navigateToDetail: (Category) -> Unit) {
    AppMediaCard(
        painter = rememberAsyncImagePainter(category.strCategoryThumb.value),
        imageDescription = stringResource(R.string.image),
        onClick = { navigateToDetail(category) },
        modifier = Modifier.fillMaxWidth(),
        imageAspectRatio = 16f / 9f,
    ) {
        Text(
            text = category.strCategory.value,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
