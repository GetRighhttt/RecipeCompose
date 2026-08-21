package com.example.recipe_app_compose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.LoginField
import com.example.recipe_app_compose.core.components.NetworkUnavailableScreen
import com.example.recipe_app_compose.core.components.PasswordField
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
                val user = auth.currentUser

                val context = LocalContext.current
                val connectivityMonitor = rememberConnectivityMonitor()
                val connectionState by connectivityMonitor.status.collectAsStateWithLifecycle()
                val isConnected = connectionState == ConnectivityStatus.Available

                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }


                if (!isConnected) {
                    NetworkUnavailableScreen(
                        onRetry = connectivityMonitor::refresh,
                        onOpenNetworkSettings = context::openNetworkSettings,
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
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
                                    stringResource(R.string.favorite_cuisines),
                                    style = MaterialTheme.typography.headlineMedium,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.ExtraLarge))
                                LoginField(
                                    value = email,
                                    onChange = { email = it },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                                PasswordField(
                                    value = password,
                                    onChange = { password = it },
                                    submit = {},
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.Large))
                                Button(
                                    onClick = {
                                        if (user == null) {
                                            createAccount(email, password, context = this@LoginActivity)
                                        } else {
                                            signIn(email, password, context = this@LoginActivity)
                                        }
                                    },
                                    enabled = password.isNotEmpty(),
                                    shape = AppControlShape,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = AppSizes.MinimumTouchTarget),
                                ) {
                                    Text(stringResource(R.string.login))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = auth.currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            Log.d("USER_FIREBASE", "User: $user - ${user.email}")
        }
    }

    private fun createAccount(email: String, password: String, context: Activity) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("USER_FIREBASE", "createUserWithEmail:success")
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context).finish()
                    Toast.makeText(
                        this@LoginActivity,
                        "Account Created",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("USER_FIREBASE", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String, context: Activity) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("USER_FIREBASE", "signInWithEmail:success")
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context).finish()
                    Toast.makeText(this@LoginActivity, "Account Created", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("USER_FIREBASE", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        // [END sign_in_with_email]
    }
}
