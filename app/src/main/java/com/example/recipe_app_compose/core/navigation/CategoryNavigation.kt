package com.example.recipe_app_compose.core.navigation

sealed class CategoryScreen(val route: String) {
    data object RecipeScreen : CategoryScreen("recipe_screen")
    data object DetailScreen : CategoryScreen("detail_screen")
    data object RandomMealScreen : CategoryScreen("random_meal_screen")
    data object IngredientScreen : CategoryScreen("ingredient_screen")
    data object SettingsScreen : CategoryScreen("settings_screen")
    data object FavoriteScreen : CategoryScreen("favorite_screen")
    data object AccountScreen : CategoryScreen("account_screen")
    data object YelpScreen : CategoryScreen("yelp_screen")
}