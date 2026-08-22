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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.ExternalLinkText
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing

@Composable
fun InfoScreen(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.Large),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = AppSizes.MaximumReadableWidth)
                .fillMaxWidth(),
            shape = AppCardShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.ExtraLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                Text(
                    text = stringResource(R.string.app_version_label),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.apis_used),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
                ExternalLinkText(
                    text = stringResource(R.string.the_mealdb),
                    url = stringResource(R.string.https_www_themealdb_com_api_php),
                )
                ExternalLinkText(
                    text = stringResource(R.string.yelp_fusion_api),
                    url = stringResource(R.string.https_docs_developer_yelp_com_reference_v3_business_search),
                )
                ExternalLinkText(
                    text = stringResource(R.string.google_maps_api),
                    url = stringResource(R.string.https_developers_google_com_maps_documentation_android_sdk),
                )
                ExternalLinkText(
                    text = stringResource(R.string.github),
                    url = stringResource(R.string.https_github_com_getrighhttt_recipecompose),
                )
            }
        }
    }
}
