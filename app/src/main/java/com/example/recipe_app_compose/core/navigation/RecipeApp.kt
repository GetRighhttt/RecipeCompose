package com.example.recipe_app_compose.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recipe_app_compose.features.categories.presentation.view.DetailScreen
import com.example.recipe_app_compose.features.categories.presentation.view.FavoritesScreen
import com.example.recipe_app_compose.features.categories.presentation.view.InfoScreen
import com.example.recipe_app_compose.features.categories.presentation.view.IngredientDetailScreen
import com.example.recipe_app_compose.features.categories.presentation.view.IngredientScreen
import com.example.recipe_app_compose.features.categories.presentation.view.RandomMealPage
import com.example.recipe_app_compose.features.categories.presentation.view.RecipeScreen
import com.example.recipe_app_compose.features.categories.presentation.view.SavedMealDetailScreen
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.presentation.view.GoogleLocationSelectionScreen
import com.example.recipe_app_compose.features.location.presentation.view.YelpScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

/*
File for Navigation.
 */
@Composable
fun RecipeApp(navController: NavHostController, modifier: Modifier) {
    val recipeViewModel: RecipeViewModel = koinViewModel()
    val selection: RecipeNavigationSelection = koinInject()
    val navState by recipeViewModel.uiState.collectAsStateWithLifecycle()
    val featuredMealState by recipeViewModel.randUiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = CategoryScreen.RecipeScreen.route,
        modifier = modifier,
    ) {
        composable(
            route = CategoryScreen.RecipeScreen.route
        ) {
            RecipeScreen(
                uiState = navState,
                featuredMealState = featuredMealState,
                navigateToDetail = {
                    selection.category = it
                    navController.navigate(
                        CategoryScreen.DetailScreen.route
                    ) {
                        launchSingleTop = true
                    }
                },
                onSearch = {
                    navController.navigateToPrimaryDestination(
                        CategoryScreen.IngredientScreen.route
                    )
                },
                onNearbyShops = {
                    navController.navigateToPrimaryDestination(CategoryScreen.YelpScreen.route)
                },
                onFavorites = {
                    navController.navigateToPrimaryDestination(
                        CategoryScreen.FavoriteScreen.route
                    )
                },
                onFeaturedDish = {
                    navController.navigate(CategoryScreen.RandomMealScreen.route) {
                        launchSingleTop = true
                    }
                },
                onRetry = recipeViewModel::fetchCategories,
            )
        }
        composable(
            route = CategoryScreen.DetailScreen.route
        ) {
            selection.category?.let { category -> DetailScreen(category = category) }
        }
        composable(
            route = CategoryScreen.IngredientScreen.route
        ) {
            IngredientScreen(
                modifier = Modifier,
                onIngredientSelected = { ingredient ->
                    selection.ingredient = ingredient
                    navController.navigate(CategoryScreen.IngredientDetailScreen.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            route = CategoryScreen.IngredientDetailScreen.route
        ) {
            val ingredient = selection.ingredient

            if (ingredient != null) {
                IngredientDetailScreen(
                    ingredient = ingredient,
                    modifier = Modifier,
                )
            }
        }
        composable(
            route = CategoryScreen.RandomMealScreen.route
        ) {
            RandomMealPage(modifier = Modifier)
        }
        composable(
            route = CategoryScreen.FavoriteScreen.route
        ) {
            FavoritesScreen(
                onMealSelected = { meal ->
                    selection.savedMeal = meal
                    navController.navigate(CategoryScreen.FavoriteDetailScreen.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier,
            )
        }
        composable(
            route = CategoryScreen.FavoriteDetailScreen.route
        ) {
            val meal = selection.savedMeal

            if (meal != null) {
                SavedMealDetailScreen(
                    meal = meal,
                    onDeleted = navController::popBackStack,
                    modifier = Modifier,
                )
            }
        }
        composable(
            route = CategoryScreen.InfoScreen.route
        ) {
            InfoScreen(modifier = Modifier)
        }
        composable(
            route = CategoryScreen.YelpScreen.route
        ) {
            YelpScreen(
                modifier = Modifier,
                onShopSelected = { location ->
                    navController.navigate(
                        CategoryScreen.MapScreen.createRoute(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            route = CategoryScreen.MapScreen.route,
            arguments = listOf(
                navArgument(CategoryScreen.MapScreen.LATITUDE_ARGUMENT) {
                    type = NavType.StringType
                },
                navArgument(CategoryScreen.MapScreen.LONGITUDE_ARGUMENT) {
                    type = NavType.StringType
                },
            ),
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments
                ?.getString(CategoryScreen.MapScreen.LATITUDE_ARGUMENT)
                ?.toDoubleOrNull()
            val longitude = backStackEntry.arguments
                ?.getString(CategoryScreen.MapScreen.LONGITUDE_ARGUMENT)
                ?.toDoubleOrNull()

            if (latitude != null && longitude != null) {
                GoogleLocationSelectionScreen(
                    location = LocationData(latitude, longitude)
                )
            }
        }
    }
}
