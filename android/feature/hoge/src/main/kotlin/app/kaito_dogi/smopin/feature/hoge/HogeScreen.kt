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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kaito_dogi.smopin.shared.domain.smokingArea.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.Longitude
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
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
  var lastCameraPosition by remember {
    mutableStateOf(value = uiState.currentLocation)
  }
  var isMapLoaded by remember { mutableStateOf(value = false) }
  val layoutDirection = LocalLayoutDirection.current
  val safeDrawingPaddingValues = WindowInsets.safeDrawing.asPaddingValues()
  val mapContentPadding = PaddingValues(
    start = safeDrawingPaddingValues.calculateLeftPadding(layoutDirection),
    end = safeDrawingPaddingValues.calculateRightPadding(layoutDirection),
    top = safeDrawingPaddingValues.calculateTopPadding(),
    bottom = safeDrawingPaddingValues.calculateBottomPadding() + MAP_ROUTE_GUIDE_HEIGHT,
  )

  LaunchedEffect(uiState.currentLocation) {
    uiState.currentLocation?.let { currentLocation ->
      val currentCameraPosition = CameraPosition.fromLatLngZoom(currentLocation.toLatLng(), MAP_ZOOM_LEVEL)
      if (!isMapLoaded) {
        cameraPositionState.position = currentCameraPosition
        lastCameraPosition = currentLocation
        return@let
      }
      if (lastCameraPosition == null) {
        lastCameraPosition = currentLocation
        cameraPositionState.position = currentCameraPosition
        return@let
      }
      val distance = lastCameraPosition?.distanceTo(other = currentLocation) ?: 0.0
      if (distance >= MIN_CAMERA_UPDATE_DISTANCE_METER) {
        lastCameraPosition = currentLocation
        cameraPositionState.position = currentCameraPosition
      }
    }
  }

  Scaffold(
    modifier = modifier,
    contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(mapToolbarEnabled = true),
        contentPadding = mapContentPadding,
        onMapLoaded = {
          isMapLoaded = true
        },
      ) {
        uiState.smokingAreaList.forEach { smokingArea ->
          key(smokingArea.name) {
            Marker(
              state = rememberMarkerState(position = smokingArea.location.toLatLng()),
              title = smokingArea.name,
              snippet = smokingArea.name,
            )
          }
        }
      }

      Column(
        modifier = Modifier
          .fillMaxSize()
          .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(space = 8.dp),
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
        }

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
          Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            text = "ピンをタップしてから、右下の経路ボタンでルートを表示できます",
          )
        }
      }

      if (uiState.isLoading) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
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

private fun Location.distanceTo(other: Location): Double {
  val latitudeDiff = latitude.value - other.latitude.value
  val longitudeDiff = longitude.value - other.longitude.value
  val latitudeMeter = latitudeDiff * LATITUDE_DEGREE_TO_METER
  val longitudeMeter = longitudeDiff * LONGITUDE_DEGREE_TO_METER
  return kotlin.math.sqrt(latitudeMeter * latitudeMeter + longitudeMeter * longitudeMeter)
}

private fun android.location.Location.toDomainModel(): Location = Location(
  latitude = Latitude(value = latitude),
  longitude = Longitude(value = longitude),
)

private const val NETWORK_UPDATE_INTERVAL_MILLIS: Long = 10_000L
private const val NETWORK_UPDATE_DISTANCE_METER: Float = 20f
private const val GPS_UPDATE_INTERVAL_MILLIS: Long = 5_00L
private const val GPS_UPDATE_DISTANCE_METER: Float = 10f
private const val MIN_CAMERA_UPDATE_DISTANCE_METER: Double = 15.0
private const val LATITUDE_DEGREE_TO_METER: Double = 111_320.0
private const val LONGITUDE_DEGREE_TO_METER: Double = 91_000.0
private const val MAP_ZOOM_LEVEL: Float = 17f
private val MAP_ROUTE_GUIDE_HEIGHT = 72.dp
