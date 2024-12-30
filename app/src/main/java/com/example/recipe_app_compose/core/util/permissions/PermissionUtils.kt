package com.example.recipe_app_compose.core.util.permissions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.presentation.viewmodel.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class PermissionUtils(private val context: Context) {

    /*
    Location Data

    1. Check if permissions exists
    2. Request permissions
    3. Get location updates
    4. Convert to lat long to address
    */
    internal val hasLocationPermissions: (context: Context) -> Boolean = {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    internal val requestLocationUpdates: (LocationViewModel) -> Unit = { viewModel ->
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    val locationData =
                        LocationData(
                            location.latitude,
                            location.longitude
                        )
                    viewModel.updateLocation(locationData)
                }
            }
        }
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500).build()

        _fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("Deprecated")
    // geocoder to convert lat and long to address
    internal val reverseGeocodeLocation: (LocationData) -> String = { locationData ->
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(locationData.latitude, locationData.longitude)
        val addresses: MutableList<Address>? =
            geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)

        if (addresses?.isNotEmpty() == true) {
            addresses[0].getAddressLine(0)
        } else {
            "Address Not Found."
        }
    }

    /*
    NetWork Data
     */
    internal val hasNetworkPermissions: (context: Context) -> Boolean = {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_WIFI_STATE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED

    }

    internal val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    sealed interface NetworkConnectionState {
        data object Available : NetworkConnectionState
        data object Unavailable : NetworkConnectionState
    }

    internal fun networkCallback(callback: (NetworkConnectionState) -> Unit): ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            // network is available for use
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