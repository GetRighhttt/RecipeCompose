package com.example.recipe_app_compose.core.util

sealed interface RecipeEvent {
    data class Success(val resultText: String) : RecipeEvent
    data class Failure(val errorText: String) : RecipeEvent
    data object Loading : RecipeEvent
    data object Empty : RecipeEvent
}