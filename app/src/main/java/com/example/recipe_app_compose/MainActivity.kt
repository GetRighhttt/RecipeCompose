package com.example.recipe_app_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.recipe_app_compose.core.components.FullScreenDialog
import com.example.recipe_app_compose.core.components.MyBottomAppBar
import com.example.recipe_app_compose.core.components.ReusableFullScreenDialog
import com.example.recipe_app_compose.core.navigation.CategoryScreen
import com.example.recipe_app_compose.core.navigation.NavigationItem
import com.example.recipe_app_compose.core.navigation.RecipeApp
import com.example.recipe_app_compose.features.categories.presentation.view.CategoryRecipeScreen
import com.example.recipe_app_compose.features.categories.presentation.view.IngredientScreen
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.features.location.presentation.view.RequestLocation
import com.example.recipe_app_compose.features.location.presentation.view.YelpScreen
import com.example.recipe_app_compose.ui.theme.Recipe_App_ComposeTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

     @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // request location
            RequestLocation()

            val sheetState = rememberModalBottomSheetState()
            var showBottomSheet by remember { mutableStateOf(false) }
            var showFullDialogBox by remember { mutableStateOf(false) }
            var showYelpDialogBox by remember { mutableStateOf(false) }
            var showSearchDialog by remember { mutableStateOf(false) }
            var showCategoryMealDialogBox by remember { mutableStateOf(false) }
            val navController = rememberNavController()

            Recipe_App_ComposeTheme {

                /*
                Navigation Drawer Code
                 */
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

                val items = listOf(
                    NavigationItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                    ), NavigationItem(
                        title = "Favorites",
                        selectedIcon = Icons.Filled.Favorite,
                        unselectedIcon = Icons.Outlined.Favorite,
                    ), NavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                    ), NavigationItem(
                        title = "About Me",
                        selectedIcon = Icons.Filled.AccountCircle,
                        unselectedIcon = Icons.Outlined.AccountCircle
                    ), NavigationItem(
                        title = "Shops Near Me",
                        selectedIcon = Icons.Filled.ShoppingCart,
                        unselectedIcon = Icons.Outlined.ShoppingCart,
                    )
                )

                ModalNavigationDrawer(
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(modifier = Modifier.height(16.dp))
                            items.forEachIndexed { index, item ->
                                NavigationDrawerItem(
                                    label = {
                                        Text(text = item.title)
                                    },
                                    selected = index == selectedItemIndex,
                                    onClick = {
                                        val closeDrawer: Job = scope.launch {
                                            drawerState.close()
                                        }
                                        selectedItemIndex = index

                                        when (index) {
                                            0 -> {
                                                navController.navigate(CategoryScreen.RecipeScreen.route)
                                                closeDrawer.isActive
                                            }

                                            1 -> {
                                                navController.navigate(CategoryScreen.FavoriteScreen.route)
                                                closeDrawer.isActive
                                            }

                                            2 -> {
                                                navController.navigate(CategoryScreen.SettingsScreen.route)
                                                closeDrawer.isActive
                                            }

                                            3 -> {
                                                navController.navigate(CategoryScreen.AccountScreen.route)
                                                closeDrawer.isActive
                                            }

                                            4 -> {
                                                navController.navigate(CategoryScreen.YelpScreen.route)
                                                closeDrawer.isActive
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (index == selectedItemIndex) {
                                                item.selectedIcon
                                            } else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }
                        }
                    }, drawerState = drawerState
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                        TopAppBar(
                            title = {
                                Text("Favorite Cuisines", style = MaterialTheme.typography.titleLarge)
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    when {
                                        drawerState.isClosed -> {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }

                                        drawerState.isClosed ->
                                            scope.launch {
                                                drawerState.close()
                                            }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu"
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
                                        ReusableFullScreenDialog({
                                            IngredientScreen(modifier = Modifier.fillMaxSize())
                                        }) {
                                            showSearchDialog = false
                                        }
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
                                    FullScreenDialog {
                                        showFullDialogBox = false
                                    }
                                }
                            })
                    }, bottomBar = {
                        MyBottomAppBar(modifier = Modifier.fillMaxWidth(), content = {
                            IconButton(onClick = {
                                // share an email about the application or other things
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_EMAIL, arrayOf(
                                            "stefanbayne@gmail.com"
                                        )
                                    )
                                    putExtra(
                                        Intent.EXTRA_SUBJECT, "Sharing application"
                                    )
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Please checkout my Rick and Morty application that I have created!"
                                    )
                                }
                                // another approach to error handling with resolve activity
                                if (intent.resolveActivity(packageManager) != null) {
                                    startActivity(intent)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share"
                                )
                            }
                            IconButton(onClick = {
                                navController.navigate(CategoryScreen.FavoriteScreen.route) {
                                    launchSingleTop = true
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorites"
                                )
                            }
                            IconButton(onClick = {
                                navController.navigate(CategoryScreen.AccountScreen.route) {
                                    launchSingleTop = true
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Account"
                                )
                            }
                            IconButton(onClick = {
                                navController.navigate(CategoryScreen.SettingsScreen.route) {
                                    launchSingleTop = true
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                            IconButton(onClick = {
                                showYelpDialogBox = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Shops"
                                )
                                if (showYelpDialogBox) {
                                    ReusableFullScreenDialog({ YelpScreen(modifier = Modifier) }) {
                                        showYelpDialogBox = false
                                    }
                                }
                            }
                        })
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
                                    CenterAlignedTopAppBar(title = { Text("Explore Our Best Dishes!", style = MaterialTheme.typography.titleLarge) },
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
                                        })
                                }) { innerPadding ->
                                    Spacer(modifier = Modifier.padding(top = 20.dp))
                                    Column(modifier = Modifier.padding(innerPadding)) {
                                        CategoryRecipeScreen(modifier = Modifier.fillMaxSize())
                                    }
                                }
                            }
                        }
                        RecipeApp(
                            navController = navController, modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }

    }
}
