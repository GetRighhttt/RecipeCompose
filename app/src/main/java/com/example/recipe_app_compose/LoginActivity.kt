package com.example.recipe_app_compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.recipe_app_compose.core.components.AlertDialogExample
import com.example.recipe_app_compose.core.util.permissions.PermissionUtils
import com.example.recipe_app_compose.features.categories.presentation.view.LoginForm
import com.example.recipe_app_compose.ui.theme.MyLoginApplicationTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val permissionUtils = PermissionUtils(context)
            val connectionState by permissionUtils.rememberConnectivityState()
            val isConnected by remember(connectionState) {
                derivedStateOf {
                    connectionState === PermissionUtils.NetworkConnectionState.Available
                }
            }
            var showDialog by remember { mutableStateOf(false) }

            MyLoginApplicationTheme {
                if (!isConnected) {
                   showDialog = true
                    AlertDialogExample(
                        dialogTitle = "Network Unavailable",
                        dialogText = "Please connect to a network service to proceed further.",
                        onDismissRequest = { showDialog = false },
                        onConfirmation = { showDialog = false }
                    )
                } else {
                    LoginForm()
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}