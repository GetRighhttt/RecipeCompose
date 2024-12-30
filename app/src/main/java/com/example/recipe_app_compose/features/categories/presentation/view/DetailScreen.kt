package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.VerticalScrollingWithFixedHeightTextDemo
import com.example.recipe_app_compose.features.categories.domain.model.category.Category

@Composable
fun DetailScreen(category: Category) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = category.strCategory,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        Image(
            painter = rememberAsyncImagePainter(
                category.strCategoryThumb,
                imageLoader = ImageLoader.Builder(context).crossfade(500).build()
            ), modifier = Modifier
                .wrapContentSize() // wrap in layout
                .height(225.dp)
                .padding(15.dp)
                .aspectRatio(1F)
                .clip(RoundedCornerShape(10.dp)),
            contentDescription = "${category.strCategory} Thumbnail."
        )
        HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp))
        VerticalScrollingWithFixedHeightTextDemo(
            category.strCategoryDescription,
            height = 225.dp,
            size = 15.sp
        )
    }
}