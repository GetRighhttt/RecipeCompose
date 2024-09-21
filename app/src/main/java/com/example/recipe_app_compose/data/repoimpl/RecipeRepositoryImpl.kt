package com.example.recipe_app_compose.data.repoimpl

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.data.api.RetrofitInstance.apiService
import com.example.recipe_app_compose.domain.model.CategoryResponse
import com.example.recipe_app_compose.domain.model.SeafoodCategoryResponse
import com.example.recipe_app_compose.domain.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepositoryImpl() : RecipeRepository {

    override suspend fun getCategories(): Resource<CategoryResponse> {
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
                Resource.Error(e.message ?: "Unable to retrieve categories.")
            }
        }
    }

    override suspend fun getSeafoodCategories(): Resource<SeafoodCategoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSeafoodMeal()
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to retrieve seafood.")
            }
        }
    }
}