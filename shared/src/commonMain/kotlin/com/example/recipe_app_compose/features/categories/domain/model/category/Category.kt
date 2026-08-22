package com.example.recipe_app_compose.features.categories.domain.model.category

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** A MealDB category, kept platform-neutral so it can be rendered on every host. */
@Serializable
data class Category(
    val idCategory: CategoryId,
    val strCategory: CategoryName,
    val strCategoryThumb: CategoryThumb,
    val strCategoryDescription: CategoryDescription,
)

@Serializable
@JvmInline
value class CategoryId(val value: String)

@Serializable
@JvmInline
value class CategoryName(val value: String)

@Serializable
@JvmInline
value class CategoryThumb(val value: String)

@Serializable
@JvmInline
value class CategoryDescription(val value: String)
