package com.example.recipe_app_compose.features.location.presentation

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.domain.location.LocationAccess
import com.example.recipe_app_compose.features.location.domain.location.LocationAuthorization
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreference
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import com.example.recipe_app_compose.features.location.domain.states.YelpSearchArea
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NearbyStoreTest {
    @Test
    fun `manual search remains available without location authorization`() = runTest {
        val repository = FakeYelpRepository()
        val preferences = FakeLocationPreferenceStore()
        val store = NearbyStore(
            repository = repository,
            locationPreferenceStore = preferences,
            locationAccess = FakeLocationAccess(LocationAuthorization.Denied),
            scope = this,
        )

        store.restoreLocationPreference()
        advanceUntilIdle()
        store.onManualLocationChange("Chicago, IL")
        store.searchManualLocation()
        advanceUntilIdle()

        assertEquals(YelpSearchArea.NamedLocation("Chicago, IL"), store.uiState.value.searchArea)
        assertEquals("Chicago, IL", repository.requests.single().origin.locationValue())
        assertEquals(LocationPreference.AskEveryTime, preferences.current)
    }

    @Test
    fun `current location search waits for user authorization then persists the choice`() = runTest {
        val repository = FakeYelpRepository()
        val preferences = FakeLocationPreferenceStore()
        val access = FakeLocationAccess(LocationAuthorization.NotDetermined)
        val store = NearbyStore(repository, preferences, access, this)

        store.restoreLocationPreference()
        advanceUntilIdle()
        store.useCurrentLocation()
        assertTrue(access.requested)

        access.authorizationState.value = LocationAuthorization.Granted
        store.onAuthorizationChanged(LocationAuthorization.Granted)
        advanceUntilIdle()

        assertEquals(YelpSearchArea.CurrentLocation, store.uiState.value.searchArea)
        assertEquals(LocationPreference.CurrentLocation, preferences.current)
        assertEquals(LocationData(41.8781, -87.6298), repository.requests.single().origin.coordinateValue())
    }

    private class FakeYelpRepository : YelpRepository {
        val requests = mutableListOf<YelpSearchRequest>()

        override suspend fun searchShops(request: YelpSearchRequest): Resource<YelpSearchResult> {
            requests += request
            return Resource.Success(YelpSearchResult())
        }
    }

    private class FakeLocationPreferenceStore(
        initial: LocationPreference = LocationPreference.AskEveryTime,
    ) : LocationPreferenceStore {
        private val values = MutableStateFlow(initial)
        var current: LocationPreference = initial
            private set

        override val preference: Flow<LocationPreference> = values

        override suspend fun setPreference(preference: LocationPreference) {
            current = preference
            values.value = preference
        }
    }

    private class FakeLocationAccess(initial: LocationAuthorization) : LocationAccess {
        val authorizationState = MutableStateFlow(initial)
        var requested = false
            private set

        override val authorization = authorizationState

        override fun requestWhenInUseAccess() {
            requested = true
        }

        override suspend fun currentLocation(): LocationData = LocationData(41.8781, -87.6298)

        override fun openAppSettings() = Unit
    }
}

private fun com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin.locationValue(): String =
    (this as com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin.NamedLocation).value

private fun com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin.coordinateValue(): LocationData =
    (this as com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin.Coordinates).location
