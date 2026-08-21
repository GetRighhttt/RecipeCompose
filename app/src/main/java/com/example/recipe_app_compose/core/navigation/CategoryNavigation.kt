package com.example.recipe_app_compose.core.navigation

sealed class CategoryScreen(val route: String) {
    data object RecipeScreen : CategoryScreen("recipe_screen")
    data object DetailScreen : CategoryScreen("detail_screen")
    data object RandomMealScreen : CategoryScreen("random_meal_screen")
    data object IngredientScreen : CategoryScreen("ingredient_screen")
    data object IngredientDetailScreen : CategoryScreen("ingredient_detail_screen")
    data object AccountScreen : CategoryScreen("account_screen")
    data object FavoriteScreen : CategoryScreen("favorite_screen")
    data object InfoScreen : CategoryScreen("info_screen")
    data object YelpScreen : CategoryScreen("yelp_screen")
    data object MapScreen : CategoryScreen(
        "map_screen?latitude={latitude}&longitude={longitude}"
    ) {
        const val LATITUDE_ARGUMENT = "latitude"
        const val LONGITUDE_ARGUMENT = "longitude"

        fun createRoute(latitude: Double, longitude: Double): String =
            "map_screen?latitude=$latitude&longitude=$longitude"
    }
}
