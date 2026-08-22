package com.example.recipe_app_compose.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.core.components.NetworkUnavailableScreen
import com.example.recipe_app_compose.features.categories.domain.states.UiState
import com.example.recipe_app_compose.features.categories.domain.states.RandomMealUiState
import com.example.recipe_app_compose.features.categories.presentation.view.DetailScreen
import com.example.recipe_app_compose.features.categories.presentation.view.InfoScreen
import com.example.recipe_app_compose.features.categories.presentation.view.IngredientDetailContent
import com.example.recipe_app_compose.features.categories.presentation.view.IngredientSearchContent
import com.example.recipe_app_compose.features.categories.presentation.view.MealDBScreen
import com.example.recipe_app_compose.features.categories.presentation.view.RandomCategoryScreen
import com.example.recipe_app_compose.features.categories.presentation.view.RecipeScreen
import com.example.recipe_app_compose.features.categories.presentation.view.SavedMealDetailContent
import com.example.recipe_app_compose.features.onboarding.presentation.OnboardingScreen
import com.example.recipe_app_compose.ui.theme.AppTheme

@Preview(
    name = "Light",
    group = "Screens",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Preview(
    name = "Dark",
    group = "Screens",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
private annotation class AppScreenPreview

@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content,
        )
    }
}

@AppScreenPreview
@Composable
private fun OnboardingPreview() = PreviewSurface {
    OnboardingScreen(onFinished = {})
}

@AppScreenPreview
@Composable
private fun BrowseCuisinesPreview() = PreviewSurface {
    RecipeScreen(
        uiState = UiState(loading = false, list = previewCategories),
        featuredMealState = RandomMealUiState(
            loading = false,
            item = listOf(previewMeals.first()),
        ),
        navigateToDetail = {},
        onSearch = {},
        onNearbyShops = {},
        onFavorites = {},
        onFeaturedDish = {},
        onRetry = {},
    )
}

@AppScreenPreview
@Composable
private fun CuisineDetailsPreview() = PreviewSurface {
    DetailScreen(category = previewCategories.first())
}

@AppScreenPreview
@Composable
private fun DishSearchPreview() = PreviewSurface {
    IngredientSearchContent(
        searchText = "chicken",
        isSearching = false,
        searchResults = previewIngredients,
        onSearchTextChange = {},
        onIngredientSelected = {},
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

@AppScreenPreview
@Composable
private fun RecipeDetailsPreview() = PreviewSurface {
    IngredientDetailContent(
        ingredient = previewIngredients.first(),
        isFavorite = false,
        onFavorite = {},
    )
}

@AppScreenPreview
@Composable
private fun FeaturedDishPreview() = PreviewSurface {
    RandomCategoryScreen(
        categories = listOf(previewMeals.first()),
        isFavorite = false,
        onFavorite = {},
        onRefresh = {},
    )
}

@AppScreenPreview
@Composable
private fun FavoritesPreview() = PreviewSurface {
    MealDBScreen(
        meals = previewMeals,
        onDeleteAll = {},
        onDeleteMeal = {},
        onMealSelected = {},
    )
}

@AppScreenPreview
@Composable
private fun SavedRecipeDetailsPreview() = PreviewSurface {
    SavedMealDetailContent(
        meal = previewMeals.first(),
        onRemove = {},
    )
}

@AppScreenPreview
@Composable
private fun InfoPreview() = PreviewSurface {
    InfoScreen(modifier = Modifier)
}

@AppScreenPreview
@Composable
private fun OfflinePreview() = PreviewSurface {
    NetworkUnavailableScreen(
        onRetry = {},
        onOpenNetworkSettings = {},
    )
}
