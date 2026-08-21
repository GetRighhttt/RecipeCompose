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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recipe_app_compose.core.components.MyBottomAppBar
import com.example.recipe_app_compose.core.components.NetworkUnavailableScreen
import com.example.recipe_app_compose.core.navigation.CategoryScreen
import com.example.recipe_app_compose.core.navigation.NavigationItem
import com.example.recipe_app_compose.core.navigation.RecipeApp
import com.example.recipe_app_compose.core.util.connectivity.ConnectivityStatus
import com.example.recipe_app_compose.core.util.connectivity.openNetworkSettings
import com.example.recipe_app_compose.core.util.connectivity.rememberConnectivityMonitor
import com.example.recipe_app_compose.features.categories.presentation.view.CategoryRecipeScreen
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.features.location.domain.states.YelpSearchArea
import com.example.recipe_app_compose.features.location.presentation.components.YelpSearchTopAppBar
import com.example.recipe_app_compose.features.location.presentation.viewmodel.YelpViewModel
import com.example.recipe_app_compose.ui.theme.AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            val connectivityMonitor = rememberConnectivityMonitor()
            val connectionState by connectivityMonitor.status.collectAsStateWithLifecycle()
            val isConnected = connectionState == ConnectivityStatus.Available

            val sheetState = rememberModalBottomSheetState()
            var showBottomSheet by remember { mutableStateOf(false) }
            val navController = rememberNavController()

            if (!isConnected) {
                AppTheme {
                    NetworkUnavailableScreen(
                        onRetry = connectivityMonitor::refresh,
                        onOpenNetworkSettings = context::openNetworkSettings,
                    )
                }
            } else {
                val recipeViewModel: RecipeViewModel = viewModel()
                AppTheme {
                    /* Navigation Drawer Code */
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = currentBackStackEntry?.destination?.route
                    val yelpBackStackEntry = currentBackStackEntry?.takeIf {
                        it.destination.route == CategoryScreen.YelpScreen.route
                    }
                    val selectedItemIndex = when (currentRoute) {
                        CategoryScreen.SettingsScreen.route -> 1
                        CategoryScreen.InfoScreen.route -> 2
                        else -> 0
                    }
                    val drawerRoutes = setOf(
                        CategoryScreen.RecipeScreen.route,
                        CategoryScreen.SettingsScreen.route,
                        CategoryScreen.InfoScreen.route,
                    )
                    val isDrawerDestination = currentRoute == null || currentRoute in drawerRoutes
                    val screenTitle = when (currentRoute) {
                        CategoryScreen.DetailScreen.route -> R.string.details
                        CategoryScreen.RandomMealScreen.route -> R.string.featured_dish
                        CategoryScreen.IngredientScreen.route -> R.string.search_for_specific_meals
                        CategoryScreen.IngredientDetailScreen.route -> R.string.details
                        CategoryScreen.SettingsScreen.route -> R.string.settings
                        CategoryScreen.FavoriteScreen.route -> R.string.favorites
                        CategoryScreen.InfoScreen.route -> R.string.info
                        CategoryScreen.YelpScreen.route -> R.string.shops
                        CategoryScreen.MapScreen.route -> R.string.shop_location
                        else -> R.string.browse_cuisines
                    }

                    val items = listOf(
                        NavigationItem(
                            title = stringResource(R.string.home),
                            selectedIcon = Icons.Filled.Home,
                            unselectedIcon = Icons.Outlined.Home,
                        ), NavigationItem(
                            title = stringResource(R.string.settings),
                            selectedIcon = Icons.Filled.Settings,
                            unselectedIcon = Icons.Outlined.Settings,
                        ), NavigationItem(
                            title = stringResource(R.string.info),
                            selectedIcon = Icons.Filled.Info,
                            unselectedIcon = Icons.Outlined.Info
                        )
                    )
                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet {
                                Spacer(modifier = Modifier.height(16.dp))
                                items.forEachIndexed { index, item ->
                                    NavigationDrawerItem(
                                        label = {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        },
                                        selected = index == selectedItemIndex,
                                        onClick = {
                                            scope.launch {
                                                drawerState.close()
                                            }
                                            val route = when (index) {
                                                1 -> CategoryScreen.SettingsScreen.route
                                                2 -> CategoryScreen.InfoScreen.route
                                                else -> CategoryScreen.RecipeScreen.route
                                            }
                                            navController.navigate(route) {
                                                popUpTo(CategoryScreen.RecipeScreen.route) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
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
                        },
                        drawerState = drawerState,
                        gesturesEnabled = isDrawerDestination,
                    ) {
                        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                            if (yelpBackStackEntry != null) {
                                val yelpViewModel: YelpViewModel = viewModel(
                                    viewModelStoreOwner = yelpBackStackEntry,
                                )
                                val yelpState by yelpViewModel.uiState
                                    .collectAsStateWithLifecycle()
                                val yelpSearchQuery by yelpViewModel.searchQuery
                                    .collectAsStateWithLifecycle()
                                val yelpSearchActive by yelpViewModel.isSearchActive
                                    .collectAsStateWithLifecycle()
                                val searchEnabled =
                                    yelpState.searchArea == YelpSearchArea.CurrentLocation ||
                                        yelpState.searchArea is YelpSearchArea.NamedLocation

                                YelpSearchTopAppBar(
                                    query = yelpSearchQuery,
                                    searchActive = yelpSearchActive,
                                    searchEnabled = searchEnabled,
                                    onQueryChange = yelpViewModel::onSearchTextChange,
                                    onSearchActiveChange = yelpViewModel::onSearchActiveChange,
                                    onNavigateBack = navController::popBackStack,
                                )
                            } else {
                                TopAppBar(
                                    title = {
                                        Text(
                                            stringResource(screenTitle),
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            if (isDrawerDestination) {
                                                scope.launch {
                                                    if (drawerState.isClosed) {
                                                        drawerState.open()
                                                    } else {
                                                        drawerState.close()
                                                    }
                                                }
                                            } else {
                                                navController.popBackStack()
                                            }
                                        }) {
                                            Icon(
                                                imageVector = if (isDrawerDestination) {
                                                    Icons.Default.Menu
                                                } else {
                                                    Icons.AutoMirrored.Filled.ArrowBack
                                                },
                                                contentDescription = stringResource(
                                                    if (isDrawerDestination) {
                                                        R.string.menu
                                                    } else {
                                                        R.string.back
                                                    }
                                                )
                                            )
                                        }
                                    },
                                    actions = {
                                        if (currentRoute == CategoryScreen.RecipeScreen.route) {
                                            IconButton(onClick = {
                                                navController.navigate(
                                                    CategoryScreen.IngredientScreen.route
                                                ) {
                                                    launchSingleTop = true
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = stringResource(
                                                        R.string.search
                                                    )
                                                )
                                            }
                                        }
                                    },
                                )
                            }
                        }, bottomBar = {
                            if (isDrawerDestination) {
                                MyBottomAppBar(
                                    modifier = Modifier.fillMaxWidth(),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    content = {
                                        IconButton(onClick = {
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_EMAIL, arrayOf(
                                                        getString(R.string.stefanbayne_gmail_com)
                                                    )
                                                )
                                                putExtra(
                                                    Intent.EXTRA_SUBJECT,
                                                    getString(R.string.sharing_application)
                                                )
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    getString(R.string.https_github_com_getrighhttt_recipecompose)
                                                )
                                            }
                                            if (intent.resolveActivity(packageManager) != null) {
                                                startActivity(intent)
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = stringResource(R.string.share)
                                            )
                                        }
                                        IconButton(onClick = {
                                            navController.navigate(CategoryScreen.YelpScreen.route) {
                                                launchSingleTop = true
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ShoppingCart,
                                                contentDescription = stringResource(R.string.shops)
                                            )
                                        }
                                        IconButton(onClick = {
                                            navController.navigate(CategoryScreen.FavoriteScreen.route) {
                                                launchSingleTop = true
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = stringResource(R.string.favorites)
                                            )
                                        }
                                        IconButton(onClick = {
                                            navController.navigate(CategoryScreen.RandomMealScreen.route) {
                                                launchSingleTop = true
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = stringResource(R.string.play)
                                            )
                                        }
                                        IconButton(onClick = { showBottomSheet = true }) {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowUp,
                                                contentDescription = stringResource(R.string.settings)
                                            )
                                        }
                                    },
                                )
                            }
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
                                            title = {
                                                Text(
                                                    stringResource(R.string.explore_dishes),
                                                    style = MaterialTheme.typography.titleLarge
                                                )
                                            },
                                            actions = {
                                                IconButton(onClick = {
                                                    recipeViewModel.fetchCategoryMeals()
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Refresh,
                                                        contentDescription = stringResource(R.string.refresh)
                                                    )
                                                }
                                            })
                                    }) { innerPadding ->
                                        Spacer(modifier = Modifier.padding(innerPadding))
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
    }
}
