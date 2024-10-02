package com.example.recipe_app_compose.features.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.core.util.Constants
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.location.data.repoimpl.YelpRepImpl
import com.example.recipe_app_compose.features.location.domain.states.YelpStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class YelpViewModel(
    private val repository: YelpRepImpl = DependencyInjector.yelpRepo
) : ViewModel() {

    private val _yelpState = MutableStateFlow(YelpStates())
    val yelpState = _yelpState.asStateFlow()

    // states for search view
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // ingredient list to be populated by way of Flow operators
    private val _businessList = MutableStateFlow(_yelpState.value.list)

    @OptIn(FlowPreview::class)
    internal val businessList = searchQuery
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_businessList) { text, businesses ->
            if (text.isBlank()) {
                businesses
            } else {
                businesses?.filter { business ->
                    business.name.contains(text)
                }.also {
                    delay(1500L)
                    getBusinesses(text)
                    delay(1500L)
                }
            }
            // convert to State FLow
        }.onEach { _isSearching.update { false } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _businessList.value
        )

    // method to set new search value
    internal val onSearchTextChange: (String) -> Unit = { text ->
        _searchQuery.value = text
    }

    internal val getBusinesses: (String) -> Unit = { query ->
        viewModelScope.launch(Dispatchers.Main) {
            when (
                val response = repository.searchBusinesses(
                    BEARER,
                    query,
                    DEFAULT_LOCATION,
                    DEFAULT_LIMIT,
                    DEFAULT_OFFSET
                )) {
                is Resource.Error -> _yelpState.value = _yelpState.value.copy(
                    loading = false,
                    error = response.message!!
                )

                is Resource.Loading -> _yelpState.value = _yelpState.value.copy(loading = true)
                is Resource.Success -> _yelpState.value = _yelpState.value.copy(
                    loading = false,
                    list = response.data!!.restaurants,
                    error = null
                )
            }
        }
    }

    init {
        getBusinesses("")
    }

    companion object {
        const val DEFAULT_LOCATION = "Tampa"
        private const val BEARER = "Bearer ${Constants.YELP_API_KEY}"
        private const val DEFAULT_LIMIT: UInt = 50U
        private const val DEFAULT_OFFSET: UInt = 0U
    }
}