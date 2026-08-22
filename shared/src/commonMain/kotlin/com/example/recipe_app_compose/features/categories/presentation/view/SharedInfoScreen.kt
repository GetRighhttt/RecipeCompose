package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.recipe_app_compose.core.components.ExternalLinkText
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.apis_used
import com.example.recipe_app_compose.shared.generated.resources.app_version_label
import com.example.recipe_app_compose.shared.generated.resources.github
import com.example.recipe_app_compose.shared.generated.resources.github_url
import com.example.recipe_app_compose.shared.generated.resources.google_maps_api
import com.example.recipe_app_compose.shared.generated.resources.google_maps_url
import com.example.recipe_app_compose.shared.generated.resources.the_mealdb
import com.example.recipe_app_compose.shared.generated.resources.themealdb_url
import com.example.recipe_app_compose.shared.generated.resources.yelp_fusion_api
import com.example.recipe_app_compose.shared.generated.resources.yelp_url
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.jetbrains.compose.resources.stringResource

@Composable
fun SharedInfoScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(AppSpacing.Large),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = AppSizes.MaximumReadableWidth).fillMaxWidth(),
            shape = AppCardShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.ExtraLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                Text(
                    text = stringResource(Res.string.app_version_label),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(Res.string.apis_used),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
                ExternalLinkText(stringResource(Res.string.the_mealdb), stringResource(Res.string.themealdb_url))
                ExternalLinkText(stringResource(Res.string.yelp_fusion_api), stringResource(Res.string.yelp_url))
                ExternalLinkText(stringResource(Res.string.google_maps_api), stringResource(Res.string.google_maps_url))
                ExternalLinkText(stringResource(Res.string.github), stringResource(Res.string.github_url))
            }
        }
    }
}
