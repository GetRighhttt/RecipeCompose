package com.example.recipe_app_compose.data.api

import com.example.recipe_app_compose.core.util.Constants.CATEGORY_ENDPOINT
import com.example.recipe_app_compose.core.util.Constants.RANDOM_MEAL_ENDPOINT
import com.example.recipe_app_compose.core.util.Constants.CATEGORY_MEAL_ENDPOINT
import com.example.recipe_app_compose.domain.model.CategoryResponse
import com.example.recipe_app_compose.domain.model.RandomMealResponse
import com.example.recipe_app_compose.domain.model.CategoryMealResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(CATEGORY_ENDPOINT)
    suspend fun getCategories(): Response<CategoryResponse>

    // ?c=Seafood
    @GET(CATEGORY_MEAL_ENDPOINT)
    suspend fun getCategoriesMeal(
        @Query("c") c: String = getRandomList()
    ): Response<CategoryMealResponse>

    @GET(RANDOM_MEAL_ENDPOINT)
    suspend fun getRandomMeal(): Response<RandomMealResponse>
}

fun getRandomList(): String {
    val listOfFoods = listOf(
        "Beef",
        "Breakfast",
        "Chicken",
        "Dessert",
        "Goat",
        "Lamb",
        "Miscellaneous",
        "Pasta",
        "Pork",
        "Seafood",
        "Side",
        "Starter",
        "Vegan",
        "Vegetarian"
    )
    return listOfFoods.random().toString()
}