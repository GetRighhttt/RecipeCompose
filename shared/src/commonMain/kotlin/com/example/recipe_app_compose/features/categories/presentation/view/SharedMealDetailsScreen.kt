package com.example.recipe_app_compose.features.categories.presentation.view

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.BackTopAppBar
import com.example.recipe_app_compose.features.categories.domain.model.details.MealDetails
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.action_close
import com.example.recipe_app_compose.shared.generated.resources.action_open_in_new
import com.example.recipe_app_compose.shared.generated.resources.action_play
import com.example.recipe_app_compose.shared.generated.resources.action_refresh
import com.example.recipe_app_compose.shared.generated.resources.another_dish
import com.example.recipe_app_compose.shared.generated.resources.category
import com.example.recipe_app_compose.shared.generated.resources.cooking_instructions
import com.example.recipe_app_compose.shared.generated.resources.cuisine
import com.example.recipe_app_compose.shared.generated.resources.ingredients_title
import com.example.recipe_app_compose.shared.generated.resources.metadata_public
import com.example.recipe_app_compose.shared.generated.resources.nav_favorite_outline
import com.example.recipe_app_compose.shared.generated.resources.nav_restaurant
import com.example.recipe_app_compose.shared.generated.resources.onboarding_save
import com.example.recipe_app_compose.shared.generated.resources.recipe_details
import com.example.recipe_app_compose.shared.generated.resources.remove_from_saved
import com.example.recipe_app_compose.shared.generated.resources.resources
import com.example.recipe_app_compose.shared.generated.resources.dish_already_saved_message
import com.example.recipe_app_compose.shared.generated.resources.save
import com.example.recipe_app_compose.shared.generated.resources.saved
import com.example.recipe_app_compose.shared.generated.resources.unknown
import com.example.recipe_app_compose.shared.generated.resources.view_original_recipe
import com.example.recipe_app_compose.shared.generated.resources.watch_video_instructions
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppControlShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.launch

/** Shared details layout for MealDB results. Hosts provide only navigation. */
@Composable
fun SharedMealDetailsScreen(
    meal: MealDetails,
    onBack: () -> Unit,
    isSaved: Boolean = false,
    onSave: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    isRefreshing: Boolean = false,
) {
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val alreadySavedMessage = stringResource(Res.string.dish_already_saved_message, meal.name)
    Scaffold(
        topBar = {
            BackTopAppBar(
                title = stringResource(Res.string.recipe_details),
                onBack = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(scaffoldPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier.widthIn(max = AppSizes.MaximumReadableWidth).fillMaxWidth(),
                contentPadding = PaddingValues(AppSpacing.Large),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Large),
            ) {
            item {
                Surface(shape = AppCardShape, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                    Column(modifier = Modifier.fillMaxWidth().padding(AppSpacing.Large)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                meal.name,
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            when {
                                onRemove != null -> IconButton(
                                    onClick = onRemove,
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    ),
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.action_close),
                                        contentDescription = stringResource(Res.string.remove_from_saved),
                                    )
                                }
                                onSave != null && onRefresh == null -> IconButton(
                                    onClick = {
                                        if (isSaved) {
                                            scope.launch { snackbarHostState.showSnackbar(alreadySavedMessage) }
                                        } else {
                                            onSave()
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if (isSaved) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            Color.Transparent
                                        },
                                        contentColor = if (isSaved) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                    ),
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (isSaved) {
                                                Res.drawable.onboarding_save
                                            } else {
                                                Res.drawable.nav_favorite_outline
                                            }
                                        ),
                                        contentDescription = stringResource(
                                            if (isSaved) Res.string.saved else Res.string.save
                                        ),
                                    )
                                }
                            }
                        }
                        if (onRefresh != null && onSave != null) {
                            Spacer(Modifier.height(AppSpacing.Medium))
                            FeaturedMealActions(
                                isSaved = isSaved,
                                isRefreshing = isRefreshing,
                                onSave = {
                                    if (isSaved) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(alreadySavedMessage)
                                        }
                                    } else {
                                        onSave()
                                    }
                                },
                                onRefresh = onRefresh,
                            )
                        }
                    }
                }
            }
            item {
                androidx.compose.foundation.Image(
                    painter = rememberAsyncImagePainter(meal.imageUrl),
                    contentDescription = meal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(RoundedCornerShape(16.dp)),
                )
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium)) {
                    MetadataCard(
                        label = stringResource(Res.string.category),
                        value = meal.category.ifBlank { stringResource(Res.string.unknown) },
                        icon = Res.drawable.nav_restaurant,
                        modifier = Modifier.weight(1f),
                    )
                    MetadataCard(
                        label = stringResource(Res.string.cuisine),
                        value = meal.cuisine.ifBlank { stringResource(Res.string.unknown) },
                        icon = Res.drawable.metadata_public,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            if (meal.sourceUrl.isNotBlank() || meal.youtubeUrl.isNotBlank()) item {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
                    Text(stringResource(Res.string.resources), style = MaterialTheme.typography.titleLarge)
                    if (meal.sourceUrl.isNotBlank()) {
                        FilledTonalButton(
                            onClick = { uriHandler.openUri(meal.sourceUrl) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.action_open_in_new),
                                contentDescription = null,
                            )
                            Spacer(Modifier.width(AppSpacing.Small))
                            Text(stringResource(Res.string.view_original_recipe))
                        }
                    }
                    if (meal.youtubeUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = { uriHandler.openUri(meal.youtubeUrl) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.action_play),
                                contentDescription = null,
                            )
                            Spacer(Modifier.width(AppSpacing.Small))
                            Text(stringResource(Res.string.watch_video_instructions))
                        }
                    }
                }
            }
            if (meal.instructions.isNotBlank()) item {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)) {
                    Text(stringResource(Res.string.cooking_instructions), style = MaterialTheme.typography.titleLarge)
                    Surface(shape = AppCardShape, color = MaterialTheme.colorScheme.surfaceContainerLow) { Text(meal.instructions, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(AppSpacing.Large)) }
                }
            }
            if (meal.ingredients.isNotEmpty()) item {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
                    Text(stringResource(Res.string.ingredients_title), style = MaterialTheme.typography.titleLarge)
                    meal.ingredients.forEachIndexed { index, ingredient ->
                        IngredientRow(number = index + 1, ingredient = ingredient)
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun FeaturedMealActions(
    isSaved: Boolean,
    isRefreshing: Boolean,
    onSave: () -> Unit,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        FilledTonalButton(
            onClick = onSave,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                painter = painterResource(
                    if (isSaved) Res.drawable.onboarding_save
                    else Res.drawable.nav_favorite_outline
                ),
                contentDescription = null,
            )
            Spacer(Modifier.width(AppSpacing.Small))
            Text(stringResource(if (isSaved) Res.string.saved else Res.string.save))
        }
        OutlinedButton(
            onClick = onRefresh,
            enabled = !isRefreshing,
            modifier = Modifier.weight(1f),
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.action_refresh),
                    contentDescription = null,
                )
            }
            Spacer(Modifier.width(AppSpacing.Small))
            Text(stringResource(Res.string.another_dish))
        }
    }
}

@Composable
private fun MetadataCard(
    label: String,
    value: String,
    icon: DrawableResource,
    modifier: Modifier,
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
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(AppSpacing.Small))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(
                    value,
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
                    Text(number.toString(), style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.width(AppSpacing.Medium))
            Text(
                text = ingredient,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
