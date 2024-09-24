package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.VerticalScrollingWithFixedHeightTextDemo
import com.example.recipe_app_compose.features.categories.domain.model.category.Category

@Composable
fun DetailScreen(category: Category) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = category.strCategory, textAlign = TextAlign.Center)
        Image(
            painter = rememberAsyncImagePainter(
                category.strCategoryThumb
            ), modifier = Modifier
                .wrapContentSize() // wrap in layout
                .padding(8.dp)
                .aspectRatio(1F),
            contentDescription = "${category.strCategory} Thumbnail."
        )
        VerticalScrollingWithFixedHeightTextDemo(
            category.strCategoryDescription,
            height = 300.dp,
            size = 18.sp
        )
    }
}