package com.example.recipe_app_compose.features.location.domain.location

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.core.content.ContextCompat
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
actual fun rememberLocationAccess(): LocationAccess {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context.findActivity()
    val access = remember(context.applicationContext) {
        AndroidLocationAccess(context.applicationContext)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        access.onPermissionResult(
            granted = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_FINE_LOCATION] == true,
            activity = activity,
        )
    }

    access.bind(
        activity = activity,
        requestPermissions = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
        },
    )

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        access.refreshAuthorization(activity)
    }
    return access
}

private class AndroidLocationAccess(
    private val context: Context,
) : LocationAccess {
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private val mutableAuthorization = MutableStateFlow(LocationAuthorization.NotDetermined)
    private var activity: Activity? = null
    private var permissionRequester: (() -> Unit)? = null

    override val authorization: StateFlow<LocationAuthorization> = mutableAuthorization

    fun bind(activity: Activity?, requestPermissions: () -> Unit) {
        this.activity = activity
        permissionRequester = requestPermissions
        refreshAuthorization(activity)
    }

    override fun requestWhenInUseAccess() {
        if (hasLocationPermission()) {
            mutableAuthorization.value = LocationAuthorization.Granted
        } else {
            permissionRequester?.invoke()
        }
    }

    fun onPermissionResult(granted: Boolean, activity: Activity?) {
        mutableAuthorization.value = if (granted) {
            LocationAuthorization.Granted
        } else if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true ||
            activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) == true
        ) {
            LocationAuthorization.Denied
        } else {
            // Android does not distinguish a first request from a permanent denial
            // here. The next shared action will still expose Settings if needed.
            LocationAuthorization.Denied
        }
    }

    fun refreshAuthorization(activity: Activity?) {
        mutableAuthorization.value = if (hasLocationPermission()) {
            LocationAuthorization.Granted
        } else if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true ||
            activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) == true
        ) {
            LocationAuthorization.Denied
        } else {
            LocationAuthorization.NotDetermined
        }
    }

    override suspend fun currentLocation(): LocationData? {
        if (!hasLocationPermission()) return null
        val location = requestRecentLocation() ?: requestCurrentLocation()
        return location?.let { LocationData(it.latitude, it.longitude) }
    }

    override fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    private suspend fun requestCurrentLocation(): Location? {
        val request = CurrentLocationRequest.Builder()
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(MAX_LOCATION_AGE_MILLIS)
            .setDurationMillis(LOCATION_TIMEOUT_MILLIS)
            .build()
        return try {
            suspendCancellableCoroutine { continuation ->
                val tokenSource = CancellationTokenSource()
                continuation.invokeOnCancellation { tokenSource.cancel() }
                locationClient.getCurrentLocation(request, tokenSource.token)
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) continuation.resume(location)
                    }
                    .addOnFailureListener { error ->
                        if (continuation.isActive) continuation.resumeWithException(error)
                    }
                    .addOnCanceledListener { continuation.cancel() }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun requestRecentLocation(): Location? {
        val request = LastLocationRequest.Builder()
            .setMaxUpdateAgeMillis(MAX_LAST_LOCATION_AGE_MILLIS)
            .build()
        return try {
            suspendCancellableCoroutine { continuation ->
                locationClient.getLastLocation(request)
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) continuation.resume(location)
                    }
                    .addOnFailureListener { error ->
                        if (continuation.isActive) continuation.resumeWithException(error)
                    }
                    .addOnCanceledListener { continuation.cancel() }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        const val MAX_LOCATION_AGE_MILLIS = 5 * 60 * 1000L
        const val MAX_LAST_LOCATION_AGE_MILLIS = 5 * 60 * 1000L
        const val LOCATION_TIMEOUT_MILLIS = 10_000L
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
