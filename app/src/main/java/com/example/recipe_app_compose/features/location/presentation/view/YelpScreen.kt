package com.example.recipe_app_compose.features.location.presentation.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.util.permissions.foregroundLocationPermissions
import com.example.recipe_app_compose.core.util.permissions.hasForegroundLocationPermission
import com.example.recipe_app_compose.core.util.permissions.openAppPermissionSettings
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpShop
import com.example.recipe_app_compose.features.location.domain.states.YelpSearchArea
import com.example.recipe_app_compose.features.location.domain.states.YelpUiState
import com.example.recipe_app_compose.features.location.presentation.viewmodel.YelpViewModel

@Composable
fun YelpScreen(
    modifier: Modifier = Modifier,
    onShopSelected: (LocationData) -> Unit,
) {
    val context = LocalContext.current

    val viewModel: YelpViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val manualLocationText by viewModel.manualLocationQuery.collectAsStateWithLifecycle()

    var hasLocationPermission by remember {
        mutableStateOf(context.hasForegroundLocationPermission())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        hasLocationPermission = context.hasForegroundLocationPermission()
        if (hasLocationPermission) {
            viewModel.loadNearbyShops(forceRefresh = true)
        } else {
            viewModel.onLocationPermissionDenied()
        }
    }

    fun requestLocationAccess() {
        if (context.hasForegroundLocationPermission()) {
            hasLocationPermission = true
            viewModel.loadNearbyShops(forceRefresh = true)
        } else {
            permissionLauncher.launch(foregroundLocationPermissions())
        }
    }

    LifecycleResumeEffect(Unit) {
        hasLocationPermission = context.hasForegroundLocationPermission()
        if (hasLocationPermission) {
            viewModel.loadNearbyShops()
        } else {
            viewModel.onLocationPermissionDenied()
        }
        onPauseOrDispose { }
    }

    val visibleViewState = if (
        !hasLocationPermission && uiState.searchArea == YelpSearchArea.Initializing
    ) {
        uiState.copy(searchArea = YelpSearchArea.PermissionRequired)
    } else {
        uiState
    }

    YelpContent(
        uiState = visibleViewState,
        manualLocationText = manualLocationText,
        onManualLocationChange = viewModel::onManualLocationChange,
        onSearchManualLocation = viewModel::searchManualLocation,
        onRequestLocation = ::requestLocationAccess,
        onOpenAppSettings = context::openAppPermissionSettings,
        onRetry = viewModel::retry,
        onShopSelected = onShopSelected,
        modifier = modifier,
    )
}

@Composable
internal fun YelpContent(
    uiState: YelpUiState,
    manualLocationText: String,
    onManualLocationChange: (String) -> Unit,
    onSearchManualLocation: () -> Unit,
    onRequestLocation: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onRetry: () -> Unit,
    onShopSelected: (LocationData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasSearchOrigin = uiState.searchArea == YelpSearchArea.CurrentLocation ||
            uiState.searchArea is YelpSearchArea.NamedLocation

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        if (hasSearchOrigin) {
            SearchAreaLabel(uiState.searchArea)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when {
                uiState.searchArea == YelpSearchArea.PermissionRequired ->
                    LocationFallbackContent(
                        title = stringResource(R.string.find_shops_near_you),
                        message = stringResource(R.string.location_permission_explanation),
                        actionLabel = stringResource(R.string.use_my_location),
                        manualLocationText = manualLocationText,
                        onManualLocationChange = onManualLocationChange,
                        onSearchManualLocation = onSearchManualLocation,
                        onLocationAction = onRequestLocation,
                        onOpenAppSettings = onOpenAppSettings,
                        showUnavailableIcon = false,
                        modifier = Modifier.align(Alignment.Center),
                    )

                uiState.searchArea == YelpSearchArea.Initializing ||
                        uiState.searchArea == YelpSearchArea.ResolvingCurrentLocation ->
                    LocationLoadingContent(modifier = Modifier.align(Alignment.Center))

                uiState.searchArea == YelpSearchArea.LocationUnavailable ->
                    LocationFallbackContent(
                        title = stringResource(R.string.location_unavailable),
                        message = stringResource(R.string.location_unavailable_message),
                        actionLabel = stringResource(R.string.try_again),
                        manualLocationText = manualLocationText,
                        onManualLocationChange = onManualLocationChange,
                        onSearchManualLocation = onSearchManualLocation,
                        onLocationAction = onRequestLocation,
                        onOpenAppSettings = onOpenAppSettings,
                        showUnavailableIcon = true,
                        modifier = Modifier.align(Alignment.Center),
                    )

                uiState.loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                uiState.error != null -> SearchErrorContent(
                    message = uiState.error,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )

                uiState.list.isEmpty() -> Text(
                    text = stringResource(R.string.no_results_found),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> YelpListScreen(
                    shops = uiState.list,
                    onShopSelected = onShopSelected,
                )
            }
        }
    }
}

@Composable
private fun SearchAreaLabel(searchArea: YelpSearchArea) {
    val label = when (searchArea) {
        YelpSearchArea.CurrentLocation -> stringResource(R.string.near_your_current_location)
        is YelpSearchArea.NamedLocation -> stringResource(
            R.string.searching_near_location,
            searchArea.value,
        )

        else -> return
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
    )
}

@Composable
private fun LocationLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.finding_nearby_shops),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun LocationFallbackContent(
    title: String,
    message: String,
    actionLabel: String,
    manualLocationText: String,
    onManualLocationChange: (String) -> Unit,
    onSearchManualLocation: () -> Unit,
    onLocationAction: () -> Unit,
    onOpenAppSettings: () -> Unit,
    showUnavailableIcon: Boolean,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = if (showUnavailableIcon) {
                    Icons.Outlined.LocationOff
                } else {
                    Icons.Outlined.LocationOn
                },
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onLocationAction,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(actionLabel)
            }
            TextButton(onClick = onOpenAppSettings) {
                Text(stringResource(R.string.open_app_settings))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.or_enter_location_manually),
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = manualLocationText,
                onValueChange = onManualLocationChange,
                label = { Text(stringResource(R.string.city_or_zip_code)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (manualLocationText.isNotBlank()) {
                            focusManager.clearFocus()
                            onSearchManualLocation()
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    onSearchManualLocation()
                },
                enabled = manualLocationText.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.search_this_area))
            }
        }
    }
}

@Composable
private fun SearchErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.unable_to_load_shops),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.try_again))
        }
    }
}

@Composable
fun YelpListScreen(
    shops: List<YelpShop>,
    onShopSelected: (LocationData) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 144.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(shops, key = YelpShop::id) { shop ->
            YelpItem(
                shop = shop,
                onShopSelected = onShopSelected,
            )
        }
    }
}

@Composable
fun YelpItem(
    shop: YelpShop,
    onShopSelected: (LocationData) -> Unit,
) {
    val locationData =
        LocationData(shop.coordinates.latitude, shop.coordinates.longitude)
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = rememberAsyncImagePainter(shop.imageUrl),
            contentDescription = stringResource(R.string.image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onShopSelected(locationData) },
        )
        Text(
            text = "${shop.name} ${shop.displayRating()} \uD83C\uDF1F",
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 5.dp, bottom = 2.dp),
        )
        Text(
            text = "${shop.location.address1}, ${shop.location.city}, " +
                    "${shop.location.state} ${shop.location.country} " +
                    shop.location.zipCode,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 2.dp),
        )
        Text(
            text = shop.displayPhoneNumber(),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
        )
    }
}
