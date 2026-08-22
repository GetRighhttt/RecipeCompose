package com.example.recipe_app_compose.features.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.core.util.Constants
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.location.domain.location.CurrentLocationProvider
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreference
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import com.example.recipe_app_compose.features.location.domain.states.YelpSearchArea
import com.example.recipe_app_compose.features.location.domain.states.YelpUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

class YelpViewModel(
    private val repository: YelpRepository = DependencyInjector.yelpRepository,
    private val currentLocationProvider: CurrentLocationProvider =
        DependencyInjector.currentLocationProvider,
    private val locationPreferenceStore: LocationPreferenceStore =
        DependencyInjector.locationPreferenceStore,
) : ViewModel() {

    val searchQuery: StateFlow<String> field = MutableStateFlow("")
    val isSearchActive: StateFlow<Boolean> field = MutableStateFlow(false)
    val manualLocationQuery: StateFlow<String> field = MutableStateFlow("")
    val uiState: StateFlow<YelpUiState> field = MutableStateFlow(YelpUiState())

    private var searchOrigin: YelpSearchOrigin? = null
    private var locationJob: Job? = null
    private var shopSearchJob: Job? = null
    private var preferenceJob: Job? = null
    private var preferenceRestored = false

    internal fun restoreLocationPreference(hasLocationPermission: Boolean) {
        if (preferenceRestored || preferenceJob?.isActive == true) return

        preferenceJob = viewModelScope.launch {
            val preference = try {
                locationPreferenceStore.preference.first()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                LocationPreference.AskEveryTime
            }
            preferenceRestored = true

            when {
                preference == LocationPreference.CurrentLocation && hasLocationPermission ->
                    loadNearbyShops(forceRefresh = true)

                preference == LocationPreference.CurrentLocation ->
                    onLocationPermissionDenied()

                else -> uiState.update {
                    it.copy(searchArea = YelpSearchArea.LocationChoiceRequired)
                }
            }
        }
    }

    internal fun loadNearbyShops(forceRefresh: Boolean = false) {
        if (!forceRefresh && searchOrigin != null) return
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch {
            uiState.update {
                it.copy(
                    loading = false,
                    error = null,
                    searchArea = YelpSearchArea.ResolvingCurrentLocation,
                )
            }

            val location = try {
                withTimeoutOrNull(LOCATION_RESOLUTION_TIMEOUT_MILLIS) {
                    currentLocationProvider.getCurrentLocation()
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                null
            }

            if (location == null) {
                searchOrigin = null
                uiState.update {
                    it.copy(
                        loading = false,
                        list = emptyList(),
                        searchArea = YelpSearchArea.LocationUnavailable,
                    )
                }
                return@launch
            }

            val origin = YelpSearchOrigin.Coordinates(location)
            searchOrigin = origin
            persistLocationPreference(LocationPreference.CurrentLocation)
            uiState.update {
                it.copy(searchArea = YelpSearchArea.CurrentLocation)
            }
            launchShopSearch(origin)
        }
    }

    internal fun onLocationPermissionDenied() {
        locationJob?.cancel()
        shopSearchJob?.cancel()
        if (searchOrigin !is YelpSearchOrigin.NamedLocation) {
            searchOrigin = null
            uiState.update {
                it.copy(
                    loading = false,
                    list = emptyList(),
                    error = null,
                    searchArea = YelpSearchArea.PermissionRequired,
                )
            }
        }
    }

    internal fun onLocationPermissionStatusChanged(hasLocationPermission: Boolean) {
        if (!preferenceRestored) return

        when {
            !hasLocationPermission && searchOrigin is YelpSearchOrigin.Coordinates ->
                onLocationPermissionDenied()

            hasLocationPermission &&
                uiState.value.searchArea == YelpSearchArea.PermissionRequired ->
                loadNearbyShops(forceRefresh = true)
        }
    }

    internal fun onSearchTextChange(text: String) {
        searchQuery.value = text
        queueSearchForCurrentOrigin()
    }

    internal fun onSearchActiveChange(active: Boolean) {
        isSearchActive.value = active
    }

    internal fun onManualLocationChange(text: String) {
        manualLocationQuery.value = text
    }

    internal fun searchManualLocation() {
        val location = manualLocationQuery.value.trim()
        if (location.isBlank()) return

        locationJob?.cancel()
        persistLocationPreference(LocationPreference.AskEveryTime)
        val origin = YelpSearchOrigin.NamedLocation(location)
        searchOrigin = origin
        uiState.update {
            it.copy(
                error = null,
                searchArea = YelpSearchArea.NamedLocation(location),
            )
        }
        launchShopSearch(origin)
    }

    internal fun chooseAnotherLocation() {
        locationJob?.cancel()
        shopSearchJob?.cancel()
        searchOrigin = null
        searchQuery.value = ""
        persistLocationPreference(LocationPreference.AskEveryTime)
        uiState.update {
            it.copy(
                loading = false,
                list = emptyList(),
                error = null,
                searchArea = YelpSearchArea.LocationChoiceRequired,
            )
        }
    }

    internal fun retry() {
        val origin = searchOrigin
        if (origin == null) {
            loadNearbyShops(forceRefresh = true)
        } else {
            launchShopSearch(origin)
        }
    }

    private fun queueSearchForCurrentOrigin() {
        val origin = searchOrigin ?: return
        launchShopSearch(origin, debounce = true)
    }

    private fun launchShopSearch(
        origin: YelpSearchOrigin,
        debounce: Boolean = false,
    ) {
        shopSearchJob?.cancel()
        shopSearchJob = viewModelScope.launch {
            if (debounce) delay(SEARCH_DEBOUNCE)
            loadShops(currentSearchTerm(), origin)
        }
    }

    private suspend fun loadShops(term: String, origin: YelpSearchOrigin) {
        uiState.update { it.copy(loading = true, error = null) }
        when (
            val response = repository.searchShops(
                YelpSearchRequest(
                    term = term,
                    origin = origin,
                    radiusMeters = DEFAULT_RADIUS_METERS,
                )
            )
        ) {
            is Resource.Loading -> Unit

            is Resource.Error -> uiState.update {
                it.copy(
                    loading = false,
                    error = response.message ?: DEFAULT_ERROR_MESSAGE,
                )
            }

            is Resource.Success -> uiState.update {
                it.copy(
                    loading = false,
                    list = response.data?.shops.orEmpty(),
                    error = null,
                )
            }
        }
    }

    private fun currentSearchTerm(): String =
        searchQuery.value.trim().ifBlank { Constants.YELP_DEFAULT_SEARCH_TERM }

    private fun persistLocationPreference(preference: LocationPreference) {
        viewModelScope.launch {
            try {
                locationPreferenceStore.setPreference(preference)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // Preference persistence must not block restaurant search.
            }
        }
    }

    private companion object {
        val SEARCH_DEBOUNCE = 500L.milliseconds
        const val DEFAULT_RADIUS_METERS = 16_000
        const val DEFAULT_ERROR_MESSAGE = "Error fetching businesses."
        const val LOCATION_RESOLUTION_TIMEOUT_MILLIS = 12_000L
    }
}
