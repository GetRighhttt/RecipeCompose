package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.features.categories.domain.model.details.MealDetails
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppControlShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing

@Composable
internal fun MealDetailsPage(
    meal: MealDetails,
    modifier: Modifier = Modifier,
    headerAction: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = AppSizes.MaximumReadableWidth)
                .fillMaxWidth(),
            contentPadding = PaddingValues(AppSpacing.Large),
        ) {
            item {
                MealDetailsContent(
                    meal = meal,
                    headerAction = headerAction,
                )
            }
        }
    }
}

@Composable
internal fun MealDetailsContent(
    meal: MealDetails,
    modifier: Modifier = Modifier,
    actions: (@Composable () -> Unit)? = null,
    headerAction: (@Composable () -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val category = meal.category.ifBlank { stringResource(R.string.unknown) }
    val cuisine = meal.cuisine.ifBlank { stringResource(R.string.unknown) }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = AppCardShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(AppSpacing.Large)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    headerAction?.let {
                        Spacer(modifier = Modifier.width(AppSpacing.Medium))
                        it()
                    }
                }
                actions?.let {
                    Spacer(modifier = Modifier.height(AppSpacing.Medium))
                    it()
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.Large))
        Image(
            painter = rememberAsyncImagePainter(meal.imageUrl),
            contentDescription = meal.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(AppCardShape),
        )

        Spacer(modifier = Modifier.height(AppSpacing.Large))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        ) {
            MealMetadataCard(
                label = stringResource(R.string.category),
                value = category,
                icon = Icons.Default.Restaurant,
                modifier = Modifier.weight(1f),
            )
            MealMetadataCard(
                label = stringResource(R.string.cuisine),
                value = cuisine,
                icon = Icons.Default.Public,
                modifier = Modifier.weight(1f),
            )
        }

        if (meal.sourceUrl.isNotBlank() || meal.youtubeUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
            Text(
                text = stringResource(R.string.resources),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                if (meal.sourceUrl.isNotBlank()) {
                    FilledTonalButton(
                        onClick = { uriHandler.openUri(meal.sourceUrl) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        Spacer(modifier = Modifier.width(AppSpacing.Small))
                        Text(stringResource(R.string.view_original_recipe))
                    }
                }
                if (meal.youtubeUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = { uriHandler.openUri(meal.youtubeUrl) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(AppSpacing.Small))
                        Text(stringResource(R.string.watch_video_instructions))
                    }
                }
            }
        }

        if (meal.instructions.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
            Text(
                text = stringResource(R.string.cooking_instructions),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            Surface(
                shape = AppCardShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = meal.instructions,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(AppSpacing.Large),
                )
            }
        }

        if (meal.ingredients.isNotEmpty()) {
            Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
            Text(
                text = stringResource(R.string.ingredients_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
                meal.ingredients.forEachIndexed { index, ingredient ->
                    IngredientRow(
                        number = index + 1,
                        ingredient = ingredient,
                    )
                }
            }
        }
    }
}

@Composable
private fun MealMetadataCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = AppControlShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(AppSpacing.Small))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun IngredientRow(
    number: Int,
    ingredient: String,
) {
    Surface(
        shape = AppControlShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = AppControlShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(modifier = Modifier.width(AppSpacing.Medium))
            Text(
                text = ingredient,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
