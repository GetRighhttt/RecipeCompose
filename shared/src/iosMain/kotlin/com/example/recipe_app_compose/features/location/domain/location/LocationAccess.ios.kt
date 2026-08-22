package com.example.recipe_app_compose.features.location.domain.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLLocationAccuracyHundredMeters
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.NSObject
import kotlin.coroutines.resume

@Composable
actual fun rememberLocationAccess(): LocationAccess {
    val access = remember { IosLocationAccess() }
    DisposableEffect(access) {
        onDispose(access::close)
    }
    return access
}

@OptIn(ExperimentalForeignApi::class)
private class IosLocationAccess : LocationAccess {
    private val locationManager = CLLocationManager()
    private val delegate = IosLocationDelegate(this)
    private val mutableAuthorization = MutableStateFlow(currentAuthorization())
    private var pendingLocation: CancellableContinuation<LocationData?>? = null

    override val authorization: StateFlow<LocationAuthorization> = mutableAuthorization

    init {
        locationManager.delegate = delegate
        locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters
    }

    override fun requestWhenInUseAccess() {
        if (!CLLocationManager.locationServicesEnabled()) {
            mutableAuthorization.value = LocationAuthorization.ServicesDisabled
            return
        }
        if (locationManager.authorizationStatus == kCLAuthorizationStatusNotDetermined) {
            locationManager.requestWhenInUseAuthorization()
        } else {
            mutableAuthorization.value = currentAuthorization()
        }
    }

    override suspend fun currentLocation(): LocationData? = withContext(Dispatchers.Main) {
        if (mutableAuthorization.value != LocationAuthorization.Granted) return@withContext null
        suspendCancellableCoroutine { continuation ->
            pendingLocation?.takeIf { it.isActive }?.resume(null)
            pendingLocation = continuation
            continuation.invokeOnCancellation {
                if (pendingLocation === continuation) {
                    pendingLocation = null
                    locationManager.stopUpdatingLocation()
                }
            }
            locationManager.requestLocation()
        }
    }

    override fun openAppSettings() {
        NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let { url ->
            UIApplication.sharedApplication.openURL(url)
        }
    }

    fun onAuthorizationChanged() {
        mutableAuthorization.value = currentAuthorization()
    }

    fun onLocationsUpdated(didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation
        completeLocation(location?.coordinate?.useContents {
            LocationData(latitude, longitude)
        })
    }

    fun onLocationFailure() {
        completeLocation(null)
    }

    fun close() {
        locationManager.stopUpdatingLocation()
        pendingLocation?.takeIf { it.isActive }?.resume(null)
        pendingLocation = null
        locationManager.delegate = null
    }

    private fun completeLocation(location: LocationData?) {
        pendingLocation?.takeIf { it.isActive }?.resume(location)
        pendingLocation = null
        locationManager.stopUpdatingLocation()
    }

    private fun currentAuthorization(): LocationAuthorization = when {
        !CLLocationManager.locationServicesEnabled() -> LocationAuthorization.ServicesDisabled
        locationManager.authorizationStatus == kCLAuthorizationStatusAuthorizedAlways ||
            locationManager.authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ->
            LocationAuthorization.Granted

        locationManager.authorizationStatus == kCLAuthorizationStatusDenied -> LocationAuthorization.Denied
        locationManager.authorizationStatus == kCLAuthorizationStatusRestricted -> LocationAuthorization.Restricted
        else -> LocationAuthorization.NotDetermined
    }
}

@OptIn(ExperimentalForeignApi::class)
private class IosLocationDelegate(
    private val access: IosLocationAccess,
) : NSObject(), CLLocationManagerDelegateProtocol {
    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        access.onAuthorizationChanged()
    }

    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<*>,
    ) {
        access.onLocationsUpdated(didUpdateLocations)
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        access.onLocationFailure()
    }
}
