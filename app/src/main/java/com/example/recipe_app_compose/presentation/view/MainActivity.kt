package com.example.recipe_app_compose.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.presentation.AlertDialogExample
import com.example.recipe_app_compose.presentation.FullScreenDialog
import com.example.recipe_app_compose.ui.theme.Recipe_App_ComposeTheme
import androidx.compose.ui.unit.sp as sp

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val sheetState = rememberModalBottomSheetState()
            var showBottomSheet by remember { mutableStateOf(false) }
            var showFullDialogBox by remember { mutableStateOf(false) }
            var showAlertDialogBox by remember { mutableStateOf(false) }

            Recipe_App_ComposeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text("Home")
                            },
                            navigationIcon = {
                                val context = LocalContext.current
                                IconButton(onClick = {
                                    showAlertDialogBox = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu"
                                    )

                                }
                                if (showAlertDialogBox) {
                                    AlertDialogExample(
                                        onDismissRequest = { showAlertDialogBox = false },
                                        onConfirmation = { showAlertDialogBox = false },
                                        dialogTitle = "Menu",
                                        dialogText = "This is the Navigation Menu!"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    showBottomSheet = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Add"
                                    )
                                }
                                IconButton(onClick = {
                                    showFullDialogBox = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play"
                                    )
                                }
                                if (showFullDialogBox) {
                                    FullScreenDialog { showFullDialogBox = false }
                                }
                            })
                    }) { innerPadding ->
                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            sheetState = sheetState
                        ) {
                            Column {
                                Text(
                                    "Seafood Sheet",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.padding(top = 20.dp))
                                SeafoodRecipeScreen(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                    RecipeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
