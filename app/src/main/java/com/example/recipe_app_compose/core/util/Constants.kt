package com.example.recipe_app_compose.core.util

import com.example.recipe_app_compose.BuildConfig

object Constants {
    val BASE_URL = BuildConfig.BASE_URL
    const val CATEGORY_ENDPOINT = "categories.php"
    const val CATEGORY_MEAL_ENDPOINT = "filter.php"
    const val RANDOM_MEAL_ENDPOINT = "random.php"
    const val INGREDIENT_ENDPOINT = "search.php"


    val YELP_API_KEY = BuildConfig.YELP_API_KEY
    val YELP_BASE_URL = BuildConfig.YELP_BASE_URL
    const val YELP_SEARCH_QUERY = "Steak in Wesley Chapel"
}