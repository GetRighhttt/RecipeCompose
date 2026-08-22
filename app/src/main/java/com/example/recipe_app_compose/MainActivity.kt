package com.example.recipe_app_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recipe_app_compose.core.components.NetworkUnavailableScreen
import com.example.recipe_app_compose.core.navigation.CategoryScreen
import com.example.recipe_app_compose.core.navigation.NavigationItem
import com.example.recipe_app_compose.core.navigation.RecipeApp
import com.example.recipe_app_compose.core.navigation.navigateToPrimaryDestination
import com.example.recipe_app_compose.core.util.connectivity.ConnectivityStatus
import com.example.recipe_app_compose.core.util.connectivity.openNetworkSettings
import com.example.recipe_app_compose.core.util.connectivity.rememberConnectivityMonitor
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

            val navController = rememberNavController()

            if (!isConnected) {
                AppTheme {
                    NetworkUnavailableScreen(
                        onRetry = connectivityMonitor::refresh,
                        onOpenNetworkSettings = context::openNetworkSettings,
                    )
                }
            } else {
                AppTheme {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = currentBackStackEntry?.destination?.route
                    val yelpBackStackEntry = currentBackStackEntry?.takeIf {
                        it.destination.route == CategoryScreen.YelpScreen.route
                    }
                    val selectedItemIndex = when (currentRoute) {
                        CategoryScreen.InfoScreen.route -> 1
                        else -> 0
                    }
                    val drawerRoutes = setOf(
                        CategoryScreen.RecipeScreen.route,
                        CategoryScreen.InfoScreen.route,
                    )
                    val primaryRoutes = setOf(
                        CategoryScreen.RecipeScreen.route,
                        CategoryScreen.IngredientScreen.route,
                        CategoryScreen.YelpScreen.route,
                        CategoryScreen.FavoriteScreen.route,
                    )
                    val isDrawerDestination = currentRoute == null || currentRoute in drawerRoutes
                    val isPrimaryDestination = currentRoute in primaryRoutes
                    val screenTitle = when (currentRoute) {
                        CategoryScreen.DetailScreen.route -> R.string.details
                        CategoryScreen.RandomMealScreen.route -> R.string.featured_dish
                        CategoryScreen.IngredientScreen.route -> R.string.search
                        CategoryScreen.IngredientDetailScreen.route -> R.string.recipe_details
                        CategoryScreen.FavoriteDetailScreen.route -> R.string.recipe_details
                        CategoryScreen.FavoriteScreen.route -> R.string.saved
                        CategoryScreen.InfoScreen.route -> R.string.info
                        CategoryScreen.YelpScreen.route -> R.string.shops
                        CategoryScreen.MapScreen.route -> R.string.shop_location
                        else -> R.string.explore
                    }

                    val items = listOf(
                        NavigationItem(
                            title = stringResource(R.string.home),
                            selectedIcon = Icons.Filled.Home,
                            unselectedIcon = Icons.Outlined.Home,
                        ), NavigationItem(
                            title = stringResource(R.string.info),
                            selectedIcon = Icons.Filled.Info,
                            unselectedIcon = Icons.Outlined.Info
                        )
                    )
                    val primaryItems = listOf(
                        CategoryScreen.RecipeScreen.route to NavigationItem(
                            title = stringResource(R.string.explore),
                            selectedIcon = Icons.Filled.Home,
                            unselectedIcon = Icons.Outlined.Home,
                        ),
                        CategoryScreen.IngredientScreen.route to NavigationItem(
                            title = stringResource(R.string.search),
                            selectedIcon = Icons.Filled.Search,
                            unselectedIcon = Icons.Outlined.Search,
                        ),
                        CategoryScreen.YelpScreen.route to NavigationItem(
                            title = stringResource(R.string.nearby),
                            selectedIcon = Icons.Filled.Storefront,
                            unselectedIcon = Icons.Outlined.Storefront,
                        ),
                        CategoryScreen.FavoriteScreen.route to NavigationItem(
                            title = stringResource(R.string.saved),
                            selectedIcon = Icons.Filled.Favorite,
                            unselectedIcon = Icons.Outlined.FavoriteBorder,
                        ),
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
                                                1 -> CategoryScreen.InfoScreen.route
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
                                    showBackNavigation = false,
                                )
                            } else {
                                TopAppBar(
                                    title = {
                                        if (currentRoute != CategoryScreen.RecipeScreen.route) {
                                            Text(
                                                stringResource(screenTitle),
                                                style = MaterialTheme.typography.titleLarge,
                                            )
                                        }
                                    },
                                    navigationIcon = {
                                        when {
                                            isDrawerDestination -> IconButton(onClick = {
                                                scope.launch {
                                                    if (drawerState.isClosed) {
                                                        drawerState.open()
                                                    } else {
                                                        drawerState.close()
                                                    }
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu,
                                                    contentDescription = stringResource(R.string.menu),
                                                )
                                            }

                                            !isPrimaryDestination -> IconButton(onClick = {
                                                navController.popBackStack()
                                            }) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = stringResource(R.string.back),
                                                )
                                            }
                                        }
                                    },
                                )
                            }
                        }, bottomBar = {
                            if (isPrimaryDestination) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                ) {
                                    primaryItems.forEach { (route, item) ->
                                        val selected = currentRoute == route
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = {
                                                navController.navigateToPrimaryDestination(route)
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = if (selected) {
                                                        item.selectedIcon
                                                    } else {
                                                        item.unselectedIcon
                                                    },
                                                    contentDescription = item.title,
                                                )
                                            },
                                            label = { Text(item.title) },
                                        )
                                    }
                                }
                            }
                        }) { innerPadding ->
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
