package com.example.recipe_app_compose.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun CategoryNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = CategoryScreens.HomeScreen.name
    ) {
        composable(route = CategoryScreens.HomeScreen.name) {  }
        composable(route = CategoryScreens.DetailsScreen.name) { }
    }
}

enum class CategoryScreens {
    HomeScreen,
    DetailsScreen;

    companion object {
        fun fromRoute(route: String?): CategoryScreens =
            when (route?.substringBefore("/")) {
                HomeScreen.name -> HomeScreen
                DetailsScreen.name -> DetailsScreen
                null -> HomeScreen
                else -> throw IllegalArgumentException("Route $route is not valid.")
            }
    }
}