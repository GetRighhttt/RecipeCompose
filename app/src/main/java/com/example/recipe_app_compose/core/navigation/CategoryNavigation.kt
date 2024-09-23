package com.example.recipe_app_compose.core.navigation

sealed class CategoryScreen(val route: String) {
    data object RecipeScreen: CategoryScreen("recipe_screen")
    data object DetailScreen: CategoryScreen("detail_screen")
}