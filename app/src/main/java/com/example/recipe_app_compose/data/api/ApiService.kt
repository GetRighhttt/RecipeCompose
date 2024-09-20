package com.example.recipe_app_compose.data.api

import com.example.recipe_app_compose.core.util.Constants.CATEGORY_ENDPOINT
import com.example.recipe_app_compose.core.util.Constants.SEAFOOD_MEAL_ENDPOINT
import com.example.recipe_app_compose.domain.model.CategoryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(CATEGORY_ENDPOINT)
    suspend fun getCategories(): CategoryResponse

    // ?c=Seafood
    @GET(SEAFOOD_MEAL_ENDPOINT)
    suspend fun getRandomMeal(
        @Query("c") c: String = "Seafood"
    ): CategoryResponse
}