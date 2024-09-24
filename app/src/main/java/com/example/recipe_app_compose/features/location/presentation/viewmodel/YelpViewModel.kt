package com.example.recipe_app_compose.features.location.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.core.util.Constants
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.data.repoimpl.YelpRepImpl
import com.example.recipe_app_compose.features.location.domain.states.YelpStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class YelpViewModel () : ViewModel() {

    // necessary repo composition
    private val repository = YelpRepImpl()

    private val _yelpState = MutableStateFlow(YelpStates())
    val yelpState = _yelpState.asStateFlow()

    internal val getBusinesses: (String) -> Unit = { query ->
        viewModelScope.launch(Dispatchers.IO) {
            when(val response = repository.searchBusinesses(
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

    override fun onCleared() {
        Log.d(YELP_VIEW_MODEL, "Cleared.")
    }

    companion object {
        private const val YELP_VIEW_MODEL = "MAIN_VIEW_MODEL"
        private const val BEARER = "Bearer ${Constants.YELP_API_KEY}"
        private const val DEFAULT_LOCATION = "Tampa"
        private const val DEFAULT_LIMIT: UInt = 50U
        private const val DEFAULT_OFFSET: UInt = 0U
    }
}