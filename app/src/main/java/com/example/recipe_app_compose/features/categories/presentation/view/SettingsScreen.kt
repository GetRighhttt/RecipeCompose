package com.example.recipe_app_compose.features.categories.presentation.view

import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.MinimalDialog

@Composable
fun SettingsScreen(modifier: Modifier) {

    var detailState by remember { mutableStateOf(false) }
    var preferenceState by remember { mutableStateOf(false) }
    var infoState by remember { mutableStateOf(false) }
    var accessState by remember { mutableStateOf(false) }
    var privacyState by remember { mutableStateOf(false) }
    var securityState by remember { mutableStateOf(false) }
    var updateState by remember { mutableStateOf(false) }
    var faqState by remember { mutableStateOf(false) }
    var contactState by remember { mutableStateOf(false) }
    var dialogState by remember { mutableStateOf(false) }

    val context = LocalContext.current
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
        TextButton(onClick = {
            detailState = true
        }) {
            Text(
                text = "Personal Details",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (detailState) {
            MinimalDialog("Personal Details Page") { detailState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            preferenceState = true
        }) {
            Text(
                text = "Preferences",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (preferenceState) {
            MinimalDialog("Preferences Page") { preferenceState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            infoState = true
        }) {
            Text(
                text = "Information",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (infoState) {
            MinimalDialog("Information Page") { infoState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            accessState = true
        }) {
            Text(
                text = "Accessibility",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (accessState) {
            MinimalDialog("Accessibility Page") { accessState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            privacyState = true
        }) {
            Text(
                text = "Privacy",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (privacyState) {
            MinimalDialog("Privacy Page") { privacyState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            securityState = true
        }) {
            Text(
                text = "Security",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (securityState) {
            MinimalDialog("Security Page") { securityState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            updateState = true
        }) {
            Text(
                text = "Updates",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (updateState) {
            MinimalDialog("Updates Page") { updateState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            faqState = true
        }) {
            Text(
                text = "FAQ",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (faqState) {
            MinimalDialog("FAQ Page") { faqState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            contactState = true
        }) {
            Text(
                text = "Contact",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (contactState) {
            MinimalDialog("Contact Page") { contactState = false }
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(bottom = 50.dp))
        OutlinedButton(
            onClick = {
                dialogState = true
            },
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(20.dp),
            enabled = true,
            contentPadding = PaddingValues(start = 50.dp, end = 50.dp, top = 20.dp, bottom = 20.dp),
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Delete Account",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(5.dp)
            )
        }
        if (dialogState) {
            AlertDialogExample(
                dialogTitle = "Delete Account",
                dialogText = "Are you sure you want to delete your account?",
                onDismissRequest = { dialogState = false },
                onConfirmation = {
                    dialogState = false
                    Toast.makeText(context, "Your account has been deleted", LENGTH_LONG).show()
                },
            )
        }
    }
}