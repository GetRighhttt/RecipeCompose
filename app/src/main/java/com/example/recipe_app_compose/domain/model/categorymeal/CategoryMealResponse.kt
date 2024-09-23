package com.example.recipe_app_compose.domain.model.categorymeal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryMealResponse(
    val meals: List<CategoryMeal>
) : Parcelable
