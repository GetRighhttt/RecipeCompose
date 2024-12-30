package com.example.recipe_app_compose.features.categories.domain.model.categorymeal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryMeal(
    val strMeal: String,
    val strMealThumb: String,
    val idMeal: String
) : Parcelable