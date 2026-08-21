package com.example.recipe_app_compose.features.categories.presentation.view

import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.recipe_app_compose.LoginActivity
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.MinimalDialog
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private enum class SettingsPage(
    @StringRes val titleRes: Int,
    @StringRes val contentRes: Int,
) {
    PersonalDetails(R.string.personal_details, R.string.personal_details_page),
    Preferences(R.string.preferences, R.string.preferences_page),
    Information(R.string.information, R.string.information_page),
    Accessibility(R.string.accessibility, R.string.accessibility_page),
    Privacy(R.string.privacy, R.string.privacy_page),
    Security(R.string.security, R.string.security_page),
    Updates(R.string.updates, R.string.updates_page),
    Faq(R.string.faq, R.string.faq_page),
    Contact(R.string.contact, R.string.contact_page),
}

@Composable
fun SettingsScreen(modifier: Modifier) {
    SettingsInfo(modifier)
}

@Composable
fun SettingsInfo(modifier: Modifier) {
    var activePage by remember { mutableStateOf<SettingsPage?>(null) }
    var deleteState by remember { mutableStateOf(false) }
    var signOutState by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = Firebase.auth
    val signOutSuccessfulMessage = stringResource(R.string.sign_out_successful)
    val deleteSuccessfulMessage = stringResource(R.string.delete_successful)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.Large),
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(AppSpacing.Large))

        Surface(
            shape = AppCardShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SettingsPage.entries.forEachIndexed { index, page ->
                    TextButton(
                        onClick = { activePage = page },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = AppSizes.MinimumTouchTarget),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = AppSpacing.Large,
                            vertical = AppSpacing.Medium,
                        ),
                    ) {
                        Text(
                            text = stringResource(page.titleRes),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    if (index != SettingsPage.entries.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(horizontal = AppSpacing.Large),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
        Text(
            text = stringResource(R.string.account),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        Column(
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = { signOutState = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppSizes.MinimumTouchTarget),
            ) {
                Text(stringResource(R.string.sign_out))
            }
            OutlinedButton(
                onClick = { deleteState = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppSizes.MinimumTouchTarget),
            ) {
                Text(stringResource(R.string.delete_account))
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
    }

    activePage?.let { page ->
        MinimalDialog(stringResource(page.contentRes)) { activePage = null }
    }
    if (signOutState) {
        AlertDialogExample(
            dialogTitle = stringResource(R.string.sign_out),
            dialogText = stringResource(R.string.are_you_sure_you_want_to_sign_out_of_your_account),
            onDismissRequest = { signOutState = false },
            onConfirmation = {
                signOutState = false
                auth.signOut()
                Toast.makeText(context, signOutSuccessfulMessage, Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
            },
        )
    }
    if (deleteState) {
        AlertDialogExample(
            dialogTitle = stringResource(R.string.delete_account),
            dialogText = stringResource(R.string.are_you_sure_you_want_to_delete_your_account),
            onDismissRequest = { deleteState = false },
            onConfirmation = {
                deleteState = false
                auth.currentUser?.delete()
                Toast.makeText(context, deleteSuccessfulMessage, Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
            },
        )
    }
}
