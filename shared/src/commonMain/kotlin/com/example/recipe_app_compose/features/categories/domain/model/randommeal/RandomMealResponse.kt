package com.example.recipe_app_compose.features.categories.domain.model.randommeal

import kotlinx.serialization.Serializable

@Serializable
data class RandomMealResponse(
    val meals: List<RandomMeal>? = emptyList(),
)
