package com.example.recipe_app_compose.features.categories.domain.model.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Category(
    val idCategory: @RawValue CategoryId,
    val strCategory: @RawValue CategoryName,
    val strCategoryThumb: @RawValue CategoryThumb,
    val strCategoryDescription: @RawValue CategoryDescription
) : Parcelable

@JvmInline
value class CategoryId(val value: String)

@JvmInline
value class CategoryName(val value: String)

@JvmInline
value class CategoryThumb(val value: String)

@JvmInline
value class CategoryDescription(val value: String)
