package com.example.recipe_app_compose.features.categories.domain.model.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryResponse(
    val categories: List<com.example.recipe_app_compose.features.categories.domain.model.category.Category>
) : Parcelable
