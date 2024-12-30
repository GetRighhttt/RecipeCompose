package com.example.recipe_app_compose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
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
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.components.LoginField
import com.example.recipe_app_compose.core.components.PasswordField
import com.example.recipe_app_compose.core.util.permissions.PermissionUtils
import com.example.recipe_app_compose.ui.theme.MyLoginApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyLoginApplicationTheme {
                val user = auth.currentUser

                val context = LocalContext.current
                val permissionUtils = PermissionUtils(context)
                val connectionState by permissionUtils.rememberConnectivityState()
                val isConnected by remember(connectionState) {
                    derivedStateOf {
                        connectionState === PermissionUtils.NetworkConnectionState.Available
                    }
                }
                var showDialog by remember { mutableStateOf(false) }

                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }


                if (!isConnected) {
                    showDialog = true
                    AlertDialogExample(
                        dialogTitle = "Network Unavailable",
                        dialogText = "Please connect to a network service to proceed further.",
                        onDismissRequest = { showDialog = false },
                        onConfirmation = { showDialog = false }
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 30.dp, vertical = 100.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                R.drawable.dining_two,
                                imageLoader = ImageLoader.Builder(context).crossfade(1000).build()
                            ),
                            contentDescription = stringResource(R.string.image),
                            modifier = Modifier
                                .height(150.dp)
                                .aspectRatio(1f)
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            stringResource(R.string.favorite_cuisines),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .fillMaxWidth()
                        )
                        LoginField(
                            value = email,
                            onChange = { email = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        PasswordField(
                            value = password,
                            onChange = { password = it },
                            submit = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (user == null) {
                                    createAccount(email, password, context = this@LoginActivity)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        getString(R.string.account_created),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    signIn(email, password, context = this@LoginActivity)
                                }
                            },
                            enabled = password.isNotEmpty(),
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.login))
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
