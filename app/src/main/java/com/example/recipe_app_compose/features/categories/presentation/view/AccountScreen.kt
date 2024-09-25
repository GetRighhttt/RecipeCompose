package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R

@Composable
fun AccountScreen(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.stefan_profile),
            modifier = Modifier
                .wrapContentSize() // wrap in layout
                .padding(40.dp)
                .aspectRatio(1F),
            contentDescription = "Thumbnail."
        )
        Text(
            text = "Stefan Bayne",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
        )
        Text(
            text = "Android Developer",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        )
        Text(
            text = "Email: stefanbusiness95@gmail.com",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        )
        Text(
            text = "Passionate about creating beautiful and functional Android apps.",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        )
    }
}