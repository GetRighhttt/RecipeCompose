package com.example.recipe_app_compose.core.util.permissions

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.util.Locale

class PermissionUtils(private val context: Context) {

    @SuppressLint("Deprecated")
    // geocoder to convert lat and long to address
    internal suspend fun reverseGeocodeLocation(locationData: LocationData): String =
        withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocation(
                locationData.latitude,
                locationData.longitude,
                1
            )

            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Address Not Found."
            }
        }

    /*
    NetWork Data
     */
    sealed interface NetworkConnectionState {
        data object Available : NetworkConnectionState
        data object Unavailable : NetworkConnectionState
    }

    internal fun networkCallback(callback: (NetworkConnectionState) -> Unit): ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                callback(NetworkConnectionState.Available)
            }

            override fun onLost(network: Network) {
                callback(NetworkConnectionState.Unavailable)
            }
        }

    private fun getCurrentConnectivityState(connectivityManager: ConnectivityManager): NetworkConnectionState {
        val network = connectivityManager.activeNetwork

        val isConnected = connectivityManager
            .getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

        return if (isConnected) NetworkConnectionState.Available else NetworkConnectionState.Unavailable
    }

    private fun Context.observeConnectivityAsFlow(): Flow<NetworkConnectionState> = callbackFlow {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = networkCallback { connectionState ->
            trySend(connectionState)
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        val currentState = getCurrentConnectivityState(connectivityManager)
        trySend(currentState)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    private val Context.currentConnectivityState: NetworkConnectionState
        get() {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return getCurrentConnectivityState(connectivityManager)
        }

    @Composable
    fun rememberConnectivityState(): State<NetworkConnectionState> {
        val context = LocalContext.current

        return produceState(initialValue = context.currentConnectivityState) {
            context.observeConnectivityAsFlow().collect {
                value = it
            }
        }
    }
}
