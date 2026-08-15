package com.example.recipe_app_compose.features.location.domain.states

import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpBusinesses

data class YelpStates(
    val loading: Boolean = false,
    val list: List<YelpBusinesses> = emptyList(),
    val error: String? = null,
    val searchArea: YelpSearchArea = YelpSearchArea.Initializing,
)

sealed interface YelpSearchArea {
    data object Initializing : YelpSearchArea

    data object PermissionRequired : YelpSearchArea

    data object ResolvingCurrentLocation : YelpSearchArea

    data object CurrentLocation : YelpSearchArea

    data object LocationUnavailable : YelpSearchArea

    data class NamedLocation(val value: String) : YelpSearchArea
}
