package com.example.recipe_app_compose.features.categories.data.remote.repoimpl

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.data.remote.retrofit.RetrofitInstance.apiService
import com.example.recipe_app_compose.features.categories.data.util.safeApiCall
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryResponse
import com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMealResponse
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.IngredientResponse
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMealResponse
import com.example.recipe_app_compose.features.categories.domain.repository.RecipeRepository

class RecipeRepositoryImpl : RecipeRepository {
    override suspend fun getCategories(): Resource<CategoryResponse> =
        safeApiCall(
            call = { apiService.getCategories() },
            defaultError = "Unable to retrieve categories."
        )

    override suspend fun getCategoriesMeal(): Resource<CategoryMealResponse> =
        safeApiCall(
            call = { apiService.getCategoriesMeal() },
            defaultError = "Unable to retrieve meal categories."
        )

    override suspend fun getRandomMeal(): Resource<RandomMealResponse> =
        safeApiCall(
            call = { apiService.getRandomMeal() },
            defaultError = "Unable to retrieve random meal."
        )

    override suspend fun getIngredient(ingredient: String): Resource<IngredientResponse> =
        safeApiCall(
            call = { apiService.getIngredient(ingredient) },
            defaultError = "Unable to retrieve ingredient."
        )
}