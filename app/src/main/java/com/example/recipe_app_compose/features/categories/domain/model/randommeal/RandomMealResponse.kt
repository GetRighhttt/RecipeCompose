package com.example.recipe_app_compose.features.categories.domain.model.randommeal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RandomMealResponse(
    val meals: List<com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal>
) : Parcelable
