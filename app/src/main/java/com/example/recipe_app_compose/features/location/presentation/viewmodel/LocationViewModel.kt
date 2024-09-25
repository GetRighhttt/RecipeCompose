package com.example.recipe_app_compose.features.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationViewModel() : ViewModel() {
    private val _location = MutableStateFlow<LocationData?>(null)
    val location = _location.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    operator fun MutableStateFlow<Boolean>.invoke(state: Boolean) { _isLoading.value = state }

    internal val updateLocation: (LocationData) -> Unit = { newLocation ->
        _isLoading(true)
        _location.value = newLocation
        _isLoading(false)
    }
}