package com.example.recipe_app_compose.features.categories.domain.model.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class IngredientResponse(
    val meals: List<Ingredient>? = emptyList(),
)
