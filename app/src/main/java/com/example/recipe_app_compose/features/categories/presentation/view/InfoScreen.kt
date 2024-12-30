package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.HyperlinkText

@Composable
fun InfoScreen(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.meal_android_app_v_1),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Text(
            text = stringResource(R.string.apis_used),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        )
        HyperlinkText(
            text = "",
            linkText = listOf(stringResource(R.string.the_mealdb)),
            hyperlinks = listOf(stringResource(R.string.https_www_themealdb_com_api_php))
        )
        HyperlinkText(
            text = "",
            linkText = listOf(stringResource(R.string.yelp_fusion_api)),
            hyperlinks = listOf(stringResource(R.string.https_docs_developer_yelp_com_reference_v3_business_search))
        )
        HyperlinkText(
            text = "",
            linkText = listOf(stringResource(R.string.google_maps_api)),
            hyperlinks = listOf(stringResource(R.string.https_developers_google_com_maps_documentation_android_sdk))
        )
        HyperlinkText(
            text = "",
            linkText = listOf(stringResource(R.string.github)),
            hyperlinks = listOf(stringResource(R.string.https_github_com_getrighhttt_recipecompose))
        )

    }
}