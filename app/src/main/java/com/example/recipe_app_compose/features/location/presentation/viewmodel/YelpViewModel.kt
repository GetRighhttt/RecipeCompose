package com.example.recipe_app_compose.features.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.core.util.Constants
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.location.domain.location.CurrentLocationProvider
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import com.example.recipe_app_compose.features.location.domain.states.YelpSearchArea
import com.example.recipe_app_compose.features.location.domain.states.YelpStates
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class YelpViewModel(
    private val repository: YelpRepository = DependencyInjector.yelpRepo,
    private val currentLocationProvider: CurrentLocationProvider =
        DependencyInjector.currentLocationProvider,
) : ViewModel() {

    val yelpState: StateFlow<YelpStates>
        field: MutableStateFlow<YelpStates> = MutableStateFlow(YelpStates())

    val searchQuery: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow("")

    val manualLocationQuery: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow("")

    private var searchOrigin: YelpSearchOrigin? = null
    private var locationJob: Job? = null
    private var businessSearchJob: Job? = null

    internal fun loadNearbyBusinesses(forceRefresh: Boolean = false) {
        if (!forceRefresh && searchOrigin != null) return
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch {
            yelpState.update {
                it.copy(
                    loading = false,
                    error = null,
                    searchArea = YelpSearchArea.ResolvingCurrentLocation,
                )
            }

            val location = try {
                currentLocationProvider.getCurrentLocation()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                null
            }

            if (location == null) {
                searchOrigin = null
                yelpState.update {
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
            yelpState.update {
                it.copy(searchArea = YelpSearchArea.CurrentLocation)
            }
            loadBusinesses(currentSearchTerm(), origin)
        }
    }

    internal fun onLocationPermissionDenied() {
        locationJob?.cancel()
        if (searchOrigin !is YelpSearchOrigin.NamedLocation) {
            searchOrigin = null
            yelpState.update {
                it.copy(
                    loading = false,
                    list = emptyList(),
                    error = null,
                    searchArea = YelpSearchArea.PermissionRequired,
                )
            }
        }
    }

    internal fun onSearchTextChange(text: String) {
        searchQuery.value = text
        queueSearchForCurrentOrigin()
    }

    internal fun onManualLocationChange(text: String) {
        manualLocationQuery.value = text
    }

    internal fun searchManualLocation() {
        val location = manualLocationQuery.value.trim()
        if (location.isBlank()) return

        locationJob?.cancel()
        val origin = YelpSearchOrigin.NamedLocation(location)
        searchOrigin = origin
        yelpState.update {
            it.copy(
                error = null,
                searchArea = YelpSearchArea.NamedLocation(location),
            )
        }
        businessSearchJob?.cancel()
        businessSearchJob = viewModelScope.launch {
            loadBusinesses(currentSearchTerm(), origin)
        }
    }

    internal fun retry() {
        val origin = searchOrigin
        if (origin == null) {
            loadNearbyBusinesses(forceRefresh = true)
        } else {
            businessSearchJob?.cancel()
            businessSearchJob = viewModelScope.launch {
                loadBusinesses(currentSearchTerm(), origin)
            }
        }
    }

    private fun queueSearchForCurrentOrigin() {
        val origin = searchOrigin ?: return
        businessSearchJob?.cancel()
        businessSearchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE)
            loadBusinesses(currentSearchTerm(), origin)
        }
    }

    private suspend fun loadBusinesses(term: String, origin: YelpSearchOrigin) {
        yelpState.update { it.copy(loading = true, error = null) }
        when (
            val response = repository.searchBusinesses(
                YelpSearchRequest(
                    term = term,
                    origin = origin,
                    radiusMeters = DEFAULT_RADIUS_METERS,
                )
            )
        ) {
            is Resource.Loading -> Unit

            is Resource.Error -> yelpState.update {
                it.copy(
                    loading = false,
                    error = response.message ?: DEFAULT_ERROR_MESSAGE,
                )
            }

            is Resource.Success -> yelpState.update {
                it.copy(
                    loading = false,
                    list = response.data?.restaurants.orEmpty(),
                    error = null,
                )
            }
        }
    }

    private fun currentSearchTerm(): String =
        searchQuery.value.trim().ifBlank { Constants.YELP_DEFAULT_SEARCH_TERM }

    private companion object {
        val SEARCH_DEBOUNCE = 500L.milliseconds
        const val DEFAULT_RADIUS_METERS = 16_000
        const val DEFAULT_ERROR_MESSAGE = "Error fetching businesses."
    }
}
