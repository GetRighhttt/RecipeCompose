package com.example.recipe_app_compose.features.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.recipe_app_compose.features.location.domain.model.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel() : ViewModel() {
    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location

    internal val updateLocation: (LocationData) -> Unit = { newLocation ->
        _location.value = newLocation
    }
}