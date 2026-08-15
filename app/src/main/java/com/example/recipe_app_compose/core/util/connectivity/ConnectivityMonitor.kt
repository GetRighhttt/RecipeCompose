package com.example.recipe_app_compose.core.util.connectivity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ConnectivityStatus {
    Available,
    Unavailable,
}

class ConnectivityMonitor(context: Context) {
    private val connectivityManager =
        context.applicationContext.getSystemService(ConnectivityManager::class.java)

    val status: StateFlow<ConnectivityStatus>
        field: MutableStateFlow<ConnectivityStatus> = MutableStateFlow(currentStatus())

    private var isMonitoring = false

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities,
        ) {
            status.value = networkCapabilities.toConnectivityStatus()
        }

        override fun onLost(network: Network) {
            refresh()
        }
    }

    fun start() {
        if (isMonitoring) return

        refresh()
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        isMonitoring = true
    }

    fun stop() {
        if (!isMonitoring) return

        connectivityManager.unregisterNetworkCallback(networkCallback)
        isMonitoring = false
    }

    fun refresh(): ConnectivityStatus = currentStatus().also { currentStatus ->
        status.value = currentStatus
    }

    private fun currentStatus(): ConnectivityStatus {
        val capabilities = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )
        return capabilities?.toConnectivityStatus() ?: ConnectivityStatus.Unavailable
    }
}

private fun NetworkCapabilities.toConnectivityStatus(): ConnectivityStatus {
    val hasInternet = hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    val isValidated = hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    return if (hasInternet && isValidated) {
        ConnectivityStatus.Available
    } else {
        ConnectivityStatus.Unavailable
    }
}

@Composable
fun rememberConnectivityMonitor(): ConnectivityMonitor {
    val applicationContext = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val monitor = remember(applicationContext) {
        ConnectivityMonitor(applicationContext)
    }

    DisposableEffect(lifecycleOwner, monitor) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> monitor.start()
                Lifecycle.Event.ON_STOP -> monitor.stop()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            monitor.start()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            monitor.stop()
        }
    }

    return monitor
}

fun Context.openNetworkSettings() {
    val networkSettingsIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
    } else {
        Intent(Settings.ACTION_WIRELESS_SETTINGS)
    }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    val openedNetworkSettings = runCatching {
        startActivity(networkSettingsIntent)
    }.isSuccess

    if (!openedNetworkSettings) {
        runCatching {
            startActivity(
                Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
