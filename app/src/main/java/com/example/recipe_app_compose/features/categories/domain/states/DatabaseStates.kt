package com.example.recipe_app_compose.features.categories.domain.states

import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal

data class DatabaseState(
    val loading: Boolean = true,
    val list: List<RandomMeal>? = emptyList(),
    val error: String? = null
)