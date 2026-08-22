package com.example.recipe_app_compose.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.recipe_app_compose.di.sharedAppModule
import com.example.recipe_app_compose.core.onboarding.CURRENT_ONBOARDING_VERSION
import com.example.recipe_app_compose.core.onboarding.OnboardingCompletionStore
import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.model.details.MealDetails
import com.example.recipe_app_compose.features.categories.domain.model.details.toMealDetails
import com.example.recipe_app_compose.features.categories.domain.model.details.toRandomMeal
import com.example.recipe_app_compose.features.categories.domain.model.details.containsSavedMeal
import com.example.recipe_app_compose.features.categories.presentation.RecipeStore
import com.example.recipe_app_compose.features.categories.presentation.FavoritesStore
import com.example.recipe_app_compose.features.categories.presentation.view.DetailScreen
import com.example.recipe_app_compose.features.categories.presentation.view.RecipeScreen
import com.example.recipe_app_compose.features.categories.presentation.view.SharedMealDetailsScreen
import com.example.recipe_app_compose.features.categories.presentation.view.SharedFavoritesScreen
import com.example.recipe_app_compose.features.categories.presentation.view.SharedInfoScreen
import com.example.recipe_app_compose.features.categories.presentation.view.SharedSearchScreen
import com.example.recipe_app_compose.features.onboarding.presentation.OnboardingScreen
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.feature_not_available_message
import com.example.recipe_app_compose.shared.generated.resources.feature_not_available_title
import com.example.recipe_app_compose.shared.generated.resources.explore
import com.example.recipe_app_compose.shared.generated.resources.home
import com.example.recipe_app_compose.shared.generated.resources.info
import com.example.recipe_app_compose.shared.generated.resources.menu
import com.example.recipe_app_compose.shared.generated.resources.nav_home
import com.example.recipe_app_compose.shared.generated.resources.nav_info
import com.example.recipe_app_compose.shared.generated.resources.nav_menu
import com.example.recipe_app_compose.shared.generated.resources.search
import com.example.recipe_app_compose.shared.generated.resources.nearby
import com.example.recipe_app_compose.shared.generated.resources.saved
import com.example.recipe_app_compose.ui.theme.AppTheme
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.module.Module
import org.koin.dsl.koinConfiguration
import kotlinx.coroutines.launch

@Composable
fun RecipeComposeApp(platformModule: Module) {
    KoinApplication(
        configuration = koinConfiguration {
            modules(sharedAppModule, platformModule)
        },
    ) {
        AppTheme { RecipeComposeContent() }
    }
}

@Composable
private fun RecipeComposeContent() {
    val onboardingStore: OnboardingCompletionStore = koinInject()
    var completedOnboardingVersion by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(onboardingStore) {
        completedOnboardingVersion = runCatching {
            onboardingStore.completedVersion()
        }.getOrDefault(0)
    }

    if (completedOnboardingVersion == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (completedOnboardingVersion!! < CURRENT_ONBOARDING_VERSION) {
        OnboardingScreen(
            onFinished = {
                scope.launch {
                    onboardingStore.markCompleted()
                    completedOnboardingVersion = CURRENT_ONBOARDING_VERSION
                }
            },
        )
        return
    }

    val store: RecipeStore = koinInject()
    val categories by store.uiState.collectAsState()
    val featuredMeal by store.randomMealUiState.collectAsState()
    val query by store.searchQuery.collectAsState()
    val results by store.ingredients.collectAsState()
    val ingredientState by store.ingredientUiState.collectAsState()
    val favoritesStore: FavoritesStore = koinInject()
    val favoritesState by favoritesStore.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedMeal by remember { mutableStateOf<SelectedMeal?>(null) }
    var destination by remember { mutableStateOf(SharedDestination.Explore) }
    var showInfo by remember { mutableStateOf(false) }
    val drawerState = androidx.compose.material3.rememberDrawerState(
        initialValue = androidx.compose.material3.DrawerValue.Closed,
    )

    selectedMeal?.let { selection ->
        val displayedMeal = if (selection.origin == MealDetailsOrigin.Featured) {
            featuredMeal.item.firstOrNull()?.toMealDetails() ?: selection.meal
        } else {
            selection.meal
        }
        val isSaved = favoritesState.list.containsSavedMeal(displayedMeal.id)
        SharedMealDetailsScreen(
            meal = displayedMeal,
            onBack = { selectedMeal = null },
            isSaved = isSaved,
            onSave = if (selection.origin == MealDetailsOrigin.Saved) null else {
                { favoritesStore.saveMeal(displayedMeal.toRandomMeal()) }
            },
            onRemove = if (selection.origin == MealDetailsOrigin.Saved) {
                {
                    favoritesStore.deleteMeal(displayedMeal.toRandomMeal())
                    selectedMeal = null
                }
            } else null,
            onRefresh = if (selection.origin == MealDetailsOrigin.Featured) {
                { store.fetchRandomMeal() }
            } else null,
            isRefreshing = featuredMeal.loading,
        )
        return
    }
    selectedCategory?.let { category ->
        DetailScreen(category, onBack = { selectedCategory = null })
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                SharedDrawerDestination.entries.forEach { item ->
                    val label = drawerLabel(item)
                    NavigationDrawerItem(
                        label = { Text(label) },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon()),
                                contentDescription = null,
                            )
                        },
                        selected = when (item) {
                            SharedDrawerDestination.Home -> !showInfo
                            SharedDrawerDestination.Info -> showInfo
                        },
                        onClick = {
                            showInfo = item == SharedDrawerDestination.Info
                            if (!showInfo) destination = SharedDestination.Explore
                            scope.launch { drawerState.close() }
                        },
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                if (!showInfo && destination != SharedDestination.Explore) {
                    PrimaryDestinationTopAppBar(destination.title())
                } else {
                    Surface(color = MaterialTheme.colorScheme.surface) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(Res.drawable.nav_menu),
                                contentDescription = stringResource(Res.string.menu),
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (!showInfo) {
                    SharedBottomNavigation(destination) { destination = it }
                }
            },
        ) { paddingValues ->
        if (showInfo) {
            SharedInfoScreen(Modifier.padding(paddingValues))
        } else when (destination) {
            SharedDestination.Explore -> RecipeScreen(
                uiState = categories,
                featuredMealState = featuredMeal,
                navigateToDetail = { selectedCategory = it },
                onSearch = { destination = SharedDestination.Search },
                onNearbyShops = { destination = SharedDestination.Nearby },
                onFavorites = { destination = SharedDestination.Saved },
                onFeaturedDish = {
                    selectedMeal = featuredMeal.item.firstOrNull()?.toMealDetails()?.let {
                        SelectedMeal(it, MealDetailsOrigin.Featured)
                    }
                },
                onRetry = store::fetchCategories,
                modifier = Modifier.padding(paddingValues),
            )
            SharedDestination.Search -> SharedSearchScreen(
                store = store,
                query = query,
                results = results,
                isLoading = ingredientState.loading,
                onDishSelected = {
                    selectedMeal = SelectedMeal(it.toMealDetails(), MealDetailsOrigin.Search)
                },
                modifier = Modifier.padding(paddingValues),
            )
            SharedDestination.Nearby -> NativeFeaturePending(Modifier.padding(paddingValues))
            SharedDestination.Saved -> SharedFavoritesScreen(
                uiState = favoritesState,
                onMealSelected = {
                    selectedMeal = SelectedMeal(it.toMealDetails(), MealDetailsOrigin.Saved)
                },
                onDeleteAll = favoritesStore::deleteAllMeals,
                onRetry = favoritesStore::retry,
                modifier = Modifier.padding(paddingValues),
            )
        }
        }
    }
}

private data class SelectedMeal(
    val meal: MealDetails,
    val origin: MealDetailsOrigin,
)

private enum class MealDetailsOrigin { Featured, Search, Saved }

@Composable
private fun PrimaryDestinationTopAppBar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(start = AppSpacing.Medium),
            )
        },
        windowInsets = WindowInsets(0),
    )
}

@Composable
private fun SharedDestination.title(): String = when (this) {
    SharedDestination.Explore -> stringResource(Res.string.explore)
    SharedDestination.Search -> stringResource(Res.string.search)
    SharedDestination.Nearby -> stringResource(Res.string.nearby)
    SharedDestination.Saved -> stringResource(Res.string.saved)
}

@Composable
private fun drawerLabel(destination: SharedDrawerDestination): String = when (destination) {
    SharedDrawerDestination.Home -> stringResource(Res.string.home)
    SharedDrawerDestination.Info -> stringResource(Res.string.info)
}

private fun SharedDrawerDestination.icon(): DrawableResource = when (this) {
    SharedDrawerDestination.Home -> Res.drawable.nav_home
    SharedDrawerDestination.Info -> Res.drawable.nav_info
}

@Composable
private fun NativeFeaturePending(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(AppSpacing.ExtraLarge),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.Small),
        ) {
            Text(
                text = stringResource(Res.string.feature_not_available_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(Res.string.feature_not_available_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
