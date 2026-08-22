package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.BackTopAppBar
import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.category_details
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.jetbrains.compose.resources.stringResource

@Composable
fun DetailScreen(category: Category, onBack: (() -> Unit)? = null) {
    Scaffold(
        topBar = {
            onBack?.let { navigateBack ->
                BackTopAppBar(
                    title = stringResource(Res.string.category_details),
                    onBack = navigateBack,
                )
            }
        },
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
                item { Text(category.strCategory.value, textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.fillMaxWidth()) }
                item {
                    Image(
                        painter = rememberAsyncImagePainter(category.strCategoryThumb.value),
                        contentDescription = "${category.strCategory.value} thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 10f).clip(RoundedCornerShape(16.dp)),
                    )
                }
                item { HorizontalDivider() }
                item { Text(category.strCategoryDescription.value, style = MaterialTheme.typography.bodyLarge) }
            }
        }
    }
}
