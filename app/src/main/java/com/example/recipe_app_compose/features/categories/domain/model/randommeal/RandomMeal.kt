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

/*
{
  "meals": [
    {
      "idMeal": "53032",
      "strMeal": "Tonkatsu pork",
      "strDrinkAlternate": null,
      "strCategory": "Pork",
      "strArea": "Japanese",
      "strInstructions": "STEP 1\r\nRemove the large piece of fat on the edge of each pork loin, then bash each of the loins between two pieces of baking parchment until around 1cm in thickness – you can do this using a meat tenderiser or a rolling pin. Once bashed, use your hands to reshape the meat to its original shape and thickness – this step will ensure the meat is as succulent as possible.\r\n\r\nSTEP 2\r\nPut the flour, eggs and panko breadcrumbs into three separate wide-rimmed bowls. Season the meat, then dip first in the flour, followed by the eggs, then the breadcrumbs.\r\n\r\nSTEP 3\r\nIn a large frying or sauté pan, add enough oil to come 2cm up the side of the pan. Heat the oil to 180C – if you don’t have a thermometer, drop a bit of panko into the oil and if it sinks a little then starts to fry, the oil is ready. Add two pork chops and cook for 1 min 30 secs on each side, then remove and leave to rest on a wire rack for 5 mins. Repeat with the remaining pork chops.\r\n\r\nSTEP 4\r\nWhile the pork is resting, make the sauce by whisking the ingredients together, adding a splash of water if it’s particularly thick. Slice the tonkatsu and serve drizzled with the sauce.",
      "strMealThumb": "https://www.themealdb.com/images/media/meals/lwsnkl1604181187.jpg",
      "strTags": null,
      "strYoutube": "https://www.youtube.com/watch?v=aASr5x0d3Ys",
      "strIngredient1": "Pork Chops",
      "strIngredient2": "Flour",
      "strIngredient3": "Eggs",
      "strIngredient4": "Breadcrumbs",
      "strIngredient5": "Vegetable Oil",
      "strIngredient6": "Tomato Ketchup",
      "strIngredient7": "Worcestershire Sauce",
      "strIngredient8": "Oyster Sauce",
      "strIngredient9": "Caster Sugar",
      "strSource": "https://www.bbcgoodfood.com/recipes/tonkatsu-pork",
    }
  ]
}
 */
