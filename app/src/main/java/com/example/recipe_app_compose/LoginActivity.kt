package com.example.recipe_app_compose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.EmailField
import com.example.recipe_app_compose.core.components.NetworkUnavailableScreen
import com.example.recipe_app_compose.core.components.PasswordInput
import com.example.recipe_app_compose.core.util.connectivity.ConnectivityStatus
import com.example.recipe_app_compose.core.util.connectivity.openNetworkSettings
import com.example.recipe_app_compose.core.util.connectivity.rememberConnectivityMonitor
import com.example.recipe_app_compose.ui.theme.AppControlShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import com.example.recipe_app_compose.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val context = LocalContext.current
                val connectivityMonitor = rememberConnectivityMonitor()
                val connectionState by connectivityMonitor.status.collectAsStateWithLifecycle()
                val isConnected = connectionState == ConnectivityStatus.Available

                if (!isConnected) {
                    NetworkUnavailableScreen(
                        onRetry = connectivityMonitor::refresh,
                        onOpenNetworkSettings = context::openNetworkSettings,
                    )
                } else {
                    LoginContent(
                        onSignIn = ::signIn,
                        onCreateAccount = ::createAccount,
                    )
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    openMain(R.string.account_created)
                } else {
                    showAuthenticationFailure("createUserWithEmail", task.exception)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    openMain(R.string.sign_in_successful)
                } else {
                    showAuthenticationFailure("signInWithEmail", task.exception)
                }
            }
    }

    private fun openMain(@StringRes message: Int) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAuthenticationFailure(operation: String, error: Exception?) {
        Log.w(AUTH_LOG_TAG, "$operation failed", error)
        Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val AUTH_LOG_TAG = "FirebaseAuth"
    }
}

@Composable
internal fun LoginContent(
    onSignIn: (email: String, password: String) -> Unit,
    onCreateAccount: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val credentialsAreValid = email.isNotBlank() && password.isNotBlank()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = AppSpacing.ExtraLarge,
                        vertical = AppSpacing.ExtraExtraLarge,
                    ),
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.dining_two),
                    contentDescription = stringResource(R.string.image),
                    modifier = Modifier
                        .height(128.dp)
                        .aspectRatio(1f),
                )
                Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
                Text(
                    stringResource(R.string.discover_your_next_meal),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
                EmailField(
                    value = email,
                    onChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                PasswordInput(
                    value = password,
                    onChange = { password = it },
                    submit = {
                        if (credentialsAreValid) onSignIn(email.trim(), password)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(AppSpacing.Large))
                Button(
                    onClick = { onSignIn(email.trim(), password) },
                    enabled = credentialsAreValid,
                    shape = AppControlShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = AppSizes.MinimumTouchTarget),
                ) {
                    Text(stringResource(R.string.sign_in))
                }
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                OutlinedButton(
                    onClick = { onCreateAccount(email.trim(), password) },
                    enabled = credentialsAreValid,
                    shape = AppControlShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = AppSizes.MinimumTouchTarget),
                ) {
                    Text(stringResource(R.string.create_account))
                }
            }
        }
    }
}
