package com.example.recipe_app_compose.features.categories.data.datasources.remote.api

import com.example.recipe_app_compose.core.util.Constants.CATEGORY_ENDPOINT
import com.example.recipe_app_compose.core.util.Constants.INGREDIENT_ENDPOINT
import com.example.recipe_app_compose.core.util.Constants.RANDOM_MEAL_ENDPOINT
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryResponse
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.IngredientResponse
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMealResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(CATEGORY_ENDPOINT)
    suspend fun getCategories(): Response<CategoryResponse>

    @GET(RANDOM_MEAL_ENDPOINT)
    suspend fun getRandomMeal(): Response<RandomMealResponse>

    // ?i=chicken_breast
    @GET(INGREDIENT_ENDPOINT)
    suspend fun getIngredient(
        @Query("s") s: String
    ): Response<IngredientResponse>
}
