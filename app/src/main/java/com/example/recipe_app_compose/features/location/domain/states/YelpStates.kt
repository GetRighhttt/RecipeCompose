package com.example.recipe_app_compose.features.location.domain.states

import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpBusinesses

data class YelpStates(
    val loading: Boolean = true,
    val list: List<YelpBusinesses>? = emptyList(),
    val error: String? = null
)