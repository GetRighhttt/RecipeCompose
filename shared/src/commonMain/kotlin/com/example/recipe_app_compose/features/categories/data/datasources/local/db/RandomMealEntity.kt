package com.example.recipe_app_compose.features.categories.data.datasources.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal

/** Shared Room representation of a saved recipe. */
@Entity(tableName = "random_meal_table")
data class RandomMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("meal_id") val idMeal: String?,
    @ColumnInfo("name") val strMeal: String?,
    @ColumnInfo("category") val strCategory: String?,
    @ColumnInfo("origin") val strArea: String?,
    @ColumnInfo("instructions") val strInstructions: String?,
    @ColumnInfo("thumbnail") val strMealThumb: String?,
    @ColumnInfo("youtube_url") val strYoutube: String?,
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    @ColumnInfo("source") val strSource: String?,
)

internal fun RandomMeal.toEntity() = RandomMealEntity(
    id = id,
    idMeal = idMeal,
    strMeal = strMeal,
    strCategory = strCategory,
    strArea = strArea,
    strInstructions = strInstructions,
    strMealThumb = strMealThumb,
    strYoutube = strYoutube,
    strIngredient1 = strIngredient1,
    strIngredient2 = strIngredient2,
    strIngredient3 = strIngredient3,
    strIngredient4 = strIngredient4,
    strIngredient5 = strIngredient5,
    strIngredient6 = strIngredient6,
    strIngredient7 = strIngredient7,
    strIngredient8 = strIngredient8,
    strIngredient9 = strIngredient9,
    strSource = strSource,
)

internal fun RandomMealEntity.toDomain() = RandomMeal(
    id = id,
    idMeal = idMeal,
    strMeal = strMeal,
    strCategory = strCategory,
    strArea = strArea,
    strInstructions = strInstructions,
    strMealThumb = strMealThumb,
    strYoutube = strYoutube,
    strIngredient1 = strIngredient1,
    strIngredient2 = strIngredient2,
    strIngredient3 = strIngredient3,
    strIngredient4 = strIngredient4,
    strIngredient5 = strIngredient5,
    strIngredient6 = strIngredient6,
    strIngredient7 = strIngredient7,
    strIngredient8 = strIngredient8,
    strIngredient9 = strIngredient9,
    strSource = strSource,
)
