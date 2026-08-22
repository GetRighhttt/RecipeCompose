package com.example.recipe_app_compose.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryDescription
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryId
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryName
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryThumb
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.presentation.view.DetailScreen
import com.example.recipe_app_compose.features.categories.presentation.view.FavoritesScreen
import com.example.recipe_app_compose.features.categories.presentation.view.InfoScreen
import com.example.recipe_app_compose.features.categories.presentation.view.IngredientDetailScreen
import com.example.recipe_app_compose.features.categories.presentation.view.IngredientScreen
import com.example.recipe_app_compose.features.categories.presentation.view.RandomMealPage
import com.example.recipe_app_compose.features.categories.presentation.view.RecipeScreen
import com.example.recipe_app_compose.features.categories.presentation.view.SavedMealDetailScreen
import com.example.recipe_app_compose.features.categories.presentation.view.AccountScreen
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.presentation.view.GoogleLocationSelectionScreen
import com.example.recipe_app_compose.features.location.presentation.view.YelpScreen

/*
File for Navigation.
 */
@Composable
fun RecipeApp(navController: NavHostController, modifier: Modifier) {
    val recipeViewModel: RecipeViewModel = viewModel()
    val navState by recipeViewModel.uiState.collectAsState()
    val featuredMealState by recipeViewModel.randUiState.collectAsState()

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
                    navController.currentBackStackEntry?.savedStateHandle?.set("nav", it)
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
            val category =
                navController
                    .previousBackStackEntry?.savedStateHandle?.get<Category>("nav")
                    ?: Category(
                        idCategory = CategoryId(""),
                        strCategory = CategoryName(""),
                        strCategoryThumb = CategoryThumb(""),
                        strCategoryDescription = CategoryDescription("")
                    )
            DetailScreen(category = category)
        }
        composable(
            route = CategoryScreen.IngredientScreen.route
        ) {
            IngredientScreen(
                modifier = Modifier,
                onIngredientSelected = { ingredient ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(INGREDIENT_KEY, ingredient)
                    navController.navigate(CategoryScreen.IngredientDetailScreen.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            route = CategoryScreen.IngredientDetailScreen.route
        ) {
            val ingredient = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Ingredient>(INGREDIENT_KEY)

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
            route = CategoryScreen.AccountScreen.route
        ) {
            AccountScreen(modifier = Modifier)
        }
        composable(
            route = CategoryScreen.FavoriteScreen.route
        ) {
            FavoritesScreen(
                onMealSelected = { meal ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(SAVED_MEAL_KEY, meal)
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
            val meal = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<RandomMeal>(SAVED_MEAL_KEY)

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

private const val INGREDIENT_KEY = "ingredient"
private const val SAVED_MEAL_KEY = "saved_meal"
