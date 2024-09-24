package com.example.recipe_app_compose.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.recipe_app_compose.domain.model.category.Category
import com.example.recipe_app_compose.presentation.view.AccountScreen
import com.example.recipe_app_compose.presentation.view.DetailScreen
import com.example.recipe_app_compose.presentation.view.FavoritesScreen
import com.example.recipe_app_compose.presentation.view.InfoScreen
import com.example.recipe_app_compose.presentation.view.IngredientScreen
import com.example.recipe_app_compose.presentation.view.RandomMealPage
import com.example.recipe_app_compose.presentation.view.RecipeScreen
import com.example.recipe_app_compose.presentation.view.SettingsScreen
import com.example.recipe_app_compose.presentation.viewmodel.RecipeViewModel

/*
File for Navigation.
 */
@Composable
fun RecipeApp(navController: NavHostController, modifier: Modifier) {

    // necessary to know what category the user has clicked on
    val recipeViewModel: RecipeViewModel = viewModel()
    val navState by recipeViewModel.categoriesState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = CategoryScreen.RecipeScreen.route,
    ) {
        composable(
            route = CategoryScreen.RecipeScreen.route
        ) {
            RecipeScreen(
                viewState = navState,
                navigateToDetail = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("nav", it)
                    navController.navigate(
                        CategoryScreen.DetailScreen.route
                    ) {
                        launchSingleTop = true
                    }
                })
        }
        composable(
            route = CategoryScreen.DetailScreen.route
        ) {
            val category =
                navController
                    .previousBackStackEntry?.savedStateHandle?.get<Category>("nav")
                    ?: Category(
                        "",
                        "",
                        "",
                        ""
                    )
            DetailScreen(category = category)
        }
        composable(
            route = CategoryScreen.IngredientScreen.route
        ) {
            IngredientScreen(modifier = modifier)
        }
        composable(
            route = CategoryScreen.RandomMealScreen.route
        ) {
            RandomMealPage(modifier = modifier)
        }
        composable(
            route = CategoryScreen.SettingsScreen.route
        ) {
            SettingsScreen(modifier = modifier)
        }
        composable(
            route = CategoryScreen.FavoriteScreen.route
        ) {
            FavoritesScreen(modifier = modifier)
        }
        composable(
            route = CategoryScreen.AccountScreen.route
        ) {
            AccountScreen(modifier = modifier)
        }
        composable(
            route = CategoryScreen.InfoScreen.route
        ) {
            InfoScreen(modifier = modifier)
        }
    }
}