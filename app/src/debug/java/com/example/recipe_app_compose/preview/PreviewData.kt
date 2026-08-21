package com.example.recipe_app_compose.preview

import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryDescription
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryId
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryName
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryThumb
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpCategories
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpCoordinates
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpLocations
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpShop

private const val PREVIEW_IMAGE_URL =
    "android.resource://com.example.recipe_app_compose/drawable/dining_two"

internal val previewCategories = listOf(
    Category(
        idCategory = CategoryId("1"),
        strCategory = CategoryName("Mediterranean"),
        strCategoryThumb = CategoryThumb(PREVIEW_IMAGE_URL),
        strCategoryDescription = CategoryDescription(
            "Mediterranean cooking highlights vegetables, grains, seafood, olive oil, " +
                "and bright herbs in approachable dishes made for sharing."
        ),
    ),
    Category(
        idCategory = CategoryId("2"),
        strCategory = CategoryName("Vegetarian"),
        strCategoryThumb = CategoryThumb(PREVIEW_IMAGE_URL),
        strCategoryDescription = CategoryDescription(
            "Vegetarian recipes build satisfying meals around seasonal produce, legumes, " +
                "whole grains, and deeply flavored sauces."
        ),
    ),
    Category(
        idCategory = CategoryId("3"),
        strCategory = CategoryName("Seafood"),
        strCategoryThumb = CategoryThumb(PREVIEW_IMAGE_URL),
        strCategoryDescription = CategoryDescription(
            "Seafood dishes range from quick weeknight preparations to slow-cooked stews."
        ),
    ),
)

internal val previewMeals = listOf(
    RandomMeal(
        id = 1,
        idMeal = "preview-1",
        strMeal = "Roasted Tomato and Herb Pasta",
        strCategory = "Pasta",
        strArea = "Italian",
        strInstructions = "Roast the tomatoes until caramelized. Cook the pasta until al dente, " +
            "then toss it with the tomatoes, herbs, olive oil, and a little pasta water.",
        strMealThumb = PREVIEW_IMAGE_URL,
        strYoutube = "https://www.youtube.com/",
        strIngredient1 = "Cherry tomatoes",
        strIngredient2 = "Rigatoni",
        strIngredient3 = "Fresh basil",
        strIngredient4 = "Garlic",
        strIngredient5 = "Olive oil",
        strIngredient6 = "Parmesan",
        strIngredient7 = null,
        strIngredient8 = null,
        strIngredient9 = null,
        strSource = "https://www.themealdb.com/",
    ),
    RandomMeal(
        id = 2,
        idMeal = "preview-2",
        strMeal = "Crispy Lemon Herb Salmon",
        strCategory = "Seafood",
        strArea = null,
        strInstructions = "Season the salmon, sear until crisp, and finish with lemon and herbs.",
        strMealThumb = PREVIEW_IMAGE_URL,
        strYoutube = null,
        strIngredient1 = "Salmon",
        strIngredient2 = "Lemon",
        strIngredient3 = "Dill",
        strIngredient4 = "Garlic",
        strIngredient5 = null,
        strIngredient6 = null,
        strIngredient7 = null,
        strIngredient8 = null,
        strIngredient9 = null,
        strSource = null,
    ),
)

internal val previewIngredients = listOf(
    Ingredient(
        idMeal = "preview-search-1",
        strMeal = "Chicken and Mushroom Hotpot",
        strCategory = "Chicken",
        strArea = "British",
        strInstructions = "Brown the chicken and mushrooms, add the stock, and bake under " +
            "thinly sliced potatoes until golden.",
        strMealThumb = PREVIEW_IMAGE_URL,
        strYoutube = "https://www.youtube.com/",
        strIngredient1 = "Chicken thighs",
        strIngredient2 = "Mushrooms",
        strIngredient3 = "Potatoes",
        strIngredient4 = "Chicken stock",
        strIngredient5 = "Thyme",
        strIngredient6 = null,
        strIngredient7 = null,
        strIngredient8 = null,
        strIngredient9 = null,
        strSource = "https://www.themealdb.com/",
    ),
    Ingredient(
        idMeal = "preview-search-2",
        strMeal = "Chicken Couscous",
        strCategory = "Chicken",
        strArea = "Moroccan",
        strInstructions = "Steam the couscous and serve with spiced chicken and vegetables.",
        strMealThumb = PREVIEW_IMAGE_URL,
        strYoutube = null,
        strIngredient1 = "Chicken breast",
        strIngredient2 = "Couscous",
        strIngredient3 = "Carrots",
        strIngredient4 = "Cumin",
        strIngredient5 = null,
        strIngredient6 = null,
        strIngredient7 = null,
        strIngredient8 = null,
        strIngredient9 = null,
        strSource = null,
    ),
)

internal val previewShops = listOf(
    YelpShop(
        rating = 4.8,
        phone = "+13125550100",
        id = "preview-shop-1",
        alias = "harvest-table",
        isClosed = false,
        categories = listOf(YelpCategories(alias = "newamerican", title = "New American")),
        reviewCount = 428U,
        name = "Harvest Table Kitchen",
        url = "https://www.yelp.com/",
        coordinates = YelpCoordinates(latitude = 41.8819, longitude = -87.6278),
        imageUrl = PREVIEW_IMAGE_URL,
        location = YelpLocations(
            city = "Chicago",
            country = "US",
            address2 = "",
            address3 = "",
            state = "IL",
            address1 = "100 W Randolph St",
            zipCode = "60601",
        ),
        distance = 640.0,
    ),
    YelpShop(
        rating = 4.5,
        phone = "+13125550101",
        id = "preview-shop-2",
        alias = "saffron-and-stone",
        isClosed = false,
        categories = listOf(YelpCategories(alias = "mediterranean", title = "Mediterranean")),
        reviewCount = 216U,
        name = "Saffron & Stone",
        url = "https://www.yelp.com/",
        coordinates = YelpCoordinates(latitude = 41.8832, longitude = -87.6324),
        imageUrl = PREVIEW_IMAGE_URL,
        location = YelpLocations(
            city = "Chicago",
            country = "US",
            address2 = "",
            address3 = "",
            state = "IL",
            address1 = "225 N LaSalle St",
            zipCode = "60601",
        ),
        distance = 920.0,
    ),
)
