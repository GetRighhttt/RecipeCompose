package com.example.recipe_app_compose.features.categories.domain.model.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val categories: List<Category> = emptyList(),
)
