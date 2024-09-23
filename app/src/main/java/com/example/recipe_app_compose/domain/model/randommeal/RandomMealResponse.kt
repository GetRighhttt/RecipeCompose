package com.example.recipe_app_compose.domain.model.randommeal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RandomMealResponse(
    val meals: List<RandomMeal>
) : Parcelable
