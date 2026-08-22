package com.example.recipe_app_compose.features.location.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKAnnotationViewDragState
import platform.MapKit.MKAnnotationViewDragStateEnding
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKLaunchOptionsDirectionsModeDriving
import platform.MapKit.MKLaunchOptionsDirectionsModeKey
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKMapView
import platform.MapKit.MKMapItem
import platform.MapKit.MKMarkerAnnotationView
import platform.MapKit.MKPlacemark
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIGestureRecognizerStateEnded
import platform.UIKit.UITapGestureRecognizer
import platform.Foundation.NSSelectorFromString
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformLocationMap(
    initialLocation: LocationData,
    selectedLocation: LocationData,
    markerTitle: String,
    markerSubtitle: String,
    onLocationSelected: (LocationData) -> Unit,
    onMapLoaded: () -> Unit,
    modifier: Modifier,
) {
    val initialCoordinate = remember(initialLocation) {
        CLLocationCoordinate2DMake(initialLocation.latitude, initialLocation.longitude)
    }
    val marker = remember(initialLocation) {
        MKPointAnnotation(initialCoordinate, title = null, subtitle = null)
    }
    val mapView = remember(initialLocation) {
        MKMapView().apply {
            // iOS MapKit intentionally uses gestures instead of an on-screen
            // MKZoomControl, which Apple exposes only on macOS/Mac Catalyst.
            zoomEnabled = true
            scrollEnabled = true
            rotateEnabled = true
            pitchEnabled = true
            addAnnotation(marker)
            setRegion(
                MKCoordinateRegionMakeWithDistance(
                    centerCoordinate = initialCoordinate,
                    latitudinalMeters = MAP_SPAN_METERS,
                    longitudinalMeters = MAP_SPAN_METERS,
                ),
                animated = false,
            )
        }
    }
    val mapDelegate = remember(mapView, marker) { IosMapDelegate(marker) }
    val tapHandler = remember(mapView) { IosMapTapHandler(mapView) }
    val tapRecognizer = remember(tapHandler) {
        UITapGestureRecognizer(
            target = tapHandler,
            action = NSSelectorFromString("handleTap:"),
        ).apply {
            cancelsTouchesInView = false
        }
    }

    DisposableEffect(mapView, tapRecognizer) {
        mapView.delegate = mapDelegate
        mapView.addGestureRecognizer(tapRecognizer)
        onDispose {
            mapView.delegate = null
            mapView.removeGestureRecognizer(tapRecognizer)
        }
    }

    UIKitView(
        factory = { mapView },
        modifier = modifier,
        update = {
            tapHandler.onLocationSelected = onLocationSelected
            mapDelegate.onLocationSelected = onLocationSelected
            marker.setCoordinate(
                CLLocationCoordinate2DMake(selectedLocation.latitude, selectedLocation.longitude)
            )
            marker.setTitle(markerTitle)
            marker.setSubtitle(markerSubtitle)
            onMapLoaded()
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true,
        ),
    )
}

@OptIn(ExperimentalForeignApi::class)
private class IosMapDelegate(
    private val marker: MKPointAnnotation,
) : NSObject(), MKMapViewDelegateProtocol {
    var onLocationSelected: (LocationData) -> Unit = {}

    override fun mapView(
        mapView: MKMapView,
        viewForAnnotation: MKAnnotationProtocol,
    ): MKAnnotationView? {
        if (viewForAnnotation != marker) return null

        val markerView = mapView.dequeueReusableAnnotationViewWithIdentifier(MARKER_IDENTIFIER)
            as? MKMarkerAnnotationView
            ?: MKMarkerAnnotationView(
                annotation = viewForAnnotation,
                reuseIdentifier = MARKER_IDENTIFIER,
            )
        return markerView.apply {
            annotation = viewForAnnotation
            draggable = true
            canShowCallout = true
        }
    }

    override fun mapView(
        mapView: MKMapView,
        annotationView: MKAnnotationView,
        didChangeDragState: MKAnnotationViewDragState,
        fromOldState: MKAnnotationViewDragState,
    ) {
        if (didChangeDragState != MKAnnotationViewDragStateEnding) return

        annotationView.annotation?.coordinate?.useContents {
            onLocationSelected(LocationData(latitude, longitude))
        }
    }
}

@Composable
actual fun rememberDirectionsLauncher(): DirectionsLauncher = remember {
    IosDirectionsLauncher
}

@OptIn(ExperimentalForeignApi::class)
private object IosDirectionsLauncher : DirectionsLauncher {
    @Suppress("DEPRECATION")
    override fun openDrivingDirections(destination: LocationData) {
        val coordinate = CLLocationCoordinate2DMake(
            destination.latitude,
            destination.longitude,
        )
        val destinationItem = MKMapItem(
            placemark = MKPlacemark(coordinate = coordinate),
        )
        destinationItem.openInMapsWithLaunchOptions(
            mapOf(MKLaunchOptionsDirectionsModeKey to MKLaunchOptionsDirectionsModeDriving),
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private class IosMapTapHandler(
    private val mapView: MKMapView,
) : NSObject() {
    var onLocationSelected: (LocationData) -> Unit = {}

    @OptIn(BetaInteropApi::class)
    @ObjCAction
    fun handleTap(recognizer: UITapGestureRecognizer) {
        if (recognizer.state != UIGestureRecognizerStateEnded) return
        val point = recognizer.locationInView(mapView)
        mapView.convertPoint(point, toCoordinateFromView = mapView).useContents {
            onLocationSelected(LocationData(latitude, longitude))
        }
    }
}

private const val MAP_SPAN_METERS = 10_000.0
private const val MARKER_IDENTIFIER = "selected-location"
