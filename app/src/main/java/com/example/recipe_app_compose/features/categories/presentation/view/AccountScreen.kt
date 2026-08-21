package com.example.recipe_app_compose.features.categories.presentation.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.LoginActivity
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AccountScreen(modifier: Modifier = Modifier) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showSignOutConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = Firebase.auth
    val signedInEmail = auth.currentUser?.email
        ?: stringResource(R.string.email_not_available)
    val signOutSuccessfulMessage = stringResource(R.string.sign_out_successful)
    val deleteSuccessfulMessage = stringResource(R.string.delete_successful)
    val deleteFailedMessage = stringResource(R.string.delete_account_failed)

    val returnToLogin = {
        context.startActivity(
            Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }

    AccountContent(
        signedInEmail = signedInEmail,
        onSignOut = { showSignOutConfirmation = true },
        onDeleteAccount = { showDeleteConfirmation = true },
        modifier = modifier,
    )

    if (showSignOutConfirmation) {
        AlertDialogExample(
            dialogTitle = stringResource(R.string.sign_out),
            dialogText = stringResource(R.string.are_you_sure_you_want_to_sign_out_of_your_account),
            onDismissRequest = { showSignOutConfirmation = false },
            onConfirmation = {
                showSignOutConfirmation = false
                auth.signOut()
                Toast.makeText(context, signOutSuccessfulMessage, Toast.LENGTH_SHORT).show()
                returnToLogin()
            },
        )
    }

    if (showDeleteConfirmation) {
        AlertDialogExample(
            dialogTitle = stringResource(R.string.delete_account),
            dialogText = stringResource(R.string.are_you_sure_you_want_to_delete_your_account),
            onDismissRequest = { showDeleteConfirmation = false },
            onConfirmation = {
                showDeleteConfirmation = false
                val user = auth.currentUser
                if (user == null) {
                    Toast.makeText(context, deleteFailedMessage, Toast.LENGTH_SHORT).show()
                } else {
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                deleteSuccessfulMessage,
                                Toast.LENGTH_SHORT,
                            ).show()
                            returnToLogin()
                        } else {
                            Toast.makeText(
                                context,
                                deleteFailedMessage,
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    }
                }
            },
        )
    }
}

@Composable
internal fun AccountContent(
    signedInEmail: String,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = AppSizes.MaximumReadableWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.Large),
        ) {
            Surface(
                shape = AppCardShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(AppSpacing.ExtraLarge)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.signed_in_as),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = signedInEmail,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.Large))
                    Text(
                        text = stringResource(R.string.account_management_description),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
                    Button(
                        onClick = onSignOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = AppSizes.MinimumTouchTarget),
                    ) {
                        Text(stringResource(R.string.sign_out))
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.ExtraExtraLarge))
            Text(
                text = stringResource(R.string.danger_zone),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            Surface(
                shape = AppCardShape,
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(AppSpacing.Large)) {
                    Text(
                        text = stringResource(R.string.delete_account_description),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.Large))
                    OutlinedButton(
                        onClick = onDeleteAccount,
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
            }
        }
    }
}
