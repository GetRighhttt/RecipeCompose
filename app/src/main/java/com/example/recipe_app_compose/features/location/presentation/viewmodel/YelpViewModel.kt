package com.example.recipe_app_compose.features.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.core.util.Constants
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import com.example.recipe_app_compose.features.location.domain.states.YelpStates
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class YelpViewModel(
    private val repository: YelpRepository = DependencyInjector.yelpRepo
) : ViewModel() {

    val yelpState: StateFlow<YelpStates>
        field: MutableStateFlow<YelpStates> = MutableStateFlow(YelpStates())

    val searchQuery: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow("")

    val isSearching: StateFlow<Boolean>
        field: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var businessSearchJob: Job? = null

    internal val businessList = yelpState
        .map { it.list.orEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    internal fun onSearchTextChange(text: String) {
        searchQuery.value = text
        businessSearchJob?.cancel()

        if (text.isBlank()) {
            isSearching.value = false
            return
        }

        businessSearchJob = viewModelScope.launch {
            isSearching.value = true
            delay(500L.milliseconds)
            loadBusinesses(text)
            isSearching.value = false
        }
    }

    internal fun getBusinesses(query: String = Constants.YELP_SEARCH_QUERY) {
        viewModelScope.launch {
            loadBusinesses(query)
        }
    }

    private suspend fun loadBusinesses(query: String) {
        yelpState.update { it.copy(loading = true, error = null) }
        when (val response = repository.searchBusinesses(
            BEARER,
            query,
            query,
            DEFAULT_LIMIT,
            DEFAULT_OFFSET
        )) {
            is Resource.Loading -> Unit

            is Resource.Error -> yelpState.update {
                it.copy(loading = false, error = response.message ?: "Error fetching businesses.")
            }

            is Resource.Success -> yelpState.update {
                it.copy(
                    loading = false,
                    list = response.data?.restaurants.orEmpty(),
                    error = null
                )
            }
        }
    }

    init {
        getBusinesses(Constants.YELP_SEARCH_QUERY)
    }

    companion object {
        private val BEARER = "Bearer ${Constants.YELP_API_KEY}"
        private const val DEFAULT_LIMIT: UInt = 50U
        private const val DEFAULT_OFFSET: UInt = 0U
    }
}
