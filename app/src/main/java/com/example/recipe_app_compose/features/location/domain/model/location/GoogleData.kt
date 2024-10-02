package com.example.recipe_app_compose.features.location.domain.model.location

data class GeoCodingResponse(
    val results: List<GeoCodingResult>,
    val status: String
)

data class GeoCodingResult(
    val formatted_address: String
)