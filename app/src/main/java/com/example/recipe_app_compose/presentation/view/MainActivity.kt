package com.example.recipe_app_compose.presentation.view

import android.content.Intent
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.recipe_app_compose.core.navigation.CategoryScreen
import com.example.recipe_app_compose.core.navigation.RecipeApp
import com.example.recipe_app_compose.presentation.AlertDialogExample
import com.example.recipe_app_compose.presentation.FullScreenDialog
import com.example.recipe_app_compose.presentation.IngredientFullScreenDialog
import com.example.recipe_app_compose.presentation.MyBottomAppBar
import com.example.recipe_app_compose.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.ui.theme.Recipe_App_ComposeTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val sheetState = rememberModalBottomSheetState()
            var showBottomSheet by remember { mutableStateOf(false) }
            var showFullDialogBox by remember { mutableStateOf(false) }
            var showSearchDialog by remember { mutableStateOf(false) }
            var showAlertDialogBox by remember { mutableStateOf(false) }
            var showCategoryMealDialogBox by remember { mutableStateOf(false) }

            val navController = rememberNavController()

            Recipe_App_ComposeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text("Recipes")
                            },
                            navigationIcon = {
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
                                        contentDescription = "Up"
                                    )
                                }
                                IconButton(onClick = {
                                    showSearchDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search"
                                    )
                                    if (showSearchDialog) {
                                        IngredientFullScreenDialog { showSearchDialog = false }
                                    }
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
                    },
                    bottomBar = {
                        MyBottomAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            content = {
                                IconButton(
                                    onClick = {
                                        // share an email about the application or other things
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_EMAIL, arrayOf(
                                                    "stefanbayne@gmail.com"
                                                )
                                            )
                                            putExtra(Intent.EXTRA_SUBJECT, "Sharing application")
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Please checkout my Rick and Morty application that I have created!"
                                            )
                                        }
                                        // another approach to error handling with resolve activity
                                        if (intent.resolveActivity(packageManager) != null) {
                                            startActivity(intent)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share"
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        navController.navigate(CategoryScreen.SettingsScreen.route) {
                                            launchSingleTop = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings"
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        navController.navigate(CategoryScreen.FavoriteScreen.route) {
                                            launchSingleTop = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Favorites"
                                    )
                                }
                            }
                        )
                    }) { innerPadding ->
                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            sheetState = sheetState,
                            tonalElevation = 20.dp,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Scaffold(topBar = {
                                CenterAlignedTopAppBar(
                                    title = { Text("Explore Our Best Dishes!") },
                                    actions = {
                                        IconButton(onClick = {
                                            showCategoryMealDialogBox = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Refresh"
                                            )
                                        }
                                        if (showCategoryMealDialogBox) {
                                            showCategoryMealDialogBox = false
                                            val viewModel: RecipeViewModel = viewModel()
                                            viewModel.fetchCategoryMeals()
                                        }
                                    }
                                )
                            }) { innerPadding ->
                                Spacer(modifier = Modifier.padding(top = 20.dp))
                                Column(modifier = Modifier.padding(innerPadding)) {
                                    CategoryRecipeScreen(modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    }
                    RecipeApp(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
