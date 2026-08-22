package com.example.recipe_app_compose.features.location.presentation.viewmodel

import com.example.recipe_app_compose.core.testing.MainDispatcherRule
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreference
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import com.example.recipe_app_compose.features.location.domain.states.YelpSearchArea
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class YelpViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state waits for the stored location preference`() {
        val viewModel = YelpViewModel(
            repository = FakeYelpRepository(),
            currentLocationProvider = { null },
            locationPreferenceStore = FakeLocationPreferenceStore(),
        )

        assertEquals(
            YelpSearchArea.RestoringPreference,
            viewModel.uiState.value.searchArea,
        )
    }

    @Test
    fun `ask every time preference restores the location choice`() =
        runTest(mainDispatcherRule.dispatcher) {
            val viewModel = YelpViewModel(
                repository = FakeYelpRepository(),
                currentLocationProvider = { null },
                locationPreferenceStore = FakeLocationPreferenceStore(),
            )

            viewModel.restoreLocationPreference(hasLocationPermission = false)
            advanceUntilIdle()

            assertEquals(
                YelpSearchArea.LocationChoiceRequired,
                viewModel.uiState.value.searchArea,
            )
        }

    @Test
    fun `stored current location preference loads when permission remains granted`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val preferences = FakeLocationPreferenceStore(
                LocationPreference.CurrentLocation,
            )
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = {
                    LocationData(latitude = 28.18, longitude = -82.35)
                },
                locationPreferenceStore = preferences,
            )

            viewModel.restoreLocationPreference(hasLocationPermission = true)
            advanceUntilIdle()

            assertEquals(YelpSearchArea.CurrentLocation, viewModel.uiState.value.searchArea)
            assertEquals(1, repository.requests.size)
            assertEquals(LocationPreference.CurrentLocation, preferences.current)
        }

    @Test
    fun `stored current location preference requests permission after revocation`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = { null },
                locationPreferenceStore = FakeLocationPreferenceStore(
                    LocationPreference.CurrentLocation,
                ),
            )

            viewModel.restoreLocationPreference(hasLocationPermission = false)
            advanceUntilIdle()

            assertEquals(YelpSearchArea.PermissionRequired, viewModel.uiState.value.searchArea)
            assertTrue(repository.requests.isEmpty())
        }

    @Test
    fun `search mode is retained by the route scoped view model`() {
        val viewModel = YelpViewModel(
            repository = FakeYelpRepository(),
            currentLocationProvider = { null },
            locationPreferenceStore = FakeLocationPreferenceStore(),
        )

        viewModel.onSearchActiveChange(true)

        assertTrue(viewModel.isSearchActive.value)
    }

    @Test
    fun `declining user driven location access exposes the manual fallback`() {
        val repository = FakeYelpRepository()
        val viewModel = YelpViewModel(
            repository = repository,
            currentLocationProvider = { null },
            locationPreferenceStore = FakeLocationPreferenceStore(),
        )

        viewModel.onLocationPermissionDenied()

        assertEquals(YelpSearchArea.PermissionRequired, viewModel.uiState.value.searchArea)
        assertTrue(repository.requests.isEmpty())
    }

    @Test
    fun `nearby search uses current coordinates and default restaurant term`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val location = LocationData(latitude = 28.18, longitude = -82.35)
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = { location },
                locationPreferenceStore = FakeLocationPreferenceStore(),
            )

            viewModel.loadNearbyShops()
            advanceUntilIdle()

            assertEquals(YelpSearchArea.CurrentLocation, viewModel.uiState.value.searchArea)
            assertEquals(1, repository.requests.size)
            val request = repository.requests.single()
            assertEquals("restaurants", request.term)
            assertEquals(16_000, request.radiusMeters)
            assertEquals(
                location,
                (request.origin as YelpSearchOrigin.Coordinates).location,
            )
        }

    @Test
    fun `successful current location search remembers the user choice`() =
        runTest(mainDispatcherRule.dispatcher) {
            val preferences = FakeLocationPreferenceStore()
            val viewModel = YelpViewModel(
                repository = FakeYelpRepository(),
                currentLocationProvider = {
                    LocationData(latitude = 28.18, longitude = -82.35)
                },
                locationPreferenceStore = preferences,
            )

            viewModel.loadNearbyShops()
            advanceUntilIdle()

            assertEquals(LocationPreference.CurrentLocation, preferences.current)
        }

    @Test
    fun `choosing another location clears the remembered user choice`() =
        runTest(mainDispatcherRule.dispatcher) {
            val preferences = FakeLocationPreferenceStore(
                LocationPreference.CurrentLocation,
            )
            val viewModel = YelpViewModel(
                repository = FakeYelpRepository(),
                currentLocationProvider = { null },
                locationPreferenceStore = preferences,
            )

            viewModel.chooseAnotherLocation()
            advanceUntilIdle()

            assertEquals(LocationPreference.AskEveryTime, preferences.current)
            assertEquals(
                YelpSearchArea.LocationChoiceRequired,
                viewModel.uiState.value.searchArea,
            )
        }

    @Test
    fun `location unavailable does not call Yelp and exposes fallback state`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = { null },
                locationPreferenceStore = FakeLocationPreferenceStore(),
            )

            viewModel.loadNearbyShops()
            advanceUntilIdle()

            assertEquals(
                YelpSearchArea.LocationUnavailable,
                viewModel.uiState.value.searchArea,
            )
            assertTrue(repository.requests.isEmpty())
        }

    @Test
    fun `location resolution timeout exposes fallback instead of loading forever`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = {
                    delay(Long.MAX_VALUE.milliseconds)
                    null
                },
                locationPreferenceStore = FakeLocationPreferenceStore(),
            )

            viewModel.loadNearbyShops()
            advanceUntilIdle()

            assertEquals(
                YelpSearchArea.LocationUnavailable,
                viewModel.uiState.value.searchArea,
            )
            assertTrue(repository.requests.isEmpty())
        }

    @Test
    fun `manual location fallback sends a named Yelp origin`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val preferences = FakeLocationPreferenceStore(
                LocationPreference.CurrentLocation,
            )
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = { null },
                locationPreferenceStore = preferences,
            )

            viewModel.onManualLocationChange(" Austin, TX ")
            viewModel.searchManualLocation()
            advanceUntilIdle()

            assertEquals(
                YelpSearchArea.NamedLocation("Austin, TX"),
                viewModel.uiState.value.searchArea,
            )
            assertEquals(
                YelpSearchOrigin.NamedLocation("Austin, TX"),
                repository.requests.single().origin,
            )
            assertEquals(LocationPreference.AskEveryTime, preferences.current)
        }

    @Test
    fun `revoked permission removes an active coordinate search`() =
        runTest(mainDispatcherRule.dispatcher) {
            val viewModel = YelpViewModel(
                repository = FakeYelpRepository(),
                currentLocationProvider = {
                    LocationData(latitude = 28.18, longitude = -82.35)
                },
                locationPreferenceStore = FakeLocationPreferenceStore(
                    LocationPreference.CurrentLocation,
                ),
            )

            viewModel.restoreLocationPreference(hasLocationPermission = true)
            advanceUntilIdle()
            viewModel.onLocationPermissionStatusChanged(hasLocationPermission = false)

            assertEquals(YelpSearchArea.PermissionRequired, viewModel.uiState.value.searchArea)
            assertTrue(viewModel.uiState.value.list.isEmpty())
        }

    @Test
    fun `search text is debounced around the selected origin`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val location = LocationData(latitude = 28.18, longitude = -82.35)
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = { location },
                locationPreferenceStore = FakeLocationPreferenceStore(),
            )
            viewModel.loadNearbyShops()
            advanceUntilIdle()

            viewModel.onSearchTextChange("coffee")
            runCurrent()
            assertEquals(1, repository.requests.size)

            advanceUntilIdle()
            assertEquals(2, repository.requests.size)
            assertEquals("coffee", repository.requests.last().term)
            assertTrue(repository.requests.last().origin is YelpSearchOrigin.Coordinates)
        }

    @Test
    fun `manual search wins when an in-flight location request is cancelled`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = {
                    delay(Long.MAX_VALUE.milliseconds)
                    null
                },
                locationPreferenceStore = FakeLocationPreferenceStore(),
            )
            viewModel.loadNearbyShops()
            runCurrent()

            viewModel.onManualLocationChange("Chicago")
            viewModel.searchManualLocation()
            advanceUntilIdle()

            assertEquals(
                YelpSearchArea.NamedLocation("Chicago"),
                viewModel.uiState.value.searchArea,
            )
            assertEquals(1, repository.requests.size)
            assertEquals(
                YelpSearchOrigin.NamedLocation("Chicago"),
                repository.requests.single().origin,
            )
        }

    @Test
    fun `manual search cancels a stale shop response for the current location`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository { request ->
                if (request.origin is YelpSearchOrigin.Coordinates) {
                    delay(Long.MAX_VALUE.milliseconds)
                    Resource.Error("stale location response")
                } else {
                    Resource.Success(YelpSearchResult(total = 0U, shops = emptyList()))
                }
            }
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = {
                    LocationData(latitude = 28.18, longitude = -82.35)
                },
                locationPreferenceStore = FakeLocationPreferenceStore(),
            )

            viewModel.loadNearbyShops()
            runCurrent()
            viewModel.onManualLocationChange("Chicago")
            viewModel.searchManualLocation()
            advanceUntilIdle()

            assertEquals(
                YelpSearchArea.NamedLocation("Chicago"),
                viewModel.uiState.value.searchArea,
            )
            assertEquals(null, viewModel.uiState.value.error)
            assertEquals(2, repository.requests.size)
            assertTrue(repository.requests.first().origin is YelpSearchOrigin.Coordinates)
            assertEquals(
                YelpSearchOrigin.NamedLocation("Chicago"),
                repository.requests.last().origin,
            )
        }

    @Test
    fun `resuming with an existing origin does not duplicate the initial request`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeYelpRepository()
            val viewModel = YelpViewModel(
                repository = repository,
                currentLocationProvider = {
                    LocationData(latitude = 28.18, longitude = -82.35)
                },
                locationPreferenceStore = FakeLocationPreferenceStore(),
            )

            viewModel.loadNearbyShops()
            advanceUntilIdle()
            viewModel.loadNearbyShops()
            advanceUntilIdle()

            assertEquals(1, repository.requests.size)
        }

    private class FakeYelpRepository(
        private val response: suspend (YelpSearchRequest) -> Resource<YelpSearchResult> = {
            Resource.Success(YelpSearchResult(total = 0U, shops = emptyList()))
        },
    ) : YelpRepository {
        val requests = mutableListOf<YelpSearchRequest>()

        override suspend fun searchShops(
            request: YelpSearchRequest,
        ): Resource<YelpSearchResult> {
            requests += request
            return response(request)
        }
    }

    private class FakeLocationPreferenceStore(
        initialPreference: LocationPreference = LocationPreference.AskEveryTime,
    ) : LocationPreferenceStore {
        private val storedPreference = MutableStateFlow(initialPreference)

        override val preference: Flow<LocationPreference> = storedPreference
        val current: LocationPreference
            get() = storedPreference.value

        override suspend fun setPreference(preference: LocationPreference) {
            storedPreference.value = preference
        }
    }
}
