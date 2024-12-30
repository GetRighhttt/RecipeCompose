package com.example.recipe_app_compose.features.categories.domain.model.randommeal

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "random_meal_table")
data class RandomMeal(
    @PrimaryKey(autoGenerate = true)
    val id: Int, // created

    @ColumnInfo("meal_id")
    val idMeal: String?,
    @ColumnInfo("name")
    val strMeal: String?,
    @ColumnInfo("category")
    val strCategory: String?,
    @ColumnInfo("origin")
    val strArea: String?,
    @ColumnInfo("instructions")
    val strInstructions: String?,
    @ColumnInfo("thumbnail")
    val strMealThumb: String?,
    @ColumnInfo("youtube_url")
    val strYoutube: String?,
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    @ColumnInfo("source")
    val strSource: String?
) : Parcelable