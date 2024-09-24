package com.example.recipe_app_compose.features.categories.domain.model.categorymeal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryMeal(
    val strMeal: String,
    val strMealThumb: String,
    val idMeal: String
) : Parcelable

/*
 "meals": [
    {
      "strMeal": "Baked salmon with fennel & tomatoes",
      "strMealThumb": "https://www.themealdb.com/images/media/meals/1548772327.jpg",
      "idMeal": "52959"
    },
 */
