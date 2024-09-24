package com.example.recipe_app_compose.features.categories.domain.model.categorymeal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryMealResponse(
    val meals: List<com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMeal>
) : Parcelable
