package com.example.recipe_app_compose.features.location.presentation

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.domain.location.LocationAccess
import com.example.recipe_app_compose.features.location.domain.location.LocationAuthorization
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreference
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import com.example.recipe_app_compose.features.location.domain.states.YelpSearchArea
import com.example.recipe_app_compose.features.location.domain.states.YelpUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Shared state and actions for restaurant discovery on Android and iOS. */
class NearbyStore(
    private val repository: YelpRepository,
    private val locationPreferenceStore: LocationPreferenceStore,
    private val locationAccess: LocationAccess,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val mutableSearchQuery = MutableStateFlow("")
    private val mutableManualLocationQuery = MutableStateFlow("")
    private val mutableUiState = MutableStateFlow(YelpUiState())

    val searchQuery: StateFlow<String> = mutableSearchQuery
    val manualLocationQuery: StateFlow<String> = mutableManualLocationQuery
    val uiState: StateFlow<YelpUiState> = mutableUiState

    private var searchOrigin: YelpSearchOrigin? = null
    private var locationJob: Job? = null
    private var shopSearchJob: Job? = null
    private var preferenceJob: Job? = null
    private var preferenceRestored = false
    private var waitingForAuthorization = false

    fun restoreLocationPreference() {
        if (preferenceRestored || preferenceJob?.isActive == true) return

        preferenceJob = scope.launch {
            val preference = try {
                locationPreferenceStore.preference.first()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                LocationPreference.AskEveryTime
            }
            preferenceRestored = true

            if (preference == LocationPreference.CurrentLocation &&
                locationAccess.authorization.value == LocationAuthorization.Granted
            ) {
                resolveCurrentLocation()
            } else {
                mutableUiState.update {
                    it.copy(searchArea = YelpSearchArea.LocationChoiceRequired)
                }
            }
        }
    }

    fun onAuthorizationChanged(authorization: LocationAuthorization) {
        if (!preferenceRestored) return

        when {
            authorization == LocationAuthorization.Granted && waitingForAuthorization -> {
                waitingForAuthorization = false
                resolveCurrentLocation()
            }

            authorization != LocationAuthorization.Granted && waitingForAuthorization -> {
                waitingForAuthorization = false
                showAuthorizationFallback(authorization)
            }

            authorization != LocationAuthorization.Granted &&
                searchOrigin is YelpSearchOrigin.Coordinates -> {
                onLocationPermissionLost(authorization)
            }
        }
    }

    fun useCurrentLocation() {
        when (val authorization = locationAccess.authorization.value) {
            LocationAuthorization.Granted -> resolveCurrentLocation()
            LocationAuthorization.NotDetermined -> {
                waitingForAuthorization = true
                locationAccess.requestWhenInUseAccess()
            }

            else -> showAuthorizationFallback(authorization)
        }
    }

    fun onSearchTextChange(text: String) {
        mutableSearchQuery.value = text
        searchOrigin?.let { launchShopSearch(it, debounce = true) }
    }

    fun onManualLocationChange(text: String) {
        mutableManualLocationQuery.value = text
    }

    fun searchManualLocation() {
        val location = manualLocationQuery.value.trim()
        if (location.isBlank()) return

        waitingForAuthorization = false
        locationJob?.cancel()
        persistLocationPreference(LocationPreference.AskEveryTime)
        val origin = YelpSearchOrigin.NamedLocation(location)
        searchOrigin = origin
        mutableUiState.update {
            it.copy(
                error = null,
                searchArea = YelpSearchArea.NamedLocation(location),
            )
        }
        launchShopSearch(origin)
    }

    fun chooseAnotherLocation() {
        waitingForAuthorization = false
        locationJob?.cancel()
        shopSearchJob?.cancel()
        searchOrigin = null
        mutableSearchQuery.value = ""
        persistLocationPreference(LocationPreference.AskEveryTime)
        mutableUiState.update {
            it.copy(
                loading = false,
                list = emptyList(),
                error = null,
                searchArea = YelpSearchArea.LocationChoiceRequired,
            )
        }
    }

    fun retry() {
        when (val origin = searchOrigin) {
            null -> useCurrentLocation()
            else -> launchShopSearch(origin)
        }
    }

    fun close() = scope.cancel()

    private fun resolveCurrentLocation() {
        if (locationJob?.isActive == true) return

        locationJob = scope.launch {
            mutableUiState.update {
                it.copy(
                    loading = false,
                    error = null,
                    searchArea = YelpSearchArea.ResolvingCurrentLocation,
                )
            }

            val location = try {
                locationAccess.currentLocation()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                null
            }

            if (location == null) {
                searchOrigin = null
                mutableUiState.update {
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
            mutableUiState.update { it.copy(searchArea = YelpSearchArea.CurrentLocation) }
            launchShopSearch(origin)
        }
    }

    private fun onLocationPermissionLost(authorization: LocationAuthorization) {
        locationJob?.cancel()
        shopSearchJob?.cancel()
        searchOrigin = null
        showAuthorizationFallback(authorization)
    }

    private fun showAuthorizationFallback(authorization: LocationAuthorization) {
        val area = when (authorization) {
            LocationAuthorization.ServicesDisabled -> YelpSearchArea.LocationUnavailable
            LocationAuthorization.NotDetermined -> YelpSearchArea.LocationChoiceRequired
            LocationAuthorization.Granted -> YelpSearchArea.LocationChoiceRequired
            LocationAuthorization.Denied,
            LocationAuthorization.Restricted,
            -> YelpSearchArea.PermissionRequired
        }
        mutableUiState.update {
            it.copy(
                loading = false,
                list = emptyList(),
                error = null,
                searchArea = area,
            )
        }
    }

    private fun launchShopSearch(
        origin: YelpSearchOrigin,
        debounce: Boolean = false,
    ) {
        shopSearchJob?.cancel()
        shopSearchJob = scope.launch {
            if (debounce) delay(SEARCH_DEBOUNCE_MILLIS)
            loadShops(currentSearchTerm(), origin)
        }
    }

    private suspend fun loadShops(term: String, origin: YelpSearchOrigin) {
        mutableUiState.update { it.copy(loading = true, error = null) }
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
            is Resource.Error -> mutableUiState.update {
                it.copy(loading = false, error = response.message ?: DEFAULT_ERROR_MESSAGE)
            }

            is Resource.Success -> mutableUiState.update {
                it.copy(loading = false, list = response.data?.shops.orEmpty(), error = null)
            }
        }
    }

    private fun currentSearchTerm(): String =
        searchQuery.value.trim().ifBlank { DEFAULT_SEARCH_TERM }

    private fun persistLocationPreference(preference: LocationPreference) {
        scope.launch {
            try {
                locationPreferenceStore.setPreference(preference)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // Persisting a preference must not prevent a restaurant search.
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 500L
        const val DEFAULT_RADIUS_METERS = 16_000
        const val DEFAULT_SEARCH_TERM = "restaurants"
        const val DEFAULT_ERROR_MESSAGE = "Unable to load shops."
    }
}
