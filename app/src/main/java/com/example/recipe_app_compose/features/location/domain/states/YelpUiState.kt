package com.example.recipe_app_compose.features.location.domain.states

import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpShop

data class YelpUiState(
    val loading: Boolean = false,
    val list: List<YelpShop> = emptyList(),
    val error: String? = null,
    val searchArea: YelpSearchArea = YelpSearchArea.LocationChoiceRequired,
)

sealed interface YelpSearchArea {
    data object LocationChoiceRequired : YelpSearchArea
    data object PermissionRequired : YelpSearchArea
    data object ResolvingCurrentLocation : YelpSearchArea
    data object CurrentLocation : YelpSearchArea
    data object LocationUnavailable : YelpSearchArea
    data class NamedLocation(val value: String) : YelpSearchArea
}
