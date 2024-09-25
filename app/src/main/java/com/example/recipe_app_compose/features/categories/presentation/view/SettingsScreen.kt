package com.example.recipe_app_compose.features.categories.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Settings",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 30.dp)
        )
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "Privacy ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "Personal Details ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "Preferences ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "Accessibility ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "Information ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "Updates ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "FAQ ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "Security ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        TextButton(onClick = {}) {
            Text(
                text = "Contact ->",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(bottom = 50.dp))
        OutlinedButton(onClick = {},
            shape = RoundedCornerShape(30.dp),
            elevation = ButtonDefaults.buttonElevation(20.dp),
            enabled = true
        ) {
            Text(
                text = "Sign Out",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
    }
}

