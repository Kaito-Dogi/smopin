package app.kaito_dogi.smopin.feature.hoge

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kaito_dogi.smopin.shared.domain.smokingArea.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.Longitude
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Composable
fun HogeScreen(
  modifier: Modifier = Modifier,
  viewModel: HogeViewModel = metroViewModel(),
) {
  val context = LocalContext.current
  val uiState: HogeUiState by viewModel.uiState.collectAsStateWithLifecycle()
  var hasLocationPermission by remember { mutableStateOf(value = context.isLocationPermissionGranted()) }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
  ) { resultMap ->
    hasLocationPermission = resultMap.values.any { it }
  }

  LaunchedEffect(key1 = Unit) {
    viewModel.onCreate()
  }

  LaunchedEffect(hasLocationPermission) {
    if (hasLocationPermission) {
      context.locationUpdateFlow().collect(viewModel::onCurrentLocationUpdate)
    }
  }

  val initialPosition = remember(uiState.currentLocation, uiState.smokingAreaList) {
    uiState.currentLocation?.toLatLng()
      ?: uiState.smokingAreaList.getOrNull(index = 0)?.location?.toLatLng()
      ?: LatLng(35.681236, 139.767125)
  }
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(initialPosition, 17f)
  }
  val currentLocationMarkerState = remember {
    MarkerState(position = initialPosition)
  }

  LaunchedEffect(uiState.currentLocation) {
    uiState.currentLocation?.let { currentLocation ->
      val currentLatLng = currentLocation.toLatLng()
      currentLocationMarkerState.position = currentLatLng
      cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 17f)
    }
  }

  Scaffold(modifier = modifier) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues = innerPadding),
      verticalArrangement = Arrangement.spacedBy(space = dp(8)),
    ) {
      if (!hasLocationPermission) {
        Button(
          modifier = Modifier.fillMaxWidth(),
          onClick = {
            permissionLauncher.launch(
              arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
              ),
            )
          },
        ) {
          Text(text = "現在地を取得")
        }
      }

      uiState.currentLocation?.let { currentLocation ->
        Text(text = "current latitude: ${currentLocation.latitude.value}")
        Text(text = "current longitude: ${currentLocation.longitude.value}")
      }

      if (uiState.isLoading) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(weight = 1f),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      } else {
        GoogleMap(
          modifier = Modifier
            .fillMaxWidth()
            .weight(weight = 1f),
          cameraPositionState = cameraPositionState,
        ) {
          uiState.smokingAreaList.forEach { smokingArea ->
            Marker(
              state = rememberMarkerState(position = smokingArea.location.toLatLng()),
              title = smokingArea.name,
              snippet = smokingArea.name,
            )
          }
          uiState.currentLocation?.let {
            Marker(
              state = currentLocationMarkerState,
              title = "現在地",
              snippet = "現在地",
            )
          }
        }
      }
    }
  }
}

private fun Context.locationUpdateFlow(): Flow<Location> = callbackFlow {
  val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
  if (locationManager == null) {
    close()
    return@callbackFlow
  }

  val listener = LocationListener { location ->
    trySend(location.toDomainModel())
  }

  getLastKnownLocation(locationManager = locationManager)?.let { location ->
    trySend(location)
  }

  if (
    isLocationPermissionGranted() &&
    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
  ) {
    locationManager.requestLocationUpdates(
      LocationManager.NETWORK_PROVIDER,
      NETWORK_UPDATE_INTERVAL_MILLIS,
      NETWORK_UPDATE_DISTANCE_METER,
      listener,
      Looper.getMainLooper(),
    )
  }

  if (
    isFineLocationPermissionGranted() &&
    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
  ) {
    locationManager.requestLocationUpdates(
      LocationManager.GPS_PROVIDER,
      GPS_UPDATE_INTERVAL_MILLIS,
      GPS_UPDATE_DISTANCE_METER,
      listener,
      Looper.getMainLooper(),
    )
  }

  awaitClose {
    locationManager.removeUpdates(listener)
  }
}

private fun Context.getLastKnownLocation(locationManager: LocationManager): Location? =
  locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
    ?.toDomainModel()
    ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
      ?.toDomainModel()
    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
      ?.toDomainModel()

private fun Context.isLocationPermissionGranted(): Boolean {
  val fineLocationPermission =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
  val coarseLocationPermission =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
  return fineLocationPermission == PackageManager.PERMISSION_GRANTED
    || coarseLocationPermission == PackageManager.PERMISSION_GRANTED
}

private fun Context.isFineLocationPermissionGranted(): Boolean =
  ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
    PackageManager.PERMISSION_GRANTED

private fun Location.toLatLng(): LatLng = LatLng(latitude.value, longitude.value)

private fun android.location.Location.toDomainModel(): Location = Location(
  latitude = Latitude(value = latitude),
  longitude = Longitude(value = longitude),
)

private const val NETWORK_UPDATE_INTERVAL_MILLIS: Long = 10_000L
private const val NETWORK_UPDATE_DISTANCE_METER: Float = 20f
private const val GPS_UPDATE_INTERVAL_MILLIS: Long = 5_000L
private const val GPS_UPDATE_DISTANCE_METER: Float = 10f
