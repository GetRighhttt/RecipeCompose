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
import com.example.recipe_app_compose.presentation.view.DetailScreen
import com.example.recipe_app_compose.presentation.view.RecipeScreen
import com.example.recipe_app_compose.presentation.viewmodel.RecipeViewModel

/*
File for Navigation.
 */
@Composable
fun RecipeApp(navController: NavHostController, modifier: Modifier) {

    // necessary to know what category the user has clicked on
    val recipeViewModel: RecipeViewModel = viewModel()
    val navState by recipeViewModel.categoriesState.collectAsState()

    NavHost(navController = navController, startDestination = CategoryScreen.RecipeScreen.route) {
        composable(route = CategoryScreen.RecipeScreen.route) {
            RecipeScreen(viewState = navState, navigateToDetail = {
                navController.currentBackStackEntry?.savedStateHandle?.set("nav", it)
                navController.navigate(CategoryScreen.DetailScreen.route)
            })
        }
        composable(route = CategoryScreen.DetailScreen.route) {
            val category =
                navController.previousBackStackEntry?.savedStateHandle?.get<Category>("nav")
                    ?: Category("", "", "", "")
            DetailScreen(category = category)
        }
    }
}