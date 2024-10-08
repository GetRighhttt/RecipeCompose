package com.example.recipe_app_compose.features.categories.domain.model.ingredient

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientResponse(
    val meals: List<Ingredient>
) : Parcelable
