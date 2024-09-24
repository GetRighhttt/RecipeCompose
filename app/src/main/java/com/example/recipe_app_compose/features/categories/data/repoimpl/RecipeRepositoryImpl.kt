package com.example.recipe_app_compose.features.categories.data.repoimpl

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.data.api.RetrofitInstance.apiService
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMealResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepositoryImpl :
    com.example.recipe_app_compose.features.categories.domain.repository.RecipeRepository {

    override suspend fun getCategories(): Resource<com.example.recipe_app_compose.features.categories.domain.model.category.CategoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategories()
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to retrieve Categories.")
            }
        }
    }

    override suspend fun getCategoriesMeal(): Resource<com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMealResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategoriesMeal()
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to retrieve Category Meals.")
            }
        }
    }

    override suspend fun getRandomMeal(): Resource<RandomMealResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRandomMeal()
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to retrieve Random Meal.")
            }
        }
    }

    override suspend fun getIngredient(ingredient: String): Resource<com.example.recipe_app_compose.features.categories.domain.model.ingredient.IngredientResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getIngredient(ingredient)
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e:Exception) {
                Resource.Error(e.message ?: "Unable to retrieve Ingredient.")
            }
        }
    }
}