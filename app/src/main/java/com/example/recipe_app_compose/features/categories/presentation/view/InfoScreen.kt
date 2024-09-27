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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            text = "Meal Android App v.1",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Text(
            text = "APIs Used:",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        )
        HyperlinkText(
            text = "",
            linkText = listOf("The MealDB"),
            hyperlinks = listOf("https://www.themealdb.com/api.php")
        )
        HyperlinkText(
            text = "",
            linkText = listOf("Yelp Fusion API"),
            hyperlinks = listOf("https://docs.developer.yelp.com/reference/v3_business_search")
        )
        HyperlinkText(
            text = "",
            linkText = listOf("Google Maps API"),
            hyperlinks = listOf("https://developers.google.com/maps/documentation/android-sdk")
        )
        HyperlinkText(
            text = "",
            linkText = listOf("GitHub"),
            hyperlinks = listOf("https://github.com/GetRighhttt/RecipeCompose")
        )

    }
}