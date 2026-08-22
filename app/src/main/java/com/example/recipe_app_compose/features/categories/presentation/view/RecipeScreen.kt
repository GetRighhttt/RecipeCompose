package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.ConfirmationDialog
import com.example.recipe_app_compose.core.components.AppMediaCard
import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.states.RandomMealUiState
import com.example.recipe_app_compose.features.categories.domain.states.UiState
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing

@Composable
fun RecipeScreen(
    uiState: UiState,
    featuredMealState: RandomMealUiState,
    navigateToDetail: (Category) -> Unit,
    onSearch: () -> Unit,
    onNearbyShops: () -> Unit,
    onFavorites: () -> Unit,
    onFeaturedDish: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )

            uiState.error != null -> ConfirmationDialog(
                title = stringResource(R.string.error),
                message = stringResource(R.string.error_occurred, uiState.error),
                onDismiss = onRetry,
                onConfirm = onRetry,
                confirmLabel = stringResource(R.string.try_again),
            )

            else -> ExploreContent(
                categories = uiState.list,
                featuredMeal = featuredMealState.item.firstOrNull(),
                navigateToDetail = navigateToDetail,
                onSearch = onSearch,
                onNearbyShops = onNearbyShops,
                onFavorites = onFavorites,
                onFeaturedDish = onFeaturedDish,
            )
        }
    }
}

@Composable
internal fun ExploreContent(
    categories: List<Category>,
    featuredMeal: RandomMeal?,
    navigateToDetail: (Category) -> Unit,
    onSearch: () -> Unit,
    onNearbyShops: () -> Unit,
    onFavorites: () -> Unit,
    onFeaturedDish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = AppSizes.MaximumReadableWidth)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = AppSpacing.Large,
                top = AppSpacing.Small,
                end = AppSpacing.Large,
                bottom = AppSpacing.ExtraLarge,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.ExtraLarge),
        ) {
            item {
                Column {
                    Text(
                        text = stringResource(R.string.explore),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(AppSpacing.ExtraSmall))
                    Text(
                        text = stringResource(R.string.discover_your_next_meal),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)) {
                    SectionTitle(stringResource(R.string.what_would_you_like_to_do))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
                    ) {
                        ExploreAction(
                            label = stringResource(R.string.search_dishes),
                            icon = Icons.Default.Search,
                            onClick = onSearch,
                            modifier = Modifier.weight(1f),
                        )
                        ExploreAction(
                            label = stringResource(R.string.nearby_shops),
                            icon = Icons.Default.Storefront,
                            onClick = onNearbyShops,
                            modifier = Modifier.weight(1f),
                        )
                        ExploreAction(
                            label = stringResource(R.string.saved_dishes),
                            icon = Icons.Default.Favorite,
                            onClick = onFavorites,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            featuredMeal?.let { meal ->
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)) {
                        SectionTitle(stringResource(R.string.featured_for_you))
                        FeaturedMealCard(meal = meal, onClick = onFeaturedDish)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)) {
                    SectionTitle(stringResource(R.string.browse_by_cuisine))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
                        contentPadding = PaddingValues(end = AppSpacing.Large),
                    ) {
                        items(categories, key = { it.idCategory.value }) { category ->
                            CategoryItem(
                                category = category,
                                navigateToDetail = navigateToDetail,
                                modifier = Modifier.width(210.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 104.dp),
        shape = AppCardShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
        ) {
            Surface(
                shape = AppCardShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(AppSpacing.Small)
                        .size(20.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun FeaturedMealCard(meal: RandomMeal, onClick: () -> Unit) {
    AppMediaCard(
        painter = rememberAsyncImagePainter(meal.strMealThumb.orEmpty()),
        imageDescription = meal.strMeal,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        imageAspectRatio = 16f / 9f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.strMeal ?: stringResource(R.string.unknown),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val metadata = listOfNotNull(meal.strArea, meal.strCategory)
                    .filter(String::isNotBlank)
                    .joinToString(" · ")
                if (metadata.isNotBlank()) {
                    Spacer(Modifier.height(AppSpacing.ExtraSmall))
                    Text(
                        text = metadata,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.width(AppSpacing.Medium))
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun CategoryItem(
    category: Category,
    navigateToDetail: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppMediaCard(
        painter = rememberAsyncImagePainter(category.strCategoryThumb.value),
        imageDescription = stringResource(R.string.image),
        onClick = { navigateToDetail(category) },
        modifier = modifier,
        imageAspectRatio = 4f / 3f,
    ) {
        Text(
            text = category.strCategory.value,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
