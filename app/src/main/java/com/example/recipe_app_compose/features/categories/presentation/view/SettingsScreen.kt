package com.example.recipe_app_compose.features.categories.presentation.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.LoginActivity
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.MinimalDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun SettingsScreen(modifier: Modifier) {
    SettingsInfo(modifier)
}

@Composable
fun SettingsInfo(modifier: Modifier) {
    var detailState by remember { mutableStateOf(false) }
    var preferenceState by remember { mutableStateOf(false) }
    var infoState by remember { mutableStateOf(false) }
    var accessState by remember { mutableStateOf(false) }
    var privacyState by remember { mutableStateOf(false) }
    var securityState by remember { mutableStateOf(false) }
    var updateState by remember { mutableStateOf(false) }
    var faqState by remember { mutableStateOf(false) }
    var contactState by remember { mutableStateOf(false) }
    var deleteState by remember { mutableStateOf(false) }
    var signOutState by remember { mutableStateOf(false) }
    val contextLocal = LocalContext.current

    // Firebase
    val auth = Firebase.auth
    val signOutFirebase: () -> Unit = {
        auth.signOut()
        Toast.makeText(
            contextLocal,
            contextLocal.getString(R.string.sign_out_successful), Toast.LENGTH_SHORT
        ).show()
    }
    val deleteFirebase: () -> Unit = {
        auth.currentUser?.delete()
        Toast.makeText(
            contextLocal,
            contextLocal.getString(R.string.delete_successful), Toast.LENGTH_SHORT
        ).show()
    }

    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(5.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.settings),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 30.dp)
        )
        HorizontalDivider()
        TextButton(onClick = {
            detailState = true
        }) {
            Text(
                text = stringResource(R.string.personal_details),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (detailState) {
            MinimalDialog(stringResource(R.string.personal_details_page)) { detailState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            preferenceState = true
        }) {
            Text(
                text = stringResource(R.string.preferences),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (preferenceState) {
            MinimalDialog(stringResource(R.string.preferences_page)) { preferenceState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            infoState = true
        }) {
            Text(
                text = stringResource(R.string.information),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (infoState) {
            MinimalDialog(stringResource(R.string.information_page)) { infoState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            accessState = true
        }) {
            Text(
                text = stringResource(R.string.accessibility),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (accessState) {
            MinimalDialog(stringResource(R.string.accessibility_page)) { accessState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            privacyState = true
        }) {
            Text(
                text = stringResource(R.string.privacy),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (privacyState) {
            MinimalDialog(stringResource(R.string.privacy_page)) { privacyState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            securityState = true
        }) {
            Text(
                text = stringResource(R.string.security),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (securityState) {
            MinimalDialog(stringResource(R.string.security_page)) { securityState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            updateState = true
        }) {
            Text(
                text = stringResource(R.string.updates),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (updateState) {
            MinimalDialog(stringResource(R.string.updates_page)) { updateState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            faqState = true
        }) {
            Text(
                text = stringResource(R.string.faq),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (faqState) {
            MinimalDialog(stringResource(R.string.faq_page)) { faqState = false }
        }
        HorizontalDivider()
        TextButton(onClick = {
            contactState = true
        }) {
            Text(
                text = stringResource(R.string.contact),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        if (contactState) {
            MinimalDialog(stringResource(R.string.contact_page)) { contactState = false }
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(bottom = 50.dp))

        // Buttons for Firebase
        Row(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = {
                    signOutState = true
                },
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(20.dp),
                enabled = true,
                contentPadding = PaddingValues(
                    start = 10.dp,
                    end = 10.dp,
                    top = 5.dp,
                    bottom = 5.dp
                ),
                modifier = Modifier.padding(end = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.sign_out),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(5.dp)
                )
            }
            if (signOutState) {
                AlertDialogExample(
                    dialogTitle = stringResource(R.string.sign_out),
                    dialogText = stringResource(R.string.are_you_sure_you_want_to_sign_out_of_your_account),
                    onDismissRequest = { signOutState = false },
                    onConfirmation = {
                        signOutState = false
                        signOutFirebase.invoke()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    },
                )
            }
            ElevatedButton(
                onClick = {
                    deleteState = true
                },
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(20.dp),
                enabled = true,
                contentPadding = PaddingValues(
                    start = 10.dp,
                    end = 10.dp,
                    top = 5.dp,
                    bottom = 5.dp
                ),
                modifier = Modifier.padding(start = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_account),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(5.dp)
                )
            }
            if (deleteState) {
                AlertDialogExample(
                    dialogTitle = stringResource(R.string.delete_account),
                    dialogText = stringResource(R.string.are_you_sure_you_want_to_delete_your_account),
                    onDismissRequest = { deleteState = false },
                    onConfirmation = {
                        deleteState = false
                        deleteFirebase.invoke()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    },
                )
            }
        }
    }
}